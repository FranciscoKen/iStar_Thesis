package ClassGenerator;

import net.sourceforge.plantuml.SourceStringReader;

import java.io.*;

public class ClassGenerator {
    private String s_id;
    private String PATH;


    public ClassGenerator(String s_id){
        this.s_id = s_id;
        this.PATH = "TEMP/class/";
    }

    public void generateClassDiagram(){
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

        String source = "@startuml\n";
        source += "Bob -> Alice : hello\n";
        source += "@enduml\n";

        SourceStringReader reader = new SourceStringReader(source);

        try{
            String desc = reader.outputImage(png).getDescription();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
