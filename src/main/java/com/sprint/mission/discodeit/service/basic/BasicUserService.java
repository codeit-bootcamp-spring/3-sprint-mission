package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.entity.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.dto.user.UserUpdateRequestDto;
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
    public UserResponseDto createUser(UserCreateDto userCreateDto, BinaryContentCreateRequestDto binaryContentCreateRequestDto) {
        User user = new User(userCreateDto);
        userRepository.createUser(user);

        UserStatus userStatus = new UserStatus(user.getId(), Instant.now());
        userStatusRepository.createUserStatus(userStatus);

        if (binaryContentCreateRequestDto.getData() != null) {
            BinaryContent binaryContent = new BinaryContent(user.getId(), binaryContentCreateRequestDto.getContentType(), binaryContentCreateRequestDto.getFileContentType(), binaryContentCreateRequestDto.getData().get(0));
            binaryContentRepository.createBinaryContent(binaryContent);
            user.setProfileImage(binaryContent.getData());
        }
        return createUserResponseDto(user);
    }

    @Override
    public Optional<UserResponseDto> getUser(UUID id) {
        User user = userRepository.getUser(id).orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저 입니다."));

        BinaryContent profileImg = binaryContentRepository.findProfileImageByOwnerId(user.getId());
        user.setProfileImage(profileImg.getData());

        return Optional.of(createUserResponseDto(user));
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<UserResponseDto> userResponseDtos = new ArrayList<>();

        userRepository.getAllUsers().forEach(user -> {
            BinaryContent profileImage = binaryContentRepository.findProfileImageByOwnerId(user.getId());
            user.setProfileImage(profileImage.getData());
            userResponseDtos.add(createUserResponseDto(user));
        });
        return userResponseDtos;
    }

    @Override
    public boolean modifyUser(UserUpdateRequestDto userUpdateRequestDto, BinaryContentCreateRequestDto binaryContentCreateRequestDto) {
        boolean isPasswordChange = userUpdateRequestDto.getOldPassword() != null
                && userUpdateRequestDto.getNewPassword() != null;
        boolean isProfileChange = userUpdateRequestDto.getUserProfile() != null
                && userUpdateRequestDto.getUserProfile().length > 0;

        boolean success = true;

        User user = userRepository.getUser(userUpdateRequestDto.getUserId()).orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저 입니다."));

        // 비밀번호 변경
        if (isPasswordChange) {
            if (!user.getPassword().equals(userUpdateRequestDto.getOldPassword())) {
                user.setPassword(userUpdateRequestDto.getNewPassword());
                success = userRepository.modifyUser(user);
            } else {
                success = false;
            }
        }

        // 프로필 변경
        if (isProfileChange) {
//            바이너리 파일 수정 로직
            BinaryContent binaryContent = new BinaryContent(userUpdateRequestDto.getUserId(), binaryContentCreateRequestDto.getContentType(), binaryContentCreateRequestDto.getFileContentType(), binaryContentCreateRequestDto.getData().get(0));
            binaryContentRepository.createBinaryContent(binaryContent);
        }

        if (!isPasswordChange && !isProfileChange) {
            System.out.println("변경할 내용이 없습니다.");
            return false;
        }

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException(("존재하지 않는 데이터 입니다.")));
        userStatus.update(Instant.now());
        userStatusRepository.updateUserStatus(userStatus);

        return success;
    }

    @Override
    public boolean deleteUser(UUID id) {
        User user = userRepository.getUser(id).orElseThrow(() -> new NoSuchElementException("존재 하지 않는 유저입니다."));

        user.getChannels().forEach(channel -> {
            channel.removeMember(user);
        });
        if (!user.getChannels().isEmpty()) {
            new HashSet<>(user.getChannels()).forEach(channel ->
                    channelRepository.deleteChannel(channel.getId(), user.getId())
            );
        }

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
        return true;
    }

    private UserResponseDto createUserResponseDto(User user) {
        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserResponseDto(
                user.getId(),
                user.getUserName(),
                isOnline,
                user.getProfileImage(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
