package com.sprint.mission.discodeit.service.jcf;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sprint.mission.discodeit.entity.User;

class JCFUserServiceTest {

    private JCFUserService userService;

    @BeforeEach
    public void setUp() {
        this.userService = new JCFUserService();
    }

    @Test
    @DisplayName("새로운 사용자 생성 및 ID 확인")
    void createUser_shouldReturnUserWithId() {
        // Given
        String username = "testUser";
        String email = "test@example.com";
        String password = "password";

        // When
        User createdUser = userService.createUser(username, email, password);

        // Then
        assertNotNull(createdUser, "생성된 사용자는 null이 아니어야 합니다.");
        assertNotNull(createdUser.getUserId(), "사용자 ID가 할당되어야 합니다.");
        assertEquals(username, createdUser.getUserName(), "사용자 이름이 일치해야 합니다.");
        assertEquals(email, createdUser.getEmail(), "이메일이 일치해야 합니다.");
        assertEquals(password, createdUser.getPassword(), "비밀번호가 일치해야 합니다.");
    }

    @Test
    @DisplayName("ID로 사용자 조회 성공")
    void getUserById_shouldReturnCorrectUser() {
        // Given
        User user = userService.createUser("findMe", "find@me.com", "pass");
        UUID userId = user.getUserId();

        // When
        User foundUser = userService.getUserById(userId);

        // Then
        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getUserId());
        assertEquals("findMe", foundUser.getUserName());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 사용자 조회 시 null 반환")
    void getUserById_shouldReturnNullForNonExistingUser() {
        // Given
        UUID nonExistingId = UUID.randomUUID();

        // When
        User foundUser = userService.getUserById(nonExistingId);

        // Then
        assertNull(foundUser, "존재하지 않는 사용자는 null이어야 합니다.");
    }

    @Test
    @DisplayName("모든 사용자 조회")
    void getAllUsers_shouldReturnAllCreatedUsers() {
        // Given
        userService.createUser("user1", "u1@e.com", "p1");
        userService.createUser("user2", "u2@e.com", "p2");

        // When
        List<User> allUsers = userService.getAllUsers();

        // Then
        assertNotNull(allUsers);
        assertEquals(2, allUsers.size(), "생성된 사용자 수만큼 반환되어야 합니다.");
    }

    @Test
    @DisplayName("사용자 이름 업데이트")
    void updateUserName_shouldChangeUserName() {
        // Given
        User user = userService.createUser("oldName", "old@e.com", "p");
        UUID userId = user.getUserId();
        String newUsername = "newName";

        // When
        userService.updateUserName(userId, newUsername);
        User updatedUser = userService.getUserById(userId);

        // Then
        assertNotNull(updatedUser);
        assertEquals(newUsername, updatedUser.getUserName(), "사용자 이름이 업데이트되어야 합니다.");
    }

    @Test
    @DisplayName("사용자 이메일 업데이트")
    void updateUserEmail_shouldChangeUserEmail() {
        // Given
        User user = userService.createUser("user", "old@e.com", "p");
        UUID userId = user.getUserId();
        String newEmail = "new@e.com";

        // When
        userService.updateUserEmail(userId, newEmail);
        User updatedUser = userService.getUserById(userId);

        // Then
        assertNotNull(updatedUser);
        assertEquals(newEmail, updatedUser.getEmail(), "사용자 이메일이 업데이트되어야 합니다.");
    }

    @Test
    @DisplayName("사용자 삭제")
    void deleteUser_shouldRemoveUser() {
        // Given
        User userToDelete = userService.createUser("deleteMe", "delete@me.com", "pass");
        UUID userId = userToDelete.getUserId();
        userService.createUser("keepMe", "keep@me.com", "pass");

        // When
        userService.deleteUser(userId);
        User foundUser = userService.getUserById(userId);
        List<User> remainingUsers = userService.getAllUsers();

        // Then
        assertNull(foundUser, "삭제된 사용자는 조회 시 null이어야 합니다.");
        assertEquals(1, remainingUsers.size(), "삭제 후 사용자 수가 1이어야 합니다.");
        assertEquals("keepMe", remainingUsers.get(0).getUserName(), "삭제되지 않은 사용자만 남아있어야 합니다.");
    }

    // updateUserPassword 테스트 등 추가...
}
