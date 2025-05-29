package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserFindRequest;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserCreateRequest request,
                          Optional<BinaryContentCreateRequest> binaryContentCreateRequest) {
        validateUniqueUser(request);

        BinaryContent profile = binaryContentCreateRequest
                .map(this::saveBinaryContent)
                .orElse(null);

        User user = new User(
                request.username(),
                request.email(),
                request.password(),
                profile,
                null
        );
        User savedUser = userRepository.save(user);

        UserStatus status = new UserStatus(savedUser, Instant.now());
        userStatusRepository.save(status);

        return userMapper.toDto(savedUser);
    }


    private BinaryContent saveBinaryContent(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                (long) request.bytes().length,
                request.contentType(),
                request.bytes()
        );

        return binaryContent;
    }

    private void validateUniqueUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }

    @Override
    public UserDto find(UserFindRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저는 없습니다."));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto update(UUID userId, UserUpdateRequest request,
                          Optional<BinaryContentCreateRequest> profileRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저는 없습니다."));

        if (profileRequest.isPresent()) {
            Optional.ofNullable(user.getProfile())
                    .ifPresent(binaryContentRepository::delete);

            BinaryContent newProfile = new BinaryContent(
                    profileRequest.get().fileName(),
                    (long) profileRequest.get().bytes().length,
                    profileRequest.get().contentType(),
                    profileRequest.get().bytes()
            );
            user.setProfile(binaryContentRepository.save(newProfile));
        }

        user.update(
                request.newUsername(),
                request.newEmail(),
                request.newPassword(),
                user.getProfile()
        );

        userStatusRepository.findByUserId(user.getId()).ifPresent(status -> {
            status.update(Instant.now());
        });

        User updated = userRepository.save(user);
        return userMapper.toDto(updated);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 id를 가진 유저는 없습니다.");
        }
        userRepository.deleteById(userId);
    }
}