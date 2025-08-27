package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.storage.s3.AwsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableJpaAuditing
@EnableScheduling
@EnableConfigurationProperties(AwsProperties.class)
public class AppConfig {

}