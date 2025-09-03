package com.sprint.mission.discodeit.auth.dto;

import com.sprint.mission.discodeit.dto.response.UserResponse;

public class JwtDTO {

    private UserResponse user;
    private String accessToken;

    public JwtDTO() {
    }

    public JwtDTO(UserResponse user, String accessToken) {
        this.user = user;
        this.accessToken = accessToken;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "JwtDTO{" +
            "user=" + user +
            ", accessToken='" + accessToken + '\'' +
            '}';
    }
}


