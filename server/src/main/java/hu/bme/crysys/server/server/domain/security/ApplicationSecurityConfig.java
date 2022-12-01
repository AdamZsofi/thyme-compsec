package hu.bme.crysys.server.server.domain.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Base64;
import java.util.concurrent.TimeUnit;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static hu.bme.crysys.server.server.domain.security.ApplicationUserPermission.USER_DATA_WRITE;
import static hu.bme.crysys.server.server.domain.security.ApplicationUserRole.ADMIN;
import static hu.bme.crysys.server.server.domain.security.ApplicationUserRole.USER;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class ApplicationSecurityConfig {

    private final boolean QUICK_DEBUG = true;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (QUICK_DEBUG) {
            return http
                    .csrf().ignoringAntMatchers("/user/login")
                    .and().authorizeRequests()
                    .antMatchers("/api/**").hasAnyRole(ADMIN.name(), USER.name())
                    .antMatchers("/api/management/**").hasAuthority(USER_DATA_WRITE.name())
                    .anyRequest()
                    .authenticated()
                    .and().httpBasic()
                    .and().rememberMe()
                        .tokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(1))
                        .rememberMeParameter("remember-me")
                    .and().logout()
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                    .and().build();
        } else {
            return http
                    .csrf().ignoringAntMatchers("/user/login")
                    .and().authorizeRequests()
                    .antMatchers("/user/register").permitAll()
                    .antMatchers("/user/ami_logged_in").permitAll()
                    .antMatchers("/user/ami_admin").permitAll()
                    .antMatchers("/api/**").hasAnyRole(ADMIN.name(), USER.name())
                    .antMatchers("/api/management/**").hasAuthority(USER_DATA_WRITE.name())
                    .anyRequest()
                    .authenticated()
                    .and().formLogin()
                        .loginPage("/login")
                        .loginProcessingUrl("/user/login")
                        .successHandler((req, res, auth) -> res.setStatus(HttpStatus.NO_CONTENT.value()))
                        .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                        .usernameParameter("username")
                        .passwordParameter("password")
                    .and().rememberMe()
                        .tokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(2))
                        .rememberMeParameter("remember-me")
                    .and().logout()
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                    .and().exceptionHandling()
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                    .and().build();
        }
    }

    /*@Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user")
                .password(passwordEncoder.encode("userPass"))
                .roles(USER.name())
                .build());
        manager.createUser(User.withUsername("admin")
                .password(passwordEncoder.encode("adminPass"))
                .roles(ADMIN.name())
                .build());
        return manager;
    }*/

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user")
                .password(passwordEncoder.encode("userPass"))
                .roles(USER.name())
                .build());
        manager.createUser(User.withUsername("admin")
                .password(passwordEncoder.encode("adminPass"))
                .roles(ADMIN.name())
                .build());
        return manager;
    }
}
