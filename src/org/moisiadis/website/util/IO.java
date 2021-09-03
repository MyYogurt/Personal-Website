package org.moisiadis.website.util;

import org.moisiadis.website.email.SMTPCredentials;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class for IO actions
 */
public class IO {
    /**
     * Load SES Credentials from text file
     *
     * @return SESCredentials object
     * @throws FileNotFoundException
     */
    public static SMTPCredentials loadSESCredentials(String path) throws IOException, ParserConfigurationException, SAXException {
        final String SMTPUsername = parseXML(path, "smtp-username");
        final String SMTPPassword = parseXML(path, "smtp-password");
        return new SMTPCredentials(SMTPUsername, SMTPPassword);
    }

    /**
     * Parse XML Document for value based on element ID
     * @param path Path to XML file
     * @param elementID Element ID value will be based on
     * @return Value of element. Null if element is not present.
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static String parseXML(final String path, final String elementID) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new File(path));
        document.getDocumentElement().normalize();

        NodeList list = document.getElementsByTagName(elementID);

        Node node = list.item(0);

        if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
            return node.getTextContent();
        }

        return null;
    }
}
