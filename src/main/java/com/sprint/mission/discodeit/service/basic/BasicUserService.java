package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;


    @Override
    public User create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        if (userRepository.loadByName(userCreateRequest.getName()) != null) {
            throw new IllegalArgumentException("[User] 이미 존재하는 사용자 이름입니다. (" + userCreateRequest.getName() + ")");
        }

        if (userRepository.loadByEmail(userCreateRequest.getEmail()) != null) {
            throw new IllegalArgumentException("[User] 이미 등록된 이메일입니다: " + userCreateRequest.getEmail());
        }

        UUID nullableProfileId = optionalProfileCreateRequest
                .map(profileRequest -> {
                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = BinaryContent.of(fileName, (long) bytes.length, contentType, bytes);
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(null);

        User user = User.of(userCreateRequest);
        user.updateProfileId(nullableProfileId);
        UserStatus userStatus = UserStatus.of(user.getId());
        userStatusRepository.save(userStatus);
        userRepository.save(user);

        return user;
    }

    @Override
    public UserDTO get(UUID userId) {
        User user = userRepository.loadById(userId);
        UserStatus userStatus = userStatusRepository
                .loadById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("[User] 유효하지 않은 UserStatus. (userId=" + userId + ")")
                );

        return new UserDTO(user.getId(), user.getCreatedAt(), user.getUpdatedAt(), user.getName(), user.getEmail(), user.getProfileId(), userStatus.isLoggedIn());
    }

    @Override
    public User getByName(String name) {
        return userRepository.loadByName(name);
    }

    @Override
    public List<UserDTO> getAll() {
        return userRepository.loadAll().stream()
                .map(user -> {
                    UserStatus status = userStatusRepository
                            .loadById(user.getId())
                            .orElseThrow(() ->
                                    new NoSuchElementException("[User] 유효하지 않은 UserStatus. (userId=" + user.getId() + ")")
                            );

                    return new UserDTO(
                            user.getId(),
                            user.getCreatedAt(),
                            user.getUpdatedAt(),
                            user.getName(),
                            user.getEmail(),
                            user.getProfileId(),
                            status.isLoggedIn()
                    );
                })
                .toList();
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newUpdatedAt = request.newUpdateaAT();

        UserStatus userStatus = userStatusRepository
                .loadById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("[User] 유효하지 않은 UserStatus. (userId=" + userId + ")")
                );

        userStatus.update(newUpdatedAt);

        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID id) {
        try {
            BinaryContent binaryContent = binaryContentRepository.loadById(get(id).getProfileId());
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