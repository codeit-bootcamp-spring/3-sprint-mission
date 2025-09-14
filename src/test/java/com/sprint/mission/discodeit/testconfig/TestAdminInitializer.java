package com.sprint.mission.discodeit.testconfig;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("test | security-test")
@RequiredArgsConstructor
public class TestAdminInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${discodeit.admin.username}")
  private String adminUsername;

  @Value("${discodeit.admin.email}")
  private String adminEmail;

  @Value("${discodeit.admin.password}")
  private String adminPassword;

  @Override
  public void run(String... args) {
    if (!userRepository.existsByRole(Role.ADMIN)) {
      User admin = User.create(
          adminEmail,
          adminUsername,
          passwordEncoder.encode(adminPassword),
          null,
          Role.ADMIN);
      userRepository.save(admin);
    }
  }
}
