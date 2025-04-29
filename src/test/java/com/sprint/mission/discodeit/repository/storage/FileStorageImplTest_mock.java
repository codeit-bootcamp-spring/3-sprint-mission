package com.sprint.mission.discodeit.repository.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.UserFixture;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileStorageImplTest_mock {

  @Mock
  private FileStorage fileStorage;

  @Test
  void shouldHandleConcurrentAccess() throws Exception {
    // given
    int threadCount = 10;
    List<Long> positions = new CopyOnWriteArrayList<>();
    List<Serializable> savedObjects = Collections.synchronizedList(new CopyOnWriteArrayList<>());

    User defaultUser = UserFixture.createValidUser();

    // Mock 동작 설정
    when(fileStorage.saveObject(any(Serializable.class)))
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

    when(fileStorage.saveObject(obj)).thenReturn(mockPosition);
    when(fileStorage.readAll()).thenReturn(mockStoredObjects);

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

    when(fileStorage.saveObject(obj)).thenReturn(position);
    when(fileStorage.readObject(position)).thenReturn(obj);

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

    when(fileStorage.saveObject(obj)).thenReturn(position);
    when(fileStorage.readAll()).thenReturn(initialObjects);
    doAnswer(invocation -> {
      when(fileStorage.readAll()).thenReturn(updatedObjects);
      return null;
    }).when(fileStorage).updateObject(position, updatedObj);

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

    when(fileStorage.saveObject(obj1)).thenReturn(position1);
    when(fileStorage.saveObject(obj2)).thenReturn(position2);
    when(fileStorage.readAll()).thenReturn(afterSave);
    doAnswer(invocation -> {
      when(fileStorage.readAll()).thenReturn(afterDelete);
      return null;
    }).when(fileStorage).deleteObject(position2);

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

    when(fileStorage.saveObject(obj1)).thenReturn(0L);
    when(fileStorage.saveObject(obj2)).thenReturn(1L);
    when(fileStorage.readAll()).thenReturn(initialData);

    doAnswer(invocation -> {
      when(fileStorage.readAll()).thenReturn(optimizedData);
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