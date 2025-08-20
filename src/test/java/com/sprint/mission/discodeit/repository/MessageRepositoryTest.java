package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("MessageRepository 슬라이스 테스트")
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired private ChannelRepository channelRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TestEntityManager em;

    private Channel channel;
    private User user;

    @BeforeEach
    void setUp() {
        channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "public", "public channel"));
        user = userRepository.save(new User("tom", "tom@test.com", "pw123456", null));

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("메시지 저장 성공")
    void save() {
        // when
        Message saved = messageRepository.save(new Message("hello", channel, user, List.of()));

        // then
        Optional<Message> found = messageRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("hello");
        assertThat(found.get().getChannel().getId()).isEqualTo(channel.getId());
        assertThat(found.get().getAuthor().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("특정 채널의 메시지 목록 중 첫 페이지 조회")
    void findAllByChannelId() {
        messageRepository.save(new Message("msg1", channel, user, List.of()));
        messageRepository.save(new Message("msg2", channel, user, List.of()));
        messageRepository.save(new Message("msg3", channel, user, List.of()));

        em.flush();
        em.clear();

        // when
        Pageable pageReq = PageRequest.of(0, 2);
        Slice<Message> slice = messageRepository.findAllByChannelId(channel.getId(), pageReq);

        // then
        assertThat(slice.getNumberOfElements()).isEqualTo(2);
        assertThat(slice.getContent())
            .extracting(Message::getContent)
            .hasSize(2)
            .allSatisfy(content ->
                assertThat(Set.of("msg1", "msg2", "msg3")).contains(content)
            );
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    @DisplayName("특정 채널의 메시지 목록의 첫 페이지 이후 추가적인 메시지 조회")
    void findAllByChannelIdWithCursor() throws InterruptedException {
        // given
        Message msg1 = messageRepository.saveAndFlush(new Message("msg1", channel, user, List.of()));
        Thread.sleep(200);
        Message msg2 = messageRepository.saveAndFlush(new Message("msg2", channel, user, List.of()));
        Thread.sleep(200);
        Message msg3 = messageRepository.saveAndFlush(new Message("msg3", channel, user, List.of()));

        em.clear();

        Instant cursorTime = msg2.getCreatedAt().plusMillis(1);
        Pageable pageable = PageRequest.of(0, 2);

        // when
        Slice<Message> slice = messageRepository.findAllByChannelId(
            channel.getId(), cursorTime, pageable
        );

        // then
        assertThat(slice.getNumberOfElements()).isEqualTo(1);
        assertThat(slice.getContent())
            .extracting(Message::getContent)
            .containsExactly("msg3");
        assertThat(slice.hasNext()).isFalse();
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteById() {
        // given
        Message msg = messageRepository.save(new Message("delete message", channel, user, List.of()));

        // when
        messageRepository.deleteById(msg.getId());

        // then
        assertThat(messageRepository.findById(msg.getId())).isEmpty();
    }
}
