package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserDto create(UserCreateRequest request, Optional<BinaryContentCreateRequest> profileRequest);
    UserDto update(UserUpdateRequest request);
    UserDto findById(UUID userId);
    List<UserDto> findAll();
    void deleteById(UUID userId);

//    User createUser(User user);
//    Optional<User> getUser(UUID userId);
//    List<User> getAllUsers();
//    void updateUser(User user);
//    void deleteUser(UUID userId);
}