package com.sprint.mission.discodeit.service.file;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.sprint.mission.discodeit.entity.User;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileUserServiceTest {

    private FileUserService userService;
    private Path dataDir;
    
    @BeforeAll
    void setUpClass() {
        // 테스트를 위한 디렉토리 준비
        try {
            dataDir = Paths.get(System.getProperty("user.dir"), "data", "users");
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            throw new RuntimeException("테스트 디렉토리 생성 실패", e);
        }
    }

    @AfterAll
    void tearDownClass() {
        // 테스트 종료 후 데이터 디렉토리 완전히 정리
        try {
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
        } catch (IOException e) {
            System.err.println("정리 중 오류 발생: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        // 매 테스트 전에 사용자 파일들 삭제하여 테스트 간 격리
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
        
        // 새로운 FileUserService 인스턴스 생성
        userService = new FileUserService();
    }

    @Test
    @DisplayName("사용자 생성 및 파일 저장 확인")
    void testCreateUser() throws IOException {
        // 사용자 생성
        User user = userService.createUser("테스트사용자", "test@example.com", "password123");
        
        // 파일 생성 확인
        Path userFile = dataDir.resolve(user.getUserId() + ".ser");
        
        // 모든 검증을 진행하도록 assertAll 사용
        assertAll(
            () -> assertTrue(Files.exists(userFile), "사용자 파일이 생성되어야 함"),
            () -> assertNotNull(user.getUserId(), "사용자 ID가 할당되어야 함"),
            () -> assertEquals("테스트사용자", user.getUserName(), "사용자명이 저장되어야 함"),
            () -> assertEquals("password123", user.getPassword(), "비밀번호가 저장되어야 함")
        );
    }

    @Test
    @DisplayName("ID로 사용자 조회")
    void testGetUserById() {
        // 사용자 생성
        User createdUser = userService.createUser("조회테스트", "read@example.com", "password");
        UUID userId = createdUser.getUserId();
        
        // 사용자 조회
        User retrievedUser = userService.getUserById(userId);
        
        // 조회 결과 검증
        assertAll(
            () -> assertNotNull(retrievedUser, "사용자가 조회되어야 함"),
            () -> assertEquals(userId, retrievedUser.getUserId(), "조회된 사용자의 ID가 일치해야 함"),
            () -> assertEquals("조회테스트", retrievedUser.getUserName(), "조회된 사용자의 이름이 일치해야 함"),
            () -> assertEquals("password", retrievedUser.getPassword(), "조회된 사용자의 비밀번호가 일치해야 함")
        );
    }

    @Test
    @DisplayName("모든 사용자 조회")
    void testGetAllUsers() {
        // 여러 사용자 생성
        userService.createUser("사용자1", "user1@example.com", "pass1");
        userService.createUser("사용자2", "user2@example.com", "pass2");
        userService.createUser("사용자3", "user3@example.com", "pass3");
        
        // 모든 사용자 조회
        List<User> allUsers = userService.getAllUsers();
        
        // 결과 검증
        assertEquals(3, allUsers.size(), "생성된 사용자 수와 일치해야 함");
    }

    @Test
    @DisplayName("사용자 이름 업데이트")
    void testUpdateUserName() {
        // 사용자 생성
        User user = userService.createUser("원래이름", "update@example.com", "password");
        UUID userId = user.getUserId();
        
        // 이름 업데이트
        userService.updateUserName(userId, "변경된이름");
        
        // 변경 확인
        User updatedUser = userService.getUserById(userId);
        assertAll(
            () -> assertEquals("변경된이름", updatedUser.getUserName(), "사용자 이름이 업데이트되어야 함"),
            () -> assertEquals("password", updatedUser.getPassword(), "비밀번호는 유지되어야 함")
        );
    }

    @Test
    @DisplayName("사용자 이메일 업데이트")
    void testUpdateUserEmail() {
        // 사용자 생성
        User user = userService.createUser("이메일테스트", "old@example.com", "password");
        UUID userId = user.getUserId();
        
        // 이메일 업데이트
        userService.updateUserEmail(userId, "new@example.com");
        
        // 변경 확인
        User updatedUser = userService.getUserById(userId);
        assertAll(
            () -> assertEquals("new@example.com", updatedUser.getEmail(), "사용자 이메일이 업데이트되어야 함"),
            () -> assertEquals("password", updatedUser.getPassword(), "비밀번호는 유지되어야 함")
        );
    }

    @Test
    @DisplayName("사용자 비밀번호 업데이트")
    void testUpdateUserPassword() {
        // 사용자 생성
        User user = userService.createUser("비밀번호테스트", "pwd@example.com", "oldpass");
        UUID userId = user.getUserId();
        
        // 비밀번호 업데이트
        userService.updateUserPassword(userId, "newpass");
        
        // 변경 확인 
        User updatedUser = userService.getUserById(userId);
        assertAll(
            () -> assertEquals("newpass", updatedUser.getPassword(), "사용자 비밀번호가 업데이트되어야 함"),
            () -> assertEquals("비밀번호테스트", updatedUser.getUserName(), "사용자 이름은 유지되어야 함"),
            () -> assertEquals("pwd@example.com", updatedUser.getEmail(), "사용자 이메일은 유지되어야 함")
        );
    }

    @Test
    @DisplayName("사용자 삭제")
    void testDeleteUser() throws IOException {
        // 사용자 생성
        User user = userService.createUser("삭제테스트", "delete@example.com", "password");
        UUID userId = user.getUserId();
        
        // 파일 경로 확인
        Path userFile = dataDir.resolve(userId + ".ser");
        assertTrue(Files.exists(userFile), "사용자 생성 후 파일이 존재해야 함");
        
        // 사용자 삭제
        userService.deleteUser(userId);
        
        // 삭제 확인
        assertAll(
            () -> assertFalse(Files.exists(userFile), "사용자 삭제 후 파일이 존재하지 않아야 함"),
            () -> assertNull(userService.getUserById(userId), "삭제된 사용자는 조회되지 않아야 함")
        );
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회")
    void testGetNonExistingUser() {
        // 존재하지 않는 UUID로 조회
        UUID nonExistingId = UUID.randomUUID();
        User user = userService.getUserById(nonExistingId);
        
        // 결과 검증
        assertNull(user, "존재하지 않는 사용자는 null을 반환해야 함");
    }

    @Test
    @DisplayName("null 또는 빈 비밀번호로 업데이트 시 예외 발생")
    void testUpdatePasswordWithInvalidInput() {
        // 사용자 생성
        User user = userService.createUser("예외테스트", "exception@example.com", "password");
        UUID userId = user.getUserId();
        
        // null 비밀번호로 업데이트 시도
        Exception nullException = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserPassword(userId, null);
        });
        
        // 빈 비밀번호로 업데이트 시도
        Exception emptyException = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserPassword(userId, "");
        });
        
        assertAll(
            () -> assertTrue(nullException.getMessage().contains("null") || nullException.getMessage().contains("Null"), 
                "비밀번호는 null 값일 수 없습니다."),
            () -> assertTrue(emptyException.getMessage().contains("빈") || emptyException.getMessage().contains("비어"), 
                "비밀번호는 빈 값일 수 없습니다.")
        );
    }

    @Test
    @DisplayName("여러 사용자 동시 업데이트 테스트")
    void testConcurrentUserUpdates() throws Exception {
        // 테스트 사용자 생성
        User user = userService.createUser("동시성테스트", "concurrent@example.com", "password");
        UUID userId = user.getUserId();
        
        // 여러 스레드에서 동시에 사용자 이름 업데이트
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                userService.updateUserName(userId, "업데이트된이름" + index);
            });
            threads[i].start();
        }
        
        // 모든 스레드 완료 대기
        for (Thread thread : threads) {
            thread.join();
        }
        
        // 최종 사용자 상태 확인
        User updatedUser = userService.getUserById(userId);
        assertAll(
            () -> assertNotNull(updatedUser, "사용자가 여전히 존재해야 함"),
            () -> assertTrue(updatedUser.getUserName().startsWith("업데이트된이름"), "사용자 이름이 업데이트되어야 함"),
            () -> assertEquals("password", updatedUser.getPassword(), "비밀번호는 유지되어야 함")
        );
    }

    @Test
    @DisplayName("대용량 사용자 처리 테스트")
    void testLargeNumberOfUsers() {
        // 다수의 사용자 생성
        final int userCount = 100;
        for (int i = 0; i < userCount; i++) {
            userService.createUser("대용량테스트" + i, "bulk" + i + "@example.com", "pass" + i);
        }
        
        // 모든 사용자 조회
        List<User> allUsers = userService.getAllUsers();
        
        // 결과 검증
        assertEquals(userCount, allUsers.size(), "모든 사용자가 조회되어야 함");
    }

    @Test
    @DisplayName("사용자 파일 구조 및 내용 검증")
    void testUserFileStructure() throws IOException {
        // 특정 데이터로 사용자 생성
        String userName = "파일구조테스트";
        String email = "file@example.com";
        String password = "testpassword";
        User user = userService.createUser(userName, email, password);
        
        // 파일 경로 획득
        Path userFile = dataDir.resolve(user.getUserId() + ".ser");
        
        User loadedUser = userService.getUserById(user.getUserId());
        
        // 파일 존재 및 내용 검증
        assertAll(
            () -> assertTrue(Files.exists(userFile), "사용자 파일이 존재해야 함"),
            () -> assertTrue(Files.size(userFile) > 0, "사용자 파일은 비어있지 않아야 함"),
            () -> assertEquals(userName, loadedUser.getUserName(), "사용자 이름이 올바르게 저장되어야 함"),
            () -> assertEquals(email, loadedUser.getEmail(), "이메일이 올바르게 저장되어야 함"),
            () -> assertEquals(password, loadedUser.getPassword(), "비밀번호가 올바르게 저장되어야 함")
        );
    }

    @Test
    @DisplayName("같은 ID 중복 조회 시 같은 객체 반환 확인")
    void testMultipleRetrievalOfSameUser() {
        // 사용자 생성
        User user = userService.createUser("중복조회테스트", "multiple@example.com", "password");
        UUID userId = user.getUserId();
        
        // 같은 ID로 여러 번 조회
        User firstRetrieval = userService.getUserById(userId);
        User secondRetrieval = userService.getUserById(userId);
        
        // 데이터 일관성 확인 (내용 동일)
        assertAll(
            () -> assertEquals(firstRetrieval.getUserName(), secondRetrieval.getUserName(), "여러 번 조회해도 같은 이름이어야 함"),
            () -> assertEquals(firstRetrieval.getEmail(), secondRetrieval.getEmail(), "여러 번 조회해도 같은 이메일이어야 함"),
            () -> assertEquals(firstRetrieval.getPassword(), secondRetrieval.getPassword(), "여러 번 조회해도 같은 비밀번호여야 함")
        );
    }

    @Test
    @DisplayName("사용자 삭제 후 같은 이름으로 재생성")
    void testRecreateUserAfterDeletion() {
        // 사용자 생성
        String userName = "재생성테스트";
        String email = "recreate@example.com";
        String password = "password";
        User originalUser = userService.createUser(userName, email, password);
        UUID originalId = originalUser.getUserId();
        
        // 사용자 삭제
        userService.deleteUser(originalId);
        assertNull(userService.getUserById(originalId), "삭제 후 사용자가 없어야 함");
        
        // 같은 이름과 이메일로 재생성
        String newPassword = "newpassword";
        User recreatedUser = userService.createUser(userName, email, newPassword);
        
        // 검증
        assertAll(
            () -> assertNotNull(recreatedUser, "재생성된 사용자가 있어야 함"),
            () -> assertNotEquals(originalId, recreatedUser.getUserId(), "재생성된 사용자는 새로운 ID를 가져야 함"),
            () -> assertEquals(userName, recreatedUser.getUserName(), "재생성된 사용자의 이름은 동일해야 함"),
            () -> assertEquals(email, recreatedUser.getEmail(), "재생성된 사용자의 이메일은 동일해야 함"),
            () -> assertEquals(newPassword, recreatedUser.getPassword(), "재생성된 사용자의 비밀번호는 새로 설정한 값이어야 함")
        );
    }

    @Test
    @DisplayName("존재하지 않는 사용자 업데이트 시도")
    void testUpdateNonExistingUser() {
        // 존재하지 않는 UUID
        UUID nonExistingId = UUID.randomUUID();
        
        assertAll(
            // 이름 업데이트 시도
            () -> assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUserName(nonExistingId, "새이름");
            }, "존재하지 않는 사용자 업데이트는 예외를 발생시켜야 함"),
            
            // 이메일 업데이트 시도
            () -> assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUserEmail(nonExistingId, "new@example.com");
            }, "존재하지 않는 사용자 업데이트는 예외를 발생시켜야 함"),
            
            // 비밀번호 업데이트 시도
            () -> assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUserPassword(nonExistingId, "newpassword");
            }, "존재하지 않는 사용자 업데이트는 예외를 발생시켜야 함")
        );
    }

    @Test
    @DisplayName("특수 문자가 포함된 사용자 정보 저장 및 로드")
    void testSpecialCharactersInUserData() {
        // 특수 문자가 포함된 데이터
        String nameWithSpecialChars = "테스트!@#$%^&*()";
        String emailWithSpecialChars = "special+chars.test@example.com";
        String passwordWithSpecialChars = "p@$$w0rd!";
        
        // 사용자 생성
        User user = userService.createUser(nameWithSpecialChars, emailWithSpecialChars, passwordWithSpecialChars);
        
        // 사용자 조회
        User retrievedUser = userService.getUserById(user.getUserId());
        
        // 검증
        assertAll(
            () -> assertEquals(nameWithSpecialChars, retrievedUser.getUserName(), "특수 문자가 포함된 이름이 올바르게 저장되어야 함"),
            () -> assertEquals(emailWithSpecialChars, retrievedUser.getEmail(), "특수 문자가 포함된 이메일이 올바르게 저장되어야 함"),
            () -> assertEquals(passwordWithSpecialChars, retrievedUser.getPassword(), "특수 문자가 포함된 비밀번호가 올바르게 저장되어야 함")
        );
    }

    @Test
    @DisplayName("사용자 CRUD 작업 통합 테스트")
    void testUserCRUDOperationsIntegration() {
        // Create
        User user = userService.createUser("통합테스트", "integration@example.com", "initialPass");
        UUID userId = user.getUserId();
        assertNotNull(userService.getUserById(userId), "생성된 사용자가 조회되어야 함");
        
        // Read
        User retrievedUser = userService.getUserById(userId);
        assertAll(
            () -> assertEquals("통합테스트", retrievedUser.getUserName(), "조회된 사용자의 이름이 일치해야 함"),
            () -> assertEquals("initialPass", retrievedUser.getPassword(), "조회된 사용자의 비밀번호가 일치해야 함")
        );
        
        // Update
        userService.updateUserName(userId, "업데이트된이름");
        userService.updateUserEmail(userId, "updated@example.com");
        userService.updateUserPassword(userId, "newPassword");
        
        User updatedUser = userService.getUserById(userId);
        assertAll(
            () -> assertEquals("업데이트된이름", updatedUser.getUserName(), "이름이 업데이트되어야 함"),
            () -> assertEquals("updated@example.com", updatedUser.getEmail(), "이메일이 업데이트되어야 함"),
            () -> assertEquals("newPassword", updatedUser.getPassword(), "비밀번호가 업데이트되어야 함")
        );
        
        // Delete
        userService.deleteUser(userId);
        assertNull(userService.getUserById(userId), "삭제된 사용자는 조회되지 않아야 함");
    }
}
