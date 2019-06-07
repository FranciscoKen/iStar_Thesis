package ClassGenerator;

import java.util.ArrayList;

/**
 * A class representing a collection of OCL strings in a model
 * @author Francisco Kenandi
 */
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

    public void addCustomOCL(String actor, String ielement, String statement){
        String template ="";
        template += actor + ":: " + ielement + "\n";
        template += statement+"\n\n";
        constraints.add(template);
    }

    public String exportString(){
        String result = "";
        for(String a : constraints){
            result += a;
        }

        return result;
    }
}
