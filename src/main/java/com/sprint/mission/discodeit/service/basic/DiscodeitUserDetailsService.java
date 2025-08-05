package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * PackageName  : com.sprint.mission.discodeit.service.basic
 * FileName     : CustomUserDetailsService
 * Author       : dounguk
 * Date         : 2025. 8. 5.
 */
@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        UserResponse userDto = userMapper.toDto(user);
        return new DiscodeitUserDetails(userDto, user.getPassword());
    }
}
