package Service;

import java.util.ArrayList;

public class ServiceResult {
    private boolean verdict;
    private ArrayList<String> log;
    private ArrayList<String> message;

    public ServiceResult(){
        this.verdict = true;
        this.message = new ArrayList<String>();
        this.log = new ArrayList<String>();
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

    public ArrayList<String> getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log.add(log);
    }
}
