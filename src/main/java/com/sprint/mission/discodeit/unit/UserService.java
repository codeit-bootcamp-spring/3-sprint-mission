package com.sprint.mission.discodeit.unit;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
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

    List<JpaUserResponse> findAllUsers();

    JpaUserResponse create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> profile);

    JpaUserResponse update(UUID userId, UserUpdateRequest request, MultipartFile file);

    void deleteUser(UUID userId);

}
