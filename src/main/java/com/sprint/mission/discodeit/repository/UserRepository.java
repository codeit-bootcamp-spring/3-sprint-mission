package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.repository.jcf
 * fileName       : UserRepository2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public interface UserRepository {
    User createUserByName(String username, String email, String password);
    User createUserByName(String username, String email, String password, UUID profileId);
    User findUserById(UUID userId);
    List<User> findAllUsers();
    void updateProfileIdById(UUID userId, UUID profileId);
    void updateUserById(UUID userId, String name);
    void deleteUserById(UUID userId);
    boolean isUniqueUsername(String username);
    boolean isUniqueEmail(String email);
}
