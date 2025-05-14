package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DuplicateUserException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
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
    //Q. 만약 binaryContentService를 쓰지 않는다면 binaryContentService의 코드가 여기서도 반복되는데
    // 이때는 그냥 의존하는것이 낫나? vs 중복된 코드를 써도 분리가 좋나?
    private final BinaryContentService binaryContentService;

    @Override
    public User create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest) {
        // 0. validation (name, email이 유니크 해야함)
        if (this.hasSameEmailOrName(userCreateRequest.name(), userCreateRequest.email())) {
            throw new DuplicateUserException();
        }
        // 1. 프로필 이미지 있으면 생성하고 유저 생성
        profileCreateRequest.map(binaryContentCreateRequest -> {
            BinaryContent profileBinaryContent = this.binaryContentService.create(binaryContentCreateRequest);
            User user = new User(userCreateRequest.name(), userCreateRequest.email(), userCreateRequest.password(), profileBinaryContent.getId());
            return null;
        });
        // 2. 프로필 이미지 없을때 유저 생성
        User user = new User(userCreateRequest.name(), userCreateRequest.email(), userCreateRequest.password(), null);
        // 3. DB저장
        this.userRepository.save(user);
        // 4. UserStatus 인스턴스 생성
//        UserStatusResponse userStatusResponse = this.userStatusService.create(new UserStatusCreateRequest(user.getId()));
        UserStatus userStatus = new UserStatus(user.getId());
        this.userStatusRepository.save(userStatus);

        return user;
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = this.userStatusRepository.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("userStatus with userId " + userId + " not found"));

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getProfileId(), userStatus);
    }


    @Override
    public List<User> find(String name) {
        return List.of();
    }

    @Override
    public List<UserResponse> findAll() {
        List<UserResponse> users = this.userRepository.findAll()
                .stream()
                .map(user -> {
                    UserStatus userStatus = this.userStatusRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("userStatus with userId " + user.getId() + " not found"));

                    return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getProfileId(), userStatus);
                })
                .toList();

        return users;
    }

    @Override
    public User update(UUID userId, UserUpdateRequest updateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest) {
        // 0. validation (name, email이 유니크 해야함)
        if (this.hasSameEmailOrName(updateRequest.newName(), updateRequest.newEmail())) {
            throw new DuplicateUserException();
        }

        User user = this.userRepository.findById(userId).
                orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        // 1. 프로필 이미지 업데이트
        profileCreateRequest.map(binaryContentCreateRequest -> {
            BinaryContent profileBinaryContent = this.binaryContentService.create(binaryContentCreateRequest);
            user.update(updateRequest.newName(), updateRequest.newEmail(), updateRequest.newPassword(), profileBinaryContent.getId());
            return null;
        });

        user.update(updateRequest.newName(), updateRequest.newEmail(), updateRequest.newPassword(), null);

        /* 업데이트 후 다시 DB 저장 */
        this.userRepository.save(user);

        User updatedUser = this.userRepository.findById(userId).
                orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        return updatedUser;
    }


    //관련된 도메인도 같이 삭제( BinaryContent, UserStatus )
    @Override
    public void delete(UUID userId) {
        User user = this.userRepository.findById(userId).
                orElseThrow(() -> new NoSuchElementException("User with userId " + userId + " not found"));

        /* BinaryContentRepository에서 프로필사진 삭제 */
        this.binaryContentRepository.findById(user.getProfileId()).ifPresent(profile -> {
            this.binaryContentRepository.deleteById(profile.getId());
        });

        /* UserStatusRepository에서 해당 객체 삭제 */
        UserStatus userStatus = this.userStatusRepository.findByUserId(user.getId()).
                orElseThrow(() -> new NoSuchElementException("UserStatus with userId " + user.getId() + " not found"));
        this.userStatusRepository.deleteById(userStatus.getId());

        /* UserRepository에서 해당 객체 삭제 */
        this.userRepository.deleteById(userId);
    }


    @Override
    public boolean hasSameEmailOrName(String name, String email) {
        List<User> users = this.userRepository.findAll();

        return users.stream()
                .anyMatch((user) -> {
                    return user.getEmail().equals(email) || user.getName().equals(name);
                });
    }
}
