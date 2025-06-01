package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.duplicate.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.duplicate.DuplicateNameException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserStatusException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.struct.BinaryContentStructMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("basicUserService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserMapper userMapper;
    private final BinaryContentStructMapper binaryContentMapper;

    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto userRequestDto, BinaryContentDto binaryContentDto) {
        String username = userRequestDto.username();
        String email = userRequestDto.email();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateNameException(username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        String password = userRequestDto.password();
        User user = User.builder()
                .username(username)
                .email(email)
                .password(password)
                .profile(null)
                .status(null)
                .readStatuses(new ArrayList<>())
                .build();

        // 프로필 이미지를 등록한 경우
        if (binaryContentDto != null) {
            byte[] bytes = binaryContentDto.bytes();

            BinaryContent profileImage = binaryContentMapper.toEntity(binaryContentDto);

            user.updateProfile(profileImage);

            binaryContentRepository.save(profileImage);
            binaryContentStorage.put(profileImage.getId(), bytes);
        }

        UserStatus userStatus = UserStatus.builder()
                .user(user)
                .lastActiveAt(Instant.now())
                .build();

        user.updateStatus(userStatus);

        userRepository.save(user);
        userStatusRepository.save(userStatus);

        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto findById(UUID id) {
        User user = findUser(id);

        UserStatus userStatus = findUserStatus(id);

        // 마지막 접속 시간 확인
        user.updateStatus(userStatus);

        return userMapper.toDto(user);
    }

    @Override
    public List<UserResponseDto> findAll() {
        List<UserResponseDto> users = userRepository.findAll().stream()
                .map(user -> {
                    UserStatus userStatus = findUserStatus(user.getId());
                    user.updateStatus(userStatus);
                    return userMapper.toDto(user);
                })
                .toList();

        return users;
    }

    @Override
    @Transactional
    public UserResponseDto update(UUID id, UserUpdateDto userUpdateDto,
                                  BinaryContentDto binaryContentDto) {
        User user = findUser(id);

        String newUsername = userUpdateDto.newUsername();
        String newEmail = userUpdateDto.newEmail();

        if (newUsername != null) {
            userRepository.findByUsername(newUsername)
                    .filter(u -> !u.getId().equals(user.getId()))
                    .ifPresent(u -> {
                        throw new DuplicateNameException(newUsername);
                    });
            user.updateName(newUsername);
        }

        if (newEmail != null) {
            userRepository.findByEmail(newEmail)
                    .filter(u -> !u.getId().equals(user.getId()))
                    .ifPresent(u -> {
                        throw new DuplicateEmailException(newEmail);
                    });
            user.updateEmail(newEmail);
        }

        // 프로필 이미지 처리
        BinaryContent profile = user.getProfile();
        if (binaryContentDto != null) {
            byte[] bytes = binaryContentDto.bytes();

            BinaryContent profileImage = binaryContentMapper.toEntity(binaryContentDto);

            // 기존 프로필 이미지 제거
            if (profile != null) {
                binaryContentRepository.deleteById(profile.getId());
            }

            user.updateProfile(profileImage);

            binaryContentRepository.save(profileImage);
            binaryContentStorage.put(profileImage.getId(), bytes);
        } else if (profile != null) {
            binaryContentRepository.deleteById(profile.getId());
            user.updateProfile(null);
        }

        Optional.ofNullable(userUpdateDto.newPassword()).ifPresent(user::updatePassword);

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        User user = findUser(id);

        userRepository.deleteById(id);
        userStatusRepository.deleteByUserId(id);

        if (user.getProfile() != null) {
            binaryContentRepository.deleteById(user.getProfile().getId());
        }
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(NotFoundUserException::new);
    }

    private UserStatus findUserStatus(UUID id) {
        return userStatusRepository.findByUserId(id)
                .orElseThrow(NotFoundUserStatusException::new);
    }
}
