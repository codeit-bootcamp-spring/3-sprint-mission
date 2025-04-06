package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

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
public interface UserService {

    UUID registerUser(String username);
    User findUserById(UUID userId);
    List<User> findAllUsers();
    void updateUsername(UUID userId,String newName);
    void deleteUser(UUID userId);
    void addChannel(UUID userId, UUID channelId);
    List<UUID> findChannelIdsById(UUID userId);
}
