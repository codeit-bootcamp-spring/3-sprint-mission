package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "discodeit.storage.type=local",
        "discodeit.storage.local.root-path=./binaryTest"
})
@DisplayName("UserService 통합 테스트")
@Transactional
public class MessageServiceIntegrationTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private BinaryContentStorage binaryContentStorage;

    private User savedAuthor;
    private Channel savedChannel1;
    private Channel savedChannel2;

    @BeforeEach
    void setUp() {
        User author = User.builder()
                .username("test")
                .email("test@test.com")
                .password("pwd1234")
                .build();

        UserStatus userStatus = UserStatus.builder()
                .user(author)
                .lastActiveAt(Instant.now())
                .build();

        author.updateStatus(userStatus);

        savedAuthor = userRepository.save(author);

        Channel channel1 = Channel.builder()
                .name("public")
                .description("test channel")
                .type(ChannelType.PUBLIC)
                .build();

        savedChannel1 = channelRepository.save(channel1);

        Channel channel2 = Channel.builder()
                .name("public2")
                .description("test channel2")
                .type(ChannelType.PUBLIC)
                .build();

        savedChannel2 = channelRepository.save(channel2);
    }

    @Test
    @DisplayName("메시지 등록 프로세스가 모든 계층에서 올바르게 동작해야 한다.")
    void completeCreateMessageIntegration() throws IOException {

        // given
        byte[] imageBytes = "test".getBytes(StandardCharsets.UTF_8);
        BinaryContentDto attachment = new BinaryContentDto("attachment.png", 3L,
                "image/png", imageBytes);

        MessageRequestDto request = new MessageRequestDto("Hello", savedChannel1.getId(),
                savedAuthor.getId());

        // when
        MessageResponseDto result = messageService.create(request, List.of(attachment));

        // then
        assertNotNull(result);
        assertEquals("Hello", result.content());
        assertEquals(savedChannel1.getId(), result.channelId());
        assertEquals(savedAuthor.getId(), result.author().id());
        UUID messageId = result.id();
        Optional<Message> foundMessage = messageRepository.findById(messageId);
        assertTrue(foundMessage.isPresent(), "메시지가 저장되어 있어야 한다.");
        List<BinaryContent> attachments = foundMessage.get().getAttachments();
        assertEquals(1, attachments.size());
        assertEquals("attachment.png", attachments.get(0).getFileName());
        byte[] data = binaryContentStorage.get(attachments.get(0).getId()).readAllBytes();
        assertArrayEquals(imageBytes, data);
    }

    @Test
    @DisplayName("채널 ID로 메시지 조회 프로세스가 모든 계층에서 올바르게 동작해야 한다.")
    void completeFindByChannelIdIntegration() {

        // given
        Message message1 = Message.builder()
                .author(savedAuthor)
                .channel(savedChannel1)
                .content("Hello")
                .attachments(List.of())
                .build();

        Message message2 = Message.builder()
                .author(savedAuthor)
                .channel(savedChannel2)
                .content("Hi")
                .attachments(List.of())
                .build();

        Message message3 = Message.builder()
                .author(savedAuthor)
                .channel(savedChannel2)
                .content("Nice")
                .attachments(List.of())
                .build();

        messageRepository.save(message1);
        messageRepository.save(message2);
        messageRepository.save(message3);
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        PageResponse<MessageResponseDto> result = messageService.findAllByChannelId(savedChannel2.getId(),
                null, pageable);

        // then
        List<MessageResponseDto> content = result.content();
        assertEquals(2, content.size());
        assertEquals("Nice", content.get(0).content());
        assertEquals("Hi", content.get(1).content());
        assertFalse(result.hasNext(), "채널에 있는 메시지를 모두 조회했기 때문에 다음 메시지는 없어야한다.");
        assertNull(result.nextCursor());
    }

    @Test
    @DisplayName("메시지 삭제 프로세스가 모든 계층에서 올바르게 동작해야 한다.")
    void completeDeleteMessageIntegration() {

        // given
        BinaryContent attachment = new BinaryContent("attachment.png", 3L, "image/png");

        Message message = Message.builder()
                .author(savedAuthor)
                .channel(savedChannel1)
                .content("Hello")
                .attachments(List.of(attachment))
                .build();

        BinaryContent savedAttachment = binaryContentRepository.save(attachment);
        Message savedMessage = messageRepository.save(message);

        UUID messageId = savedMessage.getId();
        UUID attachmentId = savedAttachment.getId();

        // when
        messageService.deleteById(messageId);

        // then
        Optional<Message> foundMessage = messageRepository.findById(messageId);
        assertTrue(foundMessage.isEmpty());
        Optional<BinaryContent> foundAttachment = binaryContentRepository.findById(attachmentId);
        assertTrue(foundAttachment.isEmpty(), "메시지의 첨부 파일도 삭제되어야 한다.");
    }

    @AfterEach
    void cleanUpStorage() throws IOException {
        Path testRoot = Paths.get("./binaryTest");
        if (Files.exists(testRoot)) {
            Files.walk(testRoot)
                    .sorted((a, b) -> b.compareTo(a)) // 파일 먼저, 그 다음 디렉토리 삭제
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // 무시 또는 로깅
                        }
                    });
        }
    }
}
