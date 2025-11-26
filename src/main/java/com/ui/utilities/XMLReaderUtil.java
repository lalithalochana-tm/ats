package com.ui.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLReaderUtil {

    public static List<String> readXMLAttributeValue(String elementName) {
        String sFile = "./cognitestSuite.xml";
    	List<String> listsOfGroups = new ArrayList<String>();
        List<String> fileNames= readSuitefileAttributeValue(sFile, "suite-file");
        for (String fileName : fileNames) {
        	listsOfGroups.add(readIncludeAttributeValue(fileName, elementName));
		  }
		return listsOfGroups;
        
    }
    
    public static String readIncludeAttributeValue(String fileName,String elementName) {
    	String attributeValue = null;
    	try {
            DocumentBuilderFactory newInstance = DocumentBuilderFactory.newInstance();
            newInstance.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder newDocumentBuilder = newInstance.newDocumentBuilder();
            Document doc = newDocumentBuilder.parse(new File(fileName));
            doc.getDocumentElement().normalize();
            System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
            NodeList elementsByTagName = doc.getElementsByTagName(elementName);
            Node node = elementsByTagName.item(0);
            Element element = (Element) node;
            attributeValue = element.getAttribute("name");
            return attributeValue;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return attributeValue;
    }
    
    public static List<String> readSuitefileAttributeValue(String sFile,String elementName) {
    	List<String> listsOfElements = new ArrayList<String>();
    	try {
            DocumentBuilderFactory newInstance = DocumentBuilderFactory.newInstance();
            newInstance.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder newDocumentBuilder = newInstance.newDocumentBuilder();
            Document doc = newDocumentBuilder.parse(new File(sFile));
            doc.getDocumentElement().normalize();
            NodeList elementsByTagName = doc.getElementsByTagName("suite-file");
			 int length = elementsByTagName.getLength();
			 for(int i=0;i<length;i++) {
				 Node node = elementsByTagName.item(i);
				 System.out.println(node.getNodeName());
				 Element element = (Element) node;
				 System.out.println(element.getAttribute("path"));
				 listsOfElements.add(element.getAttribute("path"));
			 }
            return listsOfElements;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return listsOfElements;
    }

}
