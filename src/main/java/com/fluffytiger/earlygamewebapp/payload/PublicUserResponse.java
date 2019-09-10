package com.fluffytiger.earlygamewebapp.payload;

import com.fluffytiger.earlygamewebapp.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class PublicUserResponse {
    @Getter @Setter
    private Long id;
    @Getter @Setter
    private String username;

    public PublicUserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }
}
