package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security 사용자 인증 정보를 제공하는 서비스.
 *
 * <p>UserDetailsService 구현체로, username 기반으로 DB에서 사용자 조회 후
 * {@link DiscodeitUserDetails} 객체로 반환.</p>
 */
@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * username으로 사용자 인증 정보를 조회.
     *
     * <p>조회된 User 엔티티를 UserDto로 변환 후 DiscodeitUserDetails로 반환.</p>
     * <p>사용자가 존재하지 않으면 {@link UserNotFoundException} 발생.</p>
     *
     * @param username 사용자 이름
     * @return 인증 정보(UserDetails)
     * @throws UsernameNotFoundException 사용자가 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> UserNotFoundException.withUsername(username));
        UserDto userDto = userMapper.toDto(user);

        return new DiscodeitUserDetails(
            userDto,
            user.getPassword()
        );
    }
}