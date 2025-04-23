package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

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
public interface UserService {
    // input = null
    User createUser(String username, String email, String password);
    // input = null
    // 찾는 유저가 없다면?
    User findUserById(UUID userId);
    // 아무 유저도 없다면?
    List<User> findAllUsers();
    // input = null
    // 찾는 유저가 없다면?
    void updateUser(UUID userId, String name);
    // input = null
    // 삭제할 유저가 없다면?
    void deleteUser(UUID userId);

}
