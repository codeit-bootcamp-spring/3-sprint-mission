package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));

        return toUserDetails(user);
    }

    public UserDetails loadUserById(UUID userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException(userId.toString()));
        return toUserDetails(user);
    }

    private DiscodeitUserDetails toUserDetails(User user) {
        return new DiscodeitUserDetails(userMapper.toDto(user), user.getPassword());
    }

}
