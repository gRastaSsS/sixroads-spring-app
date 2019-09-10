package com.fluffytiger.earlygamewebapp.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class SigninRequest {
    @Getter @Setter
    private String username;
    @Getter @Setter
    private String password;
}
