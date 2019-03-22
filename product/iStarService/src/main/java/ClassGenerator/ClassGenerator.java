package ClassGenerator;

import Model.Actor;
import Model.ActorLink;
import Model.ActorLinkType;
import Model.IStarModel;
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
        for(Actor ac : model.getActors()){
            source += "Class "+ac.getName()+"\n";
        }
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
