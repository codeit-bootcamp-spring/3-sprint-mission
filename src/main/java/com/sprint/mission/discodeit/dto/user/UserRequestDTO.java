package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import lombok.Getter;

@Getter
public class UserRequestDTO {
    private String name;
    private String email;
    private String password;
    private String introduction;

    public UserRequestDTO(String name, String email, String password, String introduction) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.introduction = introduction;
    }

    /**
     * UserRequestDTO -> User
     * @param userRequestDTO
     * @return User 객체
     */
    public static User toEntity(UserRequestDTO userRequestDTO) {
        User user = new User();

        user.updateName(userRequestDTO.getName());
        user.updateEmail(userRequestDTO.getEmail());
        user.updatePassword(userRequestDTO.getPassword());
        user.updateIntroduction(userRequestDTO.getIntroduction());

        return user;
    }
}
