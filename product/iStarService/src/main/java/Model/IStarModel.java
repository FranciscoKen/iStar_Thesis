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

    //Constructor
    public IStarModel(){
        actors = new ArrayList<Actor>();
        actorLinks = new ArrayList<ActorLink>();
    }

    public void assignActor(String id, String type, String name){
        Actor ac = new Actor(id,type,name);
        actors.add(ac);
    }

    public void assignActor(String id, String type){
        Actor ac = new Actor(id,type);
        actors.add(ac);
    }

    public void assignActorLink(ActorLinkType type,String from, String to){
        ActorLink acl = new ActorLink(type,from,to);
        actorLinks.add(acl);
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

    public ArrayList<Actor> getActors() {
        return actors;
    }

    public ArrayList<ActorLink> getActorLinks() {
        return actorLinks;
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

}
