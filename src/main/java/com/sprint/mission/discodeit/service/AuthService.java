package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.user.RoleUpdateRequest;

public interface AuthService {

    UserDto getCurrentUserInfo(DiscodeitUserDetails userDetails);

    UserDto updateRole(RoleUpdateRequest request);
}
