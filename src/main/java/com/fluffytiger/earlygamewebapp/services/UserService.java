package com.fluffytiger.earlygamewebapp.services;

import com.fluffytiger.earlygamewebapp.exceptions.CustomException;
import com.fluffytiger.earlygamewebapp.model.Role;
import com.fluffytiger.earlygamewebapp.model.User;
import com.fluffytiger.earlygamewebapp.payload.SignupRequest;
import com.fluffytiger.earlygamewebapp.repositories.UserRepository;
import com.fluffytiger.earlygamewebapp.security.JWTTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JWTTokenProvider provider;
    private final AuthenticationManager auth;

    private final EnumSet<Role> CLIENT = EnumSet.of(Role.ROLE_CLIENT);

    public UserService(UserRepository users, PasswordEncoder encoder, JWTTokenProvider provider, AuthenticationManager auth) {
        this.users = users;
        this.encoder = encoder;
        this.provider = provider;
        this.auth = auth;
    }

    public Iterable<User> list() {
        return users.findAll();
    }

    public void delete(long id) {
        this.users.deleteById(id);
    }

    public User authenticateInGame(SignupRequest data) {
        if (!users.existsByUsername(data.getUsername())) {
            User user = new User();
            user.setUsername(data.getUsername());
            user.setPassword(encoder.encode(data.getPassword()));
            user.setRoles(CLIENT);
            users.save(user);
            return user;

        } else {
            try {
                this.auth.authenticate(new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword()));
                return this.users.findByUsername(data.getUsername());

            } catch (AuthenticationException e) {
                throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
    }

    public User signin(String username, String password) {
        try {
            Authentication auth = this.auth.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(auth);
            return this.users.findByUsername(username);
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public void add(User user) {
        if (!users.existsByUsername(user.getUsername())) {
            user.setPassword(encoder.encode(user.getPassword()));
            users.save(user);

        } else {
            throw new CustomException("User exists!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public User signup(SignupRequest data) {
        if (!users.existsByUsername(data.getUsername())) {
            User user = new User();
            user.setUsername(data.getUsername());
            user.setPassword(encoder.encode(data.getPassword()));
            user.setRoles(CLIENT);
            users.save(user);
            return user;

        } else {
            throw new CustomException("User exists!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public Optional<User> getById(long id) {
        return users.findById(id);
    }

    public User getByUsername(String username) {
        return users.findByUsername(username);
    }

    public String createToken(User user) {
        return provider.create(user.getUsername(), user.getRoles());
    }
}
