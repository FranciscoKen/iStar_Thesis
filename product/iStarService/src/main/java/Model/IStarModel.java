package Model;

import org.springframework.lang.Nullable;
import org.w3c.dom.Document;

import java.util.ArrayList;

public class IStarModel {
    //Attributes
    private Document document;
    private String version;
    private ArrayList<Actor> actors;
    private ArrayList<ActorLink> actorLinks;
    private ArrayList<IntentionalElement> iElements;

    //Constructor
    public IStarModel(){
        actors = new ArrayList<Actor>();
        actorLinks = new ArrayList<ActorLink>();
        iElements = new ArrayList<IntentionalElement>();
    }

    public void assignActor(String id, String type, String name){
        Actor ac = new Actor(id,type);
        ac.setName(name);
        actors.add(ac);
    }

    public void assignActorLink(ActorLinkType type,String from, String to){
        ActorLink acl = new ActorLink(type,from,to);
        actorLinks.add(acl);
    }

    public void assignIntentionalElement(String id, IntentionalElementType type, String name, String state,String actorID){
        IntentionalElement ie = new IntentionalElement(id,type,actorID);
        ie.setName(name);
        ie.setState(state);
        iElements.add(ie);
    }

    public boolean isHasIElement(Actor a){
        boolean hasIElement = false;
        for(IntentionalElement ie : iElements){
            if(ie.getActorID().equals(a.getId())){
                hasIElement = true;
            }
        }
        return hasIElement;
    }

    // Getter & Setter
    public String getVersion() {
        return version;
    }

    public Document getDocument() {
        return document;
    }

    public int getNumberofActors(){
        return this.actors.size();
    }
    public int getNumberofActorLinks(){
        return this.actorLinks.size();
    }
    public int getNumberofIntentionalElements(Actor a){
        int temp_count = 0;
        for(IntentionalElement ie : iElements){
            if(a.getId().equals(ie.getActorID())){
                temp_count++;
            }
        }
        return temp_count;
    }

    public ArrayList<Actor> getActors() {
        return actors;
    }

    public ArrayList<ActorLink> getActorLinks() {
        return actorLinks;
    }

    public ArrayList<IntentionalElement> getiElements() {
        return iElements;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    //Printers
    public void printActors(){
        System.out.println("There are "+getNumberofActors()+" actors\n");
        for(Actor a : actors){
            System.out.print("Actor "+a.getId());
            if(!a.getName().equals(null)){
                System.out.print(" name "+a.getName());
            }
            System.out.print(" type "+a.getType()+"\n");
            printIntentionalElements(a);
            System.out.println("====================================================");
        }
    }

    public void printActorLinks(){
        System.out.println("There are "+getNumberofActorLinks()+" actor links\n");
        for(ActorLink al : actorLinks){
            System.out.print("actorLink");
            System.out.print(" type "+al.getType());
            System.out.print(" from "+al.getFrom());
            System.out.print(" to "+al.getTo()+"\n");
        }
    }

    public void printIntentionalElements(Actor actor){
        System.out.println("There are "+getNumberofIntentionalElements(actor)+" intentional elements\n");
        for(IntentionalElement ie: iElements){
            if(ie.getActorID().equals(actor.getId())){
                System.out.print("Intentional Elements "+ ie.getId() + " name "+ie.getName());
                System.out.print(" type "+ie.getType());
                System.out.println(" state "+ie.getState());
            }
        }
    }

    public void printAllIntentionalElements(){
        System.out.println("These are the existing intentional elements : ");
        for(IntentionalElement ie : iElements){
            System.out.println("Intentional Element with id "+ie.getId() + " from actor "+ie.getActorID()+" name "+ie.getName()+" type "+ie.getType()+" state "+ie.getState());
        }
    }

}
