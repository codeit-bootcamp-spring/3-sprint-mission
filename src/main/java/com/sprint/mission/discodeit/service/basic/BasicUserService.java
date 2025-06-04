package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.annotation.Logging;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Logging
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    private final UserMapper userMapper;

    /**
     * 주어진 생성 요청 DTO(유저, 프로필사진)를 기반으로 유저 생성
     *
     * @param userCreateRequest    유저 생성 요청 DTO
     * @param profileCreateRequest 프로필사진 생성 요청 DTO
     * @return 생성된 유저
     */
    @Override
    @Transactional
    public UserDto create(UserCreateRequest userCreateRequest,
            BinaryContentCreateRequest profileCreateRequest) {

        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        // username, email 중복체크
        if (userRepository.existsByUsername(username)) {
            throw new IllegalStateException("이미 존재하는 유저명입니다.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        boolean isProfileCreated = profileCreateRequest != null;
        BinaryContent binaryContent = null;

        if (isProfileCreated) {
            binaryContent = BinaryContent.builder()
                    .fileName(profileCreateRequest.fileName())
                    .contentType(profileCreateRequest.contentType())
                    .size(((long) profileCreateRequest.bytes().length))
                    .build();

            binaryContentRepository.save(binaryContent);
            binaryContentStorage.put(binaryContent.getId(), profileCreateRequest.bytes());
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(userCreateRequest.password())
                .profile(binaryContent)
                .build();

        UserStatus userStatus = UserStatus.builder()
                .user(user)
                .lastActiveAt(Instant.now())
                .build();

        // 양방향 연관관계 설정
        user.setStatus(userStatus);
        userStatus.setUser(user);

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    /**
     * 레포지토리로부터 읽어온 유저 데이터 전체 조회
     *
     * @return 조회된 유저 데이터
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * 주어진 id에 해당하는 유저 조회
     *
     * @param userId 조회할 유저의 ID
     * @return 조회된 유저 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public UserDto find(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        return userMapper.toDto(user);
    }

    /**
     * 주어진 ID에 해당하는 유저를 수정 요청 DTO(유저, 프로필사진) 값으로 수정
     *
     * @param userId               수정할 유저 ID
     * @param updateRequest        유저 수정 요청 DTO
     * @param profileCreateRequest 프로필사진 수정 요청 DTO
     * @return 수정된 유저
     */
    @Override
    @Transactional
    public UserDto update(UUID userId, UserUpdateRequest updateRequest,
            BinaryContentCreateRequest profileCreateRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        String newUsername = updateRequest.newUsername();
        String newEmail = updateRequest.newEmail();

        if (userRepository.existsByUsername(newUsername)) {
            throw new IllegalStateException("이미 존재하는 유저명입니다.");
        }
        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        boolean isProfileCreated = profileCreateRequest != null;
        BinaryContent binaryContent = null;

        if (isProfileCreated) {
            binaryContent = BinaryContent.builder()
                    .fileName(profileCreateRequest.fileName())
                    .contentType(profileCreateRequest.contentType())
                    .size(((long) profileCreateRequest.bytes().length))
                    .build();

            binaryContentRepository.save(binaryContent);
            binaryContentStorage.put(binaryContent.getId(), profileCreateRequest.bytes());
        }

        user.update(
                newUsername,
                newEmail,
                updateRequest.newPassword(),
                binaryContent
        );

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    /**
     * 주어진 id에 해당하는 유저 삭제
     *
     * @param userId 삭제할 대상 유저 id
     */
    @Override
    @Transactional
    public void delete(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        if (user.getProfile() != null) {
            binaryContentRepository.deleteById(user.getProfile().getId());
        }

        userRepository.deleteById(userId);
    }
}
