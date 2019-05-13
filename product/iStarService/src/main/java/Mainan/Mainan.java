package Mainan;

import Model.ReferencePair;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

public class Mainan {
    public static void main(String[] args) throws IOException{
        FileWriter fw = new FileWriter("temp.istarml");
        fw.write("<istarml> <diagram> <actor> </actor> <actor> </actor></diagram> </istarml>");
        fw.close();
    }
}
