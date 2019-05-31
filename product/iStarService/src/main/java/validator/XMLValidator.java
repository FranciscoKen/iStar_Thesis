package validator;

import Exception.ExceptionMessages;
import Exception.IStarException;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

import static Service.Application.f_xsd;

public class XMLValidator {
    private File xsd;
    private File xml;

    public XMLValidator(File xml){
        this.xsd = f_xsd;
        this.xml = xml;
    }

    public void validateXMLSchema() throws IStarException{
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(xsd);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(this.xml));
        } catch (SAXException | IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new IStarException(ExceptionMessages.heading+ExceptionMessages.falseSchemaException+" : "+e.getMessage(),e);
        }
    }

    public boolean isXMLConfirmSchema(){
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(this.xsd);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(this.xml));
        } catch (SAXException | IOException e) {
            return false;
        }
        return true;
    }

}
