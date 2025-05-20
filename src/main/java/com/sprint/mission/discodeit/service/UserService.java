package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.entity.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.dto.user.UserUpdateRequestDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {


    public UserResponseDto createUser(UserCreateDto userCreateDto, BinaryContentCreateRequestDto binaryContentCreateRequestDto);

    public Optional<UserResponseDto> getUser(UUID id);

    public List<UserResponseDto> getAllUsers();

    public boolean modifyUser(UserUpdateRequestDto userUpdateRequestDto, BinaryContentCreateRequestDto binaryContentCreateRequestDto);

    public boolean deleteUser(UUID id);


}
