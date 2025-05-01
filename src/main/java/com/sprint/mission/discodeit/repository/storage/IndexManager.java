package com.sprint.mission.discodeit.repository.storage;

import com.sprint.mission.discodeit.common.exception.FileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IndexManager {

  private final File indexFile;
  private Map<UUID, Long> indexMap = new HashMap<>();
  private Map<String, List<Long>> stringIndexMap = new HashMap<>();

  private static class IndexData implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 7983967426800483148L;

    Map<UUID, Long> uuidIndexMap;
    Map<String, List<Long>> stringIndexMap;

    public IndexData(Map<UUID, Long> uuidIndexMap, Map<String, List<Long>> stringIndexMap) {
      this.uuidIndexMap = uuidIndexMap;
      this.stringIndexMap = stringIndexMap;
    }
  }

  public IndexManager(String indexPath) throws FileException {
    this.indexFile = new File(indexPath);

    try {
      if (!indexFile.getParentFile().exists() && !indexFile.getParentFile().mkdirs()) {
        throw FileException.readError(indexFile.getParentFile(),
            new IOException("상위 디렉토리를 생성할 수 없습니다."));
      }
      if (!indexFile.exists()) {
        createNewIndexFile();
      }
      if (indexFile.length() > 0) {
        loadIndex();
      }
    } catch (IOException | ClassNotFoundException e) {
      throw FileException.readError(indexFile, e);
    }
  }

  private void loadIndex() throws IOException, ClassNotFoundException {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(indexFile))) {
      Object readObj = ois.readObject();
      if (readObj instanceof IndexData data) {
        this.indexMap = data.uuidIndexMap != null ? data.uuidIndexMap : new HashMap<>();
        this.stringIndexMap = data.stringIndexMap != null ? data.stringIndexMap : new HashMap<>();
      } else if (readObj instanceof Map) {
        // 역호환: 기존 구조
        this.indexMap = (Map<UUID, Long>) readObj;
        this.stringIndexMap = new HashMap<>();
      }
    }
  }

  private void createNewIndexFile() throws IOException {
    if (!indexFile.createNewFile()) {
      throw new IOException("빈 인덱스 파일을 생성할 수 없습니다: " + indexFile.getAbsolutePath());
    }
  }

  public void saveIndex() throws FileException {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(indexFile))) {
      IndexData data = new IndexData(indexMap, stringIndexMap);
      oos.writeObject(data);
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
