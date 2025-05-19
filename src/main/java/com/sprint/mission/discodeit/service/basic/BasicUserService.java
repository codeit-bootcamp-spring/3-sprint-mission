package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    /**
     * 주어진 생성 요청 DTO(유저, 프로필사진)를 기반으로 유저 생성
     *
     * @param userCreateRequest       유저 생성 요청 DTO
     * @param profileCreateRequestDTO 프로필사진 생성 요청 DTO
     * @return 생성된 유저
     * @throws IllegalStateException 유저명/이메일 중복시 예외처리
     */
    @Override
    public User create(UserCreateRequest userCreateRequest,
            BinaryContentCreateRequest profileCreateRequestDTO) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        // username, email 중복체크
        if (userRepository.existsByUserName(username)) {
            throw new IllegalStateException("이미 존재하는 유저명입니다. 다른 유저명을 입력해주세요.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 존재하는 이메일입니다. 다른 이메일을 입력해주세요.");
        }

        boolean isProfileCreated = profileCreateRequestDTO != null;
        BinaryContent binaryContent = null;

        if (isProfileCreated) {
            String fileName = profileCreateRequestDTO.fileName();
            String contentType = profileCreateRequestDTO.contentType();
            byte[] bytes = profileCreateRequestDTO.bytes();

            binaryContent = BinaryContent.builder()
                    .contentType(contentType)
                    .fileName(fileName)
                    .bytes(bytes)
                    .size((long) bytes.length)
                    .build();

            binaryContentRepository.save(binaryContent);
        }

        String password = userCreateRequest.password();

        User user = User.builder()
                .username(username)
                .email(email)
                .password(password)
                .profileId(isProfileCreated ? binaryContent.getId() : null)
                .build();

        UserStatus userStatus = UserStatus.builder()
                .userId(user.getId())
                .lastActiveAt(Instant.now())
                .build();

        userRepository.save(user);
        userStatusRepository.save(userStatus);
        return user;
    }

    /**
     * 레포지토리로부터 읽어온 유저 데이터 전체 조회
     *
     * @return 조회된 유저 데이터
     */
    @Override
    public List<UserDto> findAll() {
        List<User> userList = userRepository.findAll();

        // userStatus를 UserID와 매핑
        Map<UUID, UserStatus> userStatusMap = userStatusRepository.findAll().stream()
                .collect(Collectors.toMap(UserStatus::getUserId, us -> us));

        return userList.stream()
                .map(u -> UserDto.from(u, userStatusMap.get(u.getId())))
                .collect(Collectors.toList());
    }

    /**
     * 주어진 id에 해당하는 유저 조회
     *
     * @param userId 조회할 유저의 ID
     * @return 조회된 유저 DTO
     * @throws NoSuchElementException 해당 ID의 유저가 존재하지 않는 경우
     */
    @Override
    public UserDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 유저가 존재하지 않습니다."));

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저ID의 UserStatus가 존재하지 않습니다."));

        return UserDto.from(user, userStatus);
    }

    /**
     * 주어진 ID에 해당하는 유저를 수정 요청 DTO(유저, 프로필사진) 값으로 수정
     *
     * @param userId                  수정 대상 유저 ID
     * @param updateRequestDTO        유저 수정 요청 DTO
     * @param profileCreateRequestDTO 프로필사진 수정 요청 DTO
     * @return 수정된 유저
     * @throws NoSuchElementException 해당 ID의 유저가 존재하지 않는 경우
     */
    @Override
    public User update(UUID userId, UserUpdateRequest updateRequestDTO,
            BinaryContentCreateRequest profileCreateRequestDTO) {
        // 유저 유효성 검사
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 유저가 존재하지 않습니다."));

        String newUsername = updateRequestDTO.newUsername();
        String newEmail = updateRequestDTO.newEmail();

        if (userRepository.existsByUserName(newUsername)) {
            throw new IllegalStateException("이미 존재하는 유저명입니다. 다른 유저명을 입력해주세요.");
        }
        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalStateException("이미 존재하는 이메일입니다. 다른 이메일을 입력해주세요.");
        }

        boolean isProfileCreated = profileCreateRequestDTO != null;
        BinaryContent binaryContent = null;

        if (isProfileCreated) {
            String fileName = profileCreateRequestDTO.fileName();
            String contentType = profileCreateRequestDTO.contentType();
            byte[] bytes = profileCreateRequestDTO.bytes();

            binaryContent = BinaryContent.builder()
                    .fileName(fileName)
                    .size((long) bytes.length)
                    .contentType(contentType)
                    .bytes(bytes)
                    .build();

            binaryContentRepository.save(binaryContent);
        }

        String newPassword = updateRequestDTO.newPassword();

        user.update(
                newUsername,
                newEmail,
                newPassword,
                isProfileCreated ? binaryContent.getId() : null
        );

        userRepository.save(user);
        return user;
    }

    /**
     * 주어진 id에 해당하는 유저 삭제
     *
     * @param userId 삭제할 대상 유저 id
     * @throws NoSuchElementException 해당 유저ID의 유저나 UserStatus가 존재하지 않는 경우
     */
    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 유저가 존재하지 않습니다."));

        UUID userStatusId = userStatusRepository.findByUserId(userId)
                .map(UserStatus::getId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저ID의 UserStatus가 존재하지 않습니다."));

        userStatusRepository.deleteById(userStatusId);
        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        userRepository.deleteById(userId);
    }
}
