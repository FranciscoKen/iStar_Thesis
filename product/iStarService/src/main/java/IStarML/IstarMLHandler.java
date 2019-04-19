package IStarML;

import IStarML.ccistarml.ccfileError;
import IStarML.ccistarml.ccistarmlFile;

import java.util.Iterator;
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

            throw new Exception(gatherErrors());
        }

        boolean modelTruth = file.istarmlParser();
        if(!modelTruth){
            throw new Exception(gatherErrors());
        }
    }

    private String gatherErrors(){
        String errors="";
        LinkedList errs = file.errorList();
        Iterator it = errs.iterator();
        ccfileError thisErr;
        while(it.hasNext()){
            thisErr = (ccfileError) it.next();
            errors += "Error : "+thisErr.error + ":" + thisErr.textLine+"(Line "+thisErr.line+")"+ "\n";
        }

        return errors;
    }

}
