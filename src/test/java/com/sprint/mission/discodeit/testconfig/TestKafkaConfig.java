package com.sprint.mission.discodeit.testconfig;

import org.junit.jupiter.api.AfterAll;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.kafka.KafkaContainer;

@TestConfiguration
public class TestKafkaConfig {

  public static final KafkaContainer kafka = new KafkaContainer(
      "apache/kafka-native:3.8.0").withReuse(
      true);

  static {
    kafka.start();
  }

  @AfterAll
  static void tearDown() {
    kafka.stop();
  }
}
