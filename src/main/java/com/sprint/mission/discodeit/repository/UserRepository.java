package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.entity.dto.user.UserUpdateRequestDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    public User createUser(UserCreateDto userCreateDto);

    public Optional<User> getUser(UUID id);

    public List<User> getAllUsers();

    public boolean modifyUser(UserUpdateRequestDto userUpdateRequestDto);

    public boolean deleteUser(UUID id);

}
