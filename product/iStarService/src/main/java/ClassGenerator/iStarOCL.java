package ClassGenerator;

import java.util.ArrayList;

public class iStarOCL {
    private ArrayList<String> constraints;

    public iStarOCL(){
        constraints= new ArrayList<String>();
    }

    public void addPrePostOCL(String actor, String ielement,String preCondition, String postCondition){
        StringBuilder template = new StringBuilder();

        template.append(actor).append(":: ").append(ielement).append("\n");
        template.append("pre: ").append(preCondition).append("\n");
        template.append("post: ").append(postCondition).append("\n\n");

        constraints.add(template.toString());
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
        StringBuilder result = new StringBuilder();
        for(String a : constraints){
            result.append(a);
        }

        return result.toString();
    }
}
