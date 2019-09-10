package com.fluffytiger.earlygamewebapp.security;

import com.fluffytiger.earlygamewebapp.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

import static com.fluffytiger.earlygamewebapp.security.SecurityConstants.*;

@Component
public class JWTTokenProvider {
    private final String secretKey;

    public JWTTokenProvider() {
        this.secretKey = Base64.getEncoder().encodeToString(SECRET);
    }

    public String create(String username, Set<Role> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", new ArrayList<>(roles));

        Date now = new Date();
        Date until = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(until)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String resolve(HttpServletRequest req) {
        String bearer = req.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearer != null && bearer.startsWith(TOKEN_PREFIX)) {
            return bearer.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
