package com.sprint.mission.discodeit.repository.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.util.MemoryUtil;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FileStorageImplTest {

  private FileStorage fileStorage;
  private File storageDir;
  private File invalidDir;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    storageDir = tempDir.resolve("test-storage").toFile();
    invalidDir = tempDir.resolve("invalid-dir").toFile();
    fileStorage = new FileStorageImpl(storageDir.getPath());
  }

  private Message createDefaultTestMessage() {
    return MessageFixture.createValidMessage();
  }

  private Message createCustomTestMessage(String content) {
    User user = UserFixture.createValidUser();
    Channel channel = ChannelFixture.createPublic();
    return MessageFixture.createCustomMessage(content, user.getId(), channel);
  }

  public static Stream<Arguments> provideMessages() {
    return Stream.of(
        Arguments.of(List.of("감자", "왕감자", "대홍단감자")),
        Arguments.of(List.of("첫 번째 메시지", "두 번째 메시지", "특별한 메시지"))
    );
  }

  @ParameterizedTest
  @MethodSource("provideMessages")
  void 여러_객체_저장_후_저장_상태_확인(List<String> messageContents) {
    List<Message> objects = messageContents.stream()
        .map(content -> MessageFixture.createCustomMessage(content,
            UserFixture.createValidUser().getId(), ChannelFixture.createPublic()))
        .toList();

    objects.forEach(msg -> fileStorage.saveObject(msg.getId(), msg));

    List<Object> storedObjects = fileStorage.readAll();

    assertAll(
        () -> assertThat(storedObjects).hasSize(objects.size()),
        () -> assertThat(storedObjects).containsAll(objects),
        () -> {
          for (Message obj : objects) {
            assertThat(fileStorage.readObject(obj.getId())).isEqualTo(obj);
          }
        }
    );
  }

  @Nested
  class Create {

    @Test
    void 객체가_정상적으로_저장되어야_한다() {
      Message obj = createDefaultTestMessage();
      UUID id = obj.getId();
      fileStorage.saveObject(id, obj);

      List<Object> storedObjects = fileStorage.readAll();

      assertAll(
          () -> assertThat(storedObjects).hasSize(1),
          () -> assertThat(storedObjects.get(0)).isEqualTo(obj),
          () -> assertThat(fileStorage.readObject(id)).isEqualTo(obj)
      );
    }

    @Test
    void 동시_접근이_가능해야_한다() throws Exception {
      int threadCount = 10;
      List<UUID> ids = new CopyOnWriteArrayList<>();
      List<User> expectedUsers = IntStream.range(0, threadCount)
          .mapToObj(i -> UserFixture.createValidUser())
          .toList();

      ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
      IntStream.range(0, threadCount).forEach(i -> {
        executorService.submit(() -> {
          User user = expectedUsers.get(i);
          UUID id = user.getId();
          fileStorage.saveObject(id, user);
          ids.add(id);
        });
      });

      executorService.shutdown();
      while (!executorService.isTerminated()) {
        // 대기
      }

      List<Object> storedUsers = fileStorage.readAll();
      assertAll(
          () -> assertThat(ids).hasSize(threadCount),
          () -> assertThat(storedUsers).hasSize(threadCount),
          () -> assertThat(storedUsers).containsExactlyInAnyOrderElementsOf(expectedUsers)
      );
    }

    @Test
    void 파일을_찾을_수_없는_경우_예외가_발생해야_한다() {
      File invalidDirectory = invalidDir;
      FileStorage invalidStorage = new FileStorageImpl(invalidDirectory.getPath());

      UUID nonExistentId = UUID.randomUUID();
      try {
        invalidStorage.readObject(nonExistentId);
      } catch (RuntimeException e) {
        assertThat(e).isInstanceOf(RuntimeException.class);
      }
    }
  }

  @Nested
  class Read {

    @Test
    void readObject_메서드는_저장된_객체를_올바르게_반환해야_한다() {
      Message obj1 = createDefaultTestMessage();
      Message obj2 = createCustomTestMessage("Custom Message");
      UUID id1 = obj1.getId();
      UUID id2 = obj2.getId();

      fileStorage.saveObject(id1, obj1);
      fileStorage.saveObject(id2, obj2);

      Object retrievedObj = fileStorage.readObject(id2);

      assertThat(retrievedObj).isEqualTo(obj2);
    }

  }

  @Nested
  class Update {

    @Test
    void 업데이트_하면_파일의_객체가_업데이트_되어야_한다() {
      Message originalMessage = createDefaultTestMessage();
      Message updatedMessage = createCustomTestMessage("Updated Message");

      UUID id = originalMessage.getId();
      fileStorage.saveObject(id, originalMessage);
      fileStorage.updateObject(id, updatedMessage);
      List<Object> storedObjects = fileStorage.readAll();

      assertAll(
          () -> assertThat(storedObjects).hasSize(1),
          () -> assertThat(storedObjects.get(0)).isEqualTo(updatedMessage),
          () -> assertThat(fileStorage.readObject(id)).isEqualTo(updatedMessage)
      );
    }

    @Test
    void 업데이트_시_다른_객체는_보전되어야_한다() {
      MemoryUtil.logMemoryUsage("Before Operation");

      Message firstMessage = createDefaultTestMessage();
      Message secondMessage = createCustomTestMessage("Second Message");
      Message updatedMessage = createCustomTestMessage("Updated First Message");

      UUID id1 = firstMessage.getId();
      UUID id2 = secondMessage.getId();

      fileStorage.saveObject(id1, firstMessage);
      fileStorage.saveObject(id2, secondMessage);

      fileStorage.updateObject(id1, updatedMessage);
      List<Object> storedObjects = fileStorage.readAll();

      assertAll(
          () -> assertThat(storedObjects).hasSize(2),
          () -> assertThat(storedObjects).contains(updatedMessage, secondMessage),
          () -> assertThat(fileStorage.readObject(id1)).isEqualTo(updatedMessage),
          () -> assertThat(fileStorage.readObject(id2)).isEqualTo(secondMessage)
      );
    }
  }
}
