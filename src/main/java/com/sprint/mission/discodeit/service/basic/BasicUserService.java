package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.user.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.helper.FileUploadUtils;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : BasicUserService
 * author         : doungukkim
 * date           : 2025. 4. 17. description    :
 */
@Primary
@Service("basicUserService")
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

    private static final String PROFILE_PATH = "img/profile";
    private final JpaUserRepository userRepository;
    private final JpaBinaryContentRepository binaryContentRepository;
    private final JpaUserStatusRepository userStatusRepository;
    private final FileUploadUtils fileUploadUtils;


    @Transactional(readOnly = true)
    public List<UserFindResponse> findAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserFindResponse> userFindResponses = new ArrayList<>();
        // user fields + online 으로 response 생성
        for (User user : users) {


            UserStatus userStatus = Optional.ofNullable(userStatusRepository.findByUserId(user.getId()))
                    .orElseThrow(()->new RuntimeException("User status not found"));


            userFindResponses.add(new UserFindResponse(
                    user.getId(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfile(),
                    isOnline(userStatus)));
        }
        return userFindResponses;
    }


    @Override
    public CreateUserResponse create(
            UserCreateRequest userCreateRequest,
            Optional<BinaryContentCreateRequest> profile
    ) {
        boolean usernameNotUnique =userRepository.existsByUsername(userCreateRequest.username());
        boolean emailNotUnique = userRepository.existsByEmail(userCreateRequest.email());

        if (usernameNotUnique || emailNotUnique) {
            throw new IllegalArgumentException("User with email " + userCreateRequest.email() + " already exitsts");
        }

        BinaryContent nullableProfile = profile
                .map(
                        profileRequest -> {
                            String filename = profileRequest.fileName();
                            String contentType = profileRequest.contentType();
                            byte[] bytes = profileRequest.bytes();
                            String extension = profileRequest.fileName().substring(filename.lastIndexOf("."));
                            BinaryContent binaryContent = new BinaryContent(
                                    filename,
                                    (long)bytes.length,
                                    contentType,
                                    bytes,
                                    extension);
                            binaryContentRepository.save(binaryContent);
                            return binaryContent;
                        }
                ).orElse(null);

        User user;
        if (nullableProfile == null) {
            user = new User(userCreateRequest.username(), userCreateRequest.email(), userCreateRequest.password());
            userRepository.save(user);
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
            user = new User(
                    userCreateRequest.username(),
                    userCreateRequest.email(),
                    userCreateRequest.password(),
                    nullableProfile);
            userRepository.save(user);
        }
        // USER STATUS
        UserStatus userStatus = new UserStatus(user);
        userStatusRepository.save(userStatus);
        user.setUserStatus(userStatus); // 양방향성을 위한 주입

//
        CreateUserResponse createUserResponse = new CreateUserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                nullableProfile != null ? nullableProfile.getId() : null
        );
        return createUserResponse;
//        BinaryContent 생성 -> (분기)이미지 없을 경우 -> User 생성 -> userStatus 생성 -> return response
//                           -> (분기)이미지 있을 경우 -> User 생성 -> attachment 저장 -> userStatus 생성 -> return response
    }

    @Override
    public void deleteUser(UUID userId) {
        Objects.requireNonNull(userId, "no user Id: BasicUserService.deleteUser");

        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("sdf"));

//        UserStatus userStatus = Optional.ofNullable(userStatusRepository.findUserStatusByUserId(userId))
//                .orElseThrow(() -> new IllegalArgumentException("no userStatus exist: you have to think about why the user does not have userStatus might be deleted or not created"));
//
//        // UserStatus 삭제
//        userStatusRepository.deleteById(userStatus.getId()); // throw

        if (user.getProfile() != null) { // 프로필 있으면
            BinaryContent profile = user.getProfile();

            String directory = fileUploadUtils.getUploadPath(PROFILE_PATH);
            String extension = profile.getExtension();
            String fileName = profile.getId() + extension;
            File file = new File(directory, fileName);

            if (file.exists()) {
                boolean delete = file.delete();
                if (!delete) {
                    throw new RuntimeException("could not delete file");
                }
            }
            // BinaryContent 삭제
//            binaryContentRepository.deleteBinaryContentById(user.getProfileId()); // throw
        }
