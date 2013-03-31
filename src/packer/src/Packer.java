/**
 * Created with IntelliJ IDEA.
 * User: romanfilippov
 * Date: 09.03.13
 * Time: 23:02
 */
package packer.src;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.String;
import java.sql.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.logging.Logger;
import util.config.src.IniWorker;


class Packer{

    private String hostName = "";
    private final String className = "com.mysql.jdbc.Driver";
    private final String encProperties = "?useUnicode=true&characterEncoding=utf-8";
    private String dbName = "";
    private String userName = "";
    private String userPassword = "";
    private final String configPath = "/Users/romanfilippov/Dropbox/mydocs/Development/java/novaJoy/novajoy/config/config.ini";
    private static Logger log = Logger.getLogger(Packer.class.getName());

    private final String DEFAULT_SUBJECT = "Your rss feed from novaJoy";
    private final String DEFAULT_BODY = "Thank you for using our service!";

    Connection con = null;

    private UserItem[] users = null;

    public void InitConfiguration(IniWorker worker) {

        hostName = worker.getDBaddress();
        dbName = worker.getDBbasename();
        userName = worker.getDBuser();
        userPassword = worker.getDBpassword();
    }

    public Packer() {

        try {

            IniWorker config = new IniWorker(configPath);
            InitConfiguration(config);

            log.info("Establishing a connection...");
            String url = "jdbc:mysql://" + hostName + "/" + dbName;
            Class.forName(className);
            con = DriverManager.getConnection(url + encProperties, userName, userPassword);
            log.info("Connection established");

        } catch (ClassNotFoundException e) {
            log.warning("Class not found" + e.getMessage());
        } catch (SQLException ex) {
            log.warning(ex.getMessage());
        } catch (Exception exc) {
            log.warning(exc.getMessage());
        }
    }

    UserItem[] getUsersIds() throws SQLException {

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("select id,email from auth_user where id in (select distinct user_id from Server_collection where UNIX_TIMESTAMP(last_update_time)+delta_update_time<UNIX_TIMESTAMP());");

        int rowcount = 0;
        if (rs.last()) {
            rowcount = rs.getRow();
            rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
        } else {
            return null;
        }

        UserItem[] usersIds = new UserItem[rowcount];

        int i = 0;
        while (rs.next()) {
            usersIds[i] = new UserItem(rs.getInt(1), rs.getString(2));
            i++;
        }

        rs.close();
        st.close();

        return usersIds;
    }

    RssItem[] getDataForUserId(int uid) throws SQLException {

        Statement st = con.createStatement();
        //System.out.println("select * from Server_rssitem where rssfeed_id in (select rssfeed_id from Server_rssfeed_collection where collection_id in (select id from Server_collection where user_id = " + uid +"));");
        ResultSet rs = st.executeQuery("select * from Server_rssitem where rssfeed_id in (select rssfeed_id from Server_rssfeed_collection where collection_id in (select id from Server_collection where user_id = " + uid +"));");

        int rowcount = 0;
        if (rs.last()) {
            rowcount = rs.getRow();
            rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
        } else {
            return null;
        }

        RssItem[] items = new RssItem[rowcount];

        int i = 0;
        while (rs.next()) {
            items[i] = new RssItem(rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getDate(7));
            i++;
        }

        rs.close();
        st.close();

        return items;
    }

    DocumentItem formDocument (String target, RssItem[] userFeeds) {

        StringBuilder builder = new StringBuilder("<html><head><title>Your RSS feed from novaJoy</title></head><body>");

        for (int i = 0; i < userFeeds.length; i++) {
            builder.append(userFeeds[i].toHtml());
        }

        builder.append("</body></html>");

        return new DocumentItem(target,builder.toString());
    }

    void updateFeedTime(UserItem[] users) throws SQLException {

        log.info("Start updating feed updating times");

        String query = "update Server_collection set last_update_time=FROM_UNIXTIME(UNIX_TIMESTAMP()) where user_id in ";

        query += "(";
        query += new String(new char[users.length-1]).replace("\0", "?,");
        query += "?);";

        PreparedStatement ps = con.prepareStatement(query);

        for (int i = 0; i < users.length; i++) {

            ps.setInt(i+1,users[i].user_id);
        }

        int rs = ps.executeUpdate();

        users = null;

        if (rs > 0) {
            log.info("Updating tasks completed");
        } else {
            log.warning("Something went wrong while updating");
        }
    }

    LinkedList getPackagedData() throws SQLException {

        UserItem[] userIds = getUsersIds();
        LinkedList usersDocuments = new LinkedList();

        int j=0;

        for (; ; ) {

            if (j >= userIds.length)
                break;

            RssItem[] userFeed = getDataForUserId(userIds[j].user_id);

            if (userFeed == null) {
                j++;
                continue;
            }
            usersDocuments.add(formDocument(userIds[j].user_email, userFeed));
            j++;
        }

        return usersDocuments;
    }

    public void performRoutineTasks() {

        log.info("Starting routine tasks");
        try {

            LinkedList docs = getPackagedData();

            if (docs.isEmpty())
                throw new NullPointerException();

            String query = "insert into Server_postletters (target,title,body,attachment) values ";

            ListIterator iterator = docs.listIterator();

            while (iterator.hasNext()) {

                /*query += "('" + docs[i].target_email + "','" +
                        DEFAULT_SUBJECT + "','" + DEFAULT_BODY + "','" +
                        docs[i].user_document + "')" + (i == docs.length-1 ? ";" : ",");*/
                query += "(?,?,?,?),";
                iterator.next();
            }

            query = query.substring(0,query.length()-1) + ";";

            PreparedStatement ps = con.prepareStatement(query);

            int j = 1;
            iterator = docs.listIterator();
            while (iterator.hasNext()) {

                DocumentItem item = (DocumentItem)iterator.next();

                ps.setString(j++, item.target_email);
                ps.setString(j++, DEFAULT_SUBJECT);
                ps.setString(j++, DEFAULT_BODY);
                ps.setString(j++, item.user_document);
            }

            int rs = ps.executeUpdate();

            if (users != null)
                updateFeedTime(users);

            if (rs > 0) {
                log.info("Routine tasks completed");
            } else {
                log.warning("Something went wrong while inserting");
            }

        } catch (NullPointerException e) {
            log.info("There are no documents for update");
            return;
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
    }

    /**
     * Returns transformed String which consists result of XSLT transformation
     *
     * @return  {@code String} representing transformed document
     *          (which may be {@code null}).
     */
    public String performXSLT(String source, String stylesheet) {

        try {
            StringReader reader = new StringReader(source);
            StringWriter writer = new StringWriter();
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(
                    new javax.xml.transform.stream.StreamSource(stylesheet));

            transformer.transform(
                    new javax.xml.transform.stream.StreamSource(reader),
                    new javax.xml.transform.stream.StreamResult(writer));

            String s = writer.toString();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}



