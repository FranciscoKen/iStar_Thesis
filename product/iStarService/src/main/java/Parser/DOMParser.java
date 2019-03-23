package Parser;

import ClassGenerator.ClassGenerator;
import Model.ActorLinkType;
import Model.IStarModel;
import Model.IntentionalElementType;
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
//    public static void main(String[] args){
//
//    }


    public DOMParser() {
    }

    public IStarModel extract(){
        IStarModel model = new IStarModel();

        File fxmlFile = new File("src/main/java/validator/code-flattening.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            //generate document builder from builder factory
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();

            //get document from file
            Document doc = documentBuilder.parse(fxmlFile);

            //Initialize istar model object
//            IStarModel model = new IStarModel();

            model.setDocument(doc);

            //get Root element
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            model.setVersion(root.getAttribute("version"));
            System.out.println("Root Element: "+root.getNodeName());
            System.out.println("Model version: "+model.getVersion());


            //extract actors
            NodeList actorList = doc.getElementsByTagName("actor");
            System.out.println("---------------------------------------------");
            for(int i = 0;i<actorList.getLength();i++){
                Node currentActor = actorList.item(i);
                Element actorElement = (Element) currentActor;

                String currentActorID = actorElement.getAttribute("id");

                model.assignActor(actorElement.getAttribute("id"),actorElement.getAttribute("type"),actorElement.getAttribute("name"));

                //extract actor link
                NodeList actorLinkList = actorElement.getElementsByTagName("actorLink");
                if(actorLinkList.getLength()>0){
                    //actor link exist
                    Node currentActorLink = actorLinkList.item(0);
                    Element actorLinkElement = (Element) currentActorLink;
                    model.assignActorLink(actorLinkElement.getAttribute("type").equals("participates-in")? ActorLinkType.PARTICIPATESIN:ActorLinkType.ISA,currentActorID,actorLinkElement.getAttribute("aref"));
                }

                //extract intentional elements
                NodeList intentionalElementList = actorElement.getElementsByTagName("ielement");
                if(intentionalElementList.getLength() > 0){
                    for(int j = 0;j<intentionalElementList.getLength();j++){
                        Element ieElement = (Element) intentionalElementList.item(j);
                        IntentionalElementType currentType;
                        if(ieElement.getAttribute("type").equals("goal")){
                            currentType = IntentionalElementType.GOAL;
                        } else if(ieElement.getAttribute("type").equals("quality")){
                            currentType = IntentionalElementType.QUALITY;
                        } else if(ieElement.getAttribute("type").equals("resource")){
                            currentType = IntentionalElementType.RESOURCE;
                        } else {
                            currentType = IntentionalElementType.TASK;
                        }
                        model.assignIntentionalElement(ieElement.getAttribute("id"),currentType,ieElement.getAttribute("name"),ieElement.getAttribute("state"),currentActorID);
                    }

                }
            }
            model.printActors();
//            System.out.println("=============================================================");
            model.printActorLinks();
//            model.printAllIntentionalElements();

            ClassGenerator cg = new ClassGenerator("test-1");
            cg.generateClassDiagram(model);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return model;
    }

}
