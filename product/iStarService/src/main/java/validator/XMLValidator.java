package validator;

import Mainan.ExceptionMessages;
import Mainan.IStarException;
import Service.ServiceResult;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

public class XMLValidator {
    private String xsdPath;
    private String filePath;

    public XMLValidator(String filePath){
        this.xsdPath = "RESOURCE/istarml2.xsd";
        this.filePath = filePath;
    }

    public void validateXMLSchema() throws IStarException{
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(this.xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(this.filePath)));
        } catch (SAXException | IOException e) {
            throw new IStarException(ExceptionMessages.heading+ExceptionMessages.falseSchemaException,e);

        }
    }

    public boolean isXMLConfirmSchema(){
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(this.xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(this.filePath)));
        } catch (SAXException | IOException e) {
            return false;
        }
        return true;
    }

    public void validateModel(){

    }
}
