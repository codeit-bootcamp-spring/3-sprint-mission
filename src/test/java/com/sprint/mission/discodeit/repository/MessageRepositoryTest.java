package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
@DisplayName("MessageRepository 기능 테스트")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    private Channel savedChannel;

    @BeforeEach
    void setUp() {
        // given
        Channel channel = Channel.builder()
                .name("public")
                .type(ChannelType.PUBLIC)
                .build();

        User user = User.builder()
                .username("test")
                .email("test@test.com")
                .password("pwd1234")
                .build();

        savedChannel = channelRepository.save(channel);
        User savedUser = userRepository.save(user);

        Message message1 = Message.builder()
                .channel(channel)
                .author(user)
                .content("Hello")
                .build();

        Message message2 = Message.builder()
                .channel(channel)
                .author(user)
                .content("Hi")
                .build();

        Message message3 = Message.builder()
                .channel(channel)
                .author(user)
                .content("Nice")
                .build();

        Message message4 = Message.builder()
                .channel(channel)
                .author(user)
                .content("Wow")
                .build();

        messageRepository.saveAll(List.of(message1, message2, message3, message4));
    }

    @Test
    @DisplayName("특정 채널의 이전 메시지를 시간 기준 내림차순으로 조회한다.")
    void testFindByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc() {

        // when
        List<Message> result = messageRepository.findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(
                savedChannel.getId(), Instant.now(), PageRequest.of(0, 2)
        );

        // then
        assertEquals(2, result.size());
        assertEquals("Wow", result.get(0).getContent());
        assertEquals("Nice", result.get(1).getContent());
    }

    @Test
    @DisplayName("채널의 메시지를 페이지로 조회할 수 있어야 한다.")
    void testFindPageByChannelId() {

        // when
        List<Message> page1 = messageRepository.findPageByChannelId(savedChannel.getId(), PageRequest.of(0, 2));

        // then
        assertEquals(2, page1.size());
    }
}