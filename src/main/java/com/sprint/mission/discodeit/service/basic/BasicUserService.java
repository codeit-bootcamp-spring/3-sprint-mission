package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.user.CreateUserResponse;
import com.sprint.mission.discodeit.Dto.user.UpdateUserResponse;
import com.sprint.mission.discodeit.Dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.Dto.user.UserFindResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.helper.FileUploadUtils;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic fileName       : BasicUserService
 * author         : doungukkim date           : 2025. 4. 17. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 4. 17.        doungukkim 최초 생성
 */
@Primary
@Service("basicUserService")
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private static final String PROFILE_PATH = "img/profile";
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final FileUploadUtils fileUploadUtils;

//    private final String imagePath = fileUploadUtils.getUploadPath(PROFILE_PATH);


    @Override
    public ResponseEntity<?> create(
            UserCreateRequest userCreateRequest,
            Optional<BinaryContentCreateRequest> profile
    ) {

        boolean usernameNotUnique = !userRepository.isUniqueUsername(userCreateRequest.username());
        boolean emailNotUnique = !userRepository.isUniqueEmail(userCreateRequest.email());

        if (usernameNotUnique || emailNotUnique) {
            return ResponseEntity.status(400)
                    .body("User with email " + userCreateRequest.email() + " already exitsts");
        }

        BinaryContent nullableProfile = profile
                .map(
                        profileRequest -> {
                            String filename = profileRequest.fileName();
                            String contentType = profileRequest.contentType();
                            byte[] bytes = profileRequest.bytes();
                            String extension = profileRequest.fileName().substring(filename.lastIndexOf("."));
                            return binaryContentRepository.createBinaryContent(filename, (long) bytes.length,
                                    contentType, bytes, extension);
                        }
                ).orElse(null);

        User user;
        if (nullableProfile == null) {
            user = userRepository.createUserByName(
                    userCreateRequest.username(),
                    userCreateRequest.email(),
                    userCreateRequest.password());
        } else {
            // 사진 저장 로직 (BinaryContent 객체 생성 -> 유저 생성 -> 파일 BinaryContent ID로 저장)
            String uploadPath = fileUploadUtils.getUploadPath(PROFILE_PATH);

            String originalFileName = nullableProfile.getFileName();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = nullableProfile.getId() + extension;

            File profileImage = new File(uploadPath, newFileName);
            // 사진 저장
            try (FileOutputStream fos = new FileOutputStream(profileImage)) {
                fos.write(nullableProfile.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("image not saved", e);
            }

            // USER 객체 생성
            user = userRepository.createUserByName(
                    userCreateRequest.username(),
                    userCreateRequest.email(),
                    userCreateRequest.password(),
                    nullableProfile.getId());
        }
        // USER STATUS
        userStatusRepository.createUserStatus(user.getId());

        CreateUserResponse createUserResponse = new CreateUserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                nullableProfile != null ? nullableProfile.getId() : null
        );
        return ResponseEntity.status(201).body(createUserResponse);
//        BinaryContent 생성 -> (분기)이미지 없을 경우 -> User 생성 -> userStatus 생성 -> return response
//                           -> (분기)이미지 있을 경우 -> User 생성 -> attachment 저장 -> userStatus 생성 -> return response
    }


    @Override
    public ResponseEntity<?> findUserById(UUID userId) {
        Objects.requireNonNull(userId, "User 아이디 입력 없음: BasicUserService.findUserById");
        User user = userRepository.findUserById(userId); // throw

        UserStatus userStatus = Optional.ofNullable(userStatusRepository.findUserStatusByUserId(userId))
                .orElseThrow(() -> new IllegalStateException("userStatus is null"));

        boolean online = userStatusRepository.isOnline(userStatus.getId()); // throw

        UserFindResponse userFindResponse = new UserFindResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                online);
        return ResponseEntity.status(HttpStatus.OK).body(userFindResponse);
    }


    @Override
    public ResponseEntity<?> findAllUsers() {
        List<User> users = userRepository.findAllUsers(); // emptyList
        List<UserFindResponse> userFindResponses = new ArrayList<>();

        // user fields + online 으로 response 생성
        for (User user : users) {
            UserStatus userStatus = Optional.ofNullable(
                            userStatusRepository.findUserStatusByUserId(user.getId()))
                    .orElseThrow(() -> new RuntimeException(
                            "user does not have a userStatus")); // userStatus has to exist (user:userStatus = 1:1)

            boolean online = userStatusRepository.isOnline(userStatus.getId());

            userFindResponses.add(new UserFindResponse(
                    user.getId(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfileId(),
                    online));
        }
        return ResponseEntity.status(HttpStatus.OK).body(userFindResponses);
    }

    // name, email, password 수정 image는 optional
    @Override
    public ResponseEntity<?> update(UUID userId, UserUpdateRequest request, MultipartFile file) {

        User user = userRepository.findUserById(userId);
        if (user == null) {
            return ResponseEntity.status(404).body("user with id " + userId + " not found");
        }

        String oldName = user.getUsername();
        String oldEmail = user.getEmail();
        String newName = request.newUsername();
        String newEmail = request.newEmail();

        if (newName == null || newName.isBlank()) {
            newName = oldName;
        }
        if (newEmail == null || newEmail.isBlank()) {
            newEmail = oldEmail;
        }


        // name : 있으면 400
        if (userRepository.hasSameName(newName) && (!oldName.equals(newName))) { // 있고 내 이름도 아닌경우
            return ResponseEntity.status(400).body("user with name" + request.newUsername() + " already exists");
        }
        userRepository.updateNameById(user.getId(), newName);

        // email: 있으면 400
        if (userRepository.hasSameEmail(newEmail) && (!oldEmail.equals(newEmail))) { // 있고 내 이메일이 아닌경우
            return ResponseEntity.status(400).body("user with email " + request.newPassword() + " already exists");
        }
        userRepository.updateEmailById(user.getId(), newEmail);


        // password: 없으면 내버려두고 있으면 수정
        if (request.newPassword() != null) {
            userRepository.updatePasswordById(userId, request.newPassword());
        }
        // 메모리 유저정보 업데이트
        user = userRepository.findUserById(userId);
        System.out.println(user.toString());
        // 프로필 여부 확인 (있으면 삭제 후 추가)
        if (user.getProfileId() != null) {
            System.out.println("프로필 있음 삭제를 시작합니다.");
            // delete file
            BinaryContent profile = binaryContentRepository.findById(user.getProfileId());

            String directory = fileUploadUtils.getUploadPath(PROFILE_PATH);
            String extension = profile.getExtension();
            String fileName = user.getProfileId() + extension;
            File oldFile = new File(directory, fileName);

            if (oldFile.exists()) {
                boolean delete = oldFile.delete();
                if (!delete) {
                    throw new RuntimeException("could not delete file");
                }
            }
            // BinaryContent 삭제
            binaryContentRepository.deleteBinaryContentById(user.getProfileId());
            System.out.println("삭제완료");
        }
        // 수정중...-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        // binary content
        if (hasValue(file)) {
            BinaryContent binaryContent;
            try {
                String filename = file.getOriginalFilename();
                String contentType = file.getContentType();
                String extension = file.getOriginalFilename().substring(filename.lastIndexOf("."));

                byte[] bytes = file.getBytes();
                binaryContent = binaryContentRepository.createBinaryContent(filename, (long) bytes.length,
                        contentType, bytes, extension);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // add file
            String uploadPath = fileUploadUtils.getUploadPath(PROFILE_PATH);

            String originalFileName = binaryContent.getFileName();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = binaryContent.getId() + extension;

            File profileImage = new File(uploadPath, newFileName);

            try (FileOutputStream fos = new FileOutputStream(profileImage)) {
                fos.write(binaryContent.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("image not saved", e);
            }
            // update user
            userRepository.updateProfileIdById(userId, binaryContent.getId());
        }

        User userForBody = userRepository.findUserById(userId);

        UpdateUserResponse response = new UpdateUserResponse(
                userForBody.getCreatedAt(),
                userForBody.getUpdatedAt(),
                userForBody.getUsername(),
                userForBody.getEmail(),
                userForBody.getPassword(),
                userForBody.getProfileId()
        );

        return ResponseEntity.status(200)
                .body(response);
        // 파일 확인(있음) -> 파일 삭제 -> binary content 삭제 -> binary content 추가 -> 파일 생성 -> user 업데이트
        // 파일 확인(없음) ->                                  -> binary content 추가 -> 파일 생성 -> user 업데이트
    }

    private boolean hasValue(MultipartFile attachmentFiles) {
        return (attachmentFiles != null) && (!attachmentFiles.isEmpty());
    }


    // not required
    @Override
    public ResponseEntity<?> updateUser(UUID userId, String name) {
        Objects.requireNonNull(userId, "user 아이디 입력 없음: BasicUserService.updateUser");
        Objects.requireNonNull(name, "이름 입력 없음: BasicUserService.updateUser");
        userRepository.updateNameById(userId, name);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "name updated " + name));
    }


    @Override
    public ResponseEntity<?> deleteUser(UUID userId) {
        Objects.requireNonNull(userId, "no user Id: BasicUserService.deleteUser");

        User user = userRepository.findUserById(userId);

        if (user == null) return ResponseEntity.status(404).body("User with id " + userId + "not found");

        UserStatus userStatus = Optional.ofNullable(userStatusRepository.findUserStatusByUserId(userId))
                .orElseThrow(() -> new IllegalArgumentException("no userStatus exist: you have to think about why the user does not have userStatus might be deleted or not created"));

        // UserStatus 삭제
        userStatusRepository.deleteById(userStatus.getId()); // throw

        if (user.getProfileId() != null) { // 프로필 있으면
            BinaryContent profile = binaryContentRepository.findById(user.getProfileId());

            String directory = fileUploadUtils.getUploadPath(PROFILE_PATH);
            String extension = profile.getExtension();
            String fileName = user.getProfileId() + extension;
            File file = new File(directory, fileName);

            if (file.exists()) {
                boolean delete = file.delete();
                if (!delete) {
                    throw new RuntimeException("could not delete file");
                }
            }
            // BinaryContent 삭제
            binaryContentRepository.deleteBinaryContentById(user.getProfileId()); // throw
        }
        // User 삭제
        userRepository.deleteUserById(userId); // throw
        return ResponseEntity.status(204).body("삭제 완료");
    }

    private boolean uploadFIle(BinaryContent profile) {
        return false;
    }


    private boolean deleteFile(BinaryContent profile) {
        return false;
    }
}
