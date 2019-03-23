package ClassGenerator;

import Model.*;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.*;
import java.util.ArrayList;

public class ClassGenerator {
    private String s_id;
    private String PATH;


    public ClassGenerator(String s_id){
        this.s_id = s_id;
        this.PATH = "TEMP/class/";
    }

    public void generateClassDiagram(IStarModel model){
        OutputStream png = null;
        File directory = new File(PATH);
        if(! directory.exists()){
            directory.mkdirs();
        }

        try{
            png = new FileOutputStream(PATH+s_id+".png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Initialize DSL
        String source = "@startuml\n";


        //CONVERT IStar object into class diagram
        //actor conversion
        for(Actor ac : model.getActors()){
            //checks if the actor has name (since it's optional).
            if(!ac.getName().equals("")){
                source += "Class "+ac.getName().replaceAll("\\s+","");
            } else {
                //uses actor ID if name doesn't exist
                source += "Class "+ac.getId().replaceAll("\\s+","");
            }

            ArrayList<Resource> temp_resource = new ArrayList<Resource>();

            if(model.isHasIElement(ac)){
                //ielement conversion (goal, task, quality)
                //similar rule to actor, if ielement doesn't have a name, we use ID
                source+= "{ \n";
                for(IntentionalElement ie : model.getiElements()){
                    if(ie.getActorID().equals(ac.getId())){
                        if(ie.getType().equals(IntentionalElementType.TASK)){
                            if(!ie.getName().equals("")){
                                source += "{method} -"+ie.getName()+"()\n";
                            } else {
                                source += "{method} -"+ie.getId()+"()\n";
                            }
                        } else if(ie.getType().equals(IntentionalElementType.GOAL)||ie.getType().equals(IntentionalElementType.QUALITY) ){
                            if(!ie.getName().equals("")){
                                source += "{field} -"+ie.getName()+":boolean\n";
                            } else {
                                source += "{field} -"+ie.getId()+":boolean\n";
                            }
                        } else if(ie.getType().equals(IntentionalElementType.RESOURCE)){
                            Resource r = new Resource(ie.getName().equals("")?ie.getId().replaceAll("\\s+",""):ie.getName().replaceAll("\\s+",""),ac.getName().equals("")?ac.getId().replaceAll("\\s+",""):ac.getName().replaceAll("\\s+",""));
                            temp_resource.add(r);
                        }
                    }
                }
                source += "}\n";
                //resource conversion
                for(Resource r : temp_resource){
                    source += "Class "+r.getName()+"{\n";
                    source += "{field} +availability : boolean \n";
                    source +="}\n";
                    source += r.getActor() + " -- " + r.getName()+"\n";
                }
                temp_resource = null;
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


        //End DSL
        source += "@enduml\n";

        SourceStringReader reader = new SourceStringReader(source);

        try{
            String desc = reader.outputImage(png).getDescription();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Resource{
        private String id;
        private String name;
        private String actor;

        public Resource(String name, String actor){
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
