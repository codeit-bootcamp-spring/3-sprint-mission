package com.sprint.mission.discodeit.service.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileMessageServiceTest {

    private FileUserService userService;
    private FileChannelService channelService;
    private FileMessageService messageService;
    private FileUserRepository userRepository;
    private FileChannelRepository channelRepository;
    private FileMessageRepository messageRepository;
    private Path messageDataDir;
    private Path userDataDir;
    private Path channelDataDir;
    private UUID testUserId;
    private UUID testChannelId;

    @BeforeAll
    void setUpClass() {
        try {
            // 데이터 디렉토리 경로 설정
            userDataDir = Paths.get(System.getProperty("user.dir"), "data", "users");
            channelDataDir = Paths.get(System.getProperty("user.dir"), "data", "channels");
            messageDataDir = Paths.get(System.getProperty("user.dir"), "data", "messages");

            // 모든 데이터 디렉토리 생성
            Files.createDirectories(userDataDir);
            Files.createDirectories(channelDataDir);
            Files.createDirectories(messageDataDir);

            // Repository 인스턴스 생성
            userRepository = new FileUserRepository();
            channelRepository = new FileChannelRepository();
            messageRepository = new FileMessageRepository();

            // Service 인스턴스 생성 (Repository 주입)
            userService = new FileUserService(userRepository);
            channelService = new FileChannelService(userService, channelRepository);

        } catch (IOException e) {
            throw new RuntimeException("테스트 디렉토리 또는 서비스 초기화 실패", e);
        }
    }

    @AfterAll
    void tearDownClass() {
        // 테스트 종료 후 모든 데이터 디렉토리 정리
        cleanupDirectory(messageDataDir);
        cleanupDirectory(channelDataDir);
        cleanupDirectory(userDataDir);
    }

    // 디렉토리 정리 헬퍼 메서드
    private void cleanupDirectory(Path dirPath) {
        if (Files.exists(dirPath)) {
            try {
                Files.walk(dirPath)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                System.err.println("파일 삭제 실패: " + path + " - " + e.getMessage());
                            }
                        });
            } catch (IOException e) {
                System.err.println("디렉토리 정리 중 오류 발생: " + dirPath + " - " + e.getMessage());
            }
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        // 각 테스트 전에 데이터 디렉토리 내용 비우기
        clearDirectory(messageDataDir);
        clearDirectory(channelDataDir);
        clearDirectory(userDataDir);

        // Service 인스턴스 재생성 (Repository 재사용)
        messageService = new FileMessageService(userService, channelService, messageRepository);

        // 테스트용 사용자 생성
        User testUser = userService.createUser("테스트메시지작성자", "msg@example.com", "password123");
        testUserId = testUser.getUserId();

        // 테스트용 채널 생성 (작성자가 소유자)
        Channel testChannel = channelService.createChannel("메시지테스트채널", false, "", testUserId);
        testChannelId = testChannel.getChannelId();
    }

    // 디렉토리 내용 비우기 헬퍼 메서드
    private void clearDirectory(Path dirPath) throws IOException {
        if (Files.exists(dirPath)) {
            try (var stream = Files.list(dirPath)) {
                stream.forEach(path -> {
                    try {
                        // 디렉토리 내 파일만 삭제 (하위 디렉토리는 가정하지 않음)
                        if (Files.isRegularFile(path)) {
                            Files.deleteIfExists(path);
                        }
                    } catch (IOException e) {
                        System.err.println("파일 삭제 실패: " + path + " - " + e.getMessage());
                    }
                });
            }
        }
    }

    @Test
    @DisplayName("메시지 생성 및 저장 확인")
    void createMessage_Success() {
        var content = "Hello, world!";
        var message = messageService.createMessage(content, testUserId, testChannelId);

        var loaded = messageService.getMessageById(message.getMessageId());

        assertAll(
                () -> assertNotNull(message.getMessageId()),
                () -> assertEquals(content, message.getContent()),
                () -> assertEquals(testUserId, message.getAuthorId()),
                () -> assertEquals(testChannelId, message.getChannelId()),
                () -> assertNotNull(loaded),
                () -> assertEquals(content, loaded.getContent())
        );
    }

    @Test
    @DisplayName("ID로 메시지 조회 - 존재하는 메시지")
    void getMessageById_ExistingMessage() {
        var content = "조회 테스트";
        var message = messageService.createMessage(content, testUserId, testChannelId);

        var found = messageService.getMessageById(message.getMessageId());

        assertAll(
                () -> assertNotNull(found),
                () -> assertEquals(content, found.getContent())
        );
    }

    @Test
    @DisplayName("채널별 메시지 조회")
    void getMessagesByChannel() {
        var msg1 = messageService.createMessage("채널1-1", testUserId, testChannelId);
        var msg2 = messageService.createMessage("채널1-2", testUserId, testChannelId);

        var user2 = userService.createUser("다른유저", "other@example.com", "pw");
        var channel2 = channelService.createChannel("다른채널", false, "", user2.getUserId());
        channelService.joinChannel(channel2.getChannelId(), testUserId, "");
        var msg3 = messageService.createMessage("채널2-1", testUserId, channel2.getChannelId());

        var list = messageService.getMessagesByChannel(testChannelId);

        assertAll(
                () -> assertEquals(2, list.size()),
                () -> assertTrue(list.stream().anyMatch(m -> m.getContent().equals("채널1-1"))),
                () -> assertTrue(list.stream().anyMatch(m -> m.getContent().equals("채널1-2")))
        );
    }

    @Test
    @DisplayName("작성자별 메시지 조회")
    void getMessagesByAuthor() {
        var msg1 = messageService.createMessage("작성자1", testUserId, testChannelId);

        // 다른 유저, 같은 채널
        var user2 = userService.createUser("작성자2", "author2@example.com", "pw");
        channelService.joinChannel(testChannelId, user2.getUserId(), "");
        var msg2 = messageService.createMessage("작성자2", user2.getUserId(), testChannelId);

        var list = messageService.getMessagesByAuthor(testUserId);

        assertAll(
                () -> assertEquals(1, list.size()),
                () -> assertEquals("작성자1", list.get(0).getContent())
        );
    }

    @Test
    @DisplayName("메시지 내용 업데이트")
    void updateMessage_Success() {
        var msg = messageService.createMessage("수정전", testUserId, testChannelId);
        messageService.updateMessage(msg.getMessageId(), "수정후");

        var updated = messageService.getMessageById(msg.getMessageId());
        assertEquals("수정후", updated.getContent());
    }

    @Test
    @DisplayName("메시지 삭제 확인")
    void deleteMessage_Success() {
        var msg = messageService.createMessage("삭제될 메시지", testUserId, testChannelId);
        var id = msg.getMessageId();

        messageService.deleteMessage(id);
        assertNull(messageService.getMessageById(id));
    }

    @Test
    @DisplayName("메시지 생성 시 존재하지 않는 작성자 ID 사용 시 예외 발생")
    void createMessage_shouldThrowExceptionForNonExistingAuthor() {
        var nonExistUserId = UUID.randomUUID();
        var ex = assertThrows(IllegalArgumentException.class, ()
                -> messageService.createMessage("fail", nonExistUserId, testChannelId)
        );
        assertTrue(ex.getMessage().contains("존재하지 않는 작성자"));
    }

    @Test
    @DisplayName("메시지 생성 시 존재하지 않는 채널 ID 사용 시 예외 발생")
    void createMessage_shouldThrowExceptionForNonExistingChannel() {
        var nonExistChannelId = UUID.randomUUID();
        var ex = assertThrows(IllegalArgumentException.class, ()
                -> messageService.createMessage("fail", testUserId, nonExistChannelId)
        );
        assertTrue(ex.getMessage().contains("존재하지 않는 채널"));
    }

    @Test
    @DisplayName("메시지 생성 시 채널에 참가하지 않은 사용자가 작성 시 예외 발생")
    void createMessage_shouldThrowExceptionWhenAuthorIsNotParticipant() {
        var user2 = userService.createUser("참가안함", "nojoin@example.com", "pw");
        var ex = assertThrows(IllegalStateException.class, ()
                -> messageService.createMessage("fail", user2.getUserId(), testChannelId)
        );
        assertTrue(ex.getMessage().contains("채널 참가자만 메시지를 작성할 수 있습니다"));
    }

    @Test
    @DisplayName("존재하지 않는 메시지 업데이트 시도 시 예외 발생")
    void updateMessage_shouldThrowExceptionForNonExistingMessage() {
        var nonExistMsgId = UUID.randomUUID();
        var ex = assertThrows(IllegalArgumentException.class, ()
                -> messageService.updateMessage(nonExistMsgId, "업데이트")
        );
        assertTrue(ex.getMessage().contains("존재하지 않는 메시지입니다"));
    }

    @Test
    @DisplayName("존재하지 않는 메시지 삭제 시도 시 예외 발생")
    void deleteMessage_shouldThrowExceptionForNonExistingMessage() {
        var nonExistMsgId = UUID.randomUUID();
        var ex = assertThrows(IllegalArgumentException.class, ()
                -> messageService.deleteMessage(nonExistMsgId)
        );
        assertTrue(ex.getMessage().contains("존재하지 않는 메시지입니다"));
    }
}
