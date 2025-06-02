package com.sprint.mission.discodeit.dto.user;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user fileName       :
 * UserEmailOrNameUpdateRequest author         : doungukkim date           : 2025. 5. 15.
 * description    : =========================================================== DATE AUTHOR
 * NOTE ----------------------------------------------------------- 2025. 5. 15. doungukkim
 * 최초 생성
 */
public record UserUpdateRequest(String newUsername, String newEmail, String newPassword) {
}
