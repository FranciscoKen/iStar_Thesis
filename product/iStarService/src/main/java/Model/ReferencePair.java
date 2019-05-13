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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReferencePair other = (ReferencePair) obj;
        if (from != other.from)
            return false;
        if(to != other.to)
            return false;

        return true;
    }
}
