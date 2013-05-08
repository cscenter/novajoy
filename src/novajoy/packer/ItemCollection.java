package novajoy.packer;

import java.util.ArrayList;

/**
 * User: romanfilippov
 * Date: 08.05.13
 * Time: 13:10
 */
public class ItemCollection {

    private ArrayList<RssItem> items;
    public String subject;
    public String format;

    public ItemCollection(String _subject, String _format) {

        items = new ArrayList<RssItem>();
        subject = _subject;
        format = _format;
    }


    public void insertItem(RssItem item) {

        items.add(item);
    }

    public int size() {
        return items.size();
    }

    public RssItem get(int index) {

        return items.get(index);
    }
}
