package com.example.demo.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        "/login",
                        "/register",
                        "/css/**"
                ).permitAll()

                .anyRequest().authenticated()
            )

            .formLogin(form -> form

                .loginPage("/login")

                .loginProcessingUrl("/login")

                .defaultSuccessUrl("/login-success", true)

                .permitAll()
            )

            .logout(logout -> logout

                .logoutSuccessUrl("/login?logout")

                .permitAll()
            );

        return http.build();
    }
}