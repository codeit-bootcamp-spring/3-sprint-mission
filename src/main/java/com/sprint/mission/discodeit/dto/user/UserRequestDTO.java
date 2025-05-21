package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;

public record UserRequestDTO(String name, String email, String password, String introduction) {

    public static User fromDTO(UserRequestDTO userRequestDTO) {
        User user = new User(userRequestDTO.name(), userRequestDTO.email(),
                userRequestDTO.password(), userRequestDTO.introduction());

        return user;
    }
}
