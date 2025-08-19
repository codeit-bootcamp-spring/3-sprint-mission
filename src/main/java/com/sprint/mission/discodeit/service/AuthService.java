package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {

    UserResponseDto getCurrentUser(UserDetails userDetails);
}
