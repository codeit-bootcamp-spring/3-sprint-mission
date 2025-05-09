package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class JCFUserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new JCFUserService();
    }

    @Test
    void createUser() {

        String userName = "이주용";
        String userId = "sjo06102";
        String password = "!qwe0123";
        String email = "sjo06102@gmail.com";

        User user = userService.create(userName, userId, password, email);

        assertAll(
                () -> assertNotNull(user, "생성된 사용자 객체가 Null입니다."),
                () -> assertEquals(userName, user.getUserName(), "사용자 이름이 다릅니다"),
                () -> assertEquals(userId, user.getUserId(), "사용자의 아이디가 다릅니다."),
                () -> assertEquals(password, user.getUserPassword(), "사용자의 비밀번호가 다릅니다"),
                () -> assertEquals(email, user.getUserEmail(), "사용자의 이멜이이 다릅니다.")
        );

    }

    @Test
    void findUserByUUID() {

        User user = userService.create("이주용", "sjo06102", "!qwe0123", "sjo06102@gmail.com");
        assertAll(
                () -> assertNotNull(userService.readById(user.getId())),
                () -> assertEquals(user, userService.readById(user.getId()))
        );

    }

    @Test
    void returnAllUsers() {

        User user = userService.create("이주용", "sjo06102", "!qwe0123", "sjo06102@gmail.com");
        assertAll(
                () -> assertNotNull(userService.readAll(), "사용자가 존재하지 않습니다."),
                () -> assertEquals(1, userService.readAll().size(), "등록된 사용자의 수가 맞지 않습니다.")
        );

    }

    @Test
    void updateUser() {

        User user = userService.create("이주용", "sjo06102", "!qwe0123", "sjo06102@gmail.com");
        userService.update(user, "이주용", "sjo06102", "!qwe0123", "sjo06102@naver.com");
        assertEquals("sjo06102@naver.com", user.getUserEmail(), "수정된 정보가 다릅니다.");

    }

    @Test
    void deleteUser() {

        User user = userService.create("이주용", "sjo06102", "!qwe0123", "sjo06102@gmail.com");
        userService.deleteById(user);
        assertNull(userService.readById(user.getId()),"삭제되지 않았습니다.");

    }


}
