package com.sprint.mission.discodeit.repository.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.UserFixture;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FileStorageImplTest_mock {

  private FileStorage fileStorage;

  @BeforeEach
  void setUp() {
    fileStorage = Mockito.mock(FileStorage.class);
  }

  private void mockSaveObject(Serializable obj, long position) {
    Mockito.when(fileStorage.saveObject(obj)).thenReturn(position);
  }

  private void mockReadAll(List<Object> objects) {
    Mockito.when(fileStorage.readAll()).thenReturn(objects);
  }

  private void mockUpdateObject(long position, Serializable updatedObj,
      List<Object> updatedObjects) {
    Mockito.doAnswer(invocation -> {
      Mockito.when(fileStorage.readAll()).thenReturn(updatedObjects);
      return null;
    }).when(fileStorage).updateObject(position, updatedObj);
  }

  private void mockDeleteObject(long position, List<Object> afterDelete) {
    Mockito.doAnswer(invocation -> {
      Mockito.when(fileStorage.readAll()).thenReturn(afterDelete);
      return null;
    }).when(fileStorage).deleteObject(position);
  }

  @Test
  void shouldHandleConcurrentAccess() throws Exception {
    // given
    int threadCount = 10;
    List<Long> positions = new CopyOnWriteArrayList<>();
    List<Serializable> savedObjects = Collections.synchronizedList(new CopyOnWriteArrayList<>());

    User defaultUser = UserFixture.createDefaultUser();

    // Mock 동작 설정
    Mockito.when(fileStorage.saveObject(Mockito.any(Serializable.class)))
        .thenAnswer(invocation -> {
          synchronized (savedObjects) {
            Serializable obj = invocation.getArgument(0);
            long position = savedObjects.size();
            savedObjects.add(obj);
            positions.add(position);
            return position;
          }
        });

    // when
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    IntStream.range(0, threadCount).forEach(i -> {
      executorService.submit(() -> {
        // 기본 유저 객체를 저장하도록 수정
        fileStorage.saveObject(defaultUser);
      });
    });

    executorService.shutdown();
    while (!executorService.isTerminated()) {
      // 스레드가 종료될 때까지 대기
    }

    // then
    assertThat(positions).hasSize(threadCount);
    assertThat(savedObjects).hasSize(threadCount);
    for (Serializable obj : savedObjects) {
      // 저장된 객체가 기본 유저 객체인지 확인
      assertThat(obj).isEqualTo(defaultUser);
    }
    assertThat(positions).containsExactlyInAnyOrder(
        IntStream.range(0, threadCount).mapToLong(i -> i).boxed().toArray(Long[]::new));
  }


  @Test
  void saveObjectShouldSaveToFileAndReturnPosition() {
    // given
    Serializable obj = "Test String";
    long mockPosition = 0L;
    List<Object> mockStoredObjects = List.of(obj);

    mockSaveObject(obj, mockPosition);
    mockReadAll(mockStoredObjects);

    // when
    long position = fileStorage.saveObject(obj);

    // then
    List<Object> storedObjects = fileStorage.readAll();
    assertAll("저장된 객체 확인",
        () -> assertThat(position).isEqualTo(0L),
        () -> assertThat(storedObjects).hasSize(1),
        () -> assertThat(storedObjects.get(0)).isEqualTo(obj)
    );
  }

  @Test
  void readObjectShouldReturnSavedObject() {
    // given
    Serializable obj = "Object 1";
    long position = 0L;

    mockSaveObject(obj, position);
    Mockito.when(fileStorage.readObject(position)).thenReturn(obj);

    // when
    fileStorage.saveObject(obj);
    Object retrievedObj = fileStorage.readObject(position);

    // then
    assertThat(retrievedObj).isEqualTo(obj);
  }

  @Test
  void updateObjectShouldUpdateObjectAtSpecificPosition() {
    // given
    Serializable obj = "Original Object";
    Serializable updatedObj = "Updated Object";
    long position = 0L;

    List<Object> initialObjects = List.of(obj);
    List<Object> updatedObjects = List.of(updatedObj);

    mockSaveObject(obj, position);
    mockReadAll(initialObjects);
    mockUpdateObject(position, updatedObj, updatedObjects);

    // when
    fileStorage.saveObject(obj);
    fileStorage.updateObject(position, updatedObj);

    // then
    List<Object> storedObjects = fileStorage.readAll();
    assertAll("업데이트된 객체 확인",
        () -> assertThat(storedObjects).hasSize(1),
        () -> assertThat(storedObjects.get(0)).isEqualTo(updatedObj)
    );
  }

  @Test
  void deleteObjectShouldRemoveObjectAtSpecificPosition() {
    // given
    Serializable obj1 = "Object to keep";
    Serializable obj2 = "Object to delete";
    long position1 = 0L;
    long position2 = 1L;

    List<Object> afterSave = List.of(obj1, obj2);
    List<Object> afterDelete = List.of(obj1);

    mockSaveObject(obj1, position1);
    mockSaveObject(obj2, position2);
    mockReadAll(afterSave);
    mockDeleteObject(position2, afterDelete);

    // when
    fileStorage.saveObject(obj1);
    fileStorage.saveObject(obj2);
    fileStorage.deleteObject(position2);

    // then
    List<Object> storedObjects = fileStorage.readAll();
    assertAll("객체 삭제 확인",
        () -> assertThat(storedObjects).hasSize(1),
        () -> assertThat(storedObjects.get(0)).isEqualTo(obj1)
    );
  }

  @Test
  void optimizeShouldReorganizeFileContents() {
    // given
    Serializable obj1 = "Object 1";
    Serializable obj2 = "Object 2";

    List<Object> initialData = List.of(obj1, obj2);
    List<Object> optimizedData = List.of(obj1, obj2);

    mockSaveObject(obj1, 0L);
    mockSaveObject(obj2, 1L);
    mockReadAll(initialData);

    Mockito.doAnswer(invocation -> {
      Mockito.when(fileStorage.readAll()).thenReturn(optimizedData);
      return null;
    }).when(fileStorage).optimize();

    // when
    fileStorage.saveObject(obj1);
    fileStorage.saveObject(obj2);
    fileStorage.optimize();

    // then
    List<Object> storedObjects = fileStorage.readAll();
    assertAll("최적화된 파일 내용 확인",
        () -> assertThat(storedObjects).hasSize(2),
        () -> assertThat(storedObjects.get(0)).isEqualTo(obj1),
        () -> assertThat(storedObjects.get(1)).isEqualTo(obj2)
    );
  }
}