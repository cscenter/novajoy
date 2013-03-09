import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.net.URL;
import java.util.Iterator;
import java.util.Scanner;

import java.net.*;
import java.io.*;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class Reader {

    public static void main(String[] args) throws Exception {
        process_reqsts("novajoy.cosqmvrs3gb3.us-east-1.rds.amazonaws.com", "novajoydb", "novauser", "ru4Afoh5");
    }


    private static void process_reqsts(String srvrName, String dbName, String usrName, String pass) {
        Connection connection;
        try {
            connection = establish_jdbc_connection(srvrName, dbName, usrName, pass);

            String query = "Select * FROM Server_rssfeed";
            String get_query1 = "UPDATE Server_rssfeed  SET content='";
            String get_query2 = "' WHERE id=";

            Statement stmt = connection.createStatement();
            PreparedStatement ps = connection.prepareStatement("");
            ResultSet rs = stmt.executeQuery(query);
            String addr;

            while (rs.next()) {
                addr = rs.getString(2);
                System.out.println(addr);
                String req = get_query1 + readfeed(addr) + get_query2 + rs.getString(1) + ";";
                ps.executeUpdate(req);
                System.out.println(req);
            } // end while
            connection.close();
        } // end try
        catch (SQLException e) {
            e.printStackTrace();
            // Could not find the database driver
        } catch (Exception e) {
            e.printStackTrace();
            // Could not connect to the database
        }

    }

    private static Connection establish_jdbc_connection(String srvrName, String dbName, String usrName, String pass) throws ClassNotFoundException, SQLException {
        String driverName = "com.mysql.jdbc.Driver";

        Class.forName(driverName);

        // Create a connection to the database
        String url = "jdbc:mysql://" + srvrName + "/" + dbName;

        Connection connection = DriverManager.getConnection(url, usrName, pass);

        System.out.println("Connected to DB" + connection);
        return connection;
    }


    private static String readfeed(String addr) throws Exception {
        URL url = new URL(addr);
        XmlReader reader = null;
        return (URLConnectionReader.getText(addr));
        /*try {

            reader = new XmlReader(url);
            SyndFeed feed = new SyndFeedInput().build(reader);
            System.out.println("Feed Title: " + feed.getAuthor());

            for (Iterator i = feed.getEntries().iterator(); i.hasNext(); ) {
                SyndEntry entry = (SyndEntry) i.next();
                //System.out.println(entry.getTitle());
            }

        } finally {
            if (reader != null)
                reader.close();
        }*/
    }
}

class URLConnectionReader {
    public static String getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }
}