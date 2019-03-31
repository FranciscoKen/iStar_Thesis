package ClassGenerator;

import java.util.ArrayList;

public class iStarOCL {
    private ArrayList<String> constraints;

    public iStarOCL(){
        constraints= new ArrayList<String>();
    }

    public void addPrePostOCL(String actor, String ielement,String preCondition, String postCondition){
        String template = "";

        template += actor + ":: " + ielement + "\n";
        template += "pre: "+preCondition+"\n";
        template += "post: "+postCondition+"\n\n";

        constraints.add(template);
    }
    public void addImplicationOCL(String actor, String ielement, String cause, String implication){
        String template ="";
        template += actor + ":: " + ielement + "\n";
        template += cause + " implies " + implication+"\n\n";
        constraints.add(template);
    }
}
