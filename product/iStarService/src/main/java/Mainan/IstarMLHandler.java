package Mainan;

import Mainan.ccistarml.ccistarmlFile;

import java.util.LinkedList;

public class IstarMLHandler {
    private String model;
    ccistarmlFile file;
    public IstarMLHandler(String model){
        this.model = model;
        this.file = new ccistarmlFile(model);
    }

    public void validate() throws Exception{
        boolean xmlTruth = file.xmlParser();
        if(!xmlTruth){
            String errors="";
            LinkedList errs = file.errorList();
            for(Object s:errs){
                errors += s.toString();
            }
            throw new Exception(errors);
        }

        boolean modelTruth = file.istarmlParser();
        if(!modelTruth){
            String errors ="";
            LinkedList errs = file.errorList();
            for(Object s : errs){
                errors+=s.toString();
            }
            throw new Exception(errors);
        }
    }


}
