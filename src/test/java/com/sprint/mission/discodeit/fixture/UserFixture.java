package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;

public class UserFixture {

    // UserDto 생성
    public static UserDto dto(String nickname, String email, BinaryContentDto profile, Role role) {
        return new UserDto(
            UUID.randomUUID(),
            nickname,
            email,
            profile,
            true,
            role
        );
    }

    // 유저 엔티티 생성
    public static User entity(String nickname, String email, BinaryContent binaryContent) {
        return new User(
            nickname,
            email,
            "password123!",
            binaryContent
        );
    }

    public static UserCreateRequest createRequest(String username, String email) {
        return new UserCreateRequest(username, email, "password123!");
    }

    // 유저 + 권한까지 설정
    public static User entityRole(String nickname, String email, Role role) {
        User user = new User(nickname, email, "password123!", null);
        user.updateRole(role);
        return user;
    }

    // 쪼쪼 유저 생성(프로필x)
    // UserDto 생성
    public static UserDto dtoZzo() {
        return new UserDto(
            UUID.randomUUID(),
            "쪼쪼",
            "zzo@email.com",
            null,
            true,
            Role.USER
        );
    }

    // 유저 엔티티 생성
    public static User entityZzo() {
        return new User(
            "쪼쪼",
            "zzo@email.com",
            "password123!",
            null
        );
    }

    public static UserCreateRequest createRequestZzo() {
        return new UserCreateRequest(
            "쪼쪼",
            "zzo@email.com",
            "password123!"
        );
    }

}
