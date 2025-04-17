package com.sprint.mission.discodeit.service.jcf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.Test;

class JCFUserServiceTest {

  @Test
  void createUser() {
  }

  @Test
  void getUserById() {
  }

  @Test
  void searchUsersByName() {
  }

  @Test
  void getUserByEmail() {
  }

  @Test
  void getAllUsers() {
  }

  @Test
  void updateUser() {
    // given
    User user = User.create("user1@example.com", "홍길동", "password1");

    // when
    user.updateName("홍길동쓰");
    String newName = user.getName();

    // then
    assertEquals(newName, user.getName());
  }

  @Test
  void deleteUser() {
  }
}