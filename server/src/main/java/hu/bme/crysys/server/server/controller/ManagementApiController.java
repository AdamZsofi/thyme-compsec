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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

import static hu.bme.crysys.server.server.domain.security.ApplicationUserRole.USER;

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
    @Autowired
    private InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/delete/file/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaffFile> deleteCaffFile(@PathVariable("id") Integer id,
                                                   @RequestBody @Validated UserData userData) {
        Integer userDataId = userData.getId();
        if (userDataId != null) {
            Optional<UserData> dataBaseUserData = userDataRepository.findById(userDataId);
            if (dataBaseUserData.isPresent()) {
                UserDetails databaseUserDetails = inMemoryUserDetailsManager.loadUserByUsername(dataBaseUserData.get().getUserName());
                UserDetails givenUserDetails = inMemoryUserDetailsManager.loadUserByUsername(userData.getUserName());
                if (databaseUserDetails.getPassword().equals(givenUserDetails.getPassword())) {
                    Optional<CaffFile> toBeDeletedCaffFile = caffFileRepository.findById(id);
                    if (toBeDeletedCaffFile.isPresent()) {
                        caffCommentRepository.deleteAll(toBeDeletedCaffFile.get().getComments());
                        caffFileRepository.delete(toBeDeletedCaffFile.get());
                        return new ResponseEntity<>(toBeDeletedCaffFile.get(), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/delete/user/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserData> deleteUserData(@PathVariable("id") Integer id,
                                                   @RequestBody @Validated UserData userData) {
        Integer userDataId = userData.getId();
        if (userDataId != null) {
            Optional<UserData> dataBaseUserData = userDataRepository.findById(userDataId);
            if (dataBaseUserData.isPresent()) {
                UserDetails databaseUserDetails = inMemoryUserDetailsManager.loadUserByUsername(dataBaseUserData.get().getUserName());
                UserDetails givenUserDetails = inMemoryUserDetailsManager.loadUserByUsername(userData.getUserName());
                if (databaseUserDetails.getPassword().equals(givenUserDetails.getPassword())) {
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
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // TODO get all usernames and their ids endpoint - ONLY simple users, don't send admins please (simple get, just so I can show a list of them)

    // TODO modify user password (instead of modify user name) - I'll send the same kind of user+pwd data as when registering, but with an existing username and a new password for it

    /*@PreAuthorize("hasRole('ROLE_ADMIN')")
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
                UserDetails databaseUserDetails = inMemoryUserDetailsManager.loadUserByUsername(dataBaseUserData.get().getUserName());
                UserDetails givenUserDetails = inMemoryUserDetailsManager.loadUserByUsername(userData.getUserName());
                if (databaseUserDetails.getPassword().equals(givenUserDetails.getPassword())) {
                    Optional<UserData> optionalToBeModifiedUserData = userDataRepository.findById(id);
                    if (optionalToBeModifiedUserData.isPresent()) {
                        //UserData toBeModifiedUser = optionalToBeModifiedUserData.get();
                        //toBeModifiedUser.setUserName(username);
                        UserDetails toBeModifiedUserDetails = inMemoryUserDetailsManager.loadUserByUsername(optionalToBeModifiedUserData.get().getUserName());
                        inMemoryUserDetailsManager.updatePassword(toBeModifiedUserDetails)
                        // fallback to merge
                        return new ResponseEntity<>(userDataRepository.saveAndFlush(toBeModifiedUser), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                } else {
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
    catch all errors
     */
    @ExceptionHandler
    public ResponseEntity<?> blockAllExceptions(Exception exception) {
        logger.debug(exception.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
