package ClassGenerator;

import net.sourceforge.plantuml.SourceStringReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ClassGenerator {
    public static void main(String[] args){
        OutputStream png = null;
        try {
            png = new FileOutputStream("src/main/java/ClassGenerator/test.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String source = "@startuml\n";
        source += "Bob -> Alice : hello\n";
        source += "@enduml\n";

        SourceStringReader reader = new SourceStringReader(source);
// Write the first image to "png"
        try {
            String desc = reader.outputImage(png).getDescription();
        } catch (IOException e) {
            e.printStackTrace();
        }
// Return a null string if no generation
    }
}
