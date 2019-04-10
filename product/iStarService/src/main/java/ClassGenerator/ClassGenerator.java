package ClassGenerator;

import Model.*;
import javafx.util.Pair;
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
        String source = "@startuml\n";

        ArrayList<Resource> temp_resource = new ArrayList<Resource>();

        //CONVERT IStar object into class diagram
        //actor conversion
        for(Map.Entry<String,Actor> actor : model.getActors().entrySet()){
            //checks if the actor has name (since it's optional).
            if(!actor.getValue().getName().equals("")){
                source += "Class "+cleanString(actor.getValue().getName());
            } else {
                //uses actor ID if name doesn't exist
                source += "Class "+cleanString(actor.getKey());
            }

            if(model.isHasIElement(actor.getKey())){
                //ielement conversion (goal, task, quality)
                //similar rule to actor, if ielement doesn't have a name, we use ID
                source+= "{ \n";
                for(Map.Entry<String,IntentionalElement> ie : model.getiElements().entrySet()){
                    if(ie.getValue().getActorID().equals(actor.getKey())){
                        if(ie.getValue().getType().equals(IntentionalElementType.TASK)){
                            if(!ie.getValue().getName().equals("")){
                                source += "{method} -"+cleanString(ie.getValue().getName())+"()\n";
                            } else {
                                source += "{method} -"+cleanString(ie.getKey())+"()\n";
                            }
                        } else if(ie.getValue().getType().equals(IntentionalElementType.GOAL)||ie.getValue().getType().equals(IntentionalElementType.QUALITY) ){
                            if(!ie.getValue().getName().equals("")){
                                source += "{field} -"+cleanString(ie.getValue().getName())+" : boolean\n";
                            } else {
                                source += "{field} -"+cleanString(ie.getKey())+" : boolean\n";
                            }
                        } else if(ie.getValue().getType().equals(IntentionalElementType.RESOURCE)){
                            Resource r = new Resource(cleanString(ie.getKey()),ie.getValue().getName().equals("")?cleanString(ie.getKey()):cleanString(ie.getValue().getName()), renameResource(actor.getValue().getName().equals("")?cleanString(ie.getKey()):cleanString(actor.getValue().getName()),temp_resource));
                            temp_resource.add(r);
                        }
                    }
                }
                source += "}\n";

                //resource conversion
                for(Resource r : temp_resource){
                    source += "Class "+cleanString(r.getName())+"{\n";
                    source += "{field} +availability : boolean \n";
                    source +="}\n";
                    source += cleanString(r.getActor()) + " -- " + cleanString(r.getName())+"\n";
                }

            } else {
                source += "\n";
            }
        }
        //actor link conversion
        for(ActorLink acl : model.getActorLinks()){
            if(acl.getType() == ActorLinkType.ISA){
                source += acl.getFrom() + " --|> " + acl.getTo()+"\n";
            } else {
                source += acl.getFrom() + " -- " + acl.getTo() + " :  participates-in \n";
            }


        }


        //Dependency conversion
        for(Map.Entry<String,Dependency> d : model.getDependencies().entrySet()){
            if(d.getValue().getDependum().getType().equals(IntentionalElementType.TASK)){
                source += "Class "+ cleanString(model.getActors().get(d.getValue().getDependee()).getName())+" {\n";
                source += "{method} +"+ cleanString(d.getValue().getDependum().getName())+"()\n";
                source += "}\n";
                source += cleanString(model.getActors().get(d.getValue().getDepender()).getName()) + " \"depender\" -- \"dependee\" "+cleanString(model.getActors().get(d.getValue().getDependee()).getName())+" :Dependency\n";

            } else if(d.getValue().getDependum().getType().equals(IntentionalElementType.GOAL) || d.getValue().getDependum().getType().equals(IntentionalElementType.QUALITY)){
                source += "Class "+ cleanString(model.getActors().get(d.getValue().getDepender()).getName()) + "{\n";
                source += "{field} +"+cleanString(d.getValue().getDependum().getName())+" : boolean \n";
                source += "}\n";

            }  else if(d.getValue().getDependum().getType().equals(IntentionalElementType.RESOURCE)){
                Resource r = new Resource(d.getKey(), renameResource(cleanString(d.getValue().getDependum().getName()),temp_resource),null);

                temp_resource.add(r);
                source += "Class "+r.getName()+" {\n";
                source += "{field} +availability : boolean\n";
                source += "}\n";
                source += cleanString(model.getActors().get(d.getValue().getDepender()).getName()) + " \"depender\" -- \"dependum\" "+r.getName()+" : Dependency\n";
                source += r.getName() + " \"dependum\" -- \"dependee\" "+cleanString(model.getActors().get(d.getValue().getDependee()).getName())+" : Dependency\n";
            }
        }
        source += "@enduml\n";
        //End DSL

        SourceStringReader reader = new SourceStringReader(source);
        try{
            String desc = reader.outputImage(png).getDescription();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Intentional Element Links conversion
        iStarOCL ocl = new iStarOCL();
        //TODO PIKIRIN DOSA ANDA OHMAIGAT SUCH A TRAVERSTY DATATYPE HAHAHAHHAHAHAHA
        for(Map.Entry<String,Actor> a : model.getActors().entrySet()){
            String currentActor = a.getKey();
            HashMap<String,ArrayList<String>> refinement_pair = new HashMap<>();
            for(Map.Entry<ReferencePair,IntentionalElementLink> iel : model.getiElementLinks().entrySet()){
                if(iel.getValue().getActorID().equals(currentActor)){
                    String currentActorName = model.getActors().get(currentActor).getName();
                    String iElementFromName = model.getiElements().get(iel.getKey().getFrom()).getName();
                    String iElementToName = model.getiElements().get(iel.getKey().getTo()).getName();

                    if(model.getiElements().get(iel.getKey().getFrom()).getType().equals(IntentionalElementType.TASK) ||
                            model.getiElements().get(iel.getKey().getTo()).getType().equals(IntentionalElementType.TASK)){
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
                            //TODO IMPLEMENT REFINEMENT_AND IN TASK
                        } else if(iel.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_OR)){
                            //TODO IMPLEMENT REFINEMENT_OR IN TASK
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
                            if(model.getiElements().get(iel.getKey().getFrom()).getType().equals(IntentionalElementType.GOAL) ||
                                    model.getiElements().get(iel.getKey().getTo()).getType().equals(IntentionalElementType.QUALITY)){
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
                            if(model.getiElements().get(iel.getKey().getFrom()).getType().equals(IntentionalElementType.GOAL) ||
                                    model.getiElements().get(iel.getKey().getTo()).getType().equals(IntentionalElementType.QUALITY)){
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
                            //TODO IMPLEMENT REFINEMENT NON TASK OR
                        } else if(iel.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_AND)){
                            //TODO IMPLEMENT REFINEMENT NON TASK AND
                        }
                    }

                    //Extract refinement links
                    //At this point uda terpusat dalam satu aktor


                    if(iel.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_AND) ||
                            iel.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_OR)){
                        if(refinement_pair.containsKey(iel.getKey().getTo())){
                            refinement_pair.get(iel.getKey().getTo()).add(iel.getKey().getFrom());
                        } else {
                            ArrayList<String> pair = new ArrayList<>();
                            pair.add(iel.getKey().getFrom());
                            refinement_pair.put(iel.getKey().getTo(),pair);
                        }
                    }
                }


            }

            //flush semua refinement link OCL
            for(Map.Entry<String,ArrayList<String>> entry : refinement_pair.entrySet()){
                String junction="";
                if(model.getiElementLinks().get(new Pair<>(entry.getValue().get(0),entry.getKey())).getType().equals(IntentionalElementLinkType.REFINEMENT_AND)){
                    junction = " and ";
                } else if(model.getiElementLinks().get(new Pair<>(entry.getValue().get(0),entry.getKey())).getType().equals(IntentionalElementLinkType.REFINEMENT_OR)){
                    junction = " or ";
                }
                if(isContainTask(entry.getKey(),entry.getValue(),model)){
                    String temp_pre ="";
                    String temp_post="";
                    for(String s : entry.getValue()){
                        if(model.getiElements().get(s).getType().equals(IntentionalElementType.TASK)){
                            temp_pre += model.getiElements().get(s).getName()+".preCondition=\"Value\" "+junction;
                            temp_post += junction + model.getiElements().get(s).getName()+".postCondition=\"Value\" ";
                        } else {
                            temp_pre += model.getiElements().get(s).getName()+"=true "+junction;
                        }
                    }
                    if(model.getiElements().get(entry.getKey()).getType().equals(IntentionalElementType.TASK)){
                        temp_pre += model.getiElements().get(entry.getKey()).getName()+".preCondition=\"Value\" "+junction;
                        temp_post += model.getiElements().get(entry.getKey()).getName()+".postCondition=\"Value\" ";
                    } else {
                        temp_post += model.getiElements().get(entry.getKey()).getName() + "=true";
                    }

                    ocl.addPrePostOCL(currentActor,
                            model.getiElements().get(entry.getKey()).getName(),
                            temp_pre,
                            temp_post);
                } else {
                    String temp_implication="";
                    String temp_cause = "";
                    for(String s : entry.getValue()){
                        temp_cause += model.getiElements().get(s).getName()+"=true";
                        temp_cause += junction;
                    }
                    temp_implication += model.getiElements().get(entry.getKey()).getName()+"=true";
                    ocl.addImplicationOCL(currentActor,
                            model.getiElements().get(entry.getKey()).getName(),
                            temp_cause,
                            temp_implication);
                }
            }
        }

        temp_resource = null;


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
