package com.sprint.mission.discodeit.refactor.repository;

import com.sprint.mission.discodeit.refactor.entity.User2;

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
public interface UserRepository2 {
    User2 createUserByName(String name);
    User2 findUserById(UUID userId);
    List<User2> findAllUsers();
    void updateUserById(UUID userId, String name);
    void deleteUserById(UUID userId);
}
