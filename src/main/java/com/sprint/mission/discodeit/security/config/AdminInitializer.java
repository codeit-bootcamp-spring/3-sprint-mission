package com.sprint.mission.discodeit.security.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Bean
	CommandLineRunner initAdminRunner(
		@Value("${discodeit.admin.username}") String username,
		@Value("${discodeit.admin.email}") String email,
		@Value("${discodeit.admin.password}") String rawPassword
	) {
		return args -> initAdminIfNoAdminExists(username, email, rawPassword);
	}

	@Transactional
	public void initAdminIfNoAdminExists(String username, String email, String rawPassword) {
		if (userRepository.existsByRole(Role.ADMIN)) {
			log.info("관리자 계정이 이미 존재합니다. 관리자 계정 초기화 단계 생략.");
			return;
		}

		String encodedPassword = passwordEncoder.encode(rawPassword);
		User admin = new User(username, email, encodedPassword, null);
		admin.updateRole(Role.ADMIN); // 엔티티에 changeRole 메서드 있어야 함

		userRepository.save(admin);
		log.warn("[어플리케이션 초기화] 관리자 계정이 생성되었습니다. username='{}', email='{}'. "
			+ "\n 로그인 후 비밀번호를 변경해 주세요.", username, email);
	}
}