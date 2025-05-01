package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.exception.FileException;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorage;
import com.sprint.mission.discodeit.repository.storage.FileStorageImpl;
import com.sprint.mission.discodeit.repository.storage.IndexManager;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

  private static final String DEFAULT_FILE_PATH = "data/binary/binary-content.ser";
  private static final String DEFAULT_ID_INDEX_PATH = "data/binary/binary-content.ser.idx";
  private static final String DEFAULT_USER_ID_INDEX_PATH = "data/binary/binary-content.ser.uid.idx";
  private static final String DEFAULT_MESSAGE_ID_INDEX_PATH = "data/binary/binary-content.ser.mid.idx";

  private final FileStorage fileStorage;
  private final IndexManager idIndexManager;
  private final IndexManager userIdIndexManager;
  private final IndexManager messageIdIndexManager;

  private FileBinaryContentRepository() {
    try {
      this.fileStorage = new FileStorageImpl(DEFAULT_FILE_PATH);
      this.idIndexManager = new IndexManager(DEFAULT_ID_INDEX_PATH);
      this.userIdIndexManager = new IndexManager(DEFAULT_USER_ID_INDEX_PATH);
      this.messageIdIndexManager = new IndexManager(DEFAULT_MESSAGE_ID_INDEX_PATH);
    } catch (Exception e) {
      throw new RuntimeException("FileBinaryContentRepository 초기화 실패: " + e.getMessage(), e);
    }
  }

  private FileBinaryContentRepository(String filePath) {
    this.fileStorage = new FileStorageImpl(filePath);
    this.idIndexManager = new IndexManager(filePath + ".idx");
    this.userIdIndexManager = new IndexManager(filePath + ".uid.idx");
    this.messageIdIndexManager = new IndexManager(filePath + ".mid.idx");
  }

  public static FileBinaryContentRepository from(String filePath) {
    return new FileBinaryContentRepository(filePath);
  }

  public static FileBinaryContentRepository createDefault() {
    return new FileBinaryContentRepository();
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    Long position = idIndexManager.getPosition(id);
    if (position == null) {
      return Optional.empty();
    }
    return Optional.ofNullable((BinaryContent) fileStorage.readObject(position));
  }

  @Override
  public Optional<BinaryContent> findByUserId(UUID userId) {
    Long position = userIdIndexManager.getPosition(userId);
    if (position == null) {
      return Optional.empty();
    }
    return Optional.ofNullable((BinaryContent) fileStorage.readObject(position));
  }

  @Override
  public List<BinaryContent> findAllByMessageId(UUID messageId) {
    List<Long> positions = messageIdIndexManager.getPositions(String.valueOf(messageId));
    if (positions == null || positions.isEmpty()) {
      return List.of();
    }
    return positions.stream()
        .map(pos -> (BinaryContent) fileStorage.readObject(pos))
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    Long existingPosition = idIndexManager.getPosition(binaryContent.getId());
    long newPosition;

    try {
      if (existingPosition != null) {
        // 기존 바이너리 컨텐트 업데이트
        fileStorage.updateObject(existingPosition, binaryContent);
        // userId 기반 인덱스 업데이트
        if (binaryContent.getUserId() != null) {
          userIdIndexManager.addEntry(binaryContent.getUserId(), existingPosition);
        }
        // messageId 기반 인덱스 업데이트
        if (binaryContent.getMessageId() != null) {
          messageIdIndexManager.addEntry(binaryContent.getMessageId().toString(), existingPosition);
        }
      } else {
        // 새로운 바이너리 컨텐트 저장
        newPosition = fileStorage.saveObject(binaryContent);
        idIndexManager.addEntry(binaryContent.getId(), newPosition);
        // userId 기반 인덱스 추가
        if (binaryContent.getUserId() != null) {
          userIdIndexManager.addEntry(binaryContent.getUserId(), newPosition);
        }
        // messageId 기반 인덱스 추가
        if (binaryContent.getMessageId() != null) {
          messageIdIndexManager.addEntry(binaryContent.getMessageId(), newPosition);
        }
      }
      idIndexManager.saveIndex();
      userIdIndexManager.saveIndex();
      messageIdIndexManager.saveIndex();
    } catch (FileException e) {
      throw new RuntimeException("바이너리 컨텐트 저장 실패: " + e.getMessage(), e);
    }
    return binaryContent;
  }

  @Override
  public void deleteById(UUID id) {
    Long positionToDelete = idIndexManager.getPosition(id);
    if (positionToDelete != null) {
      try {
        fileStorage.deleteObject(positionToDelete);
        idIndexManager.removeEntry(id);

        // userId 기반 인덱스에서 제거
        userIdIndexManager.getAllIndexEntries().forEach((userId, position) -> {
          if (position.equals(positionToDelete)) {
            userIdIndexManager.removeEntry(userId);
          }
        });

        // messageId 기반 인덱스에서 제거
        messageIdIndexManager.getAllIndexEntries().forEach((messageId, position) -> {
          if (position.equals(positionToDelete)) {
            messageIdIndexManager.removeEntry(messageId);
          }
        });

        idIndexManager.saveIndex();
        userIdIndexManager.saveIndex();
        messageIdIndexManager.saveIndex();

      } catch (FileException e) {
        throw new RuntimeException("바이너리 컨텐트 삭제 실패: " + e.getMessage(), e);
      }
    }
  }

  @Override
  public void deleteAllByMessageId(UUID messageId) {
    List<Long> positions = messageIdIndexManager.getPositions(messageId.toString());
    if (positions != null && !positions.isEmpty()) {
      try {
        for (Long position : positions) {
          BinaryContent content = (BinaryContent) fileStorage.readObject(position);
          if (content != null) {
            // 실제 삭제
            fileStorage.deleteObject(position);
            idIndexManager.removeEntry(content.getId());

            // userId 인덱스도 정리
            if (content.getUserId() != null) {
              userIdIndexManager.removeEntry(content.getUserId());
            }
          }
        }
        // messageId 인덱스 자체는 통째로 날림
        messageIdIndexManager.removeEntry(messageId);

        idIndexManager.saveIndex();
        userIdIndexManager.saveIndex();
        messageIdIndexManager.saveIndex();

      } catch (FileException e) {
        throw new RuntimeException("메시지 ID로 첨부파일 삭제 실패: " + e.getMessage(), e);
      }
    }
  }

}