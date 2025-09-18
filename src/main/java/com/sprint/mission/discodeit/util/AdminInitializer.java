package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {
        boolean adminExists = userRepository.existsByRole(Role.ADMIN);

        if (!adminExists) {
            User admin = new User("admin", "admin@admin.com", passwordEncoder.encode("admin1234"), null);
            admin.updateRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }
}
