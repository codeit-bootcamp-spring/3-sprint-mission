package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.Dto.user.UserCreateResponse;
import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadRequest;
import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadResponse;
import com.sprint.mission.discodeit.Dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserFindResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.file
 * fileName       : FileUserService
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
@Service
@Profile("file")
@RequiredArgsConstructor
public class FileUserService implements UserService {

    private final FileUserRepository fileUserRepository;
    private final FileBinaryContentRepository fileBinaryContentRepository;
    private final FileUserStatusRepository fileUserStatusRepository;


    // 이미지 저장 로직 추가
    @Override
    public UserCreateResponse create(UserCreateRequest userCreateDto) {
        Objects.requireNonNull(userCreateDto.getUsername(), "no name in parameter: BasicUserService.createUser");
        Objects.requireNonNull(userCreateDto.getEmail(), "no email in parameter: BasicUserService.createUser");
        Objects.requireNonNull(userCreateDto.getPassword(), "no password in parameter: BasicUserService.createUser");

        if ((!fileUserRepository.isUniqueUsername(userCreateDto.getUsername())) || (!fileUserRepository.isUniqueEmail(userCreateDto.getEmail()))) {
            throw new RuntimeException("not unique username or email");
        }

        // 이미지 여부 확인
        if (userCreateDto.getImage() == null) {
            // 이미지 처리 없음
            User user = fileUserRepository.createUserByName(userCreateDto.getUsername(), userCreateDto.getEmail(), userCreateDto.getPassword());
            UserStatus userStatus = fileUserStatusRepository.createUserStatus(user.getId());
            return new UserCreateResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getProfileId(),
                    userStatus.getId()
            );
        } else {
            // 이미지 처리 있음
            UUID binaryContentId = fileBinaryContentRepository.createBinaryContent(userCreateDto.getImage()).getId();
            User user = fileUserRepository.createUserByName(userCreateDto.getUsername(), userCreateDto.getEmail(), userCreateDto.getPassword(), binaryContentId);
            UserStatus userStatus = fileUserStatusRepository.createUserStatus(user.getId());
            return new UserCreateResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getProfileId(),
                    userStatus.getId()
            );
        }
    }

    @Override
    public UserFindResponse findUserById(UUID userId) {
        Objects.requireNonNull(userId, "User 아이디 입력 없음: FileUserService.findUserById");
        User user = fileUserRepository.findUserById(userId);
        Objects.requireNonNull(user, "찾는 User 없음: FileUserService.findUserById");
        // 온라인 확인 로직
        // 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.
        UserStatus userStatus = fileUserStatusRepository.findUserStatusByUserId(userId);


        if (userStatus == null) {
            throw new RuntimeException("userStatus is null");
        }

        boolean online = fileUserStatusRepository.isOnline(userStatus.getId());


        UserFindResponse userFindDto = new UserFindResponse(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getUsername(), user.getEmail(), user.getProfileId(), online);

        return userFindDto;
    }

    @Override
    public List<UserFindResponse> findAllUsers() {
        List<User> users = fileUserRepository.findAllUsers();
        List<UserFindResponse> userFindDtos = new ArrayList<>();

        // userDto를 (users + online)으로 매핑
        for (User user : users) {
            UserStatus status = fileUserStatusRepository.findUserStatusByUserId(user.getId());
            boolean online = false;
            if (status != null) {
                online = fileUserStatusRepository.isOnline(status.getId());
            }

            userFindDtos.add(new UserFindResponse(
                    user.getId(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfileId(),
                    online));
        }

        return userFindDtos;
    }


    @Override
    public ProfileUploadResponse updateImage(ProfileUploadRequest request) {
        UUID userId = request.userId();
        byte[] newImage = request.image();
        User user = fileUserRepository.findUserById(userId);
        UUID profileId = findUserById(userId).profileId();


        if (profileId == null) {
            // 없음 객체 생성
            // binary content 생성
            BinaryContent binaryContent = fileBinaryContentRepository.createBinaryContent(newImage);
            // 프로필 아이디 유저에 추가
            fileUserRepository.updateProfileIdById(userId, binaryContent.getId());

            user = fileUserRepository.findUserById(userId);
        } else{
            // binary content 프로필 변경
            fileBinaryContentRepository.updateImage(profileId, newImage);
        }
        return new ProfileUploadResponse(
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId());
    }

    @Override
    public void updateUser(UUID userId, String name) {
        Objects.requireNonNull(userId, "user 아이디 입력 없음: FileUserService.updateUser");
        Objects.requireNonNull(name, "이름 입력 없음: FileUserService.updateUser");
        fileUserRepository.updateUserById(userId, name);
    }

    @Override
    public void deleteUser(UUID userId) {
        Objects.requireNonNull(userId, "no user Id: FileUserService.deleteUser");
        User user = fileUserRepository.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("잘못된 유저아이디");
        }

        // UserStatus 삭제
        UserStatus userStatus = fileUserStatusRepository.findUserStatusByUserId(userId);
        if (userStatus == null) {
            throw new RuntimeException("no userStatus");
        }

        // BinaryContent 삭제
        if (user.getProfileId() != null) {
            fileBinaryContentRepository.deleteBinaryContentById(user.getProfileId());
        }

        fileUserStatusRepository.deleteById(userStatus.getId());
        // User 삭제
        fileUserRepository.deleteUserById(userId);
    }
}
