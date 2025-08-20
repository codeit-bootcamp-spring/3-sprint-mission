package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.DiscodeitUserDetails;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.utils.SessionContext;
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
    private final UserMapper userMapper;
    private final SessionContext sessionContext;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("[DiscodeitUserDetailsService] loadUserByUsername 실행");
        return userRepository.findByEmail(email)
            .map((user) -> {

                // status 정보
                boolean isOnline = sessionContext.getStatusFromSession(user.getId());
                return new DiscodeitUserDetails(userMapper.toDto(user, isOnline),
                    user.getPassword());
            })
            .orElseThrow(
                () -> new UsernameNotFoundException("사용자 정보를 찾을수 없습니다. Email: " + email));
    }
}
