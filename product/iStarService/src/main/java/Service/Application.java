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
        System.setProperty("http.proxyHost", "cache.itb.ac.id");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("http.proxyUser", "franciscoken");
        System.setProperty("http.proxyPassword", "43059269");
        System.setProperty("https.proxyHost", "cache.itb.ac.id");
        System.setProperty("https.proxyPort", "8080");
        System.setProperty("https.proxyUser", "franciscoken");
        System.setProperty("https.proxyPassword", "43059269");
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
