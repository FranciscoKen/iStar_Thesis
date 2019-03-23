package ClassGenerator;

import Model.*;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.*;

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
            System.out.println("DEBUG "+ac.getName());
            if(!ac.getName().equals("")){
                source += "Class "+ac.getName();
            } else {
                //uses actor ID if name doesn't exist
                source += "Class "+ac.getId();
            }

            if(model.isHasIElement(ac)){
                //ielement conversion
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
                        }
                    }
                }
                source += "}\n";
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
}
