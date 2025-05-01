package com.sprint.mission.discodeit.repository.storage;

import com.sprint.mission.discodeit.common.exception.FileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IndexManager {

  private final File indexFile;
  private Map<UUID, Long> indexMap = new HashMap<>();
  private final Map<String, List<Long>> stringIndexMap = new HashMap<>();

  public IndexManager(String indexPath) throws FileException {
    this.indexFile = new File(indexPath);

    try {
      // 폴더가 없으면 생성
      if (!indexFile.getParentFile().exists() && !indexFile.getParentFile().mkdirs()) {
        throw FileException.readError(indexFile.getParentFile(),
            new IOException("상위 디렉토리를 생성할 수 없습니다."));
      }

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

  public void addEntry(String key, long position) {
    stringIndexMap.computeIfAbsent(key, k -> new ArrayList<>()).add(position);
  }

  public void removeEntry(UUID id) {
    indexMap.remove(id);
  }

  public Long getPosition(UUID id) {
    return indexMap.get(id);
  }

  public List<Long> getPositions(String key) {
    return stringIndexMap.getOrDefault(key, new ArrayList<>());
  }

  public Map<UUID, Long> getAllIndexEntries() {
    return new HashMap<>(indexMap);
  }

  public void removeEntry(String key, long position) {
    List<Long> positions = stringIndexMap.get(key);
    if (positions != null) {
      positions.remove(position);
      if (positions.isEmpty()) {
        stringIndexMap.remove(key);
      }
    }
  }
}