package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  UserResponse create(UserRequest request,
      MultipartFile userProfileImage);

  UserResponse find(UUID id);

  List<UserResponse> findAll();

  UserResponse update(UUID id, UserRequest.Update request, MultipartFile userProfileImage);

  void delete(UUID id);
}
