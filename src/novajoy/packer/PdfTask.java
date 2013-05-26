package novajoy.packer;

import com.lowagie.text.pdf.BaseFont;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * User: romanfilippov
 * Date: 26.05.13
 * Time: 14:08
 */
public class PdfTask extends Thread {

    private String document;
    private String path;
    private static Logger logger = Logger.getLogger(PdfTask.class.getName());

    public PdfTask(String _document, String _path) {

        document = _document;
        path = _path;

    }

    private void createPdf (String htmlDocument, String path) {

        ITextRenderer renderer = new ITextRenderer();
        try {
            renderer.getFontResolver().addFont("fonts/PTS55F.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(htmlDocument.getBytes("UTF-8")));
            renderer.setDocument(doc, null);
            File file = new File(path);
            OutputStream os = new FileOutputStream(file);
            renderer.layout();
            renderer.createPDF(os);
            logger.info(path + " created succesfully.");
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {

        createPdf(document, path);
    }
}
