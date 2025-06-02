package com.sprint.mission.discodeit.dto.user;

public record UserUpdateDTO(String newUsername, String newEmail, String newPassword,
                            String newIntroduction) {

}
