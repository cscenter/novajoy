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
	private String baseAddr = "novajoy.cosqmvrs3gb3.us-east-1.rds.amazonaws.com",
			baseName = "novajoydb",
			baseUser = "novauser",
			basePass = "ru4Afoh5";

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

	private static void process_reqsts(String srvrName, String dbName,
			String usrName, String pass) {
		Connection connection;
		try {
			connection = establish_jdbc_connection(srvrName, dbName, usrName,
					pass);

			String query = "Select * FROM Server_rssfeed";
			String get_query1 = "UPDATE Server_rssfeed  SET pubDate='";
			String get_query2 = "' WHERE id=";

			Statement stmt = connection.createStatement();
			PreparedStatement ps = connection.prepareStatement("");
			ResultSet rs = stmt.executeQuery(query);
			String addr;

			while (rs.next()) {
				addr = rs.getString(2);
				System.out.println("Crawling from: " + addr);
				SyndFeed feed = readfeed(addr);
				System.out.println(feed.getEncoding());
				for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
					insert_item((SyndEntry) i.next(), connection,
							rs.getString(1));
				}

				String req = get_query1
						+ (new java.sql.Timestamp(feed.getPublishedDate()
								.getTime())) + get_query2 + rs.getString(1)
						+ ";";
				ps.executeUpdate(req);
				System.out
						.println("Crawling and updating finished on: " + addr);
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

	private static Connection establish_jdbc_connection(String srvrName,
			String dbName, String usrName, String pass)
			throws ClassNotFoundException, SQLException {
		String driverName = "com.mysql.jdbc.Driver";
		Class.forName(driverName);
		String url = "jdbc:mysql://" + srvrName + "/" + dbName;
		Connection connection = DriverManager.getConnection(url, usrName, pass);
		System.out.println("Connected to DB" + connection);
		return connection;
	}

	private static SyndFeed readfeed(String addr) throws Exception {
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

	private static void insert_item(SyndEntry entry, Connection connection,
			String feedid) throws SQLException, UnsupportedEncodingException {
		Statement stmt = connection.createStatement();
		String query = "INSERT INTO Server_rssitem (rssfeed_id, title, description, link, author, pubDate) VALUES("
				+ feedid
				+ ",\""
				+ entry.getTitle()
				+ "\",\""
				+ entry.getDescription().getValue().replace("\"", "\\\"")
				+ "\",\""
				+ entry.getLink()
				+ "\",\""
				+ entry.getAuthor()
				+ "\",\""
				+ (new java.sql.Timestamp(entry.getPublishedDate().getTime()))
				+ "\");";
		// System.out.println(query);
		stmt.executeUpdate(query);
	}

	public static void main(String[] args) throws Exception {
		new Crawler("Crawler_1").start();
	}
}
