package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
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

        if (binaryContentCreateRequest != null) {
            BinaryContent binaryContent = BinaryContent.of(binaryContentCreateRequest.getFileName(), binaryContentCreateRequest.getFilePath());
            binaryContentRepository.save(binaryContent);
            user.updateProfileId(binaryContent.getId());
        }

        userRepository.save(user);
        return user;
    }

    @Override
    public UserDTO getUser(UUID id) {
        User user = userRepository.loadById(id);
        UserStatus status = userStatusRepository.loadById(id);

        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getProfileId(), status.isLoggedIn());
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
                            user.getProfileId(),
                            status.isLoggedIn()
                    );
                })
                .toList();
    }

    @Override
    public void deleteUser(UUID id) {
        try {
            BinaryContent binaryContent = binaryContentRepository.loadById(id);
            if (binaryContent != null) {
                binaryContentRepository.delete(binaryContent.getId());
            }

//            messageRepository.deleteByUserId(id);
            readStatusRepository.deleteByUserId(id);
            userStatusRepository.deleteByUserId(id);
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