package com.sprint.mission.discodeit.security.initializer;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.init.admin", havingValue = "true", matchIfMissing = true)
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        boolean existsAdmin = userRepository.existsByRole(Role.ADMIN);
        if (!existsAdmin) {
            User admin = new User(
                "admin",
                "admin@discodeit.com",
                passwordEncoder.encode("admin1234!"),
                null
            );
            admin.updateRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }
}
