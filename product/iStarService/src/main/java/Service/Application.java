package Service;

import Storage.StorageProperties;
import Storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"Storage","Service"})
@EnableConfigurationProperties(StorageProperties.class)
public class Application {
    public static void main(String[] args){
//        System.setProperty("http.proxyHost", "167.205.22.102");
//        System.setProperty("http.proxyPort", "8800");
//        System.setProperty("https.proxyHost", "167.205.22.102");
//        System.setProperty("https.proxyPort", "8800");
        SpringApplication.run(Application.class,args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService){
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }
}
