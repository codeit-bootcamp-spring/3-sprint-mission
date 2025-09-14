package com.sprint.mission.discodeit.config;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap;

    @Value("${spring.application.name:discodeit}")
    private String appName;

    // 각 인스턴스 고유 식별자
    @Value("${INSTANCE_ID:${HOSTNAME:${random.uuid}}}")
    private String instanceId;

    /**
     * Processing(단일 처리) 컨테이너 팩토리 - 공유 그룹
     *
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> processingKafkaListenerContainerFactory() {
        Map<String, Object> props = new HashMap<>(baseProps());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, appName + ".processing.notifications");
        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(props);

        ConcurrentKafkaListenerContainerFactory<String, String> f = new ConcurrentKafkaListenerContainerFactory<>();
        f.setConsumerFactory(cf);
        f.setCommonErrorHandler(errorHandler());
        return f;
    }

    /**
     * Broadcast(모든 인스턴스) 컨테이너 팩토리 - 인스턴스별 고유 그룹
     *
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> broadcastKafkaListenerContainerFactory() {
        Map<String, Object> props = new HashMap<>(baseProps());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, appName + ".broadcast." + instanceId);
        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(props);

        ConcurrentKafkaListenerContainerFactory<String, String> f = new ConcurrentKafkaListenerContainerFactory<>();
        f.setConsumerFactory(cf);
        f.setCommonErrorHandler(errorHandler());
        return f;
    }

    private Map<String, Object> baseProps() {
        Map<String, Object> p = new HashMap<>();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        p.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        return p;
    }

    /**
     * 에러 핸들러 + DLT 1초 간격으로 3회 재시도 한다.
     */
    @Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler(new FixedBackOff(1000L, 3L));
    }
}
