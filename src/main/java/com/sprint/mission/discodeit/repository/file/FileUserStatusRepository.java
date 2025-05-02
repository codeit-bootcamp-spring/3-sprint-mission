package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.exception.FileException;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorage;
import com.sprint.mission.discodeit.repository.storage.FileStorageImpl;
import com.sprint.mission.discodeit.repository.storage.IndexManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserStatusRepository implements UserStatusRepository {

  private static final String DEFAULT_FILE_PATH = "data/user/user-status.ser";
  private static final String DEFAULT_INDEX_PATH = "data/user/user-status.ser.idx";
  private static final String DEFAULT_USER_ID_INDEX_PATH = "data/user/user-status.ser.uid.idx";

  private final FileStorage fileStorage;
  private final IndexManager indexManager; // id 기반 인덱스
  private final IndexManager userIdIndexManager; // userId 기반 인덱스

  private FileUserStatusRepository() {
    try {
      this.fileStorage = new FileStorageImpl(DEFAULT_FILE_PATH);
      this.indexManager = new IndexManager(DEFAULT_INDEX_PATH);
      this.userIdIndexManager = new IndexManager(DEFAULT_USER_ID_INDEX_PATH);
    } catch (Exception e) {
      throw new RuntimeException("FileUserRepository 초기화 실패: " + e.getMessage(), e);
    }
  }

  private FileUserStatusRepository(String filePath) {
    this.fileStorage = new FileStorageImpl(filePath);
    this.indexManager = new IndexManager(filePath + ".idx");
    this.userIdIndexManager = new IndexManager(filePath + ".uid.idx");
  }

  public static FileUserStatusRepository from(String filePath) {
    return new FileUserStatusRepository(filePath);
  }

  public static FileUserStatusRepository createDefault() {
    return new FileUserStatusRepository();
  }

  @Override
  public Optional<UserStatus> findById(UUID id) {
    Long position = indexManager.getPosition(id);
    if (position == null) {
      return Optional.empty();
    }
    return Optional.ofNullable((UserStatus) fileStorage.readObject(position));
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    Long position = userIdIndexManager.getPosition(userId);
    if (position == null) {
      return Optional.empty();
    }
    return Optional.ofNullable((UserStatus) fileStorage.readObject(position));
  }

  @Override
  public List<UserStatus> findAll() {
    List<UserStatus> userStatuses = new ArrayList<>();
    indexManager.getAllIndexEntries().forEach((id, position) -> {
      UserStatus userStatus = (UserStatus) fileStorage.readObject(position);
      if (userStatus != null) {
        userStatuses.add(userStatus);
      }
    });
    return userStatuses;
  }

  @Override
  public UserStatus save(UserStatus userStatus) {
    Long existingPosition = indexManager.getPosition(userStatus.getId());
    long newPosition;

    try {
      if (existingPosition != null) {
        // 기존 사용자 정보 업데이트
        fileStorage.updateObject(existingPosition, userStatus);
        // userId 기반 인덱스 업데이트 (userId는 변경되지 않는다고 가정)
        userIdIndexManager.addEntry(userStatus.getUserId(), existingPosition);
      } else {
        // 새로운 사용자 저장
        newPosition = fileStorage.saveObject(userStatus);
        indexManager.addEntry(userStatus.getId(), newPosition);
        userIdIndexManager.addEntry(userStatus.getUserId(), newPosition);
      }
      indexManager.saveIndex();
      userIdIndexManager.saveIndex();

    } catch (FileException e) {
      // 저장 실패 시 롤백 또는 예외 처리 필요
      throw new RuntimeException("저장 실패: " + e.getMessage(), e);
    }
    return userStatus;
  }

  @Override
  public void delete(UUID id) {
    Long positionToDelete = indexManager.getPosition(id);
    if (positionToDelete != null) {
      try {
        fileStorage.deleteObject(positionToDelete);
        indexManager.removeEntry(id);

        // userId 기반 인덱스에서도 제거 (id 기반 인덱스에서 userId를 가져오는 방법 필요)
        // TODO: 전체 스캔 후 position 비교 방식이라 비효율적 개선 필요
        userIdIndexManager.getAllIndexEntries().forEach((userId, position) -> {
          if (position.equals(positionToDelete)) {
            userIdIndexManager.removeEntry(userId);
          }
        });

        indexManager.saveIndex();
        userIdIndexManager.saveIndex();

      } catch (FileException e) {
        throw new RuntimeException("삭제 실패: " + e.getMessage(), e);
      }
    }
  }
}