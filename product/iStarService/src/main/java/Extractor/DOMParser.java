package Extractor;

import Model.*;
import Exception.*;
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

    public DOMParser() {
    }

    public IStarModel extract(String path) throws IStarException{
        IStarModel model = new IStarModel();

        File fxmlFile = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            //generate document builder from builder factory
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();

            //get document from file
            Document doc = documentBuilder.parse(fxmlFile);

            model.setDocument(doc);

            //get Root element
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            model.setVersion(root.getAttribute("version"));

            //extract actors
            NodeList actorList = doc.getElementsByTagName("actor");
            for(int i = 0;i<actorList.getLength();i++){
                Node currentActor = actorList.item(i);
                Element actorElement = (Element) currentActor;

                String currentActorID = actorElement.getAttribute("id");
                ActorType type =  ActorType.ACTOR;
                switch (actorElement.getAttribute("type")){
                    case "actor":
                        type = ActorType.ACTOR;
                    break;
                    case "role":
                        type = ActorType.ROLE;
                    break;
                    case "agent":
                        type = ActorType.AGENT;
                    break;
                }

                model.assignActor(actorElement.getAttribute("id"),type,actorElement.getAttribute("name"));

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
                        String currentIElementID = ieElement.getAttribute("id");
                        if(ieElement.getAttribute("type").equals("goal")){
                            currentType = IntentionalElementType.GOAL;
                        } else if(ieElement.getAttribute("type").equals("quality")){
                            currentType = IntentionalElementType.QUALITY;
                        } else if(ieElement.getAttribute("type").equals("resource")){
                            currentType = IntentionalElementType.RESOURCE;
                        } else {
                            currentType = IntentionalElementType.TASK;
                        }
                        //extract intentional element link
                        NodeList intentionalElementLinkList = ieElement.getElementsByTagName("ielementLink");
                        if(intentionalElementLinkList.getLength() >0){
                            for(int itr = 0;itr < intentionalElementLinkList.getLength();itr++){
                                Element ieLinkElement = (Element) intentionalElementLinkList.item(itr);
                                IntentionalElementLinkType linkType = null;

                                if(ieLinkElement.getAttribute("type").equals("refinement")){
                                    if(ieLinkElement.getAttribute("value").equals("and")){
                                        linkType = IntentionalElementLinkType.REFINEMENT_AND;
                                    } else if(ieLinkElement.getAttribute("value").equals("or")){
                                        linkType = IntentionalElementLinkType.REFINEMENT_OR;
                                    } else {
                                        throw new IStarException(ExceptionMessages.heading+ExceptionMessages.refinementValueException);
                                    }
                                } else if(ieLinkElement.getAttribute("type").equals("qualification")){
                                    linkType = IntentionalElementLinkType.QUALIFICATION;
                                } else if(ieLinkElement.getAttribute("type").equals("neededBy")){
                                    linkType = IntentionalElementLinkType.NEEDEDBY;
                                } else if(ieLinkElement.getAttribute("type").equals("contribution")){
                                    if(ieLinkElement.getAttribute("value").equals("make")){
                                        linkType = IntentionalElementLinkType.CONTRIBUTION_MAKE;
                                    } else if(ieLinkElement.getAttribute("value").equals("help")){
                                        linkType = IntentionalElementLinkType.CONTRIBUTION_HELP;
                                    } else if(ieLinkElement.getAttribute("value").equals("hurt")){
                                        linkType = IntentionalElementLinkType.CONTRIBUTION_HURT;
                                    } else if(ieLinkElement.getAttribute("value").equals("break")){
                                        linkType = IntentionalElementLinkType.CONTRIBUTION_BREAK;
                                    } else {
                                        throw new IStarException(ExceptionMessages.heading+ExceptionMessages.contributionValueException);
                                    }
                                }
                                model.assignIntentionalElementLink(currentIElementID,ieLinkElement.getAttribute("iref"),currentActorID,linkType);

                            }
                        }
                        model.assignIntentionalElement(currentIElementID,currentType,ieElement.getAttribute("name"),ieElement.getAttribute("state"),currentActorID);
                    }

                }
            }

            //extract dependency
            NodeList dependencyList = doc.getElementsByTagName("dependum");
            for(int k = 0;k<dependencyList.getLength();k++){
                Element currentDependency = (Element) dependencyList.item(k);
                String dependumID = currentDependency.getAttribute("id");
                String dependumName = currentDependency.getAttribute("name");
                String dependumState = currentDependency.getAttribute("state");
                IntentionalElementType type;
                if(currentDependency.getAttribute("type").equals("goal")){
                    type = IntentionalElementType.GOAL;
                } else if(currentDependency.getAttribute("type").equals("quality")){
                    type = IntentionalElementType.QUALITY;
                } else if(currentDependency.getAttribute("type").equals("resource")){
                    type = IntentionalElementType.RESOURCE;
                } else {
                    type = IntentionalElementType.TASK;
                }

                NodeList dependerList = currentDependency.getElementsByTagName("depender");
                Element elementDepender = (Element) dependerList.item(0);

                String depender = elementDepender.getAttribute("aref");
                String dependerElement = elementDepender.getAttribute("iref");

                NodeList dependeeList = currentDependency.getElementsByTagName("dependee");
                Element elementDependee = (Element) dependeeList.item(0);

                String dependee = elementDependee.getAttribute("aref");
                String dependeeElement = elementDependee.getAttribute("iref");

                model.assignDependency(dependumID,type,dependumName,dependumState,depender,dependee,dependerElement,dependeeElement);
            }

        } catch (IStarException iex){
            System.out.println(iex.getMessage());
            throw new IStarException(iex.getMessage(),iex);
        } catch(ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return model;
    }

}
