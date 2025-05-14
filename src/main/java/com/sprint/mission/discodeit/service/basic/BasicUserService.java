package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentType;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryOwnerType;
import com.sprint.mission.discodeit.entity.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.entity.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.dto.userStatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Primary
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;

    @Override
    public User createUser(UserCreateDto userCreateDto) {
        User user = userRepository.createUser(userCreateDto);

        UserStatusCreateRequestDto userStatusCreateRequestDto = new UserStatusCreateRequestDto(user.getId(), Instant.now());
        UserStatus userStatus = userStatusRepository.createUserStatus(userStatusCreateRequestDto);

        if (userCreateDto.getProfileImage() != null) {
            BinaryContentCreateRequestDto binaryContentCreateRequestDto = new BinaryContentCreateRequestDto(user.getId(), BinaryContentType.PROFILE_IMAGE, BinaryOwnerType.USER, userCreateDto.getProfileImage());
            BinaryContent binaryContent = binaryContentRepository.createBinaryContent(binaryContentCreateRequestDto);
            user.setProfileImage(binaryContent.getData());
        }
        return user;
    }

    @Override
    public Optional<UserResponseDto> getUser(UUID id) {
        Optional<User> user = userRepository.getUser(id);

        userStatusRepository.findAllStatus().forEach(userStatus -> {
            if (userStatus.getUserId().equals(id)) {
                user.get().setOnline(userStatus.isOnline(Instant.now()));
            }
        });

        BinaryContent profileImg = binaryContentRepository.findBinaryContentById(user.get().getId());
        user.get().setProfileImage(profileImg.getData());

        return Optional.of(createUserResponseDto(user.get()));
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<UserResponseDto> userResponseDtos = new ArrayList<>();

        userRepository.getAllUsers().forEach(user -> {
            BinaryContent profileImage = binaryContentRepository.findBinaryContentById(user.getId());
            user.setProfileImage(profileImage.getData());
            userResponseDtos.add(createUserResponseDto(user));
        });
        return userResponseDtos;
    }

    @Override
    public boolean modifyUser(UserUpdateRequestDto userUpdateRequestDto) {
        boolean isPasswordChange = userUpdateRequestDto.getOldPassword() != null
                && userUpdateRequestDto.getNewPassword() != null;
        boolean isProfileChange = userUpdateRequestDto.getUserProfile() != null
                && userUpdateRequestDto.getUserProfile().length > 0;

        boolean success = true;

        // 비밀번호 변경
        if (isPasswordChange) {
            success = userRepository.modifyUser(userUpdateRequestDto);
        }

        // 프로필 변경
        if (isProfileChange) {
//            바이너리 파일 수정 로직
            BinaryContentCreateRequestDto binaryContentCreateRequestDto = new BinaryContentCreateRequestDto(userUpdateRequestDto.getUserId(), BinaryContentType.PROFILE_IMAGE, BinaryOwnerType.USER, userUpdateRequestDto.getUserProfile());
            binaryContentRepository.createBinaryContent(binaryContentCreateRequestDto);
        }

        if (!isPasswordChange && !isProfileChange) {
            System.out.println("변경할 내용이 없습니다.");
            return false;
        }

        return success;
    }

    @Override
    public boolean deleteUser(UUID id) {
        Optional<User> userOpt = userRepository.getUser(id);

        if (userOpt.isEmpty()) {
            System.out.println("존재하지 않는 사용자입니다.");
            return false;
        }

        userOpt.ifPresent(user -> {
            user.getChannels().forEach(channel -> {
                channel.removeMember(user);
            });
            if (!user.getChannels().isEmpty()) {
                new HashSet<>(user.getChannels()).forEach(channel ->
                        channelRepository.deleteChannel(channel.getId(), user)
                );
            }
        });

        User user = userOpt.get();

        if (userRepository.deleteUser(id)) {
            // UserStatus 삭제
            userStatusRepository.findAllStatus().stream()
                    .filter(status -> status.getUserId().equals(id))
                    .forEach(status -> userStatusRepository.deleteUserStatus(status.getId()));

            // 바이너리 콘텐츠 삭제
            if (user.getProfileImage() != null) {
                binaryContentRepository.deleteBinaryContentById(user.getId());
            }

        }
        System.out.println("성공적으로 삭제되었습니다.");
        return true;
    }

    private UserResponseDto createUserResponseDto(User user) {
        return new UserResponseDto(user.getId(), user.getUserName(), user.isOnline(), user.getProfileImage(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
