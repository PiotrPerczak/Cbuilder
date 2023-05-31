package com.mokke.componentbuilder.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.mokke.componentbuilder.service.UserService;

@Configuration
public class SecurityConfig {

    //bcrypt bean definition
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //authenticationProvider bean definition
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService); //set the custom user details service
        auth.setPasswordEncoder(passwordEncoder()); //set the password encoder - bcrypt
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests(configurer -> 
            configurer
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() 
                .requestMatchers("/register/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/authenticate/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(configurer -> 
                configurer.accessDeniedPage("/access-denied"))
            .formLogin(form -> 
                form
                    .loginPage("/login-page")
                    .loginProcessingUrl("/authenticate")
                    .defaultSuccessUrl("/", true)
                    .permitAll()
            )
            .logout(logout -> logout.permitAll());
        http.csrf().disable();
        return http.build();
    }
}
