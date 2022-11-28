package hu.bme.crysys.server.server.domain.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import static hu.bme.crysys.server.server.domain.security.ApplicationUserPermission.USER_DATA_WRITE;
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
        return http
                //.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/**").hasAnyRole(ADMIN.name(), USER.name())
                .antMatchers("/api/management/**").hasAuthority(USER_DATA_WRITE.name())
                .anyRequest()
                .authenticated()
                .and().formLogin()
                    .usernameParameter("username")
                    .passwordParameter("password")
                .and().rememberMe()
                    .tokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(2))
                    .rememberMeParameter("remember-me")
                .and().logout()
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                .and().build();
        /*http
                .antMatchers("/login/**")
                .anonymous()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);*/
    }

    @Bean
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
    }
}
