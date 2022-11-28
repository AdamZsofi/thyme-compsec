package hu.bme.crysys.server.server.controller;

import hu.bme.crysys.server.server.domain.database.CaffFile;
import hu.bme.crysys.server.server.domain.database.UserData;
import hu.bme.crysys.server.server.repository.CaffCommentRepository;
import hu.bme.crysys.server.server.repository.CaffFileRepository;
import hu.bme.crysys.server.server.repository.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/management")
public class ManagementApiController {
    private static final Logger logger = LoggerFactory.getLogger(CaffApiController.class);
    @Autowired
    private CaffFileRepository caffFileRepository;
    @Autowired
    private CaffCommentRepository caffCommentRepository;
    @Autowired
    private UserDataRepository userDataRepository;

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
    catch all errors
     */
    @ExceptionHandler
    public ResponseEntity<?> blockAllExceptions(Exception exception) {
        logger.debug(exception.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
