package Model;

public class Dependency {
    private String depender;
    private String dependee;
    private IntentionalElement dependum;
    private String dependerElement;
    private String dependeeElement;

    public Dependency(String id,IntentionalElementType type,String input_depender, String input_dependee){
        this.dependum = new IntentionalElement(id,type,null);
        this.dependee = input_dependee;
        this.depender = input_depender;
    }

    public String getDepender() {
        return depender;
    }

    public String getDependee() {
        return dependee;
    }

    public IntentionalElement getDependum() {
        return dependum;
    }

    public String getDependerElement() {
        return dependerElement;
    }

    public String getDependeeElement() {
        return dependeeElement;
    }

    public void setDependerElement(String dependerElement) {
        this.dependerElement = dependerElement;
    }

    public void setDependeeElement(String dependeeElement) {
        this.dependeeElement = dependeeElement;
    }
}
