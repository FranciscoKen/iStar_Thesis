package Model;

public class ReferencePair {
    private String from;
    private String to;

    public ReferencePair(String f, String t){
        this.from = f;
        this.to = t;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
