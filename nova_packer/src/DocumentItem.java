/**
 * Created with IntelliJ IDEA.
 * User: romanfilippov
 * Date: 24.03.13
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */
public class DocumentItem {

    public int user_id;
    public String user_document;

    public DocumentItem(int _uid, String _document) {
        user_id = _uid;
        user_document = _document;
    }
}
