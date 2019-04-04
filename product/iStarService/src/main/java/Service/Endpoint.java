package Service;

import ClassGenerator.ClassGenerator;
import Model.IStarModel;
import Extractor.DOMParser;
import Storage.StorageFileNotFoundException;
import Storage.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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

    @RequestMapping("/validate")
    public ServiceResult validate(@RequestParam(value="path")String path){
        ServiceResult result = new ServiceResult();
        XMLValidator validator = new XMLValidator("src/main/java/validator/alternative.xsd",path);
        validator.validateXMLSchema(result);

        return result;
    }

    @RequestMapping("/generateClassDiagram")
    public ServiceResult classDiagram(@RequestParam(value="s_id")String s_id){
        ClassGenerator generator = new ClassGenerator(s_id);
        ServiceResult result = new ServiceResult();
        DOMParser parser = new DOMParser();
        //TEMP
        IStarModel temp;
        temp = parser.extract();
        generator.generateClassDiagram(temp);

        return result;
    }


    @RequestMapping(path = "/istarml2-xsd", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(String param) throws IOException {

        File file = new File("./src/main/java/validator/alternative.xsd");
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

//    @GetMapping("/demo/upload")
//    public String listUploadedFiles(Model model) throws IOException {
//
//    }

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
    @PostMapping("/dummy/validate")
    public ServiceResult validate(@RequestParam(value="file")MultipartFile file){
        ServiceResult result = new ServiceResult();
        result.setLog("Received file "+file.getOriginalFilename()+" with type "+file.getContentType());
        result.setVerdict(true);
        validateiStarML2File(file,result);
        return result;
    }

    @PostMapping("/dummy/generate-class-diagram")
    public ServiceResult generateClassDiagram(@RequestParam(value="file")MultipartFile file){
        ServiceResult result = new ServiceResult();
        result.setVerdict(true);
        validateiStarML2File(file,result);
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

    @GetMapping(value="/dummy/get-class-image",produces=MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> getClassImage(@RequestParam(value="uid") String uid) throws IOException {
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

    @GetMapping(value="/dummy/get-class-OCL")
    public ResponseEntity<Resource> getClassOCL(@RequestParam(value="uid") String uid) throws IOException {
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

    private void validateiStarML2File(MultipartFile file, ServiceResult result){
        if(file.isEmpty()){
            result.setVerdict(false);
            result.setMessage("Error! File is empty");
        }
        if(!FilenameUtils.getExtension(file.getOriginalFilename()).equals("istarml2")) {
            result.setVerdict(false);
            result.setMessage("Error! Required .istarml2 file");
        }
    }
}
