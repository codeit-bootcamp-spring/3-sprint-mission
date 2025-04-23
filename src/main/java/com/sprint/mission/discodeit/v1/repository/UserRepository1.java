package com.sprint.mission.discodeit.v1.repository;

import com.sprint.mission.discodeit.v1.entity.User1;

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
public interface UserRepository1 {
    UUID saveUser(String username);
    User1 findUserById(UUID userId);
    List<User1> findAllUsers();
    void updateUsernameByIdAndName(UUID userId, String newName);
    void deleteUserById(UUID userId);
    void addChannelInUserByIdAndChannelId(UUID userId, UUID channelId);
    List<UUID> findChannelIdsInId(UUID userId);
    void deleteChannelIdInUser(UUID channelId, UUID userId);
}
