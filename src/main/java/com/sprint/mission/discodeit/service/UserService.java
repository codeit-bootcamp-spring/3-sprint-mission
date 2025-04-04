package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service
 * fileName       : UserService
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */
public interface UserService {

    UUID registerUser(String username);
    List<User> findUserById(UUID userId);
    List<User> findAllUsers();
    void updateUsername(UUID userId,String newName);
    void deleteUser(UUID userId);
}
