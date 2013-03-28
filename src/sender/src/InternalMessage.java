/**
 * Created with IntelliJ IDEA.
 * User: romanfilippov
 * Date: 23.03.13
 * Time: 21:42
 * To change this template use File | Settings | File Templates.
 */
package sender.src;
public class InternalMessage {

    public String target;
    public String title;
    public String body;
    public String attachment;

    public InternalMessage(String _target, String _title, String _body, String _attachment) {

        target = _target;
        title = _title;
        body = _body;
        attachment = _attachment;
    }
}
