package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("사용자 인증 시도 : {} ", username);

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.error("사용자를 찾을 수 없습니다. : {} ", username);
                return new UsernameNotFoundException("사용자를 찾을 수 없습니다. : " + username);
            });

        UserDto userDto = userMapper.toDto(user);

        log.debug("사용자 인증 정보 로드 완료 : {} ", username);
        return new DiscodeitUserDetails(userDto, user.getPassword(),user);
    }
}
