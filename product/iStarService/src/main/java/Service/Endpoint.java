package Service;

import ClassGenerator.ClassGenerator;
import Exception.ExceptionMessages;
import Exception.IStarException;
import IStarML.IstarMLHandler;
import Model.IStarModel;
import Extractor.DOMParser;
import Storage.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import validator.ModelValidator;
import validator.XMLValidator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

@RestController
public class Endpoint {
    private final StorageService storageService;

    @Autowired
    public Endpoint(StorageService storageService){
        this.storageService = storageService;
    }


    @CrossOrigin
    @PostMapping("/istar-service/validate")
    public ResponseEntity<?> validate(@RequestParam(value="file")MultipartFile file){

        try {
            validateiStarML2File(file);

            storageService.store(file);

            //VALIDATE SCHEMA
            XMLValidator validator = new XMLValidator("TEMP/model/"+file.getOriginalFilename());
            validator.validateXMLSchema();

            DOMParser parser = new DOMParser();
            IStarModel model = parser.extract("TEMP/model/"+file.getOriginalFilename());

            //VALIDATE MODEL
            ModelValidator mv = new ModelValidator();
            mv.validateModel(model);
        } catch(IStarException iex){
            storageService.deleteAll();

            return new ResponseEntity<>(jsonify("error",iex.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            e.printStackTrace();
            storageService.deleteAll();
            return new ResponseEntity<>(jsonify("error",e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //TODO Implement Validation Rule

        storageService.deleteAll();

        return new ResponseEntity<>("",HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping("/istar-service/class-diagram")
    public ResponseEntity<?> classDiagram(@RequestParam(value="file")MultipartFile file){
        String uid = "";
        try {
            validateiStarML2File(file);

            storageService.store(file);

            //VALIDATE SCHEMA
            XMLValidator validator = new XMLValidator("TEMP/model/"+file.getOriginalFilename());

            uid = UUID.randomUUID().toString();
            ClassGenerator generator = new ClassGenerator(uid);
            DOMParser extractor = new DOMParser();

            IStarModel model = extractor.extract("TEMP/model/"+file.getOriginalFilename());
            generator.generateClassDiagram(model);

        } catch (IStarException iex) {
            storageService.deleteAll();
            return new ResponseEntity<>(jsonify("error",iex.getMessage()),HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            e.printStackTrace();
            storageService.deleteAll();
            return new ResponseEntity<>(jsonify("error",e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        storageService.deleteAll();
        return new ResponseEntity<>(jsonify("uid",uid),HttpStatus.CREATED);
    }
//
    @CrossOrigin
    @RequestMapping(path = "/istarml2-xsd", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(String param) throws IOException {

        File file = new File("RESOURCE/alternative.xsd");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=istarml2.xsd");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @CrossOrigin
    @GetMapping(value="/istar-service/class-image",produces=MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> getClassImage(@RequestParam(value="uid") String uid) throws IOException {
        File file = new File("TEMP/class/"+uid+"/"+uid+".png");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=class.png");

        if(!file.exists()){
            System.out.println("FILE NOT FOUND");
            return new ResponseEntity<>(jsonify("error",ExceptionMessages.classResourceNotExistException),HttpStatus.NOT_FOUND);
        }
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("image/png"))
                .body(resource);
    }

    @CrossOrigin
    @GetMapping(value="/istar-service/class-ocl")
    public ResponseEntity<?> getClassOCL(@RequestParam(value="uid") String uid) throws IOException {
        File file = new File("TEMP/class/"+uid+"/"+uid+".ocl");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=class.ocl");

        if(!file.exists()){
            System.out.println("File not exist!");
            return new ResponseEntity<>(jsonify("error",ExceptionMessages.classResourceNotExistException),HttpStatus.NOT_FOUND);
        }
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @CrossOrigin
    @PostMapping(value="/dummy/validate-istarml")
    public ResponseEntity<?> validateiStarML(@RequestParam(value="istarml") MultipartFile model) {
        String string_model ="";
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(model.getBytes());
            string_model = IOUtils.toString(stream,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("",HttpStatus.INTERNAL_SERVER_ERROR);
        }

        IstarMLHandler imlh = new IstarMLHandler(string_model);
        try {
            imlh.validate();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }


    @CrossOrigin
    @PostMapping(value="/dummy/convert-istarml")
    public ResponseEntity<Resource> convertiStarML(@RequestParam(value="file") MultipartFile file) throws IOException {

        try{
            if(FilenameUtils.getExtension(file.getOriginalFilename()).equals("istarml") && !file.isEmpty()) {
                throw new Exception("Wrong file type or file is empty");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        File responseFile = new File("TEMP/dummy/developer-SR.istarml2");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=developer-SR.istarml2");

        Path path = Paths.get(responseFile.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));


        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(responseFile.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }


    //ADDITIONAL FUNCTIONS//

    private void validateiStarML2File(MultipartFile file) throws IStarException{
        if(file.isEmpty()){
            throw new IStarException(ExceptionMessages.emptyFileException);
        }
        if(!FilenameUtils.getExtension(file.getOriginalFilename()).equals("istarml2")) {
            throw new IStarException(ExceptionMessages.falseFileTypeException);
        }
    }

    private HashMap<String,String> jsonify(String key, String message){
        HashMap<String,String> obj = new HashMap<>();
        obj.put(key,message);
        return obj;
    }
}
