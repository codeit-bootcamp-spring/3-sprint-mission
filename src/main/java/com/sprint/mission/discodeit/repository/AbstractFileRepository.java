package com.sprint.mission.discodeit.repository;

import java.io.*;
import java.util.*;

public abstract class AbstractFileRepository<T extends Serializable, ID> {
  protected final Map<ID, T> dataMap = new HashMap<>();
  private final String filePath;

  protected AbstractFileRepository(String filePath) {
    this.filePath = filePath;

    File directory = new File(new File(filePath).getParent());
    // filePath는 ~~~/.discodeit/user.ser 파일 전체 경로
    // .getParent()로 상위 디렉터리 경로(~~~/.discodeit/)를 가져온다. 이 경로를 바탕으로 File 객체를 생성
    // 초기화 시점은 해당 빈이 DI되는 시점 - FileUserRepository가 빈으로 등록되고 이때 생성자가 호출되고, .discodeit 폴더를 생성
    if (!directory.exists()) {
      boolean created = directory.mkdirs();
      if (!created) { // 권한 or 경로 error
        throw new IllegalStateException("디렉터리 생성에 실패했습니다.: ");
      }
    }

    loadData();
  }

  private void loadData() {
    File file = new File(filePath);
    if (!file.exists()) return;

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      Map<ID, T> loadedData = (Map<ID, T>) ois.readObject();
      dataMap.putAll(loadedData);
    } catch (IOException | ClassNotFoundException e) {
      throw new IllegalStateException("데이터 로딩에 실패: " + filePath, e);
    }
  }

  protected void saveData() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
      oos.writeObject(dataMap);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public T save(T entity, ID id) {
    dataMap.put(id, entity);
    saveData();
    return entity;
  }

  public Optional<T> findById(ID id) {
    return Optional.ofNullable(dataMap.get(id));
  }

  public List<T> findAll() {
    return new ArrayList<>(dataMap.values());
  }

  public T update(T entity, ID id) {
    if (!dataMap.containsKey(id)) {
      throw new IllegalArgumentException("해당 ID가 존재하지 않습니다: " + id);
    }
    dataMap.put(id, entity);
    saveData();
    return entity;
  }

  public void delete(ID id) {
    dataMap.remove(id);
    saveData();
  }
}

