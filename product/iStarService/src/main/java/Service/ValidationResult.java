package Service;

import java.util.ArrayList;

public class ValidationResult {
    private boolean verdict;
    private ArrayList<String> message;

    public ValidationResult(){
        this.verdict = true;
        this.message = new ArrayList<String>();
    }

    public boolean getVerdict() {
        return verdict;
    }

    public void setVerdict(boolean verdict) {
        this.verdict = verdict;
    }

    public ArrayList<String> getMessage() {
        return message;
    }

    public void setMessage(String input_message) {
        this.message.add(input_message);
    }
}
