package com.sprint.mission.discodeit.dto.auth;

import lombok.Getter;

@Getter
public class LoginDTO {
    private String name;
    private String password;

    public LoginDTO(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
