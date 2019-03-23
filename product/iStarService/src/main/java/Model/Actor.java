package Model;

import org.springframework.lang.Nullable;

import java.util.ArrayList;

public class Actor {
    private String id;
    private String name;
    private String type;
    private ArrayList<IntentionalElement> iElements;

    public Actor(String s_id, String s_type){
        this.id = s_id;
        this.type = s_type;
        iElements = new ArrayList<IntentionalElement>();
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
