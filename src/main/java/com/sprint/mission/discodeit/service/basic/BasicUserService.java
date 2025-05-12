package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.User.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.User.UserDTO;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequestDTO;
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
     * @param userCreateRequestDTO 유저 생성 요청 DTO
     * @param profileCreateRequestDTO 프로필사진 생성 요청 DTO
     * @return 생성된 유저
     * @throws IllegalStateException 유저명/이메일 중복시 예외처리
     */
    @Override
    public User create(UserCreateRequestDTO userCreateRequestDTO, BinaryContentCreateRequestDTO profileCreateRequestDTO) {
        String userName = userCreateRequestDTO.userName();
        String email = userCreateRequestDTO.email();

        // username, email 중복체크
        if (userRepository.existsByUserName(userName)) {
            throw new IllegalStateException("이미 존재하는 유저명입니다. 다른 유저명을 입력해주세요.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 존재하는 이메일입니다. 다른 이메일을 입력해주세요.");
        }

        // BinaryContent 생성
        boolean isProfileCreated = profileCreateRequestDTO != null;
        BinaryContent binaryContent = null;

        // 프로필 파라미터가 있는 경우
        if (isProfileCreated) {
            String fileName = profileCreateRequestDTO.fileName();
            String contentType = profileCreateRequestDTO.contentType();
            byte[] content = profileCreateRequestDTO.content();

            binaryContent = BinaryContent.builder()
                    .contentType(contentType)
                    .fileName(fileName)
                    .content(content)
                    .size((long) content.length)
                    .build();

            binaryContentRepository.save(binaryContent);
        }

        String password = userCreateRequestDTO.password();

        // 유저 생성
        User user = User.builder()
                .userName(userName)
                .email(email)
                .password(password)
                .profileId(isProfileCreated ? binaryContent.getId(): null)
                .build();

        // UserStatus 생성
        UserStatus userStatus = UserStatus.builder()
                .userId(user.getId())
                .lastAccessedAt(Instant.now())
                .build();

        // 데이터 저장
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
    public List<UserDTO> findAll() {
        List<User> userList = userRepository.findAll();

        // userStatus를 UserID와 매핑
        Map<UUID, UserStatus> userStatusMap = userStatusRepository.findAll().stream()
                .collect(Collectors.toMap(UserStatus::getUserId, us->us));

        // UserResponseDTO 리스트 형태로 리턴
        return userList.stream()
                .map(u-> UserDTO.from(u,userStatusMap.get(u.getId())))
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
    public UserDTO find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NoSuchElementException("해당 ID의 유저가 존재하지 않습니다."));

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저ID의 UserStatus가 존재하지 않습니다."));

        // UserResponseDTO 형태로 리턴
        return UserDTO.from(user,userStatus);
    }

    /**
     * 주어진 ID에 해당하는 유저를 수정 요청 DTO(유저, 프로필사진) 값으로 수정
     *
     * @param userId 수정 대상 유저 ID
     * @param updateRequestDTO 유저 수정 요청 DTO
     * @param profileCreateRequestDTO 프로필사진 수정 요청 DTO
     * @return 수정된 유저
     * @throws NoSuchElementException 해당 ID의 유저가 존재하지 않는 경우
     */
    @Override
    public User update(UUID userId, UserUpdateRequestDTO updateRequestDTO, BinaryContentCreateRequestDTO profileCreateRequestDTO) {
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

        // BinaryContent 생성
        boolean isProfileCreated = profileCreateRequestDTO != null;
        BinaryContent binaryContent = null;

        // 프로필 파라미터가 있는 경우
        if (isProfileCreated) {
            String fileName = profileCreateRequestDTO.fileName();
            String contentType = profileCreateRequestDTO.contentType();
            byte[] content = profileCreateRequestDTO.content();

            binaryContent = BinaryContent.builder()
                    .fileName(fileName)
                    .size((long) content.length)
                    .contentType(contentType)
                    .content(content)
                    .build();

            binaryContentRepository.save(binaryContent);
        }

        String newPassword = updateRequestDTO.newPassword();

        // 유저 수정
        user.update(
                newUsername,
                newEmail,
                newPassword,
                isProfileCreated ? binaryContent.getId(): null
        );

        // 데이터 저장
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
                .orElseThrow(()-> new NoSuchElementException("해당 유저ID의 UserStatus가 존재하지 않습니다."));

        // 관련 도메인 삭제 (BinaryContent, UserStatus)
        userStatusRepository.deleteById(userStatusId);
        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        // 유저 삭제
        userRepository.deleteById(userId);
    }
}
