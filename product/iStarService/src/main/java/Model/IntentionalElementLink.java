package Model;

public class IntentionalElementLink {
    private String from;
    private String to;
    private IntentionalElementLinkType type;
    private String actorID;

    public IntentionalElementLink(String in_from,String in_to,String actorID, IntentionalElementLinkType type){
        this.from = in_from;
        this.to = in_to;
        this.type = type;
        this.actorID = actorID;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public IntentionalElementLinkType getType() {
        return type;
    }

    public String getActorID() {
        return actorID;
    }
}
