package hu.bme.crysys.server.server.domain.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class CorsLogoutHandler implements LogoutHandler {
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        System.err.println("LOGOUUUUUUUUT");
        response.setHeader("Access-Control-Allow-Origin", "https://localhost:3000");
        response.setHeader("Access-Control-Allow-Headers", "https://localhost:3000");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
}