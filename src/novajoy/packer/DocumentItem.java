/**
 * Created with IntelliJ IDEA.
 * User: romanfilippov
 * Date: 24.03.13
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */
package novajoy.packer;
public class DocumentItem {

    public String target_email;
    public String user_document;
    public String subject;
    public String format;

    public DocumentItem(String _target_email, String _document, String _subject, String _format) {
        target_email = _target_email;
        user_document = _document;
        subject = _subject;
        format = _format;
    }
}
