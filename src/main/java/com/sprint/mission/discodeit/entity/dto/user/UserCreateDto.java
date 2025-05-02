package com.sprint.mission.discodeit.entity.dto.user;

import com.sprint.mission.discodeit.entity.common.Period;
import lombok.Getter;
import lombok.ToString;

@ToString(exclude = "password")
@Getter
public class UserCreateDto extends Period {

    private String username;
    private String password;
    private byte[] profileImage;

    public UserCreateDto(String password, String username, byte[] profileImage) {
        this.password = password;
        this.profileImage = profileImage;
        this.username = username;
    }

    public UserCreateDto(String password, String username) {
        this.password = password;
        this.username = username;
    }
}
