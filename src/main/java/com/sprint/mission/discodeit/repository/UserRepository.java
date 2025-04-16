package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository
 * fileName       : UserRepository
 * author         : doungukkim
 * date           : 2025. 4. 16.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 16.        doungukkim       최초 생성
 */
public interface UserRepository {
    UUID saveUser(String username);
    User findUserById(UUID userId);
    List<User> findAllUsers();
    void updateUsernameByIdAndName(UUID userId, String newName);
    void deleteUserById(UUID userId);
    void addChannelInUserByIdAndChannelId(UUID userId, UUID channelId);
    List<UUID> findChannelIdsInId(UUID userId);
    void deleteChannelIdInUser(UUID channelId, UUID userId);
}
