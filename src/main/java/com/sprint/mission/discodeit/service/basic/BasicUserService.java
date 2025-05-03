package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.dto.entity.User;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;


    @Override
    public User createUser(UserCreateRequest userCreateRequest, BinaryContentCreateRequest binaryContentCreateRequest) {
        if (userRepository.loadByName(userCreateRequest.getName()) != null) {
            throw new IllegalArgumentException("[User] 이미 존재하는 사용자 이름입니다. (" + userCreateRequest.getName() + ")");
        }

        if (userRepository.loadByEmail(userCreateRequest.getEmail()) != null) {
            throw new IllegalArgumentException("[User] 이미 등록된 이메일입니다: " + userCreateRequest.getEmail());
        }

        User user = User.of(userCreateRequest);

        UserStatus userStatus = UserStatus.of(user.getId());
        userStatusRepository.save(userStatus);
        user.updateUserStatusId(userStatus.getId());
        userRepository.save(user);

        if (binaryContentCreateRequest != null) {
            UserUpdateRequest userUpdateRequest = new UserUpdateRequest(user.getId(), binaryContentCreateRequest);
            BinaryContent binaryContent = updateUserProfileImage(userUpdateRequest);
            user.updateProfileImageId(binaryContent.getId());
        }

        userRepository.save(user);
        return user;
    }

    @Override
    public UserDTO getUser(UUID id) {
        User user = userRepository.loadById(id);
        UserStatus status = userStatusRepository.loadById(id);

        return new UserDTO(user.getId(), user.getName(), user.getEmail(), status.isLoggedIn());
    }

    @Override
    public User getUserByName(String name) {
        return userRepository.loadByName(name);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.loadAll().stream()
                .map(user -> {
                    UserStatus status = userStatusRepository.loadById(user.getId());
                    return new UserDTO(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            status.isLoggedIn()
                    );
                })
                .toList();
    }

    @Override
    public BinaryContent updateUserProfileImage(UserUpdateRequest userUpdateRequest) {
        User user = userRepository.loadById(userUpdateRequest.getUserId());
        BinaryContentCreateRequest binaryContentCreateRequest = userUpdateRequest.getBinaryContentCreateRequest();
        BinaryContent binaryContent;

        if (user.getProfileImageId() == null) {
            binaryContent = BinaryContent.forUserProfileImage(user.getId(), binaryContentCreateRequest);
            binaryContentRepository.save(binaryContent);
            userRepository.save(user.updateProfileImageId(binaryContent.getId()));
        } else {
            binaryContent = binaryContentRepository.loadByUserId(user.getId());
            binaryContentRepository.save(
                    binaryContent.update(binaryContentCreateRequest.getFileName(), binaryContentCreateRequest.getFilePath())
            );
        }

        return binaryContent;
    }

    @Override
    public void deleteUser(UUID id) {
        try {
            BinaryContent binaryContent = binaryContentRepository.loadByUserId(id);
            if (binaryContent != null) {
                binaryContentRepository.delete(binaryContent.getId(), binaryContent.getUserId());
            }

//            messageRepository.deleteByUserId(id);
            readStatusRepository.deleteByUserId(id);
            userStatusRepository.deleteById(id);
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