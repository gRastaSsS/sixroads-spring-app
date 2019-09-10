package com.fluffytiger.earlygamewebapp.security;

import com.fluffytiger.earlygamewebapp.model.Role;
import com.fluffytiger.earlygamewebapp.services.UserDetailsServiceImpl;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    @Order(1)
    public class ApiSecurity extends WebSecurityConfigurerAdapter {
        private final JWTTokenProvider provider;

        public ApiSecurity(JWTTokenProvider provider) {
            this.provider = provider;
        }

        @Bean
        public AuthenticationManager manager() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/**").authorizeRequests()
                    .antMatchers("/api/users/authenticate").permitAll()
                    .anyRequest().authenticated();

            http.apply(new JWTTokenFilterConfigurer(userDetailsService, provider))
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            http.csrf().disable();
        }
    }

    @Configuration
    @Profile("dev")
    public class TurnOffWebSecurity extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/**").permitAll();
        }
    }

    @Configuration
    @Profile("prod")
    public class WebSecurity extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .antMatchers("/").permitAll()
                        .antMatchers("/download/**").permitAll()
                        .antMatchers("/eula").permitAll()
                        .antMatchers("/admin/**").hasAuthority(Role.ROLE_ADMIN.getAuthority())
                        .anyRequest().authenticated()
                    .and()
                        .formLogin()
                        .loginPage("/login")
                        .permitAll()
                    .and()
                        .rememberMe()
                    .and()
                        .logout()
                        .permitAll();
        }
    }
}
