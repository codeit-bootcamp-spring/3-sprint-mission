package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.response.AdvancedJpaPageResponse;
import com.sprint.mission.discodeit.dto.message.response.JpaMessageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.helper.FileUploadUtils;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaChannelRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaMessageRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.unit.basic.BasicMessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * PackageName  : com.sprint.mission.discodeit.integration
 * FileName     : MessageTest
 * Author       : dounguk
 * Date         : 2025. 6. 23.
 */
@DisplayName("Message 통합 테스트")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(
    properties = {
        "file.upload.all.path=${java.io.tmpdir}/upload-test"
    }
)
public class MessageTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private BasicMessageService messageService;

    @Autowired
    private JpaMessageRepository messageRepository;

    @Autowired
    private JpaChannelRepository channelRepository;

    @Autowired
    private JpaBinaryContentRepository binaryContentRepository;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private FileUploadUtils fileUploadUtils;

    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("upload-test-");
        ReflectionTestUtils.setField(fileUploadUtils, "path", tempDir.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        FileSystemUtils.deleteRecursively(tempDir);
    }

    @Test
    @DisplayName("커서 기반 메세지를 리스트로 찾을 수 있어야 한다.")
    void findMessages_cursor_success() throws Exception {
        // given
        int numberOfMessages = 20;
        int cursorIndex = 11;
        int size = 10;

        Instant cursor = Instant.parse(String.format("20%02d-06-23T00:00:00Z", cursorIndex));

        User user = User.builder()
            .username("paul")
            .password("1234")
            .email("paul@paul.com")
            .build();
        userRepository.save(user);

        Channel channel = Channel.builder()
            .name("channel1")
            .type(ChannelType.PUBLIC)
            .build();
        channelRepository.save(channel);

        for (int i = 0; i < numberOfMessages; i++) {
            Message message = Message.builder()
                .channel(channel)
                .author(user)
                .content("content #"+(i+1))
                .build();
            messageRepository.save(message);
            String date = String.format("20%02d-06-23T00:00:00Z", i + 1);
            ReflectionTestUtils.setField(message, "createdAt", Instant.parse(date));

        }

        Pageable pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());

        // when
        AdvancedJpaPageResponse result = messageService.findAllByChannelIdAndCursor(channel.getId(), cursor, pageable);

        // then
        assertThat(result.content().size()).isLessThanOrEqualTo(numberOfMessages);
        assertThat(result.content()).allSatisfy(message -> {
            assertThat(message.createdAt()).isBefore(cursor);
        });
    }

    @Test
    @DisplayName("채널 정보가 없을경우 빈 리스트를 반환한다.")
    void findMessage_noUser_EmptyList() throws Exception {
        // when
        AdvancedJpaPageResponse response =
            messageService.findAllByChannelIdAndCursor(UUID.randomUUID(), null, PageRequest.of(0, 10, Sort.by("createdAt").descending()));

        // then
        assertThat(response.totalElements()).isEqualTo(0);
        assertThat(response.content()).isEmpty();
    }

    @Test
    @DisplayName("메세지 생성 프로세스가 모든 계층에서 올바르게 동작해야 한다.")
    void createMessage_success() throws Exception {
        // given
        User user = User.builder()
            .username("paul")
            .email("a@a.com")
            .password("1234")
            .build();
        userRepository.save(user);

        Channel channel = Channel.builder()
            .name("public channel")
            .type(ChannelType.PUBLIC)
            .build();
        channelRepository.save(channel);

        MessageCreateRequest request = MessageCreateRequest.builder()
            .content("content")
            .channelId(channel.getId())
            .authorId(user.getId())
            .build();

        MockMultipartFile jsonPart = new MockMultipartFile(
            "messageCreateRequest",
            "avatar.png",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        byte[] img = new byte[]{1, 2};
        MockMultipartFile imgPart = new MockMultipartFile(
            "profile", "avatar.png", MediaType.IMAGE_PNG_VALUE, img);
        List<MultipartFile> files = Arrays.asList(jsonPart, imgPart);

        // when
        JpaMessageResponse message = messageService.createMessage(request, files);

        // then
        assertThat(message).isNotNull();
        assertThat(message.channelId()).isEqualTo(channel.getId());
        assertThat(message.content()).isEqualTo("content");
        assertThat(message.author().id()).isEqualTo(user.getId());
        assertThat(message.attachments().size()).isEqualTo(2);
        assertThat(binaryContentRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("메세지 생성중 채널 정보가 없을경우 ChannelNotFound를 반환한다.")
    void createMessage_noChannel_ChannelNotFound() throws Exception {
        // given
        User user = User.builder()
            .username("paul")
            .email("a@a.com")
            .password("1234")
            .build();
        userRepository.save(user);

        UUID channelId = UUID.randomUUID();

        MessageCreateRequest request = MessageCreateRequest.builder()
            .content("content")
            .channelId(channelId)
            .authorId(user.getId())
            .build();

        MockMultipartFile jsonPart = new MockMultipartFile(
            "messageCreateRequest",
            "avatar.png",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        byte[] img = new byte[]{1, 2};
        MockMultipartFile imgPart = new MockMultipartFile(
            "profile", "avatar.png", MediaType.IMAGE_PNG_VALUE, img);
        List<MultipartFile> files = Arrays.asList(jsonPart, imgPart);
        // when
        ChannelNotFoundException result = assertThrows(ChannelNotFoundException.class, () -> messageService.createMessage(request, files));

        assertThat(result.getMessage()).isEqualTo("채널을 찾을 수 없습니다.");
        assertThat(result.getDetails().get("channelId")).isEqualTo(channelId);
        assertThat(result.getErrorCode()).isEqualTo(ErrorCode.CHANNEL_NOT_FOUND);
    }

    @Test
    @DisplayName("메세지 삭제 프로세스가 정상 작동 한다.")
    void deleteMessage_success() throws Exception {
        // given
        User user = User.builder()
            .username("paul")
            .email("a@a.com")
            .password("1234")
            .build();
        userRepository.save(user);

        Channel channel = Channel.builder()
            .name("public channel")
            .type(ChannelType.PUBLIC)
            .build();
        channelRepository.save(channel);

        Message message = Message.builder()
            .author(user)
            .channel(channel)
            .content("content")
            .build();
        messageRepository.save(message);

        // when
        messageRepository.deleteById(message.getId());

        // then
        assertThat(messageRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("삭제할 메세지가 없으면 MessageNotFoundException을 반환한다.")
    void deleteMessage_noMessage_MessageNotFoundException() throws Exception {
        // given
        long beforeCount = messageRepository.count();

        // when
        MessageNotFoundException exception = assertThrows(MessageNotFoundException.class, () -> messageService.deleteMessage(UUID.randomUUID()));

        // then
        assertThat(messageRepository.count()).isEqualTo(beforeCount);
        assertThat(exception.getMessage()).isEqualTo("메세지를 찾을 수 없습니다.");
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MESSAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("메세지 업데이트 프로세스가 정상 작동한다.")
    void messageUpdate_success() throws Exception {
        // given
        User user = User.builder()
            .username("paul")
            .email("a@a.com")
            .password("1234")
            .build();
        userRepository.save(user);

        Channel channel = Channel.builder()
            .name("public channel")
            .type(ChannelType.PUBLIC)
            .build();
        channelRepository.save(channel);

        Message message = Message.builder()
            .author(user)
            .channel(channel)
            .content("content")
            .build();
        messageRepository.save(message);

        MessageUpdateRequest request = new MessageUpdateRequest("new content");

        // when
        JpaMessageResponse response = messageService.updateMessage(message.getId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.author().id()).isEqualTo(user.getId());
        assertThat(response.channelId()).isEqualTo(channel.getId());
        assertThat(response.content()).isEqualTo("new content");
    }

    @Test
    @DisplayName("업데이트 메세지를 찾지 못할경우 MessageNotFoundException을 반환한다.")
    void updateMessage_noMessage_MessageNotFoundException() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("new content");

        // when
        MessageNotFoundException exception = assertThrows(MessageNotFoundException.class, () -> messageService.updateMessage(messageId, request));

        // then
        assertThat(exception.getMessage()).isEqualTo("메세지를 찾을 수 없습니다.");
        assertThat(exception.getDetails().get("messageId")).isEqualTo(messageId);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MESSAGE_NOT_FOUND);
    }

}
