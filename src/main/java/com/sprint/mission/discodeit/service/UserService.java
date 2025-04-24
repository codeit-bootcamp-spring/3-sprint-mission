package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.FindUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.entitiy.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    public User create(CreateUserRequest userRequest);
    public User create(CreateUserRequest userRequest, CreateBinaryContentRequest binaryContentRequest);
    public List<FindUserRequest> findAll();
    public FindUserRequest find(UUID id);
    public void update(UpdateUserRequest updateUserRequest);
    public void update(UpdateUserRequest updateUserRequest, CreateBinaryContentRequest binaryContentRequest);
    public void delete(User user);

}
