package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UserService {

  UserDto create(
      @Valid UserCreateRequest userCreateRequest,
      @Valid Optional<BinaryContentCreateRequest> profileCreateRequest
  );

  UserDto find(@NotNull UUID userId);

  List<UserDto> findAll();

  UserDto update(
      @NotNull UUID userId,
      @Valid UserUpdateRequest userUpdateRequest,
      @Valid Optional<BinaryContentCreateRequest> profileCreateRequest
  );

  void delete(@NotNull UUID userId);

  UserDto updateUserRole(@Valid RoleUpdateRequest roleUpdateRequest);
}