package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserCreateResponse;
import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadRequest;
import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadResponse;
import com.sprint.mission.discodeit.Dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserFindResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.helper.FileUploadUtils;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.util.*;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : BasicUserService
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
@Primary
@Service("basicUserService")
@RequiredArgsConstructor
public class BasicUserService  implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final FileUploadUtils fileUploadUtils;


    @Override
    public ResponseEntity<?> create(
            UserCreateRequest userCreateRequest,
            Optional<BinaryContentCreateRequest> optionalProfileCreateRequest
    ) {

        boolean usernameNotUnique = !userRepository.isUniqueUsername(userCreateRequest.username());
        boolean emailNotUnique = !userRepository.isUniqueEmail(userCreateRequest.email());

        if (usernameNotUnique || emailNotUnique) {

            throw new IllegalStateException("not unique username or email");
        }


        BinaryContent nullableProfile = optionalProfileCreateRequest
                .map(
                        profileRequest -> {
                            String filename = profileRequest.fileName();
                            String contentType = profileRequest.contentType();
                            byte[] bytes = profileRequest.bytes();
                            String extension = profileRequest.fileName().substring(filename.lastIndexOf("."));
//                            binaryContentRepository.createBinaryContent(filename, (long) bytes.length, contentType, bytes);
                            return binaryContentRepository.createBinaryContent(filename, (long) bytes.length, contentType, bytes, extension);
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
            String uploadPath = fileUploadUtils.getUploadPath("img/profile");

            String originalFileName = nullableProfile.getFileName();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = nullableProfile.getId() + extension;

            File profileImage = new File(uploadPath, newFileName);
            // 사진 저장
            try(FileOutputStream fos = new FileOutputStream(profileImage)){
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
        UserStatus userStatus = userStatusRepository.createUserStatus(user.getId());

        UserCreateResponse userCreateResponse = new UserCreateResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                nullableProfile != null ? nullableProfile.getId() : null,
                userStatus.getId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreateResponse);
//        BinaryContent 생성 -> (분기)이미지 없을 경우 -> User 생성 -> userStatus 생성 -> return response
//                           -> (분기)이미지 있을 경우 -> User 생성 -> attachment 저장 -> userStatus 생성 -> return response
    }



    @Override
    public ResponseEntity<?> findUserById(UUID userId) {
        Objects.requireNonNull(userId, "User 아이디 입력 없음: BasicUserService.findUserById");
        User user = userRepository.findUserById(userId); // throw

        UserStatus userStatus = Optional.ofNullable(userStatusRepository.findUserStatusByUserId(userId)).orElseThrow(() -> new IllegalStateException("userStatus is null"));

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
            UserStatus userStatus = Optional.ofNullable(userStatusRepository.findUserStatusByUserId(user.getId()))
                    .orElseThrow(() -> new RuntimeException("user does not have a userStatus")); // userStatus has to exist (user:userStatus = 1:1)

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



    // ❗️❗️❗️수정 필요
    @Override
    public ProfileUploadResponse updateImage(ProfileUploadRequest request) {
//        UUID userId = request.userId();
//        byte[] newImage = request.image();
//        User user = userRepository.findUserById(userId); // throw
//        UUID profileId = userRepository.findUserById(userId).getProfileId(); // throw
//
//
//        if (profileId == null) {
//            // 없음 객체 생성
//            // binary content 생성
//            BinaryContent binaryContent = binaryContentRepository.createBinaryContent(newImage);
//            // 프로필 아이디 유저에 추가
//            userRepository.updateProfileIdById(userId, binaryContent.getId()); //throw
//
//            user = userRepository.findUserById(userId); //throw
//        } else{
//            // binary content 프로필 변경
//            binaryContentRepository.updateImage(profileId, newImage); // throw
//        }
//        return new ProfileUploadResponse(
//                user.getCreatedAt(),
//                user.getUpdatedAt(),
//                user.getId(),
//                user.getUsername(),
//                user.getEmail(),
//                user.getProfileId());
        return null;
    }

    // not required
    @Override
    public ResponseEntity<?> updateUser(UUID userId, String name) {
        Objects.requireNonNull(userId, "user 아이디 입력 없음: BasicUserService.updateUser");
        Objects.requireNonNull(name, "이름 입력 없음: BasicUserService.updateUser");
        userRepository.updateUserById(userId, name);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "name updated " + name));
    }


    @Override
    public void deleteUser(UUID userId) {
        Objects.requireNonNull(userId, "no user Id: BasicUserService.deleteUser");

        User user = userRepository.findUserById(userId); // throw

        UserStatus userStatus = Optional.ofNullable(userStatusRepository.findUserStatusByUserId(userId))
                .orElseThrow(() -> new IllegalArgumentException("no userStatus exist"));

        // UserStatus 삭제
        userStatusRepository.deleteById(userStatus.getId()); // throw

        if (user.getProfileId() != null) { // 프로필 있으면
            BinaryContent profile = binaryContentRepository.findById(user.getProfileId());

            String directory = fileUploadUtils.getUploadPath("img/profile");
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
    }
}
