package validator;

import Service.ServiceResult;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

public class XMLValidator {
//    public static void main(String[] args){
//        System.out.println(validateXMLSchema("src/main/java/validator/alternative.xsd", "src/main/java/validator/code-flattening.xml"));
//    }
    private String xsdPath;
    private String xmlPath;

    public XMLValidator(String xsd,String xml){
        this.xsdPath = xsd;
        this.xmlPath = xml;
    }

    public void validateXMLSchema(ServiceResult result) {

        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(this.xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(this.xmlPath)));
        } catch (SAXException | IOException e) {
            result.setVerdict(false);
            result.setMessage((String)e.getMessage());
        }
    }
}
