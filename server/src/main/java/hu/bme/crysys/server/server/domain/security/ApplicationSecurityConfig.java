package hu.bme.crysys.server.server.domain.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.util.concurrent.TimeUnit;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;

import static hu.bme.crysys.server.server.domain.security.ApplicationUserRole.ADMIN;
import static hu.bme.crysys.server.server.domain.security.ApplicationUserRole.USER;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class ApplicationSecurityConfig {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.cors().configurationSource(request-> {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(List.of("https://localhost:3000"));
                        configuration.setAllowedMethods(List.of("GET","POST"));
                        configuration.setAllowedHeaders(List.of("*"));
                        configuration.setAllowCredentials(true);
                        return configuration;
                    }).and()
                .requiresChannel(channel -> channel.anyRequest().requiresSecure())
                .csrf().ignoringAntMatchers("/user/login", "/user/register")
                .and().authorizeRequests()
                .antMatchers("/user/register").permitAll()
                .antMatchers("/user/ami_logged_in").permitAll()
                .antMatchers("/user/ami_admin").permitAll()
                .antMatchers("/api/**").hasAnyRole(ADMIN.name(), USER.name())
                .anyRequest()
                .authenticated()
                .and().formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/user/login")
                    .successHandler((req, res, auth) -> {
                        res.setHeader("Access-Control-Allow-Origin", "https://localhost:3000");
                        res.setHeader("Access-Control-Allow-Headers", "https://localhost:3000");
                        res.setHeader("Access-Control-Allow-Credentials", "true");
                        res.setStatus(HttpStatus.NO_CONTENT.value());
                    })
                    .failureHandler((req, res, auth) -> {
                        res.setHeader("Access-Control-Allow-Origin", "https://localhost:3000");
                        res.setHeader("Access-Control-Allow-Headers", "https://localhost:3000");
                        res.setHeader("Access-Control-Allow-Credentials", "true");
                        res.setStatus(HttpStatus.UNAUTHORIZED.value());
                    })
                    .usernameParameter("username")
                    .passwordParameter("password")
                .and()
                .logout()
                    .logoutUrl("/user/logout")
                    .addLogoutHandler(corsLogoutHandler())
                    .logoutSuccessHandler((request, response, authentication) -> {
                        response.setHeader("Access-Control-Allow-Origin", "https://localhost:3000");
                        response.setHeader("Access-Control-Allow-Headers", "https://localhost:3000");
                        response.setHeader("Access-Control-Allow-Credentials", "true");
                        response.setStatus(HttpStatus.NO_CONTENT.value());
                    })
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                .and().exceptionHandling()
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and().build();
    }

    @Bean
    public CorsLogoutHandler corsLogoutHandler() {
        return new CorsLogoutHandler();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setUseReferer(true);
        return handler;
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        String userPass = System.getenv("THYME_USER_PASS");
        if(userPass == null){
            throw new RuntimeException("THYME_USER_PASS needs to be set");
        }

        String adminPass = System.getenv("THYME_ADMIN_PASS");
        if(adminPass == null){
            throw new RuntimeException("THYME_ADMIN_PASS needs to be set");
        }

        manager.createUser(User.withUsername("user")
                .password(passwordEncoder.encode(userPass))
                .roles(USER.name())
                .build());
        manager.createUser(User.withUsername("admin")
                .password(passwordEncoder.encode(adminPass))
                .roles(ADMIN.name())
                .build());
        return manager;
    }
}
