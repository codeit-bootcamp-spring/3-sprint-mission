package com.sprint.mission.discodeit.repository.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.common.exception.FileException;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.FileFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.testutil.MemoryUtil;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FileStorageImplTest {

  private FileStorage fileStorage;
  private File storageFile;
  private File invalidFile;

  @BeforeEach
  void setUp() throws IOException {
    storageFile = FileFixture.createTestFile("test-storage.ser");
    invalidFile = FileFixture.createTestFile("invalid_file.ser");
    if (storageFile.exists()) {
      storageFile.delete();
    }
    if (invalidFile.exists()) {
      invalidFile.delete();
    }

    fileStorage = new FileStorageImpl(storageFile.getPath());
  }

  @AfterEach
  void tearDown() {
    if (storageFile.exists()) {
      storageFile.delete();
    }
    if (invalidFile.exists()) {
      invalidFile.delete();
    }

  }

  private Message createDefaultTestMessage() {
    return MessageFixture.createDefaultMessage();
  }

  private Message createCustomTestMessage(String content) {
    Channel channel = ChannelFixture.createDefaultChannel();
    User creator = channel.getCreator();
    return MessageFixture.createCustomMessage(content, creator, channel);
  }

  private static Stream<Arguments> provideMessages() {
    return Stream.of(
        Arguments.of(List.of("감자", "왕감자", "대홍단감자")),
        Arguments.of(List.of("첫 번째 메시지", "두 번째 메시지", "특별한 메시지"))
    );
  }

  @Test
  void shouldHandleConcurrentAccess() throws Exception {
    // given
    int threadCount = 10;
    List positions = new CopyOnWriteArrayList<>();
    List expectedUsers = IntStream.range(0, threadCount)
        .mapToObj(i -> UserFixture.createDefaultUser()).toList(); // 예상 저장 유저 객체 리스트

    // when
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    IntStream.range(0, threadCount).forEach(i -> {
      executorService.submit(() -> {
        User user = (User) expectedUsers.get(i); // 각 스레드가 고유한 기본 유저를 가져옴
        long position = fileStorage.saveObject(user);
        positions.add(position);
      });
    });

    executorService.shutdown();
    while (!executorService.isTerminated()) {
      // 스레드가 종료될 때까지 대기
    }

    // then
    List<Object> storedUsers = fileStorage.readAll();
    assertAll(
        () -> assertThat(positions).hasSize(threadCount),
        () -> assertThat(storedUsers).hasSize(threadCount),
        () -> assertThat(storedUsers).containsExactlyInAnyOrderElementsOf(expectedUsers)
    );

  }

  @Test
  void saveObjectShouldSaveToFileAndReturnPosition() {
    // given
    Serializable obj = createDefaultTestMessage();
    long position = fileStorage.saveObject(obj);

    // when
    List<Object> storedObjects = fileStorage.readAll();

    // then
    assertAll(
        () -> assertThat(position).isEqualTo(0L),
        () -> assertThat(storedObjects).hasSize(1),
        () -> assertThat(storedObjects.get(0)).isEqualTo(obj)
    );
  }

  @Test
  void readObjectShouldReturnSavedObject() {
    // given
    Serializable obj1 = createDefaultTestMessage();
    Serializable obj2 = createCustomTestMessage("Custom Message");
    long position1 = fileStorage.saveObject(obj1);
    long position2 = fileStorage.saveObject(obj2);

    // when
    Object retrievedObj = fileStorage.readObject(position2);

    // then
    assertThat(retrievedObj).isEqualTo(obj2);
  }

  @Test
  void updateSingleObjectShouldReplaceSamePosition() {
    // given
    Serializable originalMessage = createDefaultTestMessage();
    Serializable updatedMessage = createCustomTestMessage("Updated Message");

    // when
    long initialPosition = fileStorage.saveObject(originalMessage);
    fileStorage.updateObject(initialPosition, updatedMessage);
    List<Object> storedObjects = fileStorage.readAll();

    // then
    assertAll(
        () -> assertThat(storedObjects).hasSize(1),
        () -> assertThat(storedObjects.get(0)).isEqualTo(updatedMessage),
        () -> assertThat(fileStorage.readObject(initialPosition)).isEqualTo(updatedMessage)
    );
  }

  @Test
  void updateObjectInMultipleShouldPreserveOthers() {
    MemoryUtil.logMemoryUsage("Before Operation");

    // given
    Serializable firstMessage = createDefaultTestMessage();
    Serializable secondMessage = createCustomTestMessage("Second Message");
    Serializable updatedMessage = createCustomTestMessage("Updated First Message");

    // when
    long firstPosition = fileStorage.saveObject(firstMessage);
    long secondPosition = fileStorage.saveObject(secondMessage);
    fileStorage.updateObject(firstPosition, updatedMessage);
    List<Object> storedObjects = fileStorage.readAll();

    // then
    assertAll(
        () -> assertThat(storedObjects)
            .as("저장된 전체 객체 수는 2개여야 한다.")
            .hasSize(2),
        () -> assertThat(storedObjects.get(0))
            .as("삭제하며 앞당기므로 기존 두 번째의 객체가 조회돼야 한다.")
            .isEqualTo(secondMessage),
        () -> assertThat(storedObjects.get(1))
            .as("수정된 객체는 파일의 끝에 삽입된다.")
            .isEqualTo(updatedMessage)
    );
  }


  @Test
  void optimizeShouldReorganizeFileContents() {
    // given
    Serializable obj1 = createDefaultTestMessage();
    Serializable obj2 = createCustomTestMessage("Another Content");
    fileStorage.saveObject(obj1);
    fileStorage.saveObject(obj2);

    // when
    fileStorage.optimize();
    List<Object> storedObjects = fileStorage.readAll();

    // then
    assertAll(
        () -> assertThat(storedObjects).hasSize(2),
        () -> assertThat(storedObjects.get(0)).isEqualTo(obj1),
        () -> assertThat(storedObjects.get(1)).isEqualTo(obj2)
    );
  }

  @Test
  void shouldThrowExceptionIfFileNotFound() throws IOException {
    // when & then
    try {
      FileStorage invalidStorage = new FileStorageImpl(invalidFile.getPath());
      invalidStorage.readAll();
    } catch (FileException e) {
      assertThat(e.getErrorCode()).isEqualTo(FileException.FILE_READ_ERROR);
    }
  }

  @ParameterizedTest
  @MethodSource("provideMessages")
  @DisplayName("여러 객체 저장 후 모든 반환 위치와 저장 상태 확인")
  void saveMultipleObjectsAndVerify(List<String> messageContents) {
    // given
    List<Message> objects = messageContents.stream()
        .map(content -> MessageFixture.createCustomMessage(content,
            UserFixture.createDefaultUser(), ChannelFixture.createDefaultChannel()))
        .toList();

    // when
    List<Long> positions = objects.stream()
        .map(fileStorage::saveObject)
        .toList();

    List<Object> storedObjects = fileStorage.readAll();

    // then
    assertAll(
        () -> assertThat(positions).hasSize(objects.size()),
        () -> assertThat(storedObjects).containsAll(objects),
        () -> {
          for (int i = 0; i < positions.size(); i++) {
            assertThat(fileStorage.readObject(positions.get(i))).isEqualTo(objects.get(i));
          }
        }
    );
  }
}