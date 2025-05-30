package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.user.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.service fileName       : UserService2
 * author         : doungukkim date           : 2025. 4. 17. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 4. 17.        doungukkim 최초 생성
 */
public interface UserService {

    List<UserFindResponse> findAllUsers();

    CreateUserResponse create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> profile);

    void deleteUser(UUID userId);

    UpdateUserResponse update(UUID userId, UserUpdateRequest request, MultipartFile file);

    // not required
    ResponseEntity<?> findUserById(UUID userId);

    ResponseEntity<?> updateUser(UUID userId, String name);
}
