package sender.src;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: romanfilippov
 * Date: 23.03.13
 * Time: 21:00
 */
public class NovaSender {

    private final String hostName = "";
    private final String className = "com.mysql.jdbc.Driver";
    private final String dbName = "";
    private final String userName = "";
    private final String userPassword = "";
    private static Logger log = Logger.getLogger(NovaSender.class.getName());

    // E-mail sender configuration

    private Properties props = null;
    private Session session = null;
    private final String host = "email-smtp.us-east-1.amazonaws.com";
    private final String from = "novajoy.org@gmail.com";
    private final String pass = "";
    private final String smtpPort = "587";

    private static final String SMTP_USERNAME = "";
    private static final String SMTP_PASSWORD = "";

    InternalMessage[] collection = null;

    Connection con = null;

    private static String DEFAULT_SUBJECT = "your rss feed from novaJoy";

    public NovaSender() {

        try {

            log.info("Establishing a connection...");
            String url = "jdbc:mysql://" + hostName + "/" + dbName;
            Class.forName(className);
            con = DriverManager.getConnection(url, userName, userPassword);
            log.info("Connection established");

            log.info("Setting mail properties");

            props = System.getProperties();
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", from);
            props.put("mail.smtp.password", pass);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", "true");
            props.put("mail.transport.protocol", "smtp");

            session = Session.getDefaultInstance(props, null);

            log.info("Init completed.");

        } catch (ClassNotFoundException e) {
            log.warning("Class not found" + e.getMessage());
        } catch (SQLException ex) {
            log.warning(ex.getMessage());
        }
    }

    public void performSend(Message message) throws MessagingException {

        Transport transport = session.getTransport("smtp");
        transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
        log.info("Message sent");
    }

    InternalMessage[] getMessages() throws SQLException {

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("Select target,title,body,attachment from Server_postletters");

        int rowcount = 0;
        if (rs.last()) {
            rowcount = rs.getRow();
            rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
        } else {
            return null;
        }

        InternalMessage[] messages = new InternalMessage[rowcount];

        int i = 0;
        while (rs.next()) {
            messages[i] = new InternalMessage(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4));
            i++;
        }

        return messages;
    }

    public Message prepareMessage(InternalMessage msg) throws MessagingException {

        return formMessage(msg.title, msg.body, msg.attachment, msg.target.split(","));
    }

    public void performRoutineTasks() {

        log.info("Starting routines");

        try {

            // get messages from database queue
            collection = getMessages();

            for (int i = 0; i < collection.length; i++) {

                Message message =  prepareMessage(collection[i]);
                performSend(message);
            }

        } catch (SQLException e) {
            log.warning(e.getMessage());
        } catch (MessagingException e) {
            log.warning(e.getMessage());
        }

        log.info("Routines finished");
    }

    /**
     * Returns mail message with attachment
     *
     * @return  {@code Message} instance
     *
     */
    public Message formMessage(String subject, String body, String content, String[] to) throws MessagingException {

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
        message.setSubject(subject);

        Multipart multipart = new MimeMultipart();

        // message body
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(body);

        multipart.addBodyPart(bodyPart);

        if (content != null) {
            // message attach
            MimeBodyPart attachmentPart = new MimeBodyPart();

            try {
                DataSource ds = new ByteArrayDataSource(content.getBytes("UTF-8"), "application/octet-stream");
                attachmentPart = new MimeBodyPart();
                attachmentPart.setDataHandler(new DataHandler(ds));
            } catch (Exception e) {
                log.warning(e.getMessage());
            }

            attachmentPart.setFileName("feed.html");
            multipart.addBodyPart(attachmentPart);
        }

        // Put parts in message
        message.setContent(multipart);

        return message;
    }
}
