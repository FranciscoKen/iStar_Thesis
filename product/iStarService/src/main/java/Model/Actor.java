package Model;

public class Actor {
    private String id;
    private String name;
    private ActorType type;

    public Actor(String s_id, ActorType s_type){
        this.id = s_id;
        this.type = s_type;
    }

    public String getName() {
        if(this.name==null){
            return null;
        } else {
            return name;
        }

    }

    public String getId() {
        return id;
    }

    public ActorType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

}
