package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public User createUser(UserCreateRequest userCreateRequest, BinaryContentCreateRequest profileImage) {
        return null;
    }

    @Override
    public UserDTO getUser(UUID id) {
        return null;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return List.of();
    }

    @Override
    public User getUserByName(String name) {
        return data.values().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public BinaryContent updateUserProfileImage(UserUpdateRequest userUpdateRequest) {
        return null;
    }

    @Override
    public void deleteUser(UUID id) {
        data.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }
}
