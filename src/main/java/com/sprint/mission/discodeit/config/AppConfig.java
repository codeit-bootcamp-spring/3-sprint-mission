package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 애플리케이션의 핵심 설정을 담당하는 클래스입니다.
 * 
 * <p>JPA Auditing과 스케줄링 기능을 활성화하여 엔티티의 생성/수정 시간 자동 기록과
 * 정기적인 작업 실행을 지원합니다.</p>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Configuration
@EnableJpaAuditing
@EnableScheduling
public class AppConfig {
}
