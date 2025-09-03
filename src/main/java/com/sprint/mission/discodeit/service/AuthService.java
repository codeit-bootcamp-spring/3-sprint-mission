package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;

public interface AuthService {

    UserResponse updateUserRole(UserRoleUpdateRequest request);

}
