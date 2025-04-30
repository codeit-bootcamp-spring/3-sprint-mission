package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DuplicateUserException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    // FIXME : Q: service 의존하면 안됨
    private final UserStatusService userStatusService;
    private final BinaryContentRepository binaryContentRepository;

    //Q: binaryConent를 따로 만들고 여기서는 그 Id 만 전달해줄껀지 아니면 여기서 binaryConent를 생성할건지
    // -> 일단 여기서 this.binaryContentService.create() 호출해서 생성
    @Override
    public UserCreateResponse create(UserCreateRequest createRequest) {
        // 0. validation (name, email이 유니크 해야함)
        if (this.hasSameEmailOrName(createRequest.name(), createRequest.email())) {
            throw new DuplicateUserException();
        }
        // 1. create user
        User user = new User(createRequest);
        // 2. DB저장
        this.userRepository.save(user);
        // 3. UserStatus 인스턴스 생성
        UserStatusResponse userStatusResponse = this.userStatusService.create(new UserStatusCreateRequest(user.getId()));


        return new UserCreateResponse(user, userStatusResponse.userStatus());
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
    public UserCreateResponse update(UserUpdateRequest updateRequest) {
        // 0. validation (name, email이 유니크 해야함)
        if (this.hasSameEmailOrName(updateRequest.newName(), updateRequest.newEmail())) {
            throw new DuplicateUserException();
        }

        User user = this.userRepository.findById(updateRequest.userId()).
                orElseThrow(() -> new NoSuchElementException("User with id " + updateRequest.userId() + " not found"));

        user.update(updateRequest.newName(), updateRequest.newEmail(), updateRequest.newPassword(), updateRequest.newProfileId());

        /* 업데이트 후 다시 DB 저장 */
        this.userRepository.save(user);

        User updatedUser = this.userRepository.findById(updateRequest.userId()).
                orElseThrow(() -> new NoSuchElementException("User with id " + updateRequest.userId() + " not found"));

        UserStatus userStatus = this.userStatusRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("userStatus with userId " + user.getId() + " not found"));

        return new UserCreateResponse(updatedUser, userStatus);
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
