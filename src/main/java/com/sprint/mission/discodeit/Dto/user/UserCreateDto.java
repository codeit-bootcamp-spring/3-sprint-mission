package com.sprint.mission.discodeit.Dto.user;

import lombok.Getter;

/**
 * packageName    : com.sprint.mission.discodeit.Dto
 * fileName       : UserServiceDto
 * author         : doungukkim
 * date           : 2025. 4. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 24.        doungukkim       최초 생성
 */

@Getter
public class UserCreateDto {
    private String username;
    private String email;
    private String password;
    private byte[] image;

    public UserCreateDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public UserCreateDto(String username, String email, String password, byte[] image) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.image = image;
    }
}

