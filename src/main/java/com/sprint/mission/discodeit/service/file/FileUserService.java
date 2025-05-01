package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.dto.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private final UserRepository userRepository;

    public FileUserService() {
        userRepository = new FileUserRepository();
    }

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
        return userRepository.loadByName(name);
    }

    @Override
    public BinaryContent updateUserProfileImage(UserUpdateRequest userUpdateRequest) {
        return null;
    }


    @Override
    public void deleteUser(UUID id) {
        try {
            userRepository.deleteById(id);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }
}
