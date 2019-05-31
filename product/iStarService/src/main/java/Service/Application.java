package Service;

import Exception.ExceptionMessages;
import Storage.StorageProperties;
import Storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;

@SpringBootApplication
@ComponentScan({"Storage","Service"})
@EnableConfigurationProperties(StorageProperties.class)
public class Application {
    public static File f_xsd;

    public static void main(String[] args){
        ExceptionMessages messages = new ExceptionMessages();
        SpringApplication.run(Application.class,args);
        f_xsd = new File("RESOURCE/istarml2.xsd");
    }

    @Bean
    CommandLineRunner init(StorageService storageService){
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }
}
