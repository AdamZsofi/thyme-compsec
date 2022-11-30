package hu.bme.crysys.server.server.controller;

import java.util.Objects;

import hu.bme.crysys.server.server.domain.database.UserData;
import hu.bme.crysys.server.server.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserDataRepository userDataRepository;

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
    public ResponseEntity<UserData> registerUser(@RequestBody @Validated UserData userData) {
        // TODO Check if user already logged in! Should send an error back if user is logged in already
        // TODO check password requirements here as well (already checked once on client side, but needs to be checked here as well)
        // requirements: minLength: 10, minLowercase: 1, minUppercase: 1, minNumbers: 1, minSymbols: 1
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

    @GetMapping(value = "/login",
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
    }
}
