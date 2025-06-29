package com.sprint.mission.discodeit.repository;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@DisplayName("메시지 Repo 슬라이스 테스트")
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("채널의 모든 메시지 조회 - case : success")
    void findAllByChannelIdWithAuthorSuccess() {
        BinaryContent profile = binaryContentRepository.save(
            new BinaryContent("img001.jpg", 1024L, "image/jpg")
        );
        User user = userRepository.save(
            new User("김현기","test@test.com","009874", profile)
        );
        userStatusRepository.save(new UserStatus(user, Instant.now()));

        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC,"testPublicChannel",null));

        Message message1 = new Message("testMessage1", channel, user, null);
        Message message2 = new Message("testMessage2", channel, user, null);
        messageRepository.saveAll(List.of(message1,message2));
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);

        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
            channel.getId(),
            Instant.now(),
            pageable);

        assertEquals(2, result.getNumberOfElements());
        List<String> contents = result.getContent().stream()
            .map(Message::getContent)
            .toList();

        assertTrue(contents.contains("testMessage1"));
        assertTrue(contents.contains("testMessage2"));

        for (Message message : result.getContent() ) {
            assertNotNull(message.getAuthor());
            assertNotNull(message.getAuthor().getProfile());
            assertNotNull(message.getAuthor().getStatus());
        }
    }

    @Test
    @DisplayName("채널의 모든 메시지 조회 - case : createAt 이전 메시지가 없음으로 인한 failed")
    void findAllByChannelIdWithAuthorFail() {
        User user = userRepository.save(
            new User("김현기","test@test.com","009874", null)
        );
        userStatusRepository.save(new UserStatus(user, Instant.now()));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC,"testPublicChannel",null));
        Message message = new Message("testMessage1", channel, user, null);

        Pageable pageable = PageRequest.of(0, 10);
        Instant earlier = Instant.now().minusSeconds(180);

        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
            channel.getId(),
            earlier,
            pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("채널의 최근 메시지 시간 조회 - case : success")
    void findLastMessageAyByChannelIdSuccess() {
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC,"testPublicChannel",null));
        User user = userRepository.save(new User("김현기","test@test.com","009874", null));
        userStatusRepository.save(new UserStatus(user, Instant.now()));

        Message message1 = new Message("testMessage1", channel, user, null);
        Message message2 = new Message("testMessage2", channel, user, null);
        messageRepository.saveAll(List.of(message1,message2));
        entityManager.flush();
        entityManager.clear();

        Optional<Instant> lastMessageAt = messageRepository.findLastMessageAtByChannelId(channel.getId());
        assertTrue(lastMessageAt.isPresent());

        Message latest = messageRepository.findById(message2.getId()).orElseThrow();
        assertEquals(latest.getCreatedAt(), lastMessageAt.get());
    }

    @Test
    @DisplayName("채널의 최근 메시지 시간 조회 - case : 채널이 존재하지 않음으로써 failed")
    void findLastMessageAtByChannelIdFail() {
        UUID invalidChannelId = UUID.randomUUID();
        Optional<Instant> lastMessageAt = messageRepository.findLastMessageAtByChannelId(invalidChannelId);
        assertTrue(lastMessageAt.isEmpty());
    }
}
