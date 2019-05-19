package ClassGenerator;

import Model.*;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassGenerator {
    private String s_id;
    private String PATH;


    public ClassGenerator(String s_id){
        this.s_id = s_id;
        this.PATH = "TEMP/class/"+this.s_id;
    }

    public void generateClassDiagram(IStarModel model){
        OutputStream png = null;
        File directory = new File(PATH);
        if(! directory.exists()){
            directory.mkdirs();
        }

        try{
            png = new FileOutputStream(PATH+File.separator+s_id+".png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Initialize DSL
        StringBuilder source = new StringBuilder();
        source.append("@startuml\n");

        ArrayList<Resource> temp_resource = new ArrayList<Resource>();

        //CONVERT IStar object into class diagram
        //actor conversion
        for(Map.Entry<String,Actor> actor : model.getActors().entrySet()){
            //checks if the actor has name (since it's optional).
            if(!actor.getValue().getName().equals("")){
                source.append("Class ").append(cleanString(actor.getValue().getName()));
            } else {
                //uses actor ID if name doesn't exist
                source.append("Class ").append(cleanString(actor.getKey()));
            }

            if(model.isHasIElement(actor.getKey())){
                //ielement conversion (goal, task, quality)
                //similar rule to actor, if ielement doesn't have a name, we use ID
                source.append("{ \n");
                for(Map.Entry<String,IntentionalElement> ie : model.getiElements().entrySet()){
                    if(ie.getValue().getActorID().equals(actor.getKey())){
                        if(ie.getValue().getType().equals(IntentionalElementType.TASK)){
                            if(!ie.getValue().getName().equals("")){
                                source.append("{method} -").append(cleanString(ie.getValue().getName())).append("()\n");
                            } else {
                                source.append("{method} -").append(cleanString(ie.getKey())).append("()\n");
                            }
                        } else if(ie.getValue().getType().equals(IntentionalElementType.GOAL)||ie.getValue().getType().equals(IntentionalElementType.QUALITY) ){
                            if(!ie.getValue().getName().equals("")){
                                source.append("{field} -").append(cleanString(ie.getValue().getName())).append(" : boolean\n");
                            } else {
                                source.append("{field} -").append(cleanString(ie.getKey())).append(" : boolean\n");
                            }
                        } else if(ie.getValue().getType().equals(IntentionalElementType.RESOURCE)){
                            Resource r = new Resource(cleanString(ie.getKey()),ie.getValue().getName().equals("")?cleanString(ie.getKey()):cleanString(ie.getValue().getName()), renameResource(actor.getValue().getName().equals("")?cleanString(ie.getKey()):cleanString(actor.getValue().getName()),temp_resource));
                            temp_resource.add(r);
                        }
                    }
                }
                source.append("}\n");
            } else {
                source.append("\n");
            }
        }

        //resource conversion
        for(Resource r : temp_resource){
            source.append("Class ").append(cleanString(r.getName())).append("{\n");
            source.append("{field} +availability : boolean \n");
            source.append("}\n");
            source.append(cleanString(r.getActor())).append(" -- ").append(cleanString(r.getName())).append("\n");
        }
        
        //actor link conversion
        for(ActorLink acl : model.getActorLinks()){
            if(acl.getType() == ActorLinkType.ISA){
                source.append(cleanString(model.getActors().get(acl.getFrom()).getName())).append(" --|> ").append(cleanString(model.getActors().get(acl.getTo()).getName())).append("\n");
            } else {
                source.append(cleanString(model.getActors().get(acl.getFrom()).getName())).append(" -- ").append(cleanString(model.getActors().get(acl.getTo()).getName())).append(" :  participates-in \n");
            }
        }

        


        //Dependency conversion
        for(Map.Entry<String,Dependency> d : model.getDependencies().entrySet()){
            if(d.getValue().getDependum().getType().equals(IntentionalElementType.TASK)){
                source.append("Class ").append(cleanString(model.getActors().get(d.getValue().getDependee()).getName())).append(" {\n");
                source.append("{method} +").append(cleanString(d.getValue().getDependum().getName())).append("()\n");
                source.append("}\n");
                source.append(cleanString(model.getActors().get(d.getValue().getDepender()).getName())).append(" \"depender\" -- \"dependee\" ").append(cleanString(model.getActors().get(d.getValue().getDependee()).getName())).append(" :Dependency\n");

            } else if(d.getValue().getDependum().getType().equals(IntentionalElementType.GOAL) || d.getValue().getDependum().getType().equals(IntentionalElementType.QUALITY)){
                source.append("Class ").append(cleanString(model.getActors().get(d.getValue().getDependee()).getName())).append("{\n");
                source.append("{field} +").append(cleanString(d.getValue().getDependum().getName())).append(" : boolean \n");
                source.append("}\n");
                source.append(cleanString(model.getActors().get(d.getValue().getDepender()).getName())).append(" \"depender\" -- \"dependee\" ").append(cleanString(model.getActors().get(d.getValue().getDependee()).getName())).append(" :Dependency\n");

            }  else if(d.getValue().getDependum().getType().equals(IntentionalElementType.RESOURCE)){
                 Resource r = new Resource(d.getKey(), renameResource(cleanString(d.getValue().getDependum().getName()),temp_resource),null);
                 //temp_resource.add(r);
                source.append("Class ").append(r.getName()).append(" {\n");
                source.append("{field} +availability : boolean\n");
                source.append("}\n");
                source.append(cleanString(model.getActors().get(d.getValue().getDepender()).getName())).append(" \"depender\" -- \"dependum\" ").append(r.getName()).append(" : Dependency\n");
                source.append(r.getName()).append(" \"dependum\" -- \"dependee\" ").append(cleanString(model.getActors().get(d.getValue().getDependee()).getName())).append(" : Dependency\n");
            }
        }
        source.append("@enduml\n");
        //End DSL

        SourceStringReader reader = new SourceStringReader(source.toString());
        try{
            String desc = reader.outputImage(png).getDescription();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Intentional Element Links conversion
        iStarOCL ocl = new iStarOCL();
        for(Map.Entry<String,Actor> a : model.getActors().entrySet()){
            String currentActor = a.getKey();
            HashMap<String,ArrayList<String>> refinement_pair = new HashMap<>();
            for(Map.Entry<HashMap<String,String>,IntentionalElementLink> iel : model.getiElementLinks().entrySet()){
                if(iel.getValue().getActorID().equals(currentActor)){
                    String currentActorName = model.getActors().get(currentActor).getName();
                    String iElementFromName = model.getiElements().get(getFrom(iel.getKey())).getName();
                    String iElementToName = model.getiElements().get(getTo(iel.getKey())).getName();

                    if(model.getiElements().get(getFrom(iel.getKey())).getType().equals(IntentionalElementType.TASK) ||
                            model.getiElements().get(getTo(iel.getKey())).getType().equals(IntentionalElementType.TASK)){
                        if(iel.getValue().getType().equals(IntentionalElementLinkType.NEEDEDBY)){
                            ocl.addPrePostOCL(currentActorName,
                                    iElementFromName,
                                    iElementFromName+".preCondition=\"Value\" and "+iElementToName+".availability=true",
                                    iElementFromName+".postCondition=\"Value\"");
                        } else if(iel.getValue().getType().equals(IntentionalElementLinkType.QUALIFICATION)){
                            ocl.addPrePostOCL(currentActorName,
                                    iElementToName,
                                    iElementToName+".preCondition=\"Value\"",
                                    iElementToName+".postCondition=\"Value\" and "+iElementFromName+"=true");
                        } else if(iel.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_AND)){

                        } else if(iel.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_OR)){

                        } else if(iel.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_MAKE) ||
                                iel.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_HELP)){
                            ocl.addPrePostOCL(currentActorName,
                                    iElementFromName,
                                    iElementFromName+".preCondition=\"Value\"",
                                    iElementFromName+".postCondition=\"Value\" and "+iElementToName+"=true");
                        } else if(iel.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_HURT) ||
                                iel.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_BREAK)){
                            ocl.addPrePostOCL(currentActorName,
                                    iElementFromName,
                                    iElementFromName+".preCondition=\"Value\"",
                                    iElementFromName+".postCondition=\"Value\" and "+iElementToName+"=false");
                        }
                    } else { // No Task
                        if(iel.getValue().getType().equals(IntentionalElementLinkType.QUALIFICATION)){
                            ocl.addCustomOCL(currentActorName,
                                    iElementToName,
                                    iElementFromName+"=true and "+iElementToName+".availability=true");
                        } else if(iel.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_MAKE) ||
                                iel.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_HELP)){
                            if(model.getiElements().get(getFrom(iel.getKey())).getType().equals(IntentionalElementType.GOAL) ||
                                    model.getiElements().get(getTo(iel.getKey())).getType().equals(IntentionalElementType.QUALITY)){
                                ocl.addImplicationOCL(currentActorName,
                                        iElementFromName,
                                        iElementFromName+"=true",
                                        iElementToName+"=true");
                            } else {
                                //RESOURCE
                                ocl.addImplicationOCL(currentActorName,
                                        iElementFromName,
                                        iElementFromName+".availability=true",
                                        iElementToName+"=true");
                            }
                        } else if(iel.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_BREAK) ||
                                iel.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_HURT)){
                            if(model.getiElements().get(getFrom(iel.getKey())).getType().equals(IntentionalElementType.GOAL) ||
                                    model.getiElements().get(getTo(iel.getKey())).getType().equals(IntentionalElementType.QUALITY)){
                                ocl.addImplicationOCL(currentActorName,
                                        iElementFromName,
                                        iElementFromName+"=false",
                                        iElementToName+"=false");
                            } else {
                                //RESOURCE
                                ocl.addImplicationOCL(currentActorName,
                                        iElementFromName,
                                        iElementFromName+".availability=false",
                                        iElementToName+"=false");
                            }
                        } else if(iel.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_OR)){

                        } else if(iel.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_AND)){

                        }
                    }

                    //Extract refinement links
                    //At this point uda terpusat dalam satu aktor


                    if(iel.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_AND) ||
                            iel.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_OR)){
                        if(refinement_pair.containsKey(getTo(iel.getKey()))){
                            refinement_pair.get(getTo(iel.getKey())).add(getFrom(iel.getKey()));
                        } else {
                            ArrayList<String> pair = new ArrayList<>();
                            pair.add(getTo(iel.getKey()));
                            refinement_pair.put(getFrom(iel.getKey()),pair);
                        }
                    }
                }


            }

            if(refinement_pair.size()>0){
                //flush semua refinement link OCL
                for(Map.Entry<String,ArrayList<String>> entry : refinement_pair.entrySet()){
                    String junction="";

                    HashMap<String,String> temp_ref_pair = new HashMap<>();
                    temp_ref_pair.put(entry.getKey(),entry.getValue().get(0));

                    if(model.getiElementLinks().get(temp_ref_pair).getType().equals(IntentionalElementLinkType.REFINEMENT_AND)){
                        junction = " and ";
                    } else if(model.getiElementLinks().get(temp_ref_pair).getType().equals(IntentionalElementLinkType.REFINEMENT_OR)){
                        junction = " or ";
                    }
                    if(isContainTask(entry.getKey(),entry.getValue(),model)){
                        StringBuilder temp_pre =new StringBuilder();
                        StringBuilder temp_post=new StringBuilder();
                        for(String s : entry.getValue()){
                            if(model.getiElements().get(s).getType().equals(IntentionalElementType.TASK)){
                                temp_pre.append(model.getiElements().get(s).getName()).append(".preCondition=\"Value\" ").append(junction);
                                temp_post.append(junction).append(model.getiElements().get(s).getName()).append(".postCondition=\"Value\" ");
                            } else {
                                temp_pre.append(model.getiElements().get(s).getName()).append("=true ").append(junction);
                            }
                        }
                        if(model.getiElements().get(entry.getKey()).getType().equals(IntentionalElementType.TASK)){
                            temp_pre.append(model.getiElements().get(entry.getKey()).getName()).append(".preCondition=\"Value\" ").append(junction);
                            temp_post.append(model.getiElements().get(entry.getKey()).getName()).append(".postCondition=\"Value\" ");
                        } else {
                            temp_post.append(model.getiElements().get(entry.getKey()).getName()).append("=true");
                        }

                        ocl.addPrePostOCL(model.getActors().get(currentActor).getName(),
                                model.getiElements().get(entry.getKey()).getName(),
                                temp_pre.toString(),
                                temp_post.toString());
                    } else {
                        StringBuilder temp_implication= new StringBuilder();
                        StringBuilder temp_cause = new StringBuilder();
                        for(String s : entry.getValue()){
                            temp_cause.append(model.getiElements().get(s).getName()).append("=true");
                            temp_cause.append(junction);
                        }
                        temp_implication.append(model.getiElements().get(entry.getKey()).getName()).append("=true");
                        ocl.addImplicationOCL(model.getActors().get(currentActor).getName(),
                                model.getiElements().get(entry.getKey()).getName(),
                                temp_cause.toString(),
                                temp_implication.toString());
                    }
                }
            }


        }

        // temp_resource = null;


        createOCLFile(ocl.exportString());
    }

    private String cleanString(String string){
        return string.replaceAll("[(\\)(\\s+)(\\-+)]","_");
    }

    private boolean isResourceNameExist(String name,ArrayList<Resource> resources){
        boolean found = false;
        for(int temp = 0;temp<resources.size();temp++){
            if(resources.get(temp).getName().equals(name)){
                found = true;
            }
        }

        return found;
    }

    private String renameResource(String name, ArrayList<Resource> resources){
        int iteration = 0;
        String edited_name = name;
        while(isResourceNameExist(edited_name,resources)){
            iteration++;
            edited_name = name + "_"+iteration;
        }

        if(iteration > 0){
            return edited_name;
        } else {
            return name;
        }
    }

    private void createOCLFile(String content){
        //Create directory
        File dir = new File("TEMP/class/"+this.s_id);
        String directory = "";
        if(dir.mkdirs() || dir.exists()){
            directory = dir.getPath();
        } else {
            System.out.println("ERROR : FAILED TO CREATE NEW DIRECTORY");
        }

        //Create new File
        File file = new File(directory+File.separator+this.s_id+".ocl");
        if(file.exists()){
            file.delete();
        }

        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isContainTask(String from, ArrayList<String> tos, IStarModel model){
        boolean taskExist = false;

        if(model.getiElements().get(from).getType().equals(IntentionalElementType.TASK)){
            taskExist = true;
        } else {
            for(String s : tos){
                if(model.getiElements().get(s).getType().equals(IntentionalElementType.TASK)){
                    taskExist = true;
                }
            }
        }

        return taskExist;
    }

    private String getFrom(HashMap<String,String> map){
        return map.entrySet().iterator().next().getKey();
    }

    private String getTo(HashMap<String,String> map){
        return map.entrySet().iterator().next().getValue();
    }

    private class Resource{
        private String id;
        private String name;
        private String actor;

        public Resource(String id,String name, String actor){
            this.id = id;
            this.name = name;
            this.actor = actor;
        }

        public String getName() {
            return name;
        }

        public String getActor() {
            return actor;
        }
    }
}
