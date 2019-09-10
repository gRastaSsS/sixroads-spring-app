package com.fluffytiger.earlygamewebapp.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserSession {
    public boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails;
    }

    public String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal()).getUsername();
        }

        else return null;
    }
}
