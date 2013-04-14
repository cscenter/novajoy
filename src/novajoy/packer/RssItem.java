package novajoy.packer;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: romanfilippov
 * Date: 24.03.13
 * Time: 1:08
 * To change this template use File | Settings | File Templates.
 */
public class RssItem {

    public int feed_id;
    public String title;
    public String description;
    public String link;
    public String author;
    public Date pubDate;

    public RssItem (int _feed_id, String _title, String _description, String _link, String _author, Date _pubDate) {

        feed_id = _feed_id;
        title = _title;
        description = _description;
        link = _link;
        author = _author;
        pubDate = _pubDate;
    }

    public String toHtml() {

        String result = new String();
        result += "<h1>" + title + (author != "" ? " (by " + author + ")" : "") + "</h1>";
        result += "<p>" + description + "</p>";
        result += "<p>" + link + "</p>";
        result += "<p>" + pubDate.toString() + "</p></hr>";
        return result;

    }

}
