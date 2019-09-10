package com.fluffytiger.earlygamewebapp.security;

import java.util.UUID;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864_000_000;
    public static final byte[] SECRET = UUID.randomUUID().toString().getBytes();
    public static final String TOKEN_PREFIX = "Bearer ";
}
