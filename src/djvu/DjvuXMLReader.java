/*
 * Read text content from djvu xml file
 */
package djvu;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author iychoi
 */
public class DjvuXMLReader {
    private File file;
    
    private SAXParserFactory saxFactory;
    private SAXParser saxParser;
    private DjvuSaxHandler handler;
    
    public DjvuXMLReader(File xmlFile) {
        if(xmlFile == null || !xmlFile.exists() || !xmlFile.isFile())
            throw new IllegalArgumentException("Cannot find xml file");
        
        this.file = xmlFile;
        this.saxFactory = SAXParserFactory.newInstance();
        
        this.saxFactory.setValidating(false);
    }
    
    public List<DjvuPage> parse(DjvuConfiguration conf) throws IOException {
        try {
            this.saxParser = this.saxFactory.newSAXParser();
            this.handler = new DjvuSaxHandler(conf);
            
            this.saxParser.parse(this.file, this.handler);
            
            return this.handler.getPages();
        } catch (ParserConfigurationException | SAXException ex) {
            throw new IOException(ex.getMessage());
        }
    }
}
