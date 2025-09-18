package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;

    @Override
    public UserDto getCurrentUserInfo(DiscodeitUserDetails discodeitUserDetails) {
        if (discodeitUserDetails == null) {
            return null;
        }

        String username = discodeitUserDetails.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return userMapper.toDto(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserDto updateRole(UUID uuid, Role role) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));

        user.updateRole(role);
        userRepository.save(user);

        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();

        try {
            for (Object principal : allPrincipals) {

                DiscodeitUserDetails userDetails = (DiscodeitUserDetails) principal;
                String principalName = userDetails.getUsername();

                if (user.getUsername().equals(principalName)) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);

                    for (SessionInformation session : sessions) {
                        session.expireNow();
                    }

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userMapper.toDto(user);
    }
}
