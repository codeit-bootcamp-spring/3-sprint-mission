package com.sprint.mission.discodeit.unit.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.helper.FileUploadUtils;
import com.sprint.mission.discodeit.mapper.advanced.UserMapper;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserStatusRepository;
import com.sprint.mission.discodeit.unit.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

    private static final String PROFILE_PATH = "img";
    private final JpaUserRepository userRepository;
    private final JpaBinaryContentRepository binaryContentRepository;
    private final JpaUserStatusRepository userStatusRepository;
    private final FileUploadUtils fileUploadUtils;
    private final UserMapper userMapper;
    private final BinaryContentStorage binaryContentStorage;

    private static final Logger log= LoggerFactory.getLogger(BasicUserService.class);


    @Transactional(readOnly = true)
    public List<JpaUserResponse> findAllUsers() {
        List<User> users = userRepository.findAllWithBinaryContentAndUserStatus();

        List<JpaUserResponse> responses = new ArrayList<>();
        // user fields + online 으로 response 생성
        for (User user : users) {
            userMapper.toDto(user);
            responses.add(userMapper.toDto(user));
        }
        return responses;
    }


    @Override
    public JpaUserResponse create(
            UserCreateRequest userCreateRequest,
            Optional<BinaryContentCreateRequest> profile
    ) {
        boolean usernameNotUnique = userRepository.existsByUsername(userCreateRequest.username());
        boolean emailNotUnique = userRepository.existsByEmail(userCreateRequest.email());

        if (usernameNotUnique || emailNotUnique) {
            if (usernameNotUnique) {
                throw new UserAlreadyExistsException(Map.of("username", userCreateRequest.username()));
            } else {
                throw new UserAlreadyExistsException(Map.of("email", userCreateRequest.email()));
            }
        }

        log.info("profile image is " + profile.map(BinaryContentCreateRequest::fileName).stream().findFirst().orElse(null));

        BinaryContent nullableProfile = profile
            .map(
                profileRequest -> {
                    String filename = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    String extension = profileRequest.fileName().substring(filename.lastIndexOf("."));

                    BinaryContent binaryContent = new BinaryContent(filename, (long) bytes.length, contentType, extension);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    return binaryContent;
                }
            ).orElse(null);

        User user;
        if (nullableProfile == null) {
            user = new User(userCreateRequest.username(), userCreateRequest.email(), userCreateRequest.password());
            userRepository.save(user);
        } else {
            // USER 객체 생성
            user = User.builder()
                .username(userCreateRequest.username())
                .email(userCreateRequest.email())
                .password(userCreateRequest.password())
                .profile(nullableProfile)
                .build();
            userRepository.save(user);
        }
        // USER STATUS
        UserStatus userStatus = new UserStatus(user);
        userStatusRepository.save(userStatus);
        user.setUserStatus(userStatus); // 양방향성을 위한 주입

        JpaUserResponse response = userMapper.toDto(user);
        return response;
//        BinaryContent 생성 -> (분기)이미지 없을 경우 -> User 생성 -> userStatus 생성 -> return response
//                           -> (분기)이미지 있을 경우 -> User 생성 -> attachment 저장 -> userStatus 생성 -> return response
    }

    @Override
    public void deleteUser(UUID userId) {
        Objects.requireNonNull(userId, "no user Id: BasicUserService.deleteUser");

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(Map.of("userId ", userId)));

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
        }
        // User 삭제
        userRepository.delete(user);
    }

    // name, email, password 수정 image는 optional
    @Transactional
    @Override
    public JpaUserResponse update(UUID userId, UserUpdateRequest request, MultipartFile file) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(Map.of("userId ", userId)));

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
        if (userRepository.existsByUsername(newName) && (!oldName.equals(newName))) { // 있고 내 이름도 아닌경우
            throw new  UserAlreadyExistsException(Map.of("username", newName));
        }
        user.setUsername(newName);

        // email: 있으면 400
        if (userRepository.existsByEmail(newEmail) && (!oldEmail.equals(newEmail))) { // 있고 내 이메일이 아닌경우
            throw new  UserAlreadyExistsException(Map.of("email", newEmail));
        }
        user.setEmail(newEmail);

        // password: 없으면 내버려두고 있으면 수정
        if (request.newPassword() != null) {
            user.setPassword(request.newPassword());
        }

        // 프로필 여부 확인 (있으면 삭제 후 추가)
        if (hasValue(file)) {
            if (user.getProfile() != null) {
                // delete file
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
                binaryContent = new BinaryContent(filename, (long) bytes.length, contentType, extension);
                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // update user
            user.setProfile(binaryContent);
        }

        JpaUserResponse response = userMapper.toDto(user);
        return response;
//        // 파일 확인(있음) -> 파일 삭제 -> binary content 삭제 -> binary content 추가 -> 파일 생성 -> user 업데이트
//        // 파일 확인(없음) ->                                  -> binary content 추가 -> 파일 생성 -> user 업데이트

    }

    private boolean hasValue(MultipartFile attachmentFiles) {
        return (attachmentFiles != null) && (!attachmentFiles.isEmpty());
    }
}
