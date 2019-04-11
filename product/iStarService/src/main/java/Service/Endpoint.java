package Service;

import ClassGenerator.ClassGenerator;
import Exception.ExceptionMessages;
import Exception.IStarException;
import Model.IStarModel;
import Extractor.DOMParser;
import Storage.StorageFileNotFoundException;
import Storage.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import validator.XMLValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    public ResponseEntity<String> validate(@RequestParam(value="file")MultipartFile file){

        try {
            validateiStarML2File(file);

            storageService.store(file);

            //VALIDATE SCHEMA
            XMLValidator validator = new XMLValidator("TEMP/model/"+file.getOriginalFilename());
            validator.validateXMLSchema();
            validator.validateModel();
        } catch(IStarException iex){
            return new ResponseEntity<>(iex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //TODO Implement Validation Rule



        return new ResponseEntity<>("",HttpStatus.OK);
    }

    /////////////////////////////////////////////////////////////////////
    @CrossOrigin
    @PostMapping("/istar-service/class-diagram")
    public ResponseEntity<String> classDiagram(@RequestParam(value="file")MultipartFile file){
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
            return new ResponseEntity<>(iex.getMessage(),HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(uid,HttpStatus.CREATED);
    }

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
            return new ResponseEntity<>(ExceptionMessages.classResourceNotExistException,HttpStatus.NOT_FOUND);
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
            return new ResponseEntity<>(ExceptionMessages.classResourceNotExistException,HttpStatus.NOT_FOUND);
        }
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    //////////////////////////////   DUMMY   ///////////////////////////////////////
    @PostMapping("/dummy/upload")
    public ServiceResult handleFileUpload(@RequestParam("file")MultipartFile file){
        ServiceResult result = new ServiceResult();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"OK");

        storageService.store(file);
        result.setMessage("Successfully uploaded "+file.getOriginalFilename());

        return result;
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc){
        return ResponseEntity.notFound().build();
    }

    //Dummy Endpoints for client prototype
    //List of endpoints:
    //1. Validate -> receives file as an input, sends out verdict and error message
    //2. Generate class diagram -> receives file as an input, sends out id of process
    //3. receive the class diagram image
    //4. receive the class diagram ocl
    @CrossOrigin
    @PostMapping("/dummy/validate")
    public ServiceResult dummyValidate(@RequestParam(value="file")MultipartFile file){
        ServiceResult result = new ServiceResult();
        result.setLog("Received file "+file.getOriginalFilename()+" with type "+file.getContentType());
        result.setVerdict(true);
//        validateiStarML2File(file,result);
        return result;
    }

    @CrossOrigin
    @PostMapping("/dummy/generate-class-diagram")
    public ServiceResult dummyGenerateClassDiagram(@RequestParam(value="file")MultipartFile file){
        ServiceResult result = new ServiceResult();
        result.setVerdict(true);
//        validateiStarML2File(file,result);
        String uid = UUID.randomUUID().toString();
        result.setMessage(uid);
        //Create directory
        File dir = new File("TEMP/dummy/class/"+uid);
        String directory = "";
        if(dir.mkdirs() || dir.exists()){
            directory = dir.getPath();
        } else {
            System.out.println("ERROR : FAILED TO CREATE NEW DIRECTORY");
        }

        String from = "RESOURCE/dummy/template.png";
        String to = "TEMP/dummy/class/"+uid+"/"+uid+".png";
        Path src = Paths.get(from);
        Path dest = Paths.get(to);
        try {
            Files.copy(src,dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        from = "RESOURCE/dummy/template.ocl";
        to = "TEMP/dummy/class/"+uid+"/"+uid+".ocl";
        src = Paths.get(from);
        dest = Paths.get(to);
        try {
            Files.copy(src,dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @CrossOrigin
    @GetMapping(value="/dummy/get-class-image",produces=MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> dummyGetClassImage(@RequestParam(value="uid") String uid) throws IOException {
        File file = new File("TEMP/dummy/class/"+uid+"/"+uid+".png");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=class.png");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("image/png"))
                .body(resource);
    }

    @CrossOrigin
    @GetMapping(value="/dummy/get-class-ocl")
    public ResponseEntity<Resource> dummyGetClassOCL(@RequestParam(value="uid") String uid) throws IOException {
        File file = new File("TEMP/dummy/class/"+uid+"/"+uid+".ocl");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=class.ocl");
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
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

    private void validateiStarML2File(MultipartFile file) throws IStarException{
        if(file.isEmpty()){
            throw new IStarException(ExceptionMessages.emptyFileException);
        }
        if(!FilenameUtils.getExtension(file.getOriginalFilename()).equals("istarml2")) {
            throw new IStarException(ExceptionMessages.falseFileTypeException);
        }
    }
}
