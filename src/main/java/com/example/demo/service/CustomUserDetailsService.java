package com.example.demo.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entite.*;
import com.example.demo.repo.Userrepo;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    @Autowired
    private Userrepo userrepo;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

      com.example.demo.entite.User user =
                userrepo.findByEmail(email);

        if (user == null) {

        	
            throw new UsernameNotFoundException(
                    "User not found");
            
        }
        if(!user.isActive()) {
            throw new UsernameNotFoundException(
                    "Doctor account is deactivated");
        }

        return new User(

                user.getEmail(),

                user.getPassword(),

                Collections.singletonList(

                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().name()
                        )
                )
        );
    }
}