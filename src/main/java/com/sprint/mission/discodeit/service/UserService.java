package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.FindUserRespond;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.entitiy.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    public User create(CreateUserRequest userRequest, Optional<CreateBinaryContentRequest> binaryContentRequest);
    public List<FindUserRespond> findAll();
    public FindUserRespond find(UUID id);
    public void update(UpdateUserRequest updateUserRequest);
    public void update(UpdateUserRequest updateUserRequest, CreateBinaryContentRequest binaryContentRequest);
    public void delete(UUID userId);

}