//        // User 삭제
//        userRepository2.deleteUserById(userId); // throw
        userRepository.delete(user);
    }

    // name, email, password 수정 image는 optional
    @Transactional
    @Override
    public UpdateUserResponse update(UUID userId, UserUpdateRequest request, MultipartFile file) {

        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("user with id " + userId + " not found"));

        String oldName = user.getUsername();
        String oldEmail = user.getEmail();
        String newName = request.newUsername();
        String newEmail = request.newEmail();
//
        if (newName == null || newName.isBlank()) {
            newName = oldName;
        }
        if (newEmail == null || newEmail.isBlank()) {
            newEmail = oldEmail;
        }


        // name : 있으면 400
        if (userRepository.existsByUsername(newName) && (!oldName.equals(newName))) { // 있고 내 이름도 아닌경우
            throw new IllegalArgumentException("user with name" + request.newUsername() + " already exists");
        }
        user.setUsername(newName);

        // email: 있으면 400
        if (userRepository.existsByEmail(newEmail) && (!oldEmail.equals(newEmail))) { // 있고 내 이메일이 아닌경우
            throw new IllegalArgumentException("user with email " + request.newPassword() + " already exists");
        }
        user.setEmail(newEmail);

        // password: 없으면 내버려두고 있으면 수정
        if (request.newPassword() != null) {
            user.setPassword(request.newPassword());
        }
        // 메모리 유저정보 업데이트
//        user = userRepository2.findUserById(userId);

        // 프로필 여부 확인 (있으면 삭제 후 추가)
        if (hasValue(file)) {
            if (user.getProfile() != null) {
                // delete file
//                BinaryContent profile = binaryContentRepository.findById(user.getProfileId());

                String directory = fileUploadUtils.getUploadPath(PROFILE_PATH);
                String extension = user.getProfile().getExtension();
                String fileName = user.getProfile().getId() + extension;
                File oldFile = new File(directory, fileName);

                if (oldFile.exists()) {
                    boolean delete = oldFile.delete();
                    if (!delete) {
                        throw new RuntimeException("could not delete file");
                    }
                }
                // BinaryContent 삭제
                binaryContentRepository.delete(user.getProfile());
            }

            // binary content
            BinaryContent binaryContent;
            try {
                String filename = file.getOriginalFilename();
                String contentType = file.getContentType();
                String extension = file.getOriginalFilename().substring(filename.lastIndexOf("."));

                byte[] bytes = file.getBytes();
                binaryContent = new BinaryContent(filename,(long)bytes.length,contentType,bytes,extension);
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
            user.setProfile(binaryContent);
        }


        UpdateUserResponse response = new UpdateUserResponse(
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getProfile()
        );

        return response;
//        // 파일 확인(있음) -> 파일 삭제 -> binary content 삭제 -> binary content 추가 -> 파일 생성 -> user 업데이트
//        // 파일 확인(없음) ->                                  -> binary content 추가 -> 파일 생성 -> user 업데이트

    }


    @Override
    public ResponseEntity<?> findUserById(UUID userId) {
//        Objects.requireNonNull(userId, "User 아이디 입력 없음: BasicUserService.findUserById");
//        User user = userRepository2.findUserById(userId); // throw
//
//        UserStatus userStatus = Optional.ofNullable(userStatusRepository.findUserStatusByUserId(userId))
//                .orElseThrow(() -> new IllegalStateException("userStatus is null"));
//
//        boolean online = userStatusRepository.isOnline(userStatus.getId()); // throw
//
//        UserFindResponse userFindResponse = new UserFindResponse(
//                user.getId(),
//                user.getCreatedAt(),
//                user.getUpdatedAt(),
//                user.getUsername(),
//                user.getEmail(),
//                user.getProfileId(),
//                online);
//        return ResponseEntity.status(HttpStatus.OK).body(userFindResponse);
        return null;
    }


    private static boolean isOnline(UserStatus userStatus) {
        Instant now = Instant.now();
        return Duration.between(userStatus.getLastActiveAt(), now).toMillis() < 5;
    }

    private boolean hasValue(MultipartFile attachmentFiles) {
        return (attachmentFiles != null) && (!attachmentFiles.isEmpty());
    }


    // not required
    @Override
    public ResponseEntity<?> updateUser(UUID userId, String name) {
//        Objects.requireNonNull(userId, "user 아이디 입력 없음: BasicUserService.updateUser");
//        Objects.requireNonNull(name, "이름 입력 없음: BasicUserService.updateUser");
//        userRepository2.updateNameById(userId, name);
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(Map.of("message", "name updated " + name));
        return null;
    }

}
