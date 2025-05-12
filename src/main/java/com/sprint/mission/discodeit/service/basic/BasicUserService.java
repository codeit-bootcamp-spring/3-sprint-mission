package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.user.FriendReqeustDTO;
import com.sprint.mission.discodeit.dto.user.UserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.*;
import com.sprint.mission.discodeit.exception.duplicate.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.duplicate.DuplicateNameException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserStatusException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("basicUserService")
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserRequestDTO userRequestDTO, BinaryContentDTO binaryContentDTO) {
        if (isDuplicateName(userRequestDTO.name())) {
            throw new DuplicateNameException(userRequestDTO.name());
        }

        if (isDuplicateEmail(userRequestDTO.email())) {
            throw new DuplicateEmailException(userRequestDTO.email());
        }

        User user = UserRequestDTO.toEntity(userRequestDTO);

        // 프로필 이미지를 등록한 경우
        if (binaryContentDTO != null) {
            BinaryContent profileImage = BinaryContentDTO.fromDTO(binaryContentDTO);
            user.updateProfileID(profileImage.getId());
            binaryContentRepository.save(profileImage);
        }

        UserStatus userStatus = new UserStatus(user.getId());

        userStatusRepository.save(userStatus);
        userRepository.save(user);

        return user;
    }

    @Override
    public UserResponseDTO findById(UUID id) {
        User user = findUser(id);

        UserStatus userStatus = findUserStatus(id);

        // 마지막 접속 시간 확인
        user.updateisLogin(userStatus.isLogin());

        return UserResponseDTO.toDTO(user);
    }

    @Override
    public UserResponseDTO findByName(String name) {
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new NotFoundUserException(name + " 유저를 찾을 수 없습니다."));

        UserStatus userStatus = findUserStatus(user.getId());

        user.updateisLogin(userStatus.isLogin());

        return UserResponseDTO.toDTO(user);
    }

    @Override
    public UserResponseDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException(email + "을 사용하는 유저를 찾을 수 없습니다."));

        UserStatus userStatus = findUserStatus(user.getId());

        user.updateisLogin(userStatus.isLogin());

        return UserResponseDTO.toDTO(user);
    }

    @Override
    public List<UserResponseDTO> findByNameContaining(String name) {
        return userRepository.findByNameContaining(name).stream()
                .map(user -> {
                    UserStatus userStatus = findUserStatus(user.getId());
                    user.updateisLogin(userStatus.isLogin());
                    return UserResponseDTO.toDTO(user);
                })
                .toList();
    }

    @Override
    public List<UserResponseDTO> findAll() {
        List<UserResponseDTO> users = userRepository.findAll().stream()
                .map(user -> {
                    UserStatus userStatus = findUserStatus(user.getId());
                    user.updateisLogin(userStatus.isLogin());
                    return UserResponseDTO.toDTO(user);
                })
                .toList();

        return users;
    }

    @Override
    public UserResponseDTO updateProfileImage(UUID id, BinaryContentDTO binaryContentDTO) {
        User user = findUser(id);
        // 기존 프로필 이미지의 아이디
        UUID profileImageId = user.getProfileId();
        binaryContentRepository.deleteById(profileImageId);

        // 프로필 이미지 변경
        if (binaryContentDTO != null) {
            BinaryContent profileImage = BinaryContentDTO.fromDTO(binaryContentDTO);
            user.updateProfileID(profileImage.getId());
            userRepository.save(user);
            binaryContentRepository.save(profileImage);
        } else { // 프로필 이미지 삭제
            user.updateProfileID(null);
            userRepository.save(user);
        }

        return UserResponseDTO.toDTO(user);
    }

    @Override
    public UserResponseDTO updateUserInfo(UUID id, UserRequestDTO userRequestDTO) {
        User user = findUser(id);

        user.updateName(userRequestDTO.name());
        user.updateEmail(userRequestDTO.email());
        user.updatePassword(userRequestDTO.password());
        user.updateIntroduction(userRequestDTO.introduction());

        userRepository.save(user);

        return UserResponseDTO.toDTO(user);
    }

    @Override
    public void deleteById(UUID id) {
        User user = findUser(id);

        userRepository.deleteById(id);
        userStatusRepository.deleteByUserId(id);
        binaryContentRepository.deleteById(user.getProfileId());
    }

    // 친구 추가 기능
    @Override
    public void addFriend(FriendReqeustDTO friendReqeustDTO) {
        User user1 = findUser(friendReqeustDTO.user1());
        User user2 = findUser(friendReqeustDTO.user2());

        // 두 User 각각의 friendList에 추가
        if (!user1.getFriends().contains(user2.getId())) {
            user1.getFriends().add(user2.getId());
            user2.getFriends().add(user1.getId());
        }

        // 변경사항 적용
        userRepository.save(user1);
        userRepository.save(user2);
    }

    // 친구 삭제 기능
    @Override
    public void deleteFriend(FriendReqeustDTO friendReqeustDTO) {
        User user1 = findUser(friendReqeustDTO.user1());
        User user2 = findUser(friendReqeustDTO.user2());

        if (!user1.getFriends().contains(user2.getId())) {
            throw new NotFriendsException(user1.getName() + "와(과) " + user2.getName() + "은 친구가 아닙니다.");
        }

        // 두 User 각각의 friendList에서 제거
        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());

        // 변경사항 적용
        userRepository.save(user1);
        userRepository.save(user2);
    }

    private boolean isDuplicateName(String name) {
        return userRepository.findByName(name).isPresent();
    }

    private boolean isDuplicateEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
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
