package com.fluffytiger.earlygamewebapp.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class SignupResponse {
    @Getter @Setter
    private String accessToken;
    @Getter @Setter
    private long id;
    @Getter @Setter
    private String username;
}
