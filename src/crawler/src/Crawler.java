import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.net.URL;
import java.util.Iterator;

import java.io.*;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class Crawler extends Thread {
	private String baseAddr = "***",
			baseName = "***",
			baseUser = "***",
			basePass = "***";

	public Crawler(String str) {
		super(str);
	}

	public Crawler(String str, String baseAddr, String baseName,
			String baseUser, String basePass) {
		super(str);
		this.baseAddr = baseAddr;
		this.baseName = baseName;
		this.baseUser = baseUser;
		this.basePass = basePass;
	}

	public void run() {
		process_reqsts(baseAddr, baseName, baseUser, basePass);
	}

	private void process_reqsts(String srvrName, String dbName, String usrName,
			String pass) {
		Connection connection;
		try {
			connection = establish_jdbc_connection(srvrName, dbName, usrName,
					pass);

			String query = "Select * FROM Server_rssfeed";
			String pquery = "UPDATE Server_rssfeed  SET pubDate = ? WHERE id = ?";

			Statement stmt = connection.createStatement();
			PreparedStatement ps = connection.prepareStatement(pquery);
			ResultSet rs = stmt.executeQuery(query);
			String addr;
			int items_count = 0;
			while (rs.next()) {
				addr = rs.getString(2);
				System.out.println("Crawling from: " + addr);
				SyndFeed feed = readfeed(addr);
				for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
					if (insert_item((SyndEntry) i.next(), connection,
							Long.parseLong(rs.getString(1))))
						++items_count;
				}

				ps.setTimestamp(1, new java.sql.Timestamp(feed
						.getPublishedDate().getTime()));
				ps.setLong(2, Long.parseLong(rs.getString(1)));
				ps.executeUpdate();
				System.out.println(items_count
						+ " items added.\nCrawling and updating finished on: "
						+ addr);
				items_count = 0;
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			// Could not find the database driver
		} catch (Exception e) {
			e.printStackTrace();
			// Could not connect to the database
		}
	}

	private Connection establish_jdbc_connection(String srvrName,
			String dbName, String usrName, String pass)
			throws ClassNotFoundException, SQLException {
		String driverName = "com.mysql.jdbc.Driver";
		Class.forName(driverName);
		String url = "jdbc:mysql://" + srvrName + "/" + dbName;
		Connection connection = DriverManager.getConnection(url
				+ "?useUnicode=true&characterEncoding=utf-8", usrName, pass);
		System.out.println("Connected to DB" + connection);
		return connection;
	}

	private SyndFeed readfeed(String addr) throws Exception {
		XmlReader reader = null;
		SyndFeed feed = null;
		try {
			reader = new XmlReader(new URL(addr));
			feed = new SyndFeedInput().build(reader);
		} finally {
			if (reader != null)
				reader.close();
		}
		return feed;
	}

	private boolean insert_item(SyndEntry entry, Connection connection,
			long feedid) throws SQLException {

		if (check_already_in(connection, entry.getLink())) {
			// System.out.println("Entry with link: " + entry.getLink() +
			// " already exists.");
			return false;
		}

		String pquery = "INSERT INTO Server_rssitem (rssfeed_id, title, description, link, author, pubDate) VALUES(?,?,?,?,?,?)";
		PreparedStatement ps = connection.prepareStatement(pquery);

		ps.setLong(1, feedid);
		ps.setString(2, entry.getTitle());
		ps.setString(3, entry.getDescription().getValue());
		ps.setString(4, entry.getLink());
		ps.setString(5, entry.getAuthor());
		ps.setTimestamp(6, new java.sql.Timestamp(entry.getPublishedDate()
				.getTime()));
		// System.out.println(entry.get);
		ps.executeUpdate();
		return true;
	}

	private boolean check_already_in(Connection connection, String link)
			throws SQLException {
		String pquery = "Select * FROM Server_rssitem WHERE link = ?";
		PreparedStatement ps = connection.prepareStatement(pquery);
		ps.setString(1, link);
		return ps.executeQuery().next();
	}

	public static void main(String[] args) throws Exception {
		new Crawler("Crawler_1").start();
	}
}
