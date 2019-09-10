package com.fluffytiger.earlygamewebapp.services;

import com.fluffytiger.earlygamewebapp.model.User;
import com.fluffytiger.earlygamewebapp.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository users;

    public UserDetailsServiceImpl(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = users.findByUsername(username);

        if (user == null)
            throw new UsernameNotFoundException(username);

        return new org.springframework.security.core.userdetails.User (
                user.getUsername(), user.getPassword(), user.getRoles()
        );
    }
}
