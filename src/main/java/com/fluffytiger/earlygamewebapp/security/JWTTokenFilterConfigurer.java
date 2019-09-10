package com.fluffytiger.earlygamewebapp.security;

import com.fluffytiger.earlygamewebapp.services.UserDetailsServiceImpl;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final UserDetailsServiceImpl userDetailsService;
    private final JWTTokenProvider provider;

    public JWTTokenFilterConfigurer(UserDetailsServiceImpl userDetailsService, JWTTokenProvider provider) {
        this.userDetailsService = userDetailsService;
        this.provider = provider;
    }

    @Override
    public void configure(HttpSecurity builder) {
        builder.addFilterBefore(new JWTTokenFilter(userDetailsService, provider), UsernamePasswordAuthenticationFilter.class);
    }
}
