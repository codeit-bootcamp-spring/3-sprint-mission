package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;

class BasicUserServiceTest {

    private UserService userService;
    private JCFUserRepository userRepository;

    @BeforeEach
    void setUp() {
        JCFUserRepository.clearInstance();
        userRepository = JCFUserRepository.getInstance();
        userRepository.clearData();
        userService = new BasicUserService(userRepository);
    }

    @Test
    @DisplayName("새로운 사용자 생성 및 ID 확인")
    void createUser_shouldReturnUserWithId() {
        User createdUser = userService.createUser("testUser", "test@example.com", "password");
        assertAll(
                () -> assertNotNull(createdUser),
                () -> assertNotNull(createdUser.getUserId()),
                () -> assertEquals("testUser", createdUser.getUserName()),
                () -> assertEquals("test@example.com", createdUser.getEmail()),
                () -> assertEquals("password", createdUser.getPassword())
        );
    }

    @Test
    @DisplayName("ID로 사용자 조회 성공")
    void getUserById_shouldReturnCorrectUser() {
        User user = userService.createUser("findMe", "find@me.com", "pass");
        UUID userId = user.getUserId();
        User foundUser = userService.getUserById(userId);
        assertAll(
                () -> assertNotNull(foundUser),
                () -> assertEquals(userId, foundUser.getUserId()),
                () -> assertEquals("findMe", foundUser.getUserName())
        );
    }

    @Test
    @DisplayName("모든 사용자 조회")
    void getAllUsers_shouldReturnAllCreatedUsers() {
        userService.createUser("user1", "u1@e.com", "p1");
        userService.createUser("user2", "u2@e.com", "p2");
        List<User> allUsers = userService.getAllUsers();
        assertAll(
                () -> assertNotNull(allUsers),
                () -> assertEquals(2, allUsers.size())
        );
    }

    @Test
    @DisplayName("사용자 이름 업데이트")
    void updateUserName_shouldChangeUserName() {
        User user = userService.createUser("oldName", "old@e.com", "p");
        UUID userId = user.getUserId();
        userService.updateUserName(userId, "newName");
        User updatedUser = userService.getUserById(userId);
        assertEquals("newName", updatedUser.getUserName());
    }

    @Test
    @DisplayName("사용자 삭제")
    void deleteUser_shouldRemoveUser() {
        User userToDelete = userService.createUser("deleteMe", "delete@me.com", "pass");
        UUID userId = userToDelete.getUserId();
        userService.createUser("keepMe", "keep@me.com", "pass");
        userService.deleteUser(userId);
        User foundUser = userService.getUserById(userId);
        List<User> remainingUsers = userService.getAllUsers();
        assertAll(
                () -> assertNull(foundUser),
                () -> assertEquals(1, remainingUsers.size()),
                () -> assertEquals("keepMe", remainingUsers.get(0).getUserName())
        );
    }
}
