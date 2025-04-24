package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Dto.user.UserCreateDto;
import com.sprint.mission.discodeit.Dto.user.UserFindDto;
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

    User createUser(UserCreateDto userCreateDto);
    UserFindDto findUserById(UUID userId);
    List<UserFindDto> findAllUsers();
    void updateUser(UUID userId, String name);
    void deleteUser(UUID userId);

}
