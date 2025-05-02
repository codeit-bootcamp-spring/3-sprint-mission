package com.sprint.mission.discodeit.Dto.user;

import lombok.Getter;

import java.util.Objects;

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
public class UserCreateRequest {
    private String username;
    private String email;
    private String password;
    private byte[] image;

    // 일반 생성
    public UserCreateRequest(String username, String email, String password) {
        this.username = Objects.requireNonNull(username, "no username in request");
        this.email = Objects.requireNonNull(email, "no email in request");
        this.password = Objects.requireNonNull(password, "no password in request");
    }
    // 프로필 이미지와 같이 생성
    public UserCreateRequest(String username, String email, String password, byte[] image) {
        this.username = Objects.requireNonNull(username, "no username in request");
        this.email = Objects.requireNonNull(email, "no email in request");
        this.password = Objects.requireNonNull(password, "no password in request");
        this.image = image;
    }
}

