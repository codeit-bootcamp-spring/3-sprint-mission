package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.swing.text.html.Option;

public interface UserService {

  User create(CreateUserRequest createUserRequest,
      Optional<CreateBinaryContentRequest> binaryContentRequest);

  User find(UUID userId);

  List<User> findAll();

  User update(UUID userId, UpdateUserRequest request,
      Optional<CreateBinaryContentRequest> profileCreateRequest);

  void delete(UUID userId);
}
