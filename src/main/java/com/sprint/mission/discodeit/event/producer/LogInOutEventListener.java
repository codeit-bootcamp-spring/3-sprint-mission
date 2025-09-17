package com.sprint.mission.discodeit.event.producer;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.UserLogInOutEvent;
import com.sprint.mission.discodeit.event.publisher.KafkaEventPublisher;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogInOutEventListener {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;
    private final KafkaEventPublisher kafkaEventPublisher;

    @EventListener
    @Transactional(readOnly = true)
    public void on(UserLogInOutEvent event) {

        log.info("[Kafka] UserLogInOutEvent 수신: userId={}", event.userId());
        userRepository.findById(event.userId())
            .ifPresent(user -> {
                // 유저 상태
                kafkaEventPublisher.publish("users.updated", userMapper.toDto(user));

                // DM 채널 상태
                readStatusRepository.findAllByUserId(user.getId())
                    .stream()
                    .map(ReadStatus::getChannel)
                    .filter(c -> c.getType() == ChannelType.PRIVATE)
                    .map(channelMapper::toDto)
                    .forEach(ch -> kafkaEventPublisher.publish("channels.updated", ch));
            });
    }
}
