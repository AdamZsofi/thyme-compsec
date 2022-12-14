package hu.bme.crysys.server.server.controller;

import hu.bme.crysys.server.server.domain.database.UserData;
import hu.bme.crysys.server.server.repository.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static hu.bme.crysys.server.server.domain.security.ApplicationUserRole.USER;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private InMemoryUserDetailsManager inMemoryUserDetailsManager;
    @Autowired
    private UserDataRepository userDataRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = {"/ami_admin"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> amiAdmin() {
        return new ResponseEntity<>(Boolean.TRUE.toString(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = {"/ami_logged_in"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> amiLoggedIn() {
        return new ResponseEntity<>(Boolean.TRUE.toString(), HttpStatus.OK);
    }

    @PostMapping(value = "/register",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerUser(@RequestParam("username") String username, @RequestParam("password") String password) {
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

            if (username != null) {
                if (!inMemoryUserDetailsManager.userExists(username)) {
                    UserDetails userDetails = User
                            .withUsername(username)
                            .password(passwordEncoder.encode(password))
                            .roles(USER.name())
                            .build();
                    inMemoryUserDetailsManager.createUser(userDetails);
                    UserData userData = new UserData(username);
                    userDataRepository.saveAndFlush(userData);
                    return new ResponseEntity<>(HttpStatus.OK);
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

    @ExceptionHandler
    public ResponseEntity<?> blockAllExceptions(Exception exception) {
        exception.printStackTrace();
        if(exception.getMessage().equals("Access is denied")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
