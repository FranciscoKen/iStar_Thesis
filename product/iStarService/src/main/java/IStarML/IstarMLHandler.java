package IStarML;

import IStarML.ccistarml.ccfileError;
import IStarML.ccistarml.ccistarmlFile;
import jdk.nashorn.internal.runtime.regexp.joni.constants.NodeType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class IstarMLHandler {
    private String model;
    private ccistarmlFile file;

    public IstarMLHandler(String model){
        this.model = model;
        this.file = new ccistarmlFile(model);
    }

    public void validate() throws Exception{
        boolean xmlTruth = file.xmlParser();
        if(!xmlTruth){
            throw new Exception(gatherErrors());
        }

        boolean modelTruth = file.istarmlParser();
        if(!modelTruth){
            throw new Exception(gatherErrors());
        }
    }

    public Document translateIStarML(String string_model) throws Exception{
        HashMap<String,String> ielements = new HashMap<>();
        Document doc;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(string_model));

            doc = db.parse(is);
            //TODO update intentional element link

            Node root = doc.getElementsByTagName("istarml").item(0);

            doc.renameNode(root,null,"istarml2");

            Element diagram = (Element) doc.getElementsByTagName("diagram").item(0);

            NodeList actorNodes = doc.getElementsByTagName("actor");
            Element currentActor = null;
            for(int i = 0 ; i<actorNodes.getLength();i++){

                currentActor = (Element) actorNodes.item(i);
                if(currentActor.getAttribute("type").equals("position") || currentActor.getAttribute("type").equals("")){
                    currentActor.setAttribute("type","actor");
                }

                Node currentBoundaryNode = currentActor.getElementsByTagName("boundary").item(0);
                Element currentBoundary = (Element) currentBoundaryNode;
                NodeList ielementNodes = currentBoundary.getElementsByTagName("ielement");
                for(int l = 0;l<ielementNodes.getLength();l++){
                    Element iel = (Element) ielementNodes.item(l);
                    ielements.put(iel.getAttribute("id"),iel.getAttribute("type"));
                }
                for(int k = 0; k<ielementNodes.getLength();k++){
                    parseIElementSubTree(ielementNodes.item(k),ielements);
                }

                NodeList actorLinkNodes = currentActor.getElementsByTagName("actorLink");
                for(int j = 0 ; j<actorLinkNodes.getLength();j++){
                    Element currentActorLink = (Element) actorLinkNodes.item(j);
                    if(currentActorLink.getAttribute("type").equals("is_part_of") ||
                            currentActorLink.getAttribute("type").equals("plays") ||
                            currentActorLink.getAttribute("type").equals("covers") ||
                            currentActorLink.getAttribute("type").equals("occupies")){
                        currentActorLink.setAttribute("type","participates-in");
                    } else if(currentActorLink.getAttribute("type").equals("instance_of")){
                        currentActor.removeChild(currentActorLink);
                    }
                }

            }


            NodeList ielementDependencies = diagram.getChildNodes();
            for(int a = 0;a<ielementDependencies.getLength();a++){
                if(ielementDependencies.item(a).getNodeName().equals("ielement")){
                    doc.renameNode(ielementDependencies.item(a),null,"dependum");
                    Element currentDependum = (Element) ielementDependencies.item(a);
                    if(currentDependum.getAttribute("type").equals("softgoal")){
                        currentDependum.setAttribute("type","quality");
                    }
                }
            }


            removeRecursiveNode(doc,"graphic");
            doc.normalizeDocument();

        } catch (ParserConfigurationException | SAXException | IOException e ) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return doc;
    }

    private String gatherErrors(){
        String errors="";
        LinkedList errs = file.errorList();
        Iterator it = errs.iterator();
        ccfileError thisErr;
        while(it.hasNext()){
            thisErr = (ccfileError) it.next();
            errors += "Error : "+thisErr.error + ":" + thisErr.textLine+"(Line "+thisErr.line+")"+ "\n";
        }

        return errors;
    }

    private void parseIElementSubTree(Node ielementNode,HashMap<String,String> ielements){
//        if(!ielementNode.hasChildNodes()){
//            //TODO process ielement
//            Element currentiElement = (Element) ielementNode;
//            if(currentiElement.getAttribute("type").equals("softgoal")){
//                currentiElement.setAttribute("type","quality");
//            }
//        } else {
//            validateIElementTree(ielementNode,ielements);
//        }
        validateIElementTree(ielementNode,ielements);
    }

    private void validateIElementTree(Node node,HashMap<String,String> ielements){
        Element currentiElement = (Element) node;
        if(currentiElement.getAttribute("type").equals("softgoal")){
            currentiElement.setAttribute("type","quality");
        }
        NodeList ielementLinkNodes = currentiElement.getElementsByTagName("ielementLink");
        for(int l = 0 ; l<ielementLinkNodes.getLength();l++){
            Element currentiElementLink = (Element) ielementLinkNodes.item(l);
            //TODO ini masih asumsi pertama, yang direference
            if(currentiElementLink.getAttribute("type").equals("means-end")){
                //TODO means end
                currentiElementLink.setAttribute("type","refinement");
                currentiElementLink.setAttribute("value","or");
            } else if(currentiElementLink.getAttribute("type").equals("decomposition")){
                //TODO decomposition
                if(ielements.get(currentiElementLink.getAttribute("iref")).equals("goal") ||
                        ielements.get(currentiElementLink.getAttribute("iref")).equals("task")){
                    currentiElementLink.setAttribute("type","refinement");
                    currentiElementLink.setAttribute("value","or");
                } else if(ielements.get(currentiElementLink.getAttribute("iref")).equals("resource")){
                    currentiElementLink.setAttribute("type","neededBy");
                } else if(ielements.get(currentiElementLink.getAttribute("iref")).equals("softgoal")){
                    currentiElementLink.setAttribute("type","qualification");
                }
            } else if(currentiElementLink.getAttribute("type").equals("contribution")){
                //TODO Contribution some+ some-
                if(currentiElementLink.getAttribute("value").equals("some+")){
                    currentiElementLink.setAttribute("value","help");
                } else if(currentiElementLink.getAttribute("value").equals("some-")){
                    currentiElementLink.setAttribute("value","hurt");
                } else if(currentiElementLink.getAttribute("value").equals("help") ||
                        currentiElementLink.getAttribute("value").equals("hurt") ||
                        currentiElementLink.getAttribute("value").equals("make") ||
                        currentiElementLink.getAttribute("value").equals("break")){

                } else {
                    currentiElement.removeChild(ielementLinkNodes.item(l));
                }
            }

//            if(currentiElementLink.hasChildNodes()){
//                NodeList childIElement = currentiElementLink.getChildNodes();
//                for(int i = 0;i<childIElement.getLength();i++){
//                    parseIElementSubTree(childIElement.item(i));
//                }
//            }
        }
    }

    private void moveToIElementRoot(Node currentNode, Node rootElement){

    }

    private void removeRecursiveNode(Node node, String name){
        NodeList nodeList = node.getChildNodes();
        if(nodeList.getLength()>0){
            for(int i =0;i<nodeList.getLength();i++){
                if(nodeList.item(i).getNodeName().equals(name)){
                    nodeList.item(i).getParentNode().removeChild(nodeList.item(i));
                } else {
                    removeRecursiveNode(nodeList.item(i),name);
                }
            }
        }
    }

}
