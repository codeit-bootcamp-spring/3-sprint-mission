package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final BinaryContentMapper binaryContentMapper;
    private final SessionRegistry sessionRegistry;

    public UserResponse toResponse(User user) {
        BinaryContentDto profileDto = null;
        if (user.getProfile() != null) {
            profileDto = binaryContentMapper.toDto(user.getProfile());
        }

        boolean online = isOnline(user);

        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            profileDto,
            online,
            user.getRole()
        );
    }

    private boolean isOnline(User user) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {

            if (principal instanceof DiscodeitUserDetails dud) {
                try {
                    UUID principalId = dud.getUserResponse().id();
                    if (Objects.equals(principalId, user.getId())
                        && !sessionRegistry.getAllSessions(principal, false).isEmpty()) {
                        return true;
                    }
                } catch (Throwable ignore) {
                }
            }

            if (principal instanceof UserDetails ud) {
                if (user.getUsername().equals(ud.getUsername())
                    && !sessionRegistry.getAllSessions(principal, false).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
}
