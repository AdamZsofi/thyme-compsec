package hu.bme.crysys.server.server.domain;

import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component("storageFolder")
public class StorageFolder {
    Path storageFolder;

    StorageFolder() {
        String storageFolderString = System.getenv("THYME_STORE");
        if(storageFolderString==null) {
            throw new RuntimeException("Environment variable THYME_STORE should not be null!");
        }
        Path storage = Path.of(storageFolderString);
        if(!Files.exists(storage) || !Files.isDirectory(storage)) {
            throw new RuntimeException("Storage folder does not exist or is not a directory: " + storage);
        }
        storageFolder = storage;
    }

    public Path getStorageFolder() {
        return storageFolder;
    }
}
