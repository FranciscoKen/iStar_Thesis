package Storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService{
    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties){
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file){
        try {
            if(file.isEmpty()){
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            Files.copy(file.getInputStream(),this.rootLocation.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            throw new StorageException("Failed to store file "+file.getOriginalFilename(),e);
        }
    }

    @Override
    public Stream<Path> loadAll(){
        try {
            return Files.walk(this.rootLocation,1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files",e);
        }
    }

    @Override
    public Path load(String filename){
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename){

        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()){
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: "+filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: "+filename,e);
        }
    }

    @Override
    public void deleteAll(){
        Path directory = rootLocation;
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

//        FileSystemUtils.deleteRecursively(rootLocation.toFile());

    }

    @Override
    public void init(){
        try {
            if(!Files.exists(rootLocation)){
                Files.createDirectory(rootLocation);
            }

        } catch (IOException e) {
            throw new StorageException("Could not initialize storage",e);
        }
    }

}
