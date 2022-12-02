package hu.bme.crysys.server.server.controller;

import hu.bme.crysys.server.server.domain.database.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = {"/ami_admin"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> amiAdmin() {
        return new ResponseEntity<>(Boolean.TRUE.toString(), HttpStatus.OK);
    }

    @GetMapping(value = {"/ami_logged_in"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> amiLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            return new ResponseEntity<>(Boolean.TRUE.toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE.toString(), HttpStatus.OK);
        }
    }

    @GetMapping(value = {"/current_user"})
    public ResponseEntity<String> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return new ResponseEntity<>(authentication.getName(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("anonymous", HttpStatus.OK);
        }
    }

    //@PreAuthorize("!isAuthenticated()")
    @GetMapping(value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody @Validated UserData userData) {
        String password = userData.getPassword();
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

            String username = userData.getUserName();
            if (username != null) {
                if (!inMemoryUserDetailsManager.userExists(username)) {
                    UserDetails userDetails = User
                            .withUsername(username)
                            .password(passwordEncoder.encode(password))
                            .roles(USER.name())
                            .build();
                    inMemoryUserDetailsManager.createUser(userDetails);
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

    //@PreAuthorize("!isAuthenticated()") // TODO check
    @GetMapping(value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginUser(@RequestBody @Validated UserData userData) {
        String password = userData.getPassword();
        String username = userData.getUserName();
        if (password != null && username != null) {
            if (inMemoryUserDetailsManager.userExists(username)) {
                UserDetails userDetails = inMemoryUserDetailsManager.loadUserByUsername(username);
                if (userDetails.getPassword().equals(password)) {
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
}
