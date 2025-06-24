package com.sprint.mission.discodeit.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@TestConfiguration
@EnableJpaAuditing
public class TestJpaAuditingConfig {
    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return () -> Optional.of(UUID.randomUUID()); // 테스트용 랜덤 UUID
    }
}