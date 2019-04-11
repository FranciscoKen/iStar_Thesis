package Model;

import Exception.*;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IStarModel {
    //Attributes
    private Document document;
    private String version;
    private HashMap<String,Actor> actors;
    private ArrayList<ActorLink> actorLinks;
    private HashMap<String,IntentionalElement> iElements;
    private HashMap<String,Dependency> dependencies;
    private HashMap<ReferencePair,IntentionalElementLink> iElementLinks;

    //Constructor
    public IStarModel(){
        actors = new HashMap<String,Actor>();
        actorLinks = new ArrayList<ActorLink>();
        iElements = new HashMap<String,IntentionalElement>();
        dependencies = new HashMap<String,Dependency>();
        iElementLinks = new HashMap<>();
    }

    //assignment methods
    public void assignActor(String id, ActorType type, String name) throws IStarException {
        if(actors.containsKey(id)){
            throw new IStarException(ExceptionMessages.sameIDException+"[Element "+id+"]");
        }
        Actor ac = new Actor(id,type);
        ac.setName(name.equals("")?id:name);
        actors.put(id,ac);
    }

    public void assignActorLink(ActorLinkType type,String from, String to){
        ActorLink acl = new ActorLink(type,from,to);
        actorLinks.add(acl);
    }

    public void assignIntentionalElement(String id, IntentionalElementType type, String name, String state,String actorID){
        IntentionalElement ie = new IntentionalElement(type,actorID);
        ie.setName(name.equals("")?id:name);
        ie.setState(state);
        iElements.put(id,ie);
    }

    public void assignDependency(String dependumID,IntentionalElementType type, String dependumName, String dependumState,String depender, String dependee, String dependerElement, String dependeeElement){
        Dependency d = new Dependency(type,depender,dependee);
        d.setDependeeElement(dependeeElement);
        d.setDependerElement(dependerElement);
        d.getDependum().setName(dependumName.equals("")?dependumID:dependumName);
        d.getDependum().setState(dependumState);
        dependencies.put(dependumID,d);
    }

    public void assignIntentionalElementLink(String from, String to,String actorID, IntentionalElementLinkType type){
        IntentionalElementLink iel = new IntentionalElementLink(actorID,type);
        ReferencePair pair = new ReferencePair(from,to);
        iElementLinks.put(pair,iel);
    }

    public boolean isHasIElement(String actorID){
        boolean hasIElement = false;
        for(Map.Entry<String,IntentionalElement> entry : iElements.entrySet()){
            if(entry.getValue().getActorID().equals(actorID)){
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
    public int getNumberofIntentionalElements(String actorID){
        int temp_count = 0;
        for(Map.Entry<String,IntentionalElement> entry: iElements.entrySet()){
            if(entry.getValue().getActorID().equals(actorID)){
                temp_count++;
            }
        }
        return temp_count;
    }

    public int getNumberofDependencies(){
        return this.dependencies.size();
    }

    public HashMap<String, Actor> getActors() {
        return actors;
    }

    public ArrayList<ActorLink> getActorLinks() {
        return actorLinks;
    }

    public HashMap<String, IntentionalElement> getiElements() {
        return iElements;
    }

    public HashMap<String, Dependency> getDependencies() {
        return dependencies;
    }

    public HashMap<ReferencePair, IntentionalElementLink> getiElementLinks() {
        return iElementLinks;
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
        for(Map.Entry<String,Actor> actor : actors.entrySet()){
            System.out.print("Actor "+actor.getKey());
            if(!actor.getValue().getName().equals("")){
                System.out.print(" name "+actor.getValue().getName());
            }
            System.out.print(" type "+actor.getValue().getType()+"\n");
            printIntentionalElements(actor.getKey());
            printIntentionalElementLinks(actor.getKey());
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

    public void printIntentionalElements(String actorID){
        System.out.println("There are "+getNumberofIntentionalElements(actorID)+" intentional elements\n");
        for(Map.Entry<String,IntentionalElement> entry: iElements.entrySet()){
            if(entry.getValue().getActorID().equals(actorID)){
                System.out.print("Intentional Elements "+ entry.getKey() + " name "+entry.getValue().getName());
                System.out.print(" type "+entry.getValue().getType());
                System.out.println(" state "+entry.getValue().getState());
            }
        }
    }

    public void printAllIntentionalElements(){
        System.out.println("These are the existing intentional elements : ");
        for(Map.Entry<String,IntentionalElement> entry : iElements.entrySet()){
            System.out.println("Intentional Element with id "+entry.getKey() + " from actor "+entry.getValue().getActorID()+" name "+entry.getValue().getName()+" type "+entry.getValue().getType()+" state "+entry.getValue().getState());
        }
    }

    public void printDependencies(){
        System.out.println("These are all of the dependencies : ");
        for(Map.Entry<String,Dependency> d : dependencies.entrySet()){
            System.out.println("Dependency of intentional element "+d.getValue().getDependum().getName()+" "+d.getKey()+" from actor "+d.getValue().getDependee()+"\'s "+d.getValue().getDependeeElement()+"to "+d.getValue().getDepender()+"\'s "+d.getValue().getDependerElement());
        }
    }

    public void printIntentionalElementLinks(String actorID){
        System.out.println("There are some links between these elements: ");
        for(Map.Entry<ReferencePair,IntentionalElementLink> entry : iElementLinks.entrySet()){
            if(entry.getValue().getActorID().equals(actorID)){
                System.out.println("From "+entry.getKey().getFrom()+" to "+entry.getKey().getTo()+" type "+entry.getValue().getType());
            }
        }
    }

}
