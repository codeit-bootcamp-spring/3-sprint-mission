package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  UserResponse create(UserRequest request,
      Optional<BinaryContentCreateRequest> profileCreateRequest);

  UserResponse find(UUID id);

  List<UserResponse> findAll();

  UserResponse update(UUID id, UserRequest.Update request,
      Optional<BinaryContentCreateRequest> profileCreateRequest);

  void delete(UUID id);
}
