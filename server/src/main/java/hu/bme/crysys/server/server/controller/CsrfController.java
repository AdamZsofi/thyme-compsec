package hu.bme.crysys.server.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    @RequestMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    @ExceptionHandler
    public ResponseEntity<?> blockAllExceptions(Exception exception) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
