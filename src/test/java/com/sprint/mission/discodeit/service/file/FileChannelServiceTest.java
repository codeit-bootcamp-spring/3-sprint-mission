package com.sprint.mission.discodeit.service.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileChannelServiceTest {

    private FileUserService userService;
    private FileChannelService channelService;
    private Path dataDir;
    private UUID testUserId;  // 테스트용 사용자 ID

    @BeforeAll
    void setUpClass() {
        // 테스트를 위한 디렉토리 준비
        try {
            // 사용자 디렉토리 생성
            Path userDataDir = Paths.get(System.getProperty("user.dir"), "data", "users");
            Files.createDirectories(userDataDir);

            // 채널 디렉토리 생성 및 설정
            dataDir = Paths.get(System.getProperty("user.dir"), "data", "channels");
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            throw new RuntimeException("테스트 디렉토리 생성 실패", e);
        }
    }

    @AfterAll
    void tearDownClass() {
        // 테스트 종료 후 데이터 디렉토리 완전히 정리
        try {
            // 채널 디렉토리 정리
            if (Files.exists(dataDir)) {
                Files.walk(dataDir)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                System.err.println("파일 삭제 실패: " + path);
                            }
                        });
            }

            // 사용자 디렉토리 정리
            Path userDataDir = Paths.get(System.getProperty("user.dir"), "data", "users");
            if (Files.exists(userDataDir)) {
                Files.walk(userDataDir)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                System.err.println("파일 삭제 실패: " + path);
                            }
                        });
            }
        } catch (IOException e) {
            System.err.println("정리 중 오류 발생: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        // 매 테스트 전에 파일들 삭제하여 테스트 간 격리

        // 채널 파일 삭제
        if (Files.exists(dataDir)) {
            Files.list(dataDir)
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            System.err.println("파일 삭제 실패: " + path);
                        }
                    });
        }

        // 사용자 디렉토리 파일 삭제
        Path userDataDir = Paths.get(System.getProperty("user.dir"), "data", "users");
        if (Files.exists(userDataDir)) {
            Files.list(userDataDir)
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            System.err.println("파일 삭제 실패: " + path);
                        }
                    });
        }

        // 새로운 서비스 인스턴스 생성
        userService = new FileUserService();
        channelService = new FileChannelService(userService);

        // 테스트용 사용자 생성
        User testUser = userService.createUser("테스트사용자", "test@example.com", "password123");
        testUserId = testUser.getUserId();
    }

    @Test
    @DisplayName("채널 생성 및 파일 저장 확인")
    void testCreateChannel() throws IOException {
        // 채널 생성
        Channel channel = channelService.createChannel("테스트채널", false, "", testUserId);

        // 파일 생성 확인
        Path channelFile = dataDir.resolve(channel.getChannelId() + ".ser");

        // 모든 검증을 진행하도록 assertAll 사용
        assertAll(
                () -> assertTrue(Files.exists(channelFile), "채널 파일이 생성되어야 함"),
                () -> assertNotNull(channel.getChannelId(), "채널 ID가 할당되어야 함"),
                () -> assertEquals("테스트채널", channel.getChannelName(), "채널명이 저장되어야 함"),
                () -> assertEquals(testUserId, channel.getOwnerChannelId(), "소유자 ID가 저장되어야 함"),
                () -> assertTrue(channel.isParticipant(testUserId), "소유자는 채널 참가자에 포함되어야 함")
        );
    }

    @Test
    @DisplayName("ID로 채널 조회")
    void testGetChannelById() {
        // 채널 생성
        Channel createdChannel = channelService.createChannel("조회테스트", false, "", testUserId);
        UUID channelId = createdChannel.getChannelId();

        // 채널 조회
        Channel retrievedChannel = channelService.getChannelById(channelId);

        // 조회 결과 검증
        assertAll(
                () -> assertNotNull(retrievedChannel, "채널이 조회되어야 함"),
                () -> assertEquals(channelId, retrievedChannel.getChannelId(), "조회된 채널의 ID가 일치해야 함"),
                () -> assertEquals("조회테스트", retrievedChannel.getChannelName(), "조회된 채널의 이름이 일치해야 함"),
                () -> assertEquals(testUserId, retrievedChannel.getOwnerChannelId(), "조회된 채널의 소유자가 일치해야 함")
        );
    }

    @Test
    @DisplayName("모든 채널 조회")
    void testGetAllChannels() {
        // 여러 채널 생성
        channelService.createChannel("채널1", false, "", testUserId);
        channelService.createChannel("채널2", true, "password2", testUserId);
        channelService.createChannel("채널3", false, "", testUserId);

        // 모든 채널 조회
        List<Channel> allChannels = channelService.getAllChannels();

        // 결과 검증
        assertEquals(3, allChannels.size(), "생성된 채널 수와 일치해야 함");
    }

    @Test
    @DisplayName("채널 참가자 조회")
    void testGetChannelParticipants() {
        // 채널 생성
        Channel channel = channelService.createChannel("참가자테스트", false, "", testUserId);
        UUID channelId = channel.getChannelId();

        // 새 사용자 생성 및 채널 참가
        User newUser = userService.createUser("신규사용자", "new@example.com", "password");
        channelService.joinChannel(channelId, newUser.getUserId(), "");

        // 참가자 목록 조회
        Set<UUID> participants = channelService.getChannelParticipants(channelId);

        // 검증
        assertAll(
                () -> assertEquals(2, participants.size(), "참가자 수는 2명이어야 함"),
                () -> assertTrue(participants.contains(testUserId), "참가자 목록에 소유자가 포함되어야 함"),
                () -> assertTrue(participants.contains(newUser.getUserId()), "참가자 목록에 신규 사용자가 포함되어야 함")
        );
    }

    @Test
    @DisplayName("채널 참가 기능 확인")
    void testJoinChannel() {
        // 비공개 채널 생성
        String password = "secretpass";
        Channel privateChannel = channelService.createChannel("비공개채널", true, password, testUserId);
        UUID channelId = privateChannel.getChannelId();

        // 새 사용자 생성
        User newUser = userService.createUser("참가자", "join@example.com", "password");
        UUID newUserId = newUser.getUserId();

        // 올바른 비밀번호로 참가 시도
        boolean joinResult = channelService.joinChannel(channelId, newUserId, password);

        // 검증
        assertAll(
                () -> assertTrue(joinResult, "올바른 비밀번호로 참가 시 true를 반환해야 함"),
                () -> assertTrue(channelService.getChannelParticipants(channelId).contains(newUserId), "참가 후 참가자 목록에 사용자가 포함되어야 함")
        );

        // 이미 참가한 사용자의 재참가 시도
        boolean rejoinResult = channelService.joinChannel(channelId, newUserId, password);
        assertFalse(rejoinResult, "이미 참가한 사용자는 false를 반환해야 함");
    }

    @Test
    @DisplayName("잘못된 비밀번호로 채널 참가 시도")
    void testJoinChannelWithWrongPassword() {
        // 비공개 채널 생성
        Channel privateChannel = channelService.createChannel("비밀번호테스트", true, "correctpass", testUserId);
        UUID channelId = privateChannel.getChannelId();

        // 새 사용자 생성
        User newUser = userService.createUser("참가시도자", "attempt@example.com", "password");
        UUID newUserId = newUser.getUserId();

        // 잘못된 비밀번호로 참가 시도
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            channelService.joinChannel(channelId, newUserId, "wrongpass");
        });

        // 예외 메시지 검증
        assertTrue(exception.getMessage().contains("비밀번호"), "예외 메시지에 '비밀번호' 문자열이 포함되어야 함");
    }

    @Test
    @DisplayName("채널 정보 업데이트")
    void testUpdateChannel() {
        // 채널 생성
        Channel channel = channelService.createChannel("원래채널명", false, "", testUserId);
        UUID channelId = channel.getChannelId();

        // 채널 정보 업데이트
        channelService.updateChannel(channelId, "변경된채널명", true, "newpassword");

        // 업데이트된 채널 조회
        Channel updatedChannel = channelService.getChannelById(channelId);

        // 검증
        assertAll(
                () -> assertEquals("변경된채널명", updatedChannel.getChannelName(), "채널명이 업데이트되어야 함"),
                () -> assertTrue(updatedChannel.isPrivate(), "비공개 여부가 업데이트되어야 함"),
                () -> assertEquals("newpassword", updatedChannel.getPassword(), "비밀번호가 업데이트되어야 함")
        );
    }

    @Test
    @DisplayName("채널 나가기 기능 확인")
    void testLeaveChannel() {
        // 채널 생성
        Channel channel = channelService.createChannel("퇴장테스트", false, "", testUserId);
        UUID channelId = channel.getChannelId();

        // 새 사용자 생성 및 채널 참가
        User user1 = userService.createUser("참가자1", "user1@example.com", "password");
        User user2 = userService.createUser("참가자2", "user2@example.com", "password");

        channelService.joinChannel(channelId, user1.getUserId(), "");
        channelService.joinChannel(channelId, user2.getUserId(), "");

        // 참가자 수 확인
        assertEquals(3, channelService.getChannelParticipants(channelId).size(), "참가자는 3명이어야 함");

        // 채널 나가기
        boolean leaveResult = channelService.leaveChannel(channelId, user1.getUserId());

        // 검증
        assertAll(
                () -> assertTrue(leaveResult, "채널 나가기 성공 시 true를 반환해야 함"),
                () -> assertEquals(2, channelService.getChannelParticipants(channelId).size(), "나간 후 참가자는 2명이어야 함"),
                () -> assertFalse(channelService.getChannelParticipants(channelId).contains(user1.getUserId()),
                        "나간 사용자는 참가자 목록에 없어야 함")
        );
    }

    @Test
    @DisplayName("채널 소유자가 나가기 시도 시 예외 발생")
    void testOwnerLeavingChannel() {
        // 채널 생성
        Channel channel = channelService.createChannel("소유자테스트", false, "", testUserId);
        UUID channelId = channel.getChannelId();

        // 소유자가 나가기 시도
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            channelService.leaveChannel(channelId, testUserId);
        });

        // 예외 메시지 검증
        assertTrue(exception.getMessage().contains("소유자"), "예외 메시지에 '소유자' 문자열이 포함되어야 함");
    }

    @Test
    @DisplayName("채널 삭제")
    void testDeleteChannel() throws IOException {
        // 채널 생성
        Channel channel = channelService.createChannel("삭제테스트", false, "", testUserId);
        UUID channelId = channel.getChannelId();

        // 파일 경로 확인
        Path channelFile = dataDir.resolve(channelId + ".ser");
        assertTrue(Files.exists(channelFile), "채널 생성 후 파일이 존재해야 함");

        // 채널 삭제
        channelService.deleteChannel(channelId);

        // 삭제 확인
        assertAll(
                () -> assertFalse(Files.exists(channelFile), "채널 삭제 후 파일이 존재하지 않아야 함"),
                () -> assertNull(channelService.getChannelById(channelId), "삭제된 채널은 조회되지 않아야 함")
        );
    }

    @Test
    @DisplayName("존재하지 않는 채널 조회")
    void testGetNonExistingChannel() {
        // 존재하지 않는 UUID로 조회
        UUID nonExistingId = UUID.randomUUID();
        Channel channel = channelService.getChannelById(nonExistingId);

        // 결과 검증
        assertNull(channel, "존재하지 않는 채널은 null을 반환해야 함");
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 채널 생성 시도")
    void testCreateChannelWithNonExistingUser() {
        // 존재하지 않는 사용자 ID
        UUID nonExistingUserId = UUID.randomUUID();

        // 채널 생성 시도
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            channelService.createChannel("실패채널", false, "", nonExistingUserId);
        });

        // 예외 메시지 검증
        assertTrue(exception.getMessage().contains("존재하지 않는 사용자"), "적절한 예외 메시지를 포함해야 함");
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 채널 참가 시도")
    void testJoinChannelWithNonExistingUser() {
        // 채널 생성
        Channel channel = channelService.createChannel("참가테스트", false, "", testUserId);
        UUID channelId = channel.getChannelId();

        // 존재하지 않는 사용자 ID
        UUID nonExistingUserId = UUID.randomUUID();

        // 참가 시도
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            channelService.joinChannel(channelId, nonExistingUserId, "");
        });

        // 예외 메시지 검증
        assertTrue(exception.getMessage().contains("존재하지 않는 사용자"), "적절한 예외 메시지를 포함해야 함");
    }

    @Test
    @DisplayName("존재하지 않는 채널 업데이트 시도")
    void testUpdateNonExistingChannel() {
        // 존재하지 않는 UUID
        UUID nonExistingId = UUID.randomUUID();

        // 업데이트 시도
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            channelService.updateChannel(nonExistingId, "새이름", false, "");
        });

        // 예외 메시지 검증
        assertTrue(exception.getMessage().contains("존재하지 않는 채널"), "적절한 예외 메시지를 포함해야 함");
    }

    @Test
    @DisplayName("채널 CRUD 작업 통합 테스트")
    void testChannelCRUDOperationsIntegration() {
        // Create
        Channel channel = channelService.createChannel("통합테스트", false, "", testUserId);
        UUID channelId = channel.getChannelId();
        assertNotNull(channelService.getChannelById(channelId), "생성된 채널이 조회되어야 함");

        // Read
        Channel retrievedChannel = channelService.getChannelById(channelId);
        assertAll(
                () -> assertEquals("통합테스트", retrievedChannel.getChannelName(), "조회된 채널의 이름이 일치해야 함"),
                () -> assertEquals(testUserId, retrievedChannel.getOwnerChannelId(), "조회된 채널의 소유자가 일치해야 함"),
                () -> assertFalse(retrievedChannel.isPrivate(), "조회된 채널은 공개 채널이어야 함")
        );

        // Update
        channelService.updateChannel(channelId, "업데이트된채널", true, "updatepass");

        Channel updatedChannel = channelService.getChannelById(channelId);
        assertAll(
                () -> assertEquals("업데이트된채널", updatedChannel.getChannelName(), "채널명이 업데이트되어야 함"),
                () -> assertTrue(updatedChannel.isPrivate(), "비공개 여부가 업데이트되어야 함"),
                () -> assertEquals("updatepass", updatedChannel.getPassword(), "비밀번호가 업데이트되어야 함")
        );

        // Delete
        channelService.deleteChannel(channelId);
        assertNull(channelService.getChannelById(channelId), "삭제된 채널은 조회되지 않아야 함");
    }
}
