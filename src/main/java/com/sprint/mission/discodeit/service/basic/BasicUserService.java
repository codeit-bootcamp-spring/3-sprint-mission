package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateResponse;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserAlreadyExistsException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    //TODO : binaryConent를 따로 만들고 여기서는 그 Id 만 전달해줄껀지 아니면 여기서 binaryConent를 생성할건지
    // FIXME : QUESTION. profile이 있는지 없는지에 따라 method overloading -> validation 부분, userStatus 생성, repository 저장부분 중복인데 괜찮은건가?
    // -> 어차피 user 생성할때 오버로딩 되어있으므로 하나로 합침?!?! -> 근데 파라미터4개있는 생성자만 사용됨
    @Override
    public UserCreateResponse create(UserCreateRequest createRequest) {
        // 0. validation (name, email이 유니크 해야함)
        List<User> users = this.userRepository.findAll();
        Optional<User> userNullable =
                users.stream()
                        .filter((user) -> {
                            return Objects.equals(user.getEmail(), createRequest.email()) && Objects.equals(user.getName(), createRequest.name());
                        })
                        .findAny();

        userNullable.ifPresent((user) -> {
            throw new UserAlreadyExistsException("이미 존재하는 회원입니다. 유저정보 : " + user.toString());
        });

        // 1. create user
        User user = new User(createRequest.name(), createRequest.email(), createRequest.password(), createRequest.profileId());
        // 2. UserStatus 인스턴스 생성
        UserStatus userStatus = new UserStatus(user.getId());
        //4. DB저장
        this.userRepository.save(user);

        return new UserCreateResponse(user, userStatus);
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        // FIXME : 나중에 userStatusRepository 구현체 만들고 넣어줘야함.
        // UserStatus userStatus = this.userStatusRepository.findByUserId(userId);
        UserStatus userStatus = new UserStatus(user.getId());
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
                    // FIXME : 나중에 userStatusRepository 구현체 만들고 넣어줘야함.
                    UserStatus userStatus = new UserStatus(user.getId());
                    return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getProfileId(), userStatus);
                })
                .toList();


        return users;
    }

    // QUESTION : TODO :  업데이트할때도 이메일이랑 아이디 validation check 해야하나? -> 함수로 따로 뺄것.
    @Override
    public UserCreateResponse update(UserUpdateRequest updateRequest) {
        // 다른 메소드에서 this.find() 이제 사용 못함. response 타입이 UserResponse로 바뀌어서.
        //        User user = this.find(userId);
        User user = this.userRepository.findById(updateRequest.userId()).
                orElseThrow(() -> new NoSuchElementException("User with id " + updateRequest.userId() + " not found"));

        user.update(updateRequest.newName(), updateRequest.newEmail(), updateRequest.newPassword(), updateRequest.newProfileId());
        // FIXME : 나중에 userStatusRepository 구현체 만들고 넣어줘야함.
        UserStatus userStatus = new UserStatus(user.getId());

        return new UserCreateResponse(user, userStatus);
    }


    //관련된 도메인도 같이 삭제( BinaryContent, UserStatus )
    @Override
    public void delete(UUID userId) {
        User user = this.userRepository.findById(userId).
                orElseThrow(() -> new NoSuchElementException("User with userId " + userId + " not found"));


        //TODO : BinaryContentRepository에서 해당 객체 삭제
//        User profile = this.BinaryContentRepository.findById(user.getProfileId()).
//                orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + user.getProfileId() + " not found"));
//        this.BinaryContentRepository.deleteById(profile.getProfileId());

        //TODO : UserStatusRepository에서 해당 객체 삭제
//        UserStatus userStatus = this.UserStatusRepository.findByUserId(user.getId()).
//                orElseThrow(() -> new NoSuchElementException("UserStatus with userId " + user.getId() + " not found"));
//        this.UserStatusRepository.deleteById(userStatus.getId());

        //UserRepository에서 해당 객체 삭제
        this.userRepository.deleteById(userId);
    }
}
