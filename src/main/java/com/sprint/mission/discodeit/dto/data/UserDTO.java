package com.sprint.mission.discodeit.dto.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private UUID profileId;
    private Boolean isLoggedIn;

    @Override
    public String toString() {
        return "[UserDTO] {" + name + " id=" + id + ", email=" + email + ", loggedIn=" + isLoggedIn + "}";
    }
}

