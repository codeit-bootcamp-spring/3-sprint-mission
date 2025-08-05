package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {

    UserDto getCurrentUserInfo(UserDetails userDetails);

    UserDto updateUserRole(RoleUpdateRequest roleUpdateRequest);
}
