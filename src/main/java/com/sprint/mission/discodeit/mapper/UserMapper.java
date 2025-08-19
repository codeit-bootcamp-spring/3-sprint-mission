package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final BinaryContentMapper binaryContentMapper;
    private final SessionRegistry sessionRegistry;

    public UserResponseDto toDto(User user) {
        BinaryContentResponseDto profileDto = null;

        if (user.getProfile() != null) {
            profileDto = binaryContentMapper.toDto(user.getProfile());
        }

        String username = user.getUsername();

        boolean online = isOnline(username);

        return new UserResponseDto(user.getId(), username, user.getEmail(),
            profileDto, online, user.getRole());
    }

    private boolean isOnline(String username) {
        List<Object> principals = sessionRegistry.getAllPrincipals();

        for (Object principal : principals) {
            UserDetails userDetails = (UserDetails) principal;
            String principalName = userDetails.getUsername();

            if (username.equals(principalName)) {
                return true;
            }
        }

        return false;
    }
}
