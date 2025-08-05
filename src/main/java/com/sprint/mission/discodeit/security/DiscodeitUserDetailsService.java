package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("로그인 시도한 사용자: {}", username);

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.error("해당 사용자를 찾을 수 없습니다: {}", username);
                return new UsernameNotFoundException(username + " 사용자를 찾을 수 없습니다.");
            });

        // User → UserDto 변환
        UserDto userDto = UserDto.from(user);

        return new DiscodeitUserDetails(userDto, user.getPassword());
    }


}