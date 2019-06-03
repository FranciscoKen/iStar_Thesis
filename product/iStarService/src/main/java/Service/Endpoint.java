package Service;

import ClassGenerator.ClassGenerator;
import Exception.ExceptionMessages;
import Exception.IStarException;
import IStarML.IstarMLHandler;
import Model.IStarModel;
import Extractor.DOMParser;
import Storage.StorageService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import validator.ModelValidator;
import validator.XMLValidator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
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

            String multipartFileName = file.getOriginalFilename();
            File xml = File.createTempFile(FilenameUtils.getBaseName(multipartFileName),FilenameUtils.getExtension(multipartFileName));

            FileUtils.writeByteArrayToFile(xml,file.getBytes());

            //VALIDATE SCHEMA
            XMLValidator validator = new XMLValidator(xml);
            validator.validateXMLSchema();

            DOMParser parser = new DOMParser();
            IStarModel model = parser.extract(xml);

            //VALIDATE MODEL
            ModelValidator mv = new ModelValidator();
            mv.validateModel(model);
        } catch(IStarException iex){
            iex.printStackTrace();
            return new ResponseEntity<>(jsonify("error",iex.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(jsonify("error",e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("",HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping("/istar-service/class-diagram")
    public ResponseEntity<?> classDiagram(@RequestParam(value="file")MultipartFile file){
        String uid = "";
        try {
            validateiStarML2File(file);

            String multipartFileName = file.getOriginalFilename();
            File xml = File.createTempFile(FilenameUtils.getBaseName(multipartFileName),FilenameUtils.getExtension(multipartFileName));
            FileUtils.writeByteArrayToFile(xml,file.getBytes());

            //VALIDATE INPUT FILE BEFORE
            XMLValidator xmlValidator = new XMLValidator(xml);
            xmlValidator.validateXMLSchema();

            uid = UUID.randomUUID().toString();
            ClassGenerator generator = new ClassGenerator(uid);
            DOMParser extractor = new DOMParser();

            IStarModel model = extractor.extract(xml);

            ModelValidator modelValidator = new ModelValidator();
            modelValidator.validateModel(model);

            generator.generateClassDiagram(model);

        } catch (IStarException iex) {
            return new ResponseEntity<>(jsonify("error",iex.getMessage()),HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(jsonify("error",e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
    @PostMapping(value="/istar-service/convert-istarml")
    public ResponseEntity<?> convertiStarML(@RequestParam(value="file") MultipartFile model) {
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


        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
            DOMSource domSource = new DOMSource(imlh.translateIStarML());
            StreamResult streamResult= new StreamResult(new File("TEMP/response.istarml2"));
            transformer.transform(domSource,streamResult);
        } catch (TransformerException e) {
            e.printStackTrace();
            return new ResponseEntity<>("",HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        File responseFile = new File("TEMP/response.istarml2");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=response.istarml2");

        Path path = Paths.get(responseFile.getAbsolutePath());
        try {
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(responseFile.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resource);

        } catch (IOException e) {
            return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping(value="/istar-service/ping")
    public ResponseEntity<?> ping(){
        return new ResponseEntity<>("PING!",HttpStatus.OK);
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
