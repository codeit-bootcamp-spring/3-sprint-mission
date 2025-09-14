package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 애플리케이션 시작 시 관리자 계정을 자동으로 초기화하는 컴포넌트입니다.
 * 
 * <p>애플리케이션 최초 실행 시 기본 관리자 계정을 생성하여 시스템 관리가 가능하도록 합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>기본 관리자 계정 자동 생성</li>
 *   <li>중복 생성 방지</li>
 *   <li>비밀번호 암호화</li>
 *   <li>관리자 권한 부여</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final String CONFIG_NAME = "[AdminInitializer] ";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    /**
     * 애플리케이션 시작 시 관리자 계정 초기화를 수행합니다.
     * 
     * <p>기본 관리자 계정이 존재하지 않는 경우에만 새로 생성합니다.</p>
     * 
     * @param args 명령행 인수
     * @throws Exception 초기화 중 발생할 수 있는 예외
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info(CONFIG_NAME + "어드민 계정 초기화 시작");

        String encodedPassword = passwordEncoder.encode(adminPassword);
        User admin = new User(adminUsername, adminEmail, encodedPassword, null);
        admin.updateRole(Role.ADMIN);

        if (userRepository.existsByUsername(adminUsername)) {
            log.info(CONFIG_NAME + "어드민 계정이 이미 존재합니다: {}", admin.getId());
            return;
        }
        userRepository.save(admin);

        log.info(CONFIG_NAME + "어드민 계정 생성 완료: username={}, email={}, role={}",
                admin.getUsername(), admin.getEmail(), admin.getRole());
    }
}
