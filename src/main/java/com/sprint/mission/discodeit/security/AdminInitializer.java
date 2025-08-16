package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String username;
    @Value("${admin.password}")
    private String password;
    @Value("${admin.email}")
    private String email;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.existsByRole(Role.ADMIN)) {
            return;
        }

        User user = new User(username, email, passwordEncoder.encode(password), null);
        user.updateRole(Role.ADMIN);

        userRepository.save(user);
    }
}
