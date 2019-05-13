package Model;


public class IntentionalElementLink {
    private IntentionalElementLinkType type;
    private String actorID;

    public IntentionalElementLink(String actorID, IntentionalElementLinkType type){
        this.type = type;
        this.actorID = actorID;
    }

    public IntentionalElementLinkType getType() {
        return type;
    }

    public void setType(IntentionalElementLinkType type) {
        this.type = type;
    }

    public String getActorID() {
        return actorID;
    }

    public void setActorID(String actorID) {
        this.actorID = actorID;
    }
}
