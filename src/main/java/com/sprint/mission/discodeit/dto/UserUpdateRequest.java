package com.sprint.mission.discodeit.dto;

// 수정할 값 파라미터
public record UserUpdateRequest(String newUsername, String newEmail, String newPassword) {

}

