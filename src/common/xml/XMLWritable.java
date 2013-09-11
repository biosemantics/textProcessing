/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author iychoi
 */
public abstract class XMLWritable {
    public abstract void toXML(Document doc, Element parent);
}
