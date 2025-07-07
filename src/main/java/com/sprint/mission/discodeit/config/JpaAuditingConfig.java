package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@Profile("!test") // 테스트 프로필에서는 이 Bean이 생성되지 않음
@EnableJpaAuditing
public class JpaAuditingConfig {
}
