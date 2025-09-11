package com.sprint.mission.discodeit.config;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@EnableKafka
@Configuration
public class KafkaConfig {

  private final KafkaProperties kafkaProperties;

  public KafkaConfig(KafkaProperties kafkaProperties) {
    this.kafkaProperties = kafkaProperties;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> broadcastKafkaListenerContainerFactory(
      @Value("${spring.application.name:discodeit}") String appName) {
    Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
    String instanceId = UUID.randomUUID().toString();
    props.put(ConsumerConfig.GROUP_ID_CONFIG, appName + "-broadcast-" + instanceId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(
        props);
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setMissingTopicsFatal(false);
    return factory;
  }
}