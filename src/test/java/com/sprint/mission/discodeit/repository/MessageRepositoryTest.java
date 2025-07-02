package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * MessageRepository의 JPA 슬라이스 테스트 클래스입니다.
 * <p>
 * DB와 연동하여 메시지 관련 JPA 쿼리 동작을 검증합니다.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("MessageRepository 슬라이스 테스트")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;

    private User createUser(String username, String email) {
        BinaryContent profile = new BinaryContent("profile.png", 100L, "image/png");
        User user = new User(username, email, "pw1234", profile);
        UserStatus status = new UserStatus(user, Instant.now());
        user.setStatus(status);
        return userRepository.save(user);
    }

    private Channel createChannel(String name) {
        Channel channel = new Channel(ChannelType.PUBLIC, name, "desc");
        return channelRepository.save(channel);
    }

    private Message createMessage(Channel channel, User author, String content) {
        Message message = new Message(content, channel, author, Collections.emptyList());
        return messageRepository.save(message);
    }

    @Nested
    @DisplayName("findAllByChannelId (페이징)")
    class Describe_findAllByChannelId {
        /**
         * [성공] 채널에 메시지가 있으면 Slice를 반환하는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] 채널에 메시지 존재 시 Slice 반환")
        void when_messages_exist_then_return_slice() {
            Channel channel = createChannel("c1");
            User user = createUser("u1", "u1@email.com");
            for (int i = 0; i < 7; i++) {
                createMessage(channel, user, "msg" + i);
            }
            Slice<Message> slice = messageRepository.findAllByChannelId(channel.getId(), PageRequest.of(0, 5));
            assertThat(slice.getContent()).hasSize(5);
        }
        /**
         * [실패] 채널에 메시지가 없으면 빈 Slice를 반환하는지 검증합니다.
         */
        @Test
        @DisplayName("[실패] 채널에 메시지 없음 시 빈 Slice 반환")
        void when_no_messages_then_return_empty_slice() {
            Channel channel = createChannel("c2");
            Slice<Message> slice = messageRepository.findAllByChannelId(channel.getId(), PageRequest.of(0, 5));
            assertThat(slice.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findTop1ByChannelIdOrderByCreatedAtDesc")
    class Describe_findTop1ByChannelIdOrderByCreatedAtDesc {
        /**
         * [성공] 메시지가 있으면 가장 최근 메시지를 반환하는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] 메시지 존재 시 가장 최근 메시지 반환")
        void when_messages_exist_then_return_latest() {
            Channel channel = createChannel("c3");
            User user = createUser("u2", "u2@email.com");
            createMessage(channel, user, "old");
            Message latest = createMessage(channel, user, "new");
            Optional<Message> found = messageRepository.findTop1ByChannelIdOrderByCreatedAtDesc(channel.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getContent()).isEqualTo("new");
        }
        /**
         * [실패] 메시지가 없으면 빈 Optional을 반환하는지 검증합니다.
         */
        @Test
        @DisplayName("[실패] 메시지 없음 시 빈 Optional 반환")
        void when_no_messages_then_return_empty() {
            Channel channel = createChannel("c4");
            Optional<Message> found = messageRepository.findTop1ByChannelIdOrderByCreatedAtDesc(channel.getId());
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteAllByChannelId")
    class Describe_deleteAllByChannelId {
        /**
         * [성공] 채널의 모든 메시지를 삭제할 수 있는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] 채널의 모든 메시지 삭제")
        void delete_all_messages_in_channel() {
            Channel channel = createChannel("c5");
            User user = createUser("u3", "u3@email.com");
            createMessage(channel, user, "msg1");
            createMessage(channel, user, "msg2");
            messageRepository.deleteAllByChannelId(channel.getId());
            assertThat(messageRepository.findAllByChannelId(channel.getId(), PageRequest.of(0, 10)).getContent()).isEmpty();
        }
    }
} 