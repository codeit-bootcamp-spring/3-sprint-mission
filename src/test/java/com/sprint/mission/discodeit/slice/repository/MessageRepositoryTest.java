package com.sprint.mission.discodeit.slice.repository;

import com.sprint.mission.discodeit.config.QuerydslConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jpa.JpaChannelRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaMessageRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PackageName  : com.sprint.mission.discodeit.slice
 * FileName     : MessageRepositoryTest
 * Author       : dounguk
 * Date         : 2025. 6. 20.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
@DisplayName("Message Repository 테스트")
public class MessageRepositoryTest {

    @Autowired
    private JpaMessageRepository messageRepository;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaChannelRepository channelRepository;

    private User globalUser;
    private Channel globalChannel;
    private Message globalMessage;

    @BeforeEach
    void setUp() {
        globalUser = User.builder()
            .username("paul")
            .password("1234")
            .email("paul@gmail.com")
            .build();
        userRepository.save(globalUser);

        globalChannel = Channel.builder()
            .type(ChannelType.PUBLIC)
            .name("test public channel")
            .build();
        channelRepository.save(globalChannel);

        globalMessage = Message.builder()
            .content("hello paul!")
            .channel(globalChannel)
            .author(globalUser)
            .build();
        messageRepository.save(globalMessage);
    }

    @Test
    @DisplayName("채널이 가지고 있는 모든 메세지를 가져와야 한다.")
    void whenFindByChannelId_thenGetRelatedMessages(){
        // given
        int numberOfMessages = 10;

        Channel channel = Channel.builder()
            .type(ChannelType.PUBLIC)
            .build();
        channelRepository.save(channel);

        List<Message> messages = new ArrayList<>();
        for(int i = 0; i < numberOfMessages; i++){
            Message message = new Message(globalUser, channel, "content #"+(i+1));
            messageRepository.save(message);
            messages.add(message);
        }

        // when
        List<Message> result = messageRepository.findAllByChannelId(channel.getId());

        // then
        assertThat(result.size()).isEqualTo(messages.size());
        for(Message message : result){
            assertThat(message.getChannel().getId()).isEqualTo(channel.getId());
        }
    }

