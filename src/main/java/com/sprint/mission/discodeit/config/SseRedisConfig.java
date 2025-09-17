package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.service.distributed.RedisBasedSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.sse.type", havingValue = "redis", matchIfMissing = false)
public class SseRedisConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
        RedisConnectionFactory connectionFactory,
        RedisBasedSseService sseService) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 브로드캐스트 리스너
        MessageListener broadcastListener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    String jsonMessage = new String(message.getBody());
                    RedisBasedSseService.SseMessage sseMessage = objectMapper.readValue(
                        jsonMessage, RedisBasedSseService.SseMessage.class);
                    sseService.handleBroadcastMessage(sseMessage);
                    log.debug("[Redis 브로드캐스트 수신] 이벤트: {}", sseMessage.getEventName());
                } catch (Exception e) {
                    log.error("SSE 브로드캐스트 메시지 처리 실패", e);
                }
            }
        };

        // 타겟 리스너
        MessageListener targetedListener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    String jsonMessage = new String(message.getBody());
                    RedisBasedSseService.SseMessage sseMessage = objectMapper.readValue(
                        jsonMessage, RedisBasedSseService.SseMessage.class);
                    sseService.handleTargetedMessage(sseMessage);
                    log.debug("[Redis 타겟 수신] 이벤트: {}, 대상: {}명",
                        sseMessage.getEventName(),
                        sseMessage.getTargetUsers() != null ? sseMessage.getTargetUsers().size() : 0);
                } catch (Exception e) {
                    log.error("SSE 타겟 메시지 처리 실패", e);
                }
            }
        };

        container.addMessageListener(broadcastListener, new ChannelTopic("sse:broadcast"));
        container.addMessageListener(targetedListener, new ChannelTopic("sse:targeted"));

        log.info("[Redis SSE 리스너 설정 완료] 브로드캐스트 & 타겟 채널");

        return container;
    }
}
