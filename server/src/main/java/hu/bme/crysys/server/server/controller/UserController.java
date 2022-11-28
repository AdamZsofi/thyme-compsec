package hu.bme.crysys.server.server.controller;

import java.util.Objects;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @RequestMapping(value = {"/ami_logged_in"}, method = RequestMethod.GET)
    public Boolean amiLoggedIn(@CookieValue(name="JSESSIONID", defaultValue = "") String sessionid) {
        return !sessionid.equals("");
    }
}
