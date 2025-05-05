package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(UserCreateRequest userCreateRequest, BinaryContentCreateRequest profileImage);
    UserDTO getUser(UUID id);
    List<UserDTO> getAllUsers();
    User getUserByName(String name);
    BinaryContent updateUserProfileImage(UserUpdateRequest userUpdateRequest);
    void deleteUser(UUID id);
    boolean existsById(UUID id); // JavaApplicationd에서 사용됨
}
