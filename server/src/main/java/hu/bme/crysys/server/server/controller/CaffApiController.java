package hu.bme.crysys.server.server.controller;

import hu.bme.crysys.server.server.domain.database.CaffComment;
import hu.bme.crysys.server.server.domain.database.CaffFile;
import hu.bme.crysys.server.server.domain.database.UserData;
import hu.bme.crysys.server.server.domain.parser.CaffParseResult;
import hu.bme.crysys.server.server.domain.parser.ParserController;
import hu.bme.crysys.server.server.repository.CaffCommentRepository;
import hu.bme.crysys.server.server.repository.CaffFileRepository;
import hu.bme.crysys.server.server.repository.UserDataRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // TODO search

    // TODO search result - I'll send a keyword (in whatever format you want) and I need back a list of caffs (similarly to /caff) as search results

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
            
            String path = String.valueOf(Path.of(caffFile.getPath(), caffFile.getFileName() + ".caff"));
            path = Objects.requireNonNull(this.getClass().getClassLoader().getResource(path)).getPath();
            CaffParseResult caffParseResult = ParserController.parse(path, caffFile.getId().toString());
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

            String path = String.valueOf(Path.of(caffFile.get().getPath(), caffFile.get().getFileName() + ".caff"));
            path = Objects.requireNonNull(this.getClass().getClassLoader().getResource(path)).getPath();
            CaffParseResult caffParseResult = ParserController.parse(path, id.toString());
            assert caffParseResult != null;
            innerJson.put("tags", caffParseResult.getTags().toString());

            return new ResponseEntity<>(innerJson.toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/caff/preview/{id}")
    public ResponseEntity<File> getCaffFilePicById(@PathVariable Integer id) {
        Optional<CaffFile> caffFile = caffFileRepository.findById(id);
        if (caffFile.isPresent()) {
            String path = String.valueOf(Path.of(caffFile.get().getPath(), caffFile.get().getFileName() + ".caff"));
            path = Objects.requireNonNull(this.getClass().getClassLoader().getResource(path)).getPath();
            CaffParseResult caffParseResult = ParserController.parse(path, id.toString());
            assert caffParseResult != null;
            String fileName = caffParseResult.ciffList.get(0).fileName;
            File file = new File(fileName);
            return new ResponseEntity<>(file, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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

    // TODO by -> response true

    /*
    Downloading
     */
    @GetMapping(value = "/download/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> downloadCaffFile(@PathVariable Integer id) {
        Optional<CaffFile> caffFile = caffFileRepository.findById(id);
        if (caffFile.isPresent()) {
            if (true) { // TODO userData.getDownloadableFiles().contains(caffFile.get()) // get userData from session
                try {
                    String path = String.valueOf(Paths.get(caffFile.get().getPath(), caffFile.get().getFileName() + ".caff"));
                    String file = Arrays.toString(Files.readAllBytes(Path.of(Objects.requireNonNull(this.getClass().getClassLoader().getResource(path)).toURI())));
                    return new ResponseEntity<>(file, HttpStatus.OK);
                } catch (IOException | URISyntaxException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
    Uploading
     */
    @RequestMapping(value = "/upload",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaffFile> uploadCaffFile(@RequestParam("file") MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = inMemoryUserDetailsManager.loadUserByUsername(authentication.getName());
        try {
            // TODO check if empty
            String toBeHashed = Arrays.toString(file.getBytes())
                    + userDetails.getUsername()
                    + userDetails.getPassword();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            BigInteger number = new BigInteger(1, md.digest(toBeHashed.getBytes()));
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 64) { hexString.insert(0, '0'); }
            String hash = hexString.toString();

            UserData userData = userDataRepository.findUserDataByUsername(userDetails.getUsername());
            CaffFile caffFile = new CaffFile(file.getName(), userData, hash);
            caffFile = caffFileRepository.saveAndFlush(caffFile);
            Files.createFile(Path.of(caffFile.getPath()));
            return new ResponseEntity<>(caffFile, HttpStatus.OK);
        } catch (IOException | NoSuchAlgorithmException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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
}
