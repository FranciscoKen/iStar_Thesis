package Model;

public class ActorLink {
    private ActorLinkType type;
    private String from;
    private String to;

    public ActorLink(ActorLinkType input_type,String input_from, String input_to){
        this.type = input_type;
        this.from = input_from;
        this.to = input_to;
    }

    public ActorLinkType getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}