    @Test
    @DisplayName("채널의 메세지가 아닐경우 가져올 수 없다.")
    void whenMessagesAreNotIncludedInChannel_thenCanNotFindMessages(){
        // given
        int numberOfMessages = 10;
        Channel localChannel = Channel.builder()
            .type(ChannelType.PUBLIC)
            .name("local public channel")
            .build();

        channelRepository.save(globalChannel);

        List<Message> messages = new ArrayList<>();
        for(int i = 0; i < numberOfMessages; i++){
            Message message = new Message(globalUser, globalChannel, "not a message in finding channel #"+(i+1));
            messageRepository.save(message);
            messages.add(message);
        }

        // when
        List<Message> result = messageRepository.findAllByChannelId(localChannel.getId());

        // then
        assertThat(result.size()).isNotEqualTo(messages.size());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("메세지 아이디를 이용해 메세지연관된 메세지가 있으면 true를 반환한다")
    void whenHasMatchingMessageId_thenReturnTrue(){
        // given

        // when
        boolean isExist = messageRepository.existsById(globalMessage.getId());

        // then
        assertThat(isExist).isTrue();
    }

    @Test
    @DisplayName("메시지 아이디를 이용해 연관 메세지가 없으면 false를 반환한다.")
    void whenNoMatchingMessageId_thenReturnTrue(){
        // given
        UUID uuid = UUID.randomUUID();
        // when
        boolean isExist = messageRepository.existsById(uuid);

        // then
        assertThat(isExist).isFalse();
    }

    @Test
    @DisplayName("채널 기준 메세지들이 여러개면 최근 만들어진 메세지 한개를 반환한다.")
    void whenChannelHasMultipleMessages_thenReturnRecentMessage(){
        // given
        int numberOfMessages = 10;
        List<Message> messages = new ArrayList<>();
        for(int i = 0; i < numberOfMessages; i++){
            String day = String.format("%02d", i+1);

            Message message = new Message(globalUser, globalChannel, "content #"+(i+1));
            ReflectionTestUtils.setField(message,"createdAt", Instant.parse("2025-06-" + day + "T00:00:00Z"));
            messageRepository.save(message);
            messages.add(message);
        }

        Instant targetMessageTime = messages.stream()
            .map(Message::getCreatedAt)
            .max(Comparator.naturalOrder())
            .orElseThrow();
        System.out.println("targetMessageTime: " + targetMessageTime);

        // when
        Message result = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(globalChannel.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("content #" + numberOfMessages);
        assertThat(result.getCreatedAt()).isEqualTo(targetMessageTime);
    }

    @Test
    @DisplayName("채널을 찾을 수 없으면 null을 반환한다.")
    void whenChannelNotFound_thenReturnNull(){
        // given
        UUID wrongChannelId = UUID.randomUUID();

        // when
        Message message = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(wrongChannelId);

        // then
        assertThat(message).isNull();
    }

    @Test
    @DisplayName("채널이 메세지를 가지고 있으면 가지고 있는 메세지의 수를 반환한다.")
    void whenChannelHasMessages_thenReturnNumberOfMessages(){
        // given
        int numberOfMessages = 28;

        Channel channel = Channel.builder()
            .name("local public channel")
            .type(ChannelType.PUBLIC)
            .build();
        channelRepository.save(channel);

        for (int i = 0; i < numberOfMessages; i++) {
            Message message = new Message(globalUser, channel, "content #" + (i + 1));
            ReflectionTestUtils.setField(message, "createdAt", Instant.parse(String.format("2025-06-%02dT00:00:00Z", i + 1)));
            messageRepository.save(message);
        }

        Message topMessage = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channel.getId());

        // when
        Long result = messageRepository.countByChannelId(channel.getId());

        // then
        assertThat(result).isEqualTo(numberOfMessages);
        assertThat(topMessage.getContent()).isEqualTo("content #" + numberOfMessages);
    }

    @Test
    @DisplayName("채널에 메세지가 없을경우 0을 반환한다.")
    void whenChannelHasNoMessages_thenReturnZero(){
        // given
        Channel channel = Channel.builder()
            .name("local public channel")
            .type(ChannelType.PUBLIC)
            .build();
        channelRepository.save(channel);

        // when
        Long result = messageRepository.countByChannelId(channel.getId());

        // then
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("커서가 있을경우 커서를 기준으로 메세지들을 가져온다.")
    void whenCursorExist_thenReturnListBasedOnCursor() throws Exception {
        // given
        int numberOfMessages = 28;
        int targetIndex = 5;
        UUID channelId = globalChannel.getId();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Instant cursor = Instant.parse(String.format("2025-06-%02dT00:00:00Z", targetIndex));

        for(int i = 0; i < numberOfMessages; i++){
            Message message = new Message(globalUser, globalChannel, "content #" + (i + 1));
            ReflectionTestUtils.setField(message, "createdAt", Instant.parse(String.format("2025-06-%02dT00:00:00Z", i + 1)));
            messageRepository.save(message);
        }

        // when
        List<Message> messages = messageRepository.findSliceByCursor(channelId, cursor, pageable);

        // then
        assertThat(messages.size()).isLessThanOrEqualTo(pageable.getPageSize());
        assertThat(messages).allSatisfy(message -> {
            assertThat(message.getCreatedAt()).isBefore(cursor);
        });

    }

    @Test
    @DisplayName("커서가 없을경우 가장 최신 메세지를 기준으로 가져온다.")
    void whenCursorNotExist_thenReturnLatestMessages() throws Exception {
        // given
        int numberOfMessages = 28;

        Channel channel = new Channel();
        channelRepository.save(channel);
        UUID channelId = channel.getId();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        for(int i = 0; i < numberOfMessages; i++){
            Message message = new Message(globalUser, channel, "content #" + (i + 1));
            messageRepository.save(message);
            ReflectionTestUtils.setField(message, "createdAt", Instant.parse(String.format("2025-06-%02dT00:00:00Z", i + 1)));
        }

        // when
        List<Message> slice = messageRepository.findSliceByCursor(channelId, null, pageable);

        // then
        assertThat(slice).hasSize(pageable.getPageSize() + 1);
        assertThat(slice.get(0).getCreatedAt())
            .isEqualTo(Instant.parse("2025-06-28T00:00:00Z"));
        assertThat(slice).isSortedAccordingTo(
            Comparator.comparing(Message::getCreatedAt).reversed());
    }
}
