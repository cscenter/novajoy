/**
 * Created with IntelliJ IDEA.
 * User: romanfilippov
 * Date: 23.03.13
 * Time: 21:42
 * To change this template use File | Settings | File Templates.
 */
package novajoy.sender;
public class InternalMessage {

    public int id;
    public String target;
    public String title;
    public String body;
    public String attachment;

    public InternalMessage(int _id, String _target, String _title, String _body, String _attachment) {

        id = _id;
        target = _target;
        title = _title;
        body = _body;
        attachment = _attachment;
    }
}
