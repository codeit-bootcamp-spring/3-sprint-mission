package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.DTO.Request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.service.DTO.Request.UserCreateRequest;
import com.sprint.mission.discodeit.service.DTO.Request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.DTO.UserDTO;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

//    @Autowired
//    public BasicUserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    @Override
    public User create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> portraitRequest) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();
        String password = userCreateRequest.password();
        boolean nameExists = userRepository.existsByName(username);
        if (nameExists) {
            throw new IllegalArgumentException("Username already exists");
        }
        boolean emailExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(email));
        if (emailExists) {
            throw new IllegalArgumentException("E-mail already exists");
        }

        User user = new User(username,email,password);

        UserStatus status = new UserStatus(user.getId());
        status.refresh();

        return userRepository.save(user);
    }

    @Override
    public UserDTO find(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> portraitCreateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        boolean nameExists = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(userId))
                .anyMatch(u -> u.getUsername().equals(userUpdateRequest.userName()));
        if (nameExists) {
            throw new IllegalArgumentException("Username already exists");
        }
        user.update(userUpdateRequest.userName(), userUpdateRequest.email(), userUpdateRequest.password());
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("No User found"));
        Optional.ofNullable(user.getPortraitId())
                        .ifPresent(binaryContentId -> binaryContentRepository.deleteById(binaryContentId));
        userStatusRepository.deleteById(userId);
        userRepository.deleteById(userId);

    }

    private UserDTO toUserDTO(User user) {
        Boolean isOnline = userStatusRepository.findById(user.getId()).get().isOnline();
        return new UserDTO(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getPortraitId(),
                isOnline
        );
    }
}
