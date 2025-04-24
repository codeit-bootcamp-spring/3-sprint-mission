package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Dto.UserCreateDto;
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

    // username, email unique check
    // UserStatus 생성
    User createUser(UserCreateDto userCreateDto);

    // username, email unique check
    // BinaryContent 생성
    // result에 BinaryContent의 UUID 저장
    // UserStatus 생성
//    User createUser(String username, String email, String password, byte[] image);

    User findUserById(UUID userId);
    List<User> findAllUsers();
    void updateUser(UUID userId, String name);
    void deleteUser(UUID userId);

}
