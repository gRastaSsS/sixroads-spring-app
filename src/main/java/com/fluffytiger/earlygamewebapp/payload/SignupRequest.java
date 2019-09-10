package com.fluffytiger.earlygamewebapp.payload;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignupRequest {
    @Getter @Setter
    private String username;
    @Getter @Setter
    private String password;
}
