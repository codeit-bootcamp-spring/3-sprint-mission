package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserCreateResponse;
import com.sprint.mission.discodeit.Dto.user.UserFindResponse;
import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadRequest;
import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadResponse;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.service
 * fileName       : UserService2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public interface UserService {

    UserCreateResponse create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> profile);
//    UserCreateResponse create(UserCreateRequest userCreateRequest);

    UserFindResponse findUserById(UUID userId);

    List<UserFindResponse> findAllUsers();

    ProfileUploadResponse updateImage(ProfileUploadRequest request);
    // not required
    ResponseEntity<?> updateUser(UUID userId, String name);

    void deleteUser(UUID userId);

}
