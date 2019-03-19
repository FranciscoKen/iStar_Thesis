package Parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class DOMParser {
    public static void main(String[] args){
        File fxmlFile = new File("src/main/java/validator/code-flattening.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(fxmlFile);
            doc.getDocumentElement().normalize();

            System.out.println("Root Element: "+doc.getDocumentElement().getNodeName());

            NodeList nodeList = doc.getElementsByTagName("diagram");

            System.out.println("----------------------------");

            for(int temp = 0;temp<nodeList.getLength();temp++){
                Node node = nodeList.item(temp);
                System.out.println("\nCurrent Element : "+node.getNodeName());
            }

//            for(int temp = 0; temp<nodeList.getLength();temp++){
//                Node node = nodeList.item(temp);
//
//                System.out.println("\nCurrent Element :" + node.getNodeName());
//
//                if(node.getNodeType() == Node.ELEMENT_NODE){
//                    Element element = (Element) node;
//
//                    System.out.println("Actor: "+element.getElementsByTagName("actor").item(0).getTextContent());
//                }
//            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
