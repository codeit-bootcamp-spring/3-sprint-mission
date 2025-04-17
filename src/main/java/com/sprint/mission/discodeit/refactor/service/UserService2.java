package com.sprint.mission.discodeit.refactor.service;

import com.sprint.mission.discodeit.refactor.entity.Message2;
import com.sprint.mission.discodeit.refactor.entity.User2;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.service
 * fileName       : UserService2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public interface UserService2 {

    User2 createUser(String name);
    User2 findUserById(UUID userId);
    List<User2> findAllUsers();
    void updateUser(UUID userId, String name);
    void deleteUser(UUID userId);

}
