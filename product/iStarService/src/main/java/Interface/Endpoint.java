package Interface;

import ClassGenerator.ClassGenerator;
import Model.ActorLinkType;
import Model.IStarModel;
import Parser.DOMParser;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import validator.XMLValidator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class Endpoint {
    @RequestMapping("/validate")
    public ValidationResult validate(@RequestParam(value="path")String path){
        ValidationResult result = new ValidationResult();
        XMLValidator validator = new XMLValidator("src/main/java/validator/alternative.xsd",path);
        validator.validateXMLSchema(result);

        return result;
    }

    @RequestMapping("/generateClassDiagram")
    public ValidationResult classDiagram(@RequestParam(value="s_id")String s_id){
        ClassGenerator generator = new ClassGenerator(s_id);
        ValidationResult result = new ValidationResult();
        DOMParser parser = new DOMParser();
        //TEMP
        IStarModel temp = new IStarModel();
        temp = parser.extract();
//        temp.assignActor("ac-1", "role","Attacker");
//        temp.assignActor("ac-2", "role","Defender");
        generator.generateClassDiagram(temp);

        return result;
    }


    @RequestMapping(path = "/istarml2-xsd", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(String param) throws IOException {

//        File file = new File("./Resource/alternative.xsd");
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
}
