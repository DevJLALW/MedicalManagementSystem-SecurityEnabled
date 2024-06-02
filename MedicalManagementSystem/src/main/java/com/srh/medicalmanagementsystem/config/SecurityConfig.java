package com.srh.medicalmanagementsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.sql.DataSource;

// This is where UserName, Passwords and roles are configured
@Configuration
public class SecurityConfig {

    // The DataSource instance is coming from the spring.datasource config in our application.properties file
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource){
        return new JdbcUserDetailsManager(dataSource); // Instructing spring security to get the users & authorities tables from the DB
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Define Authorization
        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers("/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/patients").hasAnyRole("DOCTOR")
        );

        // Use http basic authentication (i.e authentication with username & password) with default login form
        http.httpBasic(Customizer.withDefaults()).formLogin(Customizer.withDefaults());

        // Logout configuration
        http
           .logout(logout -> logout
                           .logoutUrl("/logout")
                           .addLogoutHandler(new SecurityContextLogoutHandler())
                            .invalidateHttpSession(true) // Invalidate HTTP session
                           .deleteCookies("JSESSIONID")
                    );

        http.csrf(csrf -> csrf.disable()); // If not disabled, post requests throw "Invalid CSRF token found"

        return http.build();
    }
}
