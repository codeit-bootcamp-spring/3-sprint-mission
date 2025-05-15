package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserPasswordUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// Lombok( 생성자 대체 )
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    // 리펙토링

    @Override
    public User create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> binaryContentCreateRequest) {
        // username, email 중복 확인
        List<User> users = userRepository.findAll();

        boolean usernameExists = users.stream()
                .anyMatch(user -> user.getUserName().equals(userCreateRequest.getUserName()));

        boolean emailExists = users.stream()
                .anyMatch(user -> user.getEmail().equals(userCreateRequest.getEmail()));

        if (usernameExists) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다");
        }

        if (emailExists) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }

        // 선택적 프로필 이미지 생성 ( 있을 수도, 없을 수도 )
        UUID profileId = null;

        // isPresent() : 인자값에 값이 존재한다면 true  | 선택적 여부 판별
        if (binaryContentCreateRequest.isPresent()) {
            BinaryContentCreateRequest bccr = binaryContentCreateRequest.get();
            BinaryContent binaryContent = new BinaryContent(
                    bccr.getFileName(),
                    bccr.getFileData(),
                    bccr.getFileType(),
                    bccr.getFileData().length
            );
            // 생성한 정보를 토대로 저장
            binaryContentRepository.save(binaryContent);
            // BinaryContent 인자값이 존재하면 해당 ID를 저장
            profileId = binaryContent.getId();
        }

        // User Create
        User user = new User(
                userCreateRequest.getUserName(),
                userCreateRequest.getPwd(),
                userCreateRequest.getEmail(),
                userCreateRequest.getPhoneNumber(),
                userCreateRequest.getStatusMessage(),
                // BinaryContent가 없으면 기본값인 null, 있다면 저장된 ID 존재
                profileId
        );
        userRepository.save(user);

        // 현재 시간 적용
        Instant now = Instant.now();

        // UserStatus 동시 생성하기
        UserStatus userStatus = new UserStatus(user.getUserId(), now);
        userStatusRepository.save(userStatus);

        return user;
    }

    @Override
    public UserDTO find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        // 온라인 여부
        boolean isOnline = userStatusRepository.findById(user.getUserId())
                .map(UserStatus::isOnline)
                .orElse(false);

        // 조회 정보 반환
        return new UserDTO(
                user.getUserId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUserName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getStatusMessage(),
                user.getProfileId(),
                isOnline
        );
    }

    @Override
    public List<UserDTO> findAll() {
        // 리스트로 전체 유저를 조회
        List<User> users = userRepository.findAll();
        return users.stream()
                // 온라인 여부 판별 후 해당 정보 값을 dto로 반환
                .map(user -> {
                    boolean isOnline = userStatusRepository.findById(user.getUserId())
                            .map(UserStatus::isOnline)
                            .orElse(false);

                    return new UserDTO(
                            user.getUserId(),
                            user.getCreatedAt(),
                            user.getUpdatedAt(),
                            user.getUserName(),
                            user.getEmail(),
                            user.getPhoneNumber(),
                            user.getStatusMessage(),
                            user.getProfileId(),
                            isOnline
                    );
                })
                // List로 저장
                .collect(Collectors.toList());
    }

    @Override
    public User update(UUID id, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> binaryContentCreateRequest) {
        // 조회 우선
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

        // 선택적 프로필 이미지 개선
        binaryContentCreateRequest.ifPresent(request -> {
            BinaryContent profileImage = new BinaryContent(
                    request.getFileName(),
                    request.getFileData(),
                    request.getFileType(),
                    request.getFileData().length
            );
            // BinaryContent 저장 ( >> 업데이트 )
            binaryContentRepository.save(profileImage);
        });

        // 사용자 정보 업데이트 ( 비밀번호 별도 )
        user.update(
                userUpdateRequest.getUserName(),
                // 기존 비밀번호 유지
                user.getPwd(),
                userUpdateRequest.getEmail(),
                userUpdateRequest.getPhoneNumber(),
                userUpdateRequest.getStatusMessage()
        );
        return userRepository.save(user);
    }

    @Override
    public User updateByPass(UUID id, UserPasswordUpdateRequest userPasswordUpdateRequest) {
        // 조회
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

        // 비밀번호 일치여부 확인
        if (!user.getPwd().equals(userPasswordUpdateRequest.getCurrentPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다");
        }

        // 비밀번호만 업데이트 ( 기존 정보는 유지 )
        user.update(
                user.getUserName(),
                userPasswordUpdateRequest.getNewPassword(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getStatusMessage()
        );
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        // cascade 옵션 적용 ( BinaryContent && UserStatus )
        binaryContentRepository.deleteById(userId);
        userStatusRepository.deleteById(userId);
        userRepository.deleteById(userId);
    }
}
