package com.sprint.mission.discodeit.repository.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.common.exception.FileException;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.testutil.MemoryUtil;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FileStorageImplTest {

  private FileStorage fileStorage;
  private File storageFile;
  private File invalidFile;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    storageFile = tempDir.resolve("test-storage.ser").toFile();
    invalidFile = tempDir.resolve("invalid_file.ser").toFile();
    fileStorage = new FileStorageImpl(storageFile.getPath());
  }

  private Message createDefaultTestMessage() {
    return MessageFixture.createValidMessage();
  }

  private Message createCustomTestMessage(String content) {
    Channel channel = ChannelFixture.createValidChannel();
    UUID userId = channel.getCreatorId();
    return MessageFixture.createCustomMessage(content, userId, channel);
  }

  private static Stream<Arguments> provideMessages() {
    return Stream.of(
        Arguments.of(List.of("감자", "왕감자", "대홍단감자")),
        Arguments.of(List.of("첫 번째 메시지", "두 번째 메시지", "특별한 메시지"))
    );
  }

  @Test
  void shouldHandleConcurrentAccess() throws Exception {
    int threadCount = 10;
    List<Long> positions = new CopyOnWriteArrayList<>();
    List<User> expectedUsers = IntStream.range(0, threadCount)
        .mapToObj(i -> UserFixture.createValidUser()).toList();

    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    IntStream.range(0, threadCount).forEach(i -> {
      executorService.submit(() -> {
        User user = expectedUsers.get(i);
        long position = fileStorage.saveObject(user);
        positions.add(position);
      });
    });

    executorService.shutdown();
    while (!executorService.isTerminated()) {
      // 대기
    }

    List<Object> storedUsers = fileStorage.readAll();
    assertAll(
        () -> assertThat(positions).hasSize(threadCount),
        () -> assertThat(storedUsers).hasSize(threadCount),
        () -> assertThat(storedUsers).containsExactlyInAnyOrderElementsOf(expectedUsers)
    );
  }

  @Test
  void saveObjectShouldSaveToFileAndReturnPosition() {
    Serializable obj = createDefaultTestMessage();
    long position = fileStorage.saveObject(obj);

    List<Object> storedObjects = fileStorage.readAll();

    assertAll(
        () -> assertThat(position).isEqualTo(0L),
        () -> assertThat(storedObjects).hasSize(1),
        () -> assertThat(storedObjects.get(0)).isEqualTo(obj)
    );
  }

  @Test
  void readObjectShouldReturnSavedObject() {
    Serializable obj1 = createDefaultTestMessage();
    Serializable obj2 = createCustomTestMessage("Custom Message");
    long position1 = fileStorage.saveObject(obj1);
    long position2 = fileStorage.saveObject(obj2);

    Object retrievedObj = fileStorage.readObject(position2);

    assertThat(retrievedObj).isEqualTo(obj2);
  }

  @Test
  void updateSingleObjectShouldReplaceSamePosition() {
    Serializable originalMessage = createDefaultTestMessage();
    Serializable updatedMessage = createCustomTestMessage("Updated Message");

    long initialPosition = fileStorage.saveObject(originalMessage);
    fileStorage.updateObject(initialPosition, updatedMessage);
    List<Object> storedObjects = fileStorage.readAll();

    assertAll(
        () -> assertThat(storedObjects).hasSize(1),
        () -> assertThat(storedObjects.get(0)).isEqualTo(updatedMessage),
        () -> assertThat(fileStorage.readObject(initialPosition)).isEqualTo(updatedMessage)
    );
  }

  @Test
  void updateObjectInMultipleShouldPreserveOthers() {
    MemoryUtil.logMemoryUsage("Before Operation");

    Serializable firstMessage = createDefaultTestMessage();
    Serializable secondMessage = createCustomTestMessage("Second Message");
    Serializable updatedMessage = createCustomTestMessage("Updated First Message");

    long firstPosition = fileStorage.saveObject(firstMessage);
    long secondPosition = fileStorage.saveObject(secondMessage);
    fileStorage.updateObject(firstPosition, updatedMessage);
    List<Object> storedObjects = fileStorage.readAll();

    assertAll(
        () -> assertThat(storedObjects).hasSize(2),
        () -> assertThat(storedObjects.get(0)).isEqualTo(secondMessage),
        () -> assertThat(storedObjects.get(1)).isEqualTo(updatedMessage)
    );
  }

  @Test
  void optimizeShouldReorganizeFileContents() {
    Serializable obj1 = createDefaultTestMessage();
    Serializable obj2 = createCustomTestMessage("Another Content");
    fileStorage.saveObject(obj1);
    fileStorage.saveObject(obj2);

    fileStorage.optimize();
    List<Object> storedObjects = fileStorage.readAll();

    assertAll(
        () -> assertThat(storedObjects).hasSize(2),
        () -> assertThat(storedObjects.get(0)).isEqualTo(obj1),
        () -> assertThat(storedObjects.get(1)).isEqualTo(obj2)
    );
  }

  @Test
  void shouldThrowExceptionIfFileNotFound() {
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
    List<Message> objects = messageContents.stream()
        .map(content -> MessageFixture.createCustomMessage(content,
            UserFixture.createValidUser().getId(), ChannelFixture.createValidChannel()))
        .toList();

    List<Long> positions = objects.stream()
        .map(fileStorage::saveObject)
        .toList();

    List<Object> storedObjects = fileStorage.readAll();

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
