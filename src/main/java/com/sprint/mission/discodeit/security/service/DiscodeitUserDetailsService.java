package com.sprint.mission.discodeit.security.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.dto.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Username으로 사용자 조회
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
		// DTO로 변환
		UserDto dto = UserDto.forAuth(user);
		// User-> UserDetails 변환
		return new DiscodeitUserDetails(dto, user.getPassword());
	}

}
