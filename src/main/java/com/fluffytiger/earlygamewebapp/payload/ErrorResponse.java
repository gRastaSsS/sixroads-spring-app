package com.fluffytiger.earlygamewebapp.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ErrorResponse {
    @Getter @Setter
    private String error;
}
