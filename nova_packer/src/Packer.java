/**
 * Created with IntelliJ IDEA.
 * User: romanfilippov
 * Date: 09.03.13
 * Time: 23:02
 */
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import java.util.logging.Logger;


class Packer{

    private final String hostName = "127.0.0.1";
    private final String className = "com.mysql.jdbc.Driver";
    private final String dbName = "novajoy";
    private final String userName = "root";
    private final String userPassword = "148976";
    private static Logger log = Logger.getLogger(Packer.class.getName());

    // E-mail sender configuration

    private final String host = "smtp.gmail.com";
    private final String from = "romanspsu";
    private final String pass = "148976Roman";
    private final String smtpPort = "587";

    Connection con = null;

    public Packer() {

        try {

            log.info("Establishing a connection...");
            String url = "jdbc:mysql://" + hostName + "/" + dbName;
            Class.forName(className);
            con = DriverManager.getConnection(url, userName, userPassword);
            log.info("Connection established");

        } catch (ClassNotFoundException e) {
            log.warning("Class not found" + e.getMessage());
        } catch (SQLException ex) {
            log.warning(ex.getMessage());
        }
    }

    /**
     * create mail message with attachment and sent
     *
     */
    public void pack() {

        try{

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select content from Server_rssfeed where `url`='http://www.test.ru/'");
            rs.next();

            //configure email settings
            Properties props = System.getProperties();
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", from);
            props.put("mail.smtp.password", pass);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", "true");


            //recipients
            String[] to = {"filiroman.tsu@gmail.com"};//, "01kz01@gmail.com", "avsmal@gmail.com", "cska63@rambler.ru"};

            Session session = Session.getDefaultInstance(props, null);

            Message message = formMessage(rs.getString(1), session, to);

            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            log.info("Message sent");

        }
        catch(Exception e){
            log.warning(e.getMessage());
        }
    }

    /**
     * Returns mail message with attachment
     *
     * @return  {@code Message} instance
     *
     */
    public Message formMessage(String content, Session session, String[] to) throws AddressException,MessagingException {

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));

        InternetAddress[] toAddress = new InternetAddress[to.length];

        // To get the array of addresses
        for( int i=0; i < to.length; i++ ) {
            toAddress[i] = new InternetAddress(to[i]);
        }

        //add recipients
        for( int i=0; i < toAddress.length; i++) {
            message.addRecipient(Message.RecipientType.TO, toAddress[i]);
        }
        message.setSubject("your rss feed from novaJoy");

        Multipart multipart = new MimeMultipart();
        MimeBodyPart attachmentPart = new MimeBodyPart();

        try {
            DataSource ds = new ByteArrayDataSource(performXSLT(content, "rss_stylesheet.xsl").getBytes("UTF-8"), "application/octet-stream");
            attachmentPart = new MimeBodyPart();
            attachmentPart.setDataHandler(new DataHandler(ds));
        } catch (Exception e) {
            log.warning(e.getMessage());
        }

        attachmentPart.setFileName("feed.html");
        multipart.addBodyPart(attachmentPart);

        // Put parts in message
        message.setContent(multipart);

        return message;
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

            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

