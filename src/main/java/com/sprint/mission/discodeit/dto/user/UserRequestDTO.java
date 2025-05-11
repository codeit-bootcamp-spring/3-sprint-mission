package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;

public record UserRequestDTO(String name, String email, String password, String introduction) {

    public static User toEntity(UserRequestDTO userRequestDTO) {
        User user = new User();

        user.updateName(userRequestDTO.name());
        user.updateEmail(userRequestDTO.email());
        user.updatePassword(userRequestDTO.password());
        user.updateIntroduction(userRequestDTO.introduction());

        return user;
    }
}
