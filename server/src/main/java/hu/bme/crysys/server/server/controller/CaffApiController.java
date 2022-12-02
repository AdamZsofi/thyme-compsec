package hu.bme.crysys.server.server.controller;

import hu.bme.crysys.server.server.domain.StorageFolder;
import hu.bme.crysys.server.server.domain.database.CaffComment;
import hu.bme.crysys.server.server.domain.database.CaffFile;
import hu.bme.crysys.server.server.domain.database.UserData;
import hu.bme.crysys.server.server.domain.parser.CaffParseResult;
import hu.bme.crysys.server.server.domain.parser.ParserController;
import hu.bme.crysys.server.server.repository.CaffCommentRepository;
import hu.bme.crysys.server.server.repository.CaffFileRepository;
import hu.bme.crysys.server.server.repository.UserDataRepository;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class CaffApiController {
    private static final Logger logger = LoggerFactory.getLogger(CaffApiController.class);
    @Autowired
    private CaffFileRepository caffFileRepository;
    @Autowired
    private CaffCommentRepository caffCommentRepository;
    @Autowired
    private UserDataRepository userDataRepository;
    @Autowired
    private InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Autowired
    @Qualifier("storageFolder")
    private StorageFolder storageFolder;

    @GetMapping(value = "/caff/search/{tag}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> findCaffsByTag(@PathVariable("tag") String tag) {
        List<JSONObject> caffs = new ArrayList<>();
        for (var caffFile : caffFileRepository.findAll()) {
            CaffParseResult caffParseResult = null;
            try {
                caffParseResult = ParserController.parse(caffFile.getCaffPath(storageFolder.getStorageFolder()));
            } catch (URISyntaxException e) {
                logger.error("Could not get caff for search");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            assert caffParseResult != null;

            if (caffParseResult.getTags().contains(tag)) {
                JSONObject innerJson = new JSONObject();
                innerJson.put("id", caffFile.getId());
                innerJson.put("filename", caffFile.getPublicFileName());
                innerJson.put("username", caffFile.getUserData().getUserName());
                innerJson.put("tags", caffParseResult.getTags().toString());
                caffs.add(innerJson);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("caffs", caffs);
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    /*
    Main page
     */
    @GetMapping(value = "/caff",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllCaffFile() {
        List<JSONObject> caffs = new ArrayList<>();
        for (var caffFile : caffFileRepository.findAll()) {
            JSONObject innerJson = new JSONObject();
            innerJson.put("id", caffFile.getId());
            innerJson.put("filename", caffFile.getPublicFileName());
            innerJson.put("username", caffFile.getUserData().getUserName());

            CaffParseResult caffParseResult = null;
            try {
                caffParseResult = ParserController.parse(caffFile.getCaffPath(storageFolder.getStorageFolder()));
            } catch (URISyntaxException e) {
                logger.error("Could not get caff");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            assert caffParseResult != null;
            innerJson.put("tags", caffParseResult.getTags().toString());

            caffs.add(innerJson);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("caffs", caffs);
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    /*
    Individual CAFF files
     */
    @GetMapping(value = "/caff/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCaffFileById(@PathVariable Integer id) {
        Optional<CaffFile> caffFile = caffFileRepository.findById(id);
        if (caffFile.isPresent()) {
            JSONObject innerJson = new JSONObject();
            innerJson.put("id", id);
            innerJson.put("filename", caffFile.get().getPublicFileName());
            innerJson.put("username", caffFile.get().getUserData().getUserName());

            CaffParseResult caffParseResult = null;
            try {
                caffParseResult = ParserController.parse(caffFile.get().getCaffPath(storageFolder.getStorageFolder()));
            } catch (URISyntaxException e) {
                logger.error("Could not get caff");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            assert caffParseResult != null;
            innerJson.put("tags", caffParseResult.getTags().toString());

            return new ResponseEntity<>(innerJson.toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/caff/preview/{id}", produces = "image/bmp")
    public @ResponseBody byte[] getCaffFilePicById(@PathVariable Integer id) {
        Optional<CaffFile> caffFile = caffFileRepository.findById(id);
        if (caffFile.isPresent()) {
            CaffParseResult caffParseResult = null;
            try {
                caffParseResult = ParserController.parse(caffFile.get().getCaffPath(storageFolder.getStorageFolder()));
            } catch (URISyntaxException e) {
                logger.error("Could not get preview");
                return new byte[0];
            }
            assert caffParseResult != null;
            String fileName = caffParseResult.ciffList.get(0).fileName;

            try {
                InputStream in = new FileInputStream(fileName);
                return IOUtils.toByteArray(in);
            } catch (IOException e) {
                logger.error("Could not get preview"); // TODO will never jump in these, as it is using the exception handler
                return new byte[0];
            }
        } else {
            logger.error("Could not get preview");
            return new byte[0];
        }
    }

    @GetMapping(value = "/caff/comment/{id}")
    public ResponseEntity<String> getCaffFileCommentById(@PathVariable Integer id) {
        Optional<CaffFile> caffFile = caffFileRepository.findById(id);
        if (caffFile.isPresent()) {

            List<JSONObject> comments = new ArrayList<>();
            for (var comment : caffFile.get().getComments()) {
                JSONObject innerJson = new JSONObject();
                innerJson.put("id", comment.getId());
                innerJson.put("author", comment.getAuthor());
                innerJson.put("content", comment.getContent());
                comments.add(innerJson);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("comments", comments);

            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/buy", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> buyCaffFile(@RequestParam("id") String id) {
        // TODO add user and id to list that the user bought the caff?
        return new ResponseEntity<>(Boolean.TRUE.toString(), HttpStatus.OK);
    }

    /*
    Downloading
     */
    @GetMapping(value = "/download/{id}",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] downloadCaffFile(@PathVariable Integer id) {
        // TODO check if user can download it
        Optional<CaffFile> caffFile = caffFileRepository.findById(id);
        if (caffFile.isPresent()) {
            try {
                InputStream in = new FileInputStream(
                    caffFile.get().getCaffPath(storageFolder.getStorageFolder()).toFile());
                return IOUtils.toByteArray(in);
            } catch (IOException | URISyntaxException e) {
                logger.error("Could not get caff file");
                return new byte[0];
            }
        } else {
            logger.error("Could not get caff file");
            return new byte[0];
        }
    }

    @GetMapping(
        value = "/get-file",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody byte[] getFile() throws IOException {
        InputStream in = getClass()
            .getResourceAsStream("/com/baeldung/produceimage/data.txt");
        return IOUtils.toByteArray(in);
    }

    /*
    Uploading
     */
    @RequestMapping(value = "/upload",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CaffFile> uploadCaffFile(@RequestParam("file") MultipartFile file, @RequestParam("caffName") String caffName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = inMemoryUserDetailsManager.loadUserByUsername(authentication.getName());
        try {
            if (file.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                String toBeHashed = Arrays.toString(file.getBytes())
                        + caffName;
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                BigInteger number = new BigInteger(1, md.digest(toBeHashed.getBytes()));
                StringBuilder hexString = new StringBuilder(number.toString(16));
                while (hexString.length() < 64) { hexString.insert(0, '0'); }
                String hash = hexString.toString();

                UserData userData = userDataRepository.findUserDataByUsername(userDetails.getUsername());
                CaffFile caffFile = new CaffFile(caffName, userData, hash);
                caffFile = caffFileRepository.saveAndFlush(caffFile);
                Path newCaffPath = caffFile.getCaffPath(storageFolder.getStorageFolder());

                if(!save(file, newCaffPath)) {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }

                CaffParseResult caffParseResult = null;
                try {
                    caffParseResult = ParserController.parse(caffFile.getCaffPath(storageFolder.getStorageFolder()));
                } catch (URISyntaxException e) {
                    logger.error("Could not get caff");
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                assert caffParseResult != null;

                return new ResponseEntity<>(caffFile, HttpStatus.OK);
            }
        } catch (IOException | NoSuchAlgorithmException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (URISyntaxException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean save(MultipartFile file, Path filePath) {
        try {
            if(!Files.exists(filePath.getParent())) {
                try {
                    Files.createDirectories(filePath.getParent());
                } catch (IOException e) {
                    logger.error("Error while creating parent directories: "+e.getMessage());
                    return false;
                }
            }
            Files.copy(file.getInputStream(), filePath);
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                logger.error(filePath+": a file of that name already exists.");
            } else {
                logger.error("Unknown error while saving caff file.");
                logger.error(e.getMessage());
            }

            return false;
        }
        return true;
    }

    /*
    Comment
     */
    @RequestMapping(value = "/comment",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaffComment> addCommentToCaffFile(@RequestParam("file_id") Integer file_id,
                                                            @RequestParam("comment") String comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = inMemoryUserDetailsManager.loadUserByUsername(authentication.getName());
        Optional<CaffFile> caffFile = caffFileRepository.findById(file_id);
        if (caffFile.isPresent()) {
            CaffComment caffComment = new CaffComment(caffFile.get(), userDetails.getUsername(), comment);
            return new ResponseEntity<>(caffCommentRepository.saveAndFlush(caffComment), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler
    public ResponseEntity<?> blockAllExceptions(Exception exception) {
        exception.printStackTrace();
        logger.debug(exception.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
