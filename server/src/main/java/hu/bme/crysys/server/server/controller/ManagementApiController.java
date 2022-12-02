package hu.bme.crysys.server.server.controller;

import hu.bme.crysys.server.server.domain.database.CaffFile;
import hu.bme.crysys.server.server.domain.database.UserData;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static hu.bme.crysys.server.server.domain.security.ApplicationUserRole.ADMIN;

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
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaffFile> deleteCaffFile(@PathVariable("id") Integer id) {
        Optional<CaffFile> toBeDeletedCaffFile = caffFileRepository.findById(id);
        if (toBeDeletedCaffFile.isPresent()) {
            caffCommentRepository.deleteAll(toBeDeletedCaffFile.get().getComments());
            caffFileRepository.delete(toBeDeletedCaffFile.get());
            return new ResponseEntity<>(toBeDeletedCaffFile.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/delete/user/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserData> deleteUserData(@PathVariable("id") Integer id) {
        Optional<UserData> toBeDeletedUser = userDataRepository.findById(id);
        if (toBeDeletedUser.isPresent()
                && !toBeDeletedUser.get().getUserName().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            for (var caffFile : toBeDeletedUser.get().getOwnFiles()) {
                caffCommentRepository.deleteAll(caffFile.getComments());
                caffFileRepository.delete(caffFile);
            }
            userDataRepository.delete(toBeDeletedUser.get());
            return new ResponseEntity<>(toBeDeletedUser.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/modify/user")
    public ResponseEntity<?> modifyUserPassword(@RequestBody @Validated UserData toBeModifiedUserData) {
        UserDetails toBeModifiedUserDetails = inMemoryUserDetailsManager.loadUserByUsername(toBeModifiedUserData.getUserName());

        String password = toBeModifiedUserDetails.getPassword();
        if (password != null) {
            String specialChars = "~`!@#$%^&*()-_=+\\|[{]};:'\",<.>/?";
            boolean numberPresent = false;
            boolean upperCasePresent = false;
            boolean lowerCasePresent = false;
            boolean specialCharacterPresent = false;
            for (int i = 0; i < password.length(); i++) {
                char currentCharacter = password.charAt(i);
                if (Character.isDigit(currentCharacter)) {
                    numberPresent = true;
                } else if (Character.isUpperCase(currentCharacter)) {
                    upperCasePresent = true;
                } else if (Character.isLowerCase(currentCharacter)) {
                    lowerCasePresent = true;
                } else if (specialChars.contains(String.valueOf(currentCharacter))) {
                    specialCharacterPresent = true;
                }
            }
            if (!numberPresent || !upperCasePresent || !lowerCasePresent || !specialCharacterPresent) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        inMemoryUserDetailsManager.updatePassword(toBeModifiedUserDetails, password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/users",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllUsers() {
        List<JSONObject> users = new ArrayList<>();
        for (var user : userDataRepository.findAll()) {
            Collection<? extends GrantedAuthority> auths =
                    inMemoryUserDetailsManager.loadUserByUsername(user.getUserName()).getAuthorities();
            if (!auths.toString().contains(ADMIN.name())) { // TODO check
                JSONObject innerJson = new JSONObject();
                innerJson.put("id", user.getId());
                innerJson.put("username", user.getUserName());
                users.add(innerJson);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("users", users);
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<?> blockAllExceptions(Exception exception) {
        exception.printStackTrace();
        logger.debug(exception.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
