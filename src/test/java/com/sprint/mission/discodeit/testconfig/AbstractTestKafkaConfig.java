package com.sprint.mission.discodeit.testconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@Slf4j
@Import(TestKafkaConfig.class)
public abstract class AbstractTestKafkaConfig {

  @DynamicPropertySource
  static void kafkaProperties(DynamicPropertyRegistry registry) {
    log.debug("[AbstractTestKafkaConfig] Setting Kafka bootstrap servers");
    String bootstrapServers = TestKafkaConfig.kafka.getBootstrapServers();
    registry.add("spring.kafka.bootstrap-servers", () -> bootstrapServers);
  }
}
