package com.sprint.mission.discodeit.v1.service;

import com.sprint.mission.discodeit.v1.entity.User1;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service
 * fileName       : UserService
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    : interface of uesr service
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */
public interface UserService1 {

    UUID registerUser(String username);
    User1 findUserById(UUID userId);
    List<User1> findAllUsers();
    void updateUsername(UUID userId,String newName);
    void deleteUser(UUID userId);
    void addChannelInUser(UUID userId, UUID channelId);
    List<UUID> findChannelIdsInId(UUID userId);
    void removeChannelIdInUsers(UUID channelId);
}
