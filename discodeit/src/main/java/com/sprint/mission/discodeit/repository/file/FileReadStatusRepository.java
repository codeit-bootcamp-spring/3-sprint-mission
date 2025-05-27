package com.sprint.mission.discodeit.repository.file;


import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.util.FileioUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(value = "repository.type", havingValue = "file")
@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

  private Map<String, ReadStatus> readStatusData;
  private Path path;

  public FileReadStatusRepository(@Value("repository.read-status-file-path") Path path) {
    this.path = path;
    if (!Files.exists(this.path)) {
      try {
        Files.createFile(this.path);
        FileioUtil.save(this.path, new HashMap<>());
      } catch (Exception e) {
        throw new RuntimeException("메시지 읽은 시간을 저장한 파일 초기화 불가능", e);
      }
    }
    FileioUtil.init(this.path);
    this.readStatusData = FileioUtil.load(this.path, ReadStatus.class);

  }


  @Override
  public Optional<ReadStatus> findById(UUID id) {
    if (!readStatusData.containsKey(id.toString())) {
      throw new NoSuchElementException("ReadStatus not found with id " + id);
    }
    return Optional.ofNullable(readStatusData.get(id.toString()));
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return readStatusData.values()
        .stream()
        .filter(readStatus -> readStatus.getUserId().equals(userId)).toList();
  }

  @Override
  public List<ReadStatus> findAllByChannelId(UUID channelId) {
    return readStatusData.values()
        .stream()
        .filter(readStatus -> readStatus.getChannelId().equals(channelId)).toList();
  }


  @Override
  public ReadStatus save(ReadStatus readStatus) {
    readStatusData.put(readStatus.getId().toString(), readStatus);
    FileioUtil.save(this.path, readStatusData);
    return readStatus;
  }

  @Override
  public boolean existsById(UUID id) {
    return readStatusData.containsKey(id.toString());
  }

  @Override
  public void deleteById(UUID id) {
    readStatusData.remove(id.toString());
    FileioUtil.save(this.path, readStatusData);
  }

  @Override
  public void deleteByChannelId(UUID channelId) {
    readStatusData.values().removeIf(readStatus -> readStatus.getChannelId().equals(channelId));
    FileioUtil.save(this.path, readStatusData);
  }

}
