package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
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
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User create(UserCreateRequest userCreateRequest) {
        String username = userCreateRequest.userName();
        String email = userCreateRequest.email();
        String password = userCreateRequest.password();

        if(userRepository.isExistUsername(username)){
            System.out.println("생성 실패 : 이미 존재하는 이름입니다.");
        }else if(userRepository.isExistEmail(email)){
            System.out.println("생성 실패 : 이미 존재하는 E-mail입니다.");
        }else {
            User user = new User(username, email, password);
            userRepository.save(user);

            UserStatus userStatus = new UserStatus(user.getId(), Instant.now());
            userStatusRepository.save(userStatus);
            return user;
        }
        return null;
    }
    @Override
    public User create(UserCreateRequest userCreateRequest, BinaryContentCreateRequest binaryContentCreateRequest) {
        User user = this.create(userCreateRequest);
        BinaryContent binaryForPortrait = new BinaryContent(
                binaryContentCreateRequest.fileName(),
                (long)binaryContentCreateRequest.content().length,
                binaryContentCreateRequest.contentType(),
                binaryContentCreateRequest.content()
        );
        user.setPortraitId(binaryForPortrait.getId());
        userRepository.save(user);
        return user;
    }

    @Override
    public UserDto findById(UUID userId) {
        User foundUser = userRepository.findById(userId);
        return toDto(foundUser);
    }

    @Override
    public UserDto findByUsername(String username) {
        User foundUser = userRepository.findAll()
                .stream()
                .filter(user1 -> user1.getUsername().equals(username)).findFirst().orElse(null);
        return toDto(foundUser);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void update(UUID userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId);
        String username = userUpdateRequest.userName();
        String email = userUpdateRequest.email();
        String password = userUpdateRequest.password();

        if (user == null) {
            System.out.println("수정 실패 : 존재하지 않는 사용자입니다.");
        } else if (userRepository.isExistUsername(username)){
            System.out.println("수정 실패 : 이미 존재하는 사용자 이름입니다.");
        } else if (userRepository.isExistEmail(email)) {
            System.out.println("수정 실패 : 이미 존재하는 사용자 메일입니다.");
        } else{
            user.update(username,email,password);
            userRepository.save(user);
        }
    }

    @Override
    public void delete(UUID userId) {
        if (findById(userId) == null) {
            throw new NoSuchElementException("수정 실패 : 존재하지 않는 사용자입니다.");
        }
        binaryContentRepository.deleteById(this.findById(userId).profileId());
        userRepository.delete(userId);
        userStatusRepository.deleteById(userId);

    }

    private UserDto toDto(User user){
        boolean isOnline = userStatusRepository.findById(user.getId())
                .map(UserStatus::isOnline)
                .orElse(null);

        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getPortraitId(),
                isOnline
        );
    }
}
