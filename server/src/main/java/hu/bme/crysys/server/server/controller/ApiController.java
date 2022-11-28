package hu.bme.crysys.server.server.controller;

import hu.bme.crysys.server.server.domain.database.CaffComment;
import hu.bme.crysys.server.server.domain.database.CaffFile;
import hu.bme.crysys.server.server.domain.database.UserData;
import hu.bme.crysys.server.server.repository.CaffCommentRepository;
import hu.bme.crysys.server.server.repository.CaffFileRepository;
import hu.bme.crysys.server.server.repository.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    @Autowired
    private CaffFileRepository caffFileRepository;
    @Autowired
    private CaffCommentRepository caffCommentRepository;
    @Autowired
    private UserDataRepository userDataRepository;

    /*
    Main page
     */
    @GetMapping(value = "/caff",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CaffFile>> getAllCaffFile() {
        logger.info("getAllCaffFile");
        // TODO what exactly to return?
        return new ResponseEntity<>(caffFileRepository.findAll(), HttpStatus.OK);
    }

    /*
    Individual CAFF files
     */
    @GetMapping(value = "/caff/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaffFile> getCaffFileById(@PathVariable Integer id) {
        logger.info("getCaffFileById " + id.toString());
        Optional<CaffFile> caffFile = caffFileRepository.findById(id);
        return caffFile
                .map(file -> new ResponseEntity<>(file, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping(value = "/caff/pic/{id}")
    public ResponseEntity<MultipartFile> getCaffFilePicById(@PathVariable Integer id) {
        logger.info("getCaffFilePicById " + id.toString());
        Optional<CaffFile> caffFile = caffFileRepository.findById(id);
        if (caffFile.isPresent()) {
            String path = caffFile.get().getPath();
            // TODO when parser finished
            logger.debug(path);
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
    Downloading
     */
    // download - we'll need another repository for that to know who can download what
    @GetMapping(value = "/buy/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCaffFileAccessRights(@PathVariable Integer id,
                                                          @RequestBody @Validated UserData userData) {
        Optional<CaffFile> caffFile = caffFileRepository.findById(id);
        if (caffFile.isPresent()) {
            if (userData.getDownloadableFiles().contains(caffFile.get())) {
                return new ResponseEntity<>("Accepted", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Declined", HttpStatus.OK);
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
    public ResponseEntity<CaffFile> uploadCaffFile(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("user_id") Integer user_id) {
        Optional<UserData> userData = userDataRepository.findById(user_id);
        if (userData.isPresent()) {
            try {
                // TODO where to save file?
                String toBeHashed = Arrays.toString(file.getBytes())
                        + userData.get().getUserName()
                        + userData.get().getId();
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                BigInteger number = new BigInteger(1, md.digest(toBeHashed.getBytes()));
                StringBuilder hexString = new StringBuilder(number.toString(16));
                while (hexString.length() < 64) { hexString.insert(0, '0'); }
                String hash = hexString.toString();

                Path path = Path.of(hash);
                if (Files.exists(path)) {
                    return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
                } else {
                    Files.createFile(path);
                    CaffFile caffFile = new CaffFile(path.toAbsolutePath().toString(), userData.get());
                    return new ResponseEntity<>(caffFileRepository.saveAndFlush(caffFile), HttpStatus.OK);
                }
            } catch (IOException ioException) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
    Comment
     */
    @RequestMapping(value = "/comment",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaffComment> addCommentToCaffFile(@RequestParam("user_id") Integer user_id,
                                                         @RequestParam("file_id") Integer file_id,
                                                         @RequestParam("comment") String comment) {
        Optional<UserData> userData = userDataRepository.findById(user_id);
        if (userData.isPresent()) {
            Optional<CaffFile> caffFile = caffFileRepository.findById(file_id);
            if (caffFile.isPresent()) {
                CaffComment caffComment = new CaffComment(caffFile.get(), userData.get().getUserName(), comment);
                return new ResponseEntity<>(caffCommentRepository.saveAndFlush(caffComment), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
    Admin
     */
    @RequestMapping(value = "/delete/file/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaffFile> deleteCaffFile(@PathVariable("id") Integer id,
                                                   @RequestBody @Validated UserData userData) {
        Integer userDataId = userData.getId();
        if (userDataId != null) {
            Optional<UserData> dataBaseUserData = userDataRepository.findById(userDataId);
            if (dataBaseUserData.isPresent()) {
                if (dataBaseUserData.get().equals(userData) && dataBaseUserData.get().getAdmin()) {
                    Optional<CaffFile> caffFile = caffFileRepository.findById(id);
                    if (caffFile.isPresent()) {
                        caffCommentRepository.deleteAll(caffFile.get().getComments());
                        caffFileRepository.delete(caffFile.get());
                        return new ResponseEntity<>(caffFile.get(), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/delete/user/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserData> deleteUserData(@PathVariable("id") Integer id,
                                                   @RequestBody @Validated UserData userData) {
        Integer userDataId = userData.getId();
        if (userDataId != null) {
            Optional<UserData> dataBaseUserData = userDataRepository.findById(userDataId);
            if (dataBaseUserData.isPresent()) {
                if (dataBaseUserData.get().equals(userData) && dataBaseUserData.get().getAdmin()) {
                    Optional<UserData> toBeDeletedUser = userDataRepository.findById(id);
                    if (toBeDeletedUser.isPresent() && !toBeDeletedUser.get().equals(userData)) {
                        for (var caffFile : toBeDeletedUser.get().getOwnFiles()) {
                            caffCommentRepository.deleteAll(caffFile.getComments());
                            caffFileRepository.delete(caffFile);
                        }
                        userDataRepository.delete(toBeDeletedUser.get());
                        return new ResponseEntity<>(toBeDeletedUser.get(), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/modify/user/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserData> modifyUserName(@PathVariable("id") Integer id,
                                                   @RequestParam("username") String username,
                                                   @RequestBody @Validated UserData userData) {
        Integer userDataId = userData.getId();
        if (userDataId != null) {
            Optional<UserData> dataBaseUserData = userDataRepository.findById(userDataId);
            if (dataBaseUserData.isPresent()) {
                if (dataBaseUserData.get().equals(userData) && dataBaseUserData.get().getAdmin()) {
                    Optional<UserData> optionalUserData = userDataRepository.findById(id);
                    if (optionalUserData.isPresent()) {
                        UserData toBeModifiedUser = optionalUserData.get();
                        toBeModifiedUser.setUserName(username);
                        // fallback to merge
                        return new ResponseEntity<>(userDataRepository.saveAndFlush(toBeModifiedUser), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
    Session
     */
    // TODO

    /*
    Login & register
     */
    @GetMapping(value = "/user/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loginUser(@RequestBody @Validated UserData userData) {
        String password = userData.getPassword();
        String username = userData.getUserName();
        if (password != null && username != null) {
            UserData existingUserData = userDataRepository.findUserDataByUsername(username);
            if (existingUserData.getPassword().equals(userData.getPassword())){
                return new ResponseEntity<>("ok", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/user/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserData> registerUser(@RequestBody @Validated UserData userData) {
        String password = userData.getPassword();
        String username = userData.getUserName();
        if (password != null && username != null) {
            UserData existingUserData = userDataRepository.findUserDataByUsername(username);
            if (existingUserData == null) {
                return new ResponseEntity<>(userDataRepository.saveAndFlush(userData), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
    catch all errors
     */
    @ExceptionHandler
    public ResponseEntity<?> blockAllExceptions(Exception exception) {
        logger.debug(exception.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
