package com.sprint.mission.discodeit.service.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.entity.User;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileUserServiceTest {

  private FileUserService fileUserService;
  private final String FILE_PATH = "data/test/users.ser";

  @BeforeEach
  void setUp() {
    // 정적 팩토리 메서드로 테스트용 파일 경로를 가진 FileUserService 인스턴스 생성
    fileUserService = FileUserService.from(FILE_PATH);
  }

  @AfterEach
  void tearDown() {
    // 테스트 후 파일 삭제
    File file = new File(FILE_PATH);
    if (file.exists()) {
      file.delete();
    }
  }

  @Test
  void createUser() {
    // given
    User user = User.create("test@example.com", "테스트 사용자", "password");

    // when
    User createdUser = fileUserService.createUser(user.getEmail(), user.getName(),
        user.getPassword());

    // then
    assertNotNull(createdUser);
    assertEquals(user.getEmail(), createdUser.getEmail());
    assertEquals(user.getName(), createdUser.getName());
    assertEquals(user.getPassword(), createdUser.getPassword());

    // 파일에 저장되었는지 확인 (객체 내용 비교)
    List<User> users = fileUserService.getAllUsers();

    users.forEach(System.out::println);

    boolean userFound = users.stream()
        .anyMatch(u -> u.getEmail().equals(user.getEmail()) &&
            u.getName().equals(user.getName()) &&
            u.getPassword().equals(user.getPassword()));
    assertTrue(userFound);
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
  }

  @Test
  void deleteUser() {
  }
}