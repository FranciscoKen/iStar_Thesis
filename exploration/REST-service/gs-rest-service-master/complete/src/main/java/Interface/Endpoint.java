package Interface;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import validator.XMLValidator;

import java.io.IOException;

@RestController
public class Endpoint {
    @RequestMapping("/validate")
    public ValidationResult validate(@RequestParam(value="path")String path){
        ValidationResult result = new ValidationResult();
        XMLValidator validator = new XMLValidator("src/main/java/validator/alternative.xsd",path);
        validator.validateXMLSchema(result);

        return result;
    }
}
