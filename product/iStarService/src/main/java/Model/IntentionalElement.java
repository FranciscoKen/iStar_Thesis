package Model;

public class IntentionalElement {
    private String id;
    private String name;
    private String actorID;
    private IntentionalElementType type;
    private String state;

    public IntentionalElement(IntentionalElementType input_type,String input_actorID){
        this.type = input_type;
        this.actorID = input_actorID;
    }

    public String getName() {
        return name;
    }

    public String getActorID() {
        return actorID;
    }

    public IntentionalElementType getType() {
        return type;
    }

    public String getState() {
        return state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(String state) {
        this.state = state;
    }
}
