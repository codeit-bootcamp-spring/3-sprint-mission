package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;

public interface AuthService {

    UserDto updateUserRole(RoleUpdateRequest request);

    UserDto updateRoleInternal(RoleUpdateRequest roleUpdateRequest);

    JwtInformation refreshToken(String refreshToken);
}
