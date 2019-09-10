package com.fluffytiger.earlygamewebapp.security;

import com.fluffytiger.earlygamewebapp.services.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTTokenFilter extends OncePerRequestFilter {
    private final UserDetailsServiceImpl userDetailsService;
    private final JWTTokenProvider provider;

    public JWTTokenFilter(UserDetailsServiceImpl userDetailsService, JWTTokenProvider provider) {
        this.userDetailsService = userDetailsService;
        this.provider = provider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String token = provider.resolve(req);

        try {
            if (token != null && provider.validate(token)) {
                Authentication auth = getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (RuntimeException e) {
            SecurityContextHolder.clearContext();
            res.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        chain.doFilter(req, res);
    }

    private Authentication getAuthentication(String token) throws UsernameNotFoundException {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(provider.getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
