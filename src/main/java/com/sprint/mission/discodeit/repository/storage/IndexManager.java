package com.sprint.mission.discodeit.repository.storage;

import com.sprint.mission.discodeit.common.exception.FileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IndexManager {

  private final File indexFile;
  private Map<UUID, Long> indexMap = new HashMap<>();

  public IndexManager(String indexPath) throws FileException {
    this.indexFile = new File(indexPath);

    try {
      // 파일 없을 경우 생성
      if (!indexFile.exists()) {
        createNewIndexFile();
      }

      // 파일이 존재하며 내용이 있을 경우 로드
      if (indexFile.length() > 0) {
        loadIndex();
      }

    } catch (IOException | ClassNotFoundException e) {
      throw FileException.readError(indexFile, e);
    }
  }

  private void loadIndex() throws IOException, ClassNotFoundException {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(indexFile))) {
      indexMap = (Map<UUID, Long>) ois.readObject();
    }
  }

  private void createNewIndexFile() throws IOException {
    if (!indexFile.getParentFile().exists() && !indexFile.getParentFile().mkdirs()) {
      throw new IOException("상위 디렉토리를 생성할 수 없습니다: " + indexFile.getParentFile().getAbsolutePath());
    }
    if (!indexFile.createNewFile()) {
      throw new IOException("빈 인덱스 파일을 생성할 수 없습니다: " + indexFile.getAbsolutePath());
    }
  }

  public void saveIndex() throws FileException {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(indexFile))) {
      oos.writeObject(indexMap);
    } catch (IOException e) {
      throw FileException.writeError(indexFile, e);
    }
  }

  public void addEntry(UUID id, long position) throws FileException {
    if (position < 0) {
      throw FileException.invalidPosition(indexFile, position);
    }
    indexMap.put(id, position);
  }

  public void removeEntry(UUID id) {
    indexMap.remove(id);
  }

  public Long getPosition(UUID id) {
    return indexMap.get(id);
  }

  public Map<UUID, Long> getAllIndexEntries() {
    return new HashMap<>(indexMap);
  }
}