package hu.bme.crysys.server.server.controller;

import java.util.Objects;

import hu.bme.crysys.server.server.domain.database.UserData;
import hu.bme.crysys.server.server.domain.security.PasswordConfiguration;
import hu.bme.crysys.server.server.repository.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static hu.bme.crysys.server.server.domain.security.ApplicationUserRole.USER;

@RestController
@RequestMapping("/user")
public class UserController {
    //@Autowired
    //private UserDataRepository userDataRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private InMemoryUserDetailsManager inMemoryUserDetailsManager;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @RequestMapping(value = {"/ami_admin"}, method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> amiAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            authentication.getAuthorities();
            return new ResponseEntity<>(Boolean.TRUE.toString(), HttpStatus.OK);
        } else {
            // TODO true only for debugging purposes
            return new ResponseEntity<>(Boolean.TRUE.toString(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = {"/ami_logged_in"}, method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> amiLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return new ResponseEntity<>(Boolean.TRUE.toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE.toString(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = {"/current_user"}, method = RequestMethod.GET)
    public ResponseEntity<String> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return new ResponseEntity<>(authentication.getName(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("anonymous", HttpStatus.OK);
        }
    }

    @GetMapping(value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody @Validated UserData userData) {
        // TODO Check if user already logged in! Should send an error back if user is logged in already
        // TODO check password requirements here as well (already checked once on client side, but needs to be checked here as well)
        // requirements: minLength: 10, minLowercase: 1, minUppercase: 1, minNumbers: 1, minSymbols: 1
        String password = userData.getPassword();
        String username = userData.getUserName();
        if (password != null && username != null) {
            if (!inMemoryUserDetailsManager.userExists(username)) {
                logger.info(String.valueOf(inMemoryUserDetailsManager.userExists(username)));
                UserDetails userDetails = User
                        .withUsername(username)
                        .password(passwordEncoder.encode(password))
                        .roles(USER.name())
                        .build();
                inMemoryUserDetailsManager.createUser(userDetails);
                logger.info(String.valueOf(inMemoryUserDetailsManager.userExists(username)));
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*@GetMapping(value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loginUser(@RequestBody @Validated UserData userData) {
        // TODO Check if user already logged in! Should send an error back if user is logged in already
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
    }*/
}
