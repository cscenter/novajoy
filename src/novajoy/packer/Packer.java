/**
 * Created with IntelliJ IDEA.
 * User: romanfilippov
 * Date: 09.03.13
 * Time: 23:02
 */
package novajoy.packer;
import java.io.*;
import java.lang.String;
import java.net.URL;
import java.sql.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.logging.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import novajoy.util.config.IniWorker;
import novajoy.util.logger.Loggers;


class Packer{

    private String hostName = "";
    private final String className = "com.mysql.jdbc.Driver";
    private final String encProperties = "?useUnicode=true&characterEncoding=utf-8";
    private String dbName = "";
    private String userName = "";
    private String userPassword = "";

    private final String configPath = "/Users/romanfilippov/Dropbox/mydocs/Development/java/novaJoy/novajoy/config/config.ini";
    private static Logger log =  new Loggers().getPackerLogger();


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

    ArrayList getDataForUserId(int uid) throws SQLException {

        Statement st = con.createStatement();
        //System.out.println("select * from Server_rssitem where rssfeed_id in (select rssfeed_id from Server_rssfeed_collection where collection_id in (select id from Server_collection where user_id = " + uid +"));");
        //ResultSet rs = st.executeQuery("select * from Server_rssitem where rssfeed_id in (select rssfeed_id from Server_rssfeed_collection where collection_id in (select id from Server_collection where user_id = " + uid +"));");
        ResultSet rs = st.executeQuery("SELECT IT.rssfeed_id , IT.title, IT.description, IT.link, IT.author, IT.pubDate, COL.id  \n" +
                "FROM Server_rssfeed RS \n" +
                "JOIN Server_rssfeed_collection CONN \n" +
                "ON RS.id=CONN.rssfeed_id \n" +
                "JOIN Server_collection COL \n" +
                "ON COL.id=CONN.collection_id \n" +
                "JOIN Server_rssitem IT \n" +
                "ON IT.rssfeed_id=RS.id\n" +
                "WHERE COL.user_id = " + uid + " ORDER BY COL.id, IT.pubDate;");

        int rowcount = 0;
        if (rs.last()) {
            rowcount = rs.getRow();
            rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
        } else {
            return null;
        }

        //RssItem[] items = new RssItem[rowcount];
        ArrayList items = new ArrayList();
        int last_group_id = 0;
        int i = 0;

        if (rs.next()) {
            last_group_id = rs.getInt(7);

            items.add(i, new ArrayList<RssItem>());
            ((ArrayList)items.get(i)).add(new RssItem(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getDate(6)));
        }

        while (rs.next()) {

            if (rs.getInt(7) == last_group_id) {

                ArrayList<RssItem> rss = (ArrayList)items.get(i);
                rss.add(new RssItem(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getDate(6)));
            } else {

                last_group_id = rs.getInt(7);
                i++;
                items.add(i, new ArrayList<RssItem>());
                ((ArrayList)items.get(i)).add(new RssItem(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getDate(6)));
            }
        }

        rs.close();
        st.close();

        return items;
    }


    DocumentItem formDocument (String target, ArrayList userFeeds) {

        StringBuilder builder = new StringBuilder("<html><head><title>Your RSS feed from novaJoy</title>" +
                "<style type =\"text/css\">\n" +
                "   body{\n" +
                "        font-family:\"Times New Roman\", Times, serif\n" +
                "   }\n" +
                "</style></head><body>");

        for (int i = 0; i < userFeeds.size(); i++) {
            builder.append(((RssItem)userFeeds.get(i)).toHtml().replaceAll("</hr>","<hr/>"));
        }

        builder.append("</body></html>");

        return new DocumentItem(target, builder.toString());
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

            ArrayList userFeed = getDataForUserId(userIds[j].user_id);

            if (userFeed == null) {
                j++;
                continue;
            }

            for (Object elem : userFeed) {

                usersDocuments.add(formDocument(userIds[j].user_email, (ArrayList)elem));
            }

            j++;
        }

        return usersDocuments;
    }

    public String saveAttachmentToPath(String attachment, String path) throws FileNotFoundException, IOException {

        File file = new File(path);
        file.mkdirs();

        int i=0;

        while (true) {

            if (!(new File(path + "/feed" + i + ".html")).exists())
                break;

            i++;
        }

        FileOutputStream os = new FileOutputStream(file.toString() + "/feed" + i + ".html");

        try {

            os.write(attachment.getBytes("UTF-8"));

        } catch (Exception e) {
            log.warning(e.getMessage());
        } finally {
            os.close();
            return "/feed" + i + ".html";
        }
    }

    public String prepareAttachmentAndSave(String email, String attachment) throws FileNotFoundException, IOException {

        String domain = email.substring(email.indexOf("@")+1);
        String name = email.substring(0, email.indexOf("@"));
        System.out.println(domain + "|" + name);
        String path = "mail_storage/" + domain + "/" + name;

        return path + saveAttachmentToPath(attachment, path);
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

                String filePath = prepareAttachmentAndSave(item.target_email, item.user_document);

                ps.setString(j++, filePath);
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
        } catch (Exception e) {
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



