package Model;

import org.springframework.lang.Nullable;

public class Actor {
    private String id;
    private String name;
    private String type;



    public Actor(String s_id, String s_type,String s_name){
        this.id = s_id;
        this.type = s_type;
        this.name = s_name;
    }

    public Actor(String s_id, String s_type){
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

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

}
