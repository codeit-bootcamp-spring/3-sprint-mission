package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.redis.RedisLockProvider;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

  private static final String ADMIN_ACCOUNT_INITIALIZER_LOCK = "ADMIN_ACCOUNT_INITIALIZER";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RedisLockProvider redisLockProvider;

  @Value("${discodeit.admin.username}")
  private String adminUsername;

  @Value("${discodeit.admin.email}")
  private String adminEmail;

  @Value("${discodeit.admin.password}")
  private String adminPassword;

  @Override
  public void run(String... args) {
    boolean locked = false;
    try {
      redisLockProvider.acquireLock(ADMIN_ACCOUNT_INITIALIZER_LOCK);
      locked = true;
      if (!userRepository.existsByRole(Role.ADMIN)) {
        User admin = User.create(
            adminEmail,
            adminUsername,
            passwordEncoder.encode(adminPassword),
            null,
            Role.ADMIN);
        userRepository.save(admin);
      }
    } catch (RedisLockProvider.RedisLockAcquisitionException e) {
      // 락 획득 실패 시 아무것도 하지 않음
    } finally {
      if (locked) {
        redisLockProvider.releaseLock(ADMIN_ACCOUNT_INITIALIZER_LOCK);
      }
    }
  }
}
