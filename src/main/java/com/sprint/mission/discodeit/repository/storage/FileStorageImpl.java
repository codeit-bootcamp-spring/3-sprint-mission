package com.sprint.mission.discodeit.repository.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileStorageImpl implements FileStorage {

  private final File directory;

  public FileStorageImpl(String directoryPath) {
    this.directory = new File(directoryPath);
    if (!directory.exists()) {
      if (!directory.mkdirs()) {
        throw new RuntimeException("디렉토리를 생성할 수 없습니다: " + directoryPath);
      }
    } else if (!directory.isDirectory()) {
      throw new RuntimeException("지정된 경로가 디렉토리가 아닙니다: " + directoryPath);
    }
  }

  private File resolveFile(UUID id) {
    return new File(directory, id.toString() + ".ser");
  }

  @Override
  public void saveObject(UUID id, Object obj) {
    File targetFile = resolveFile(id);
    if (targetFile.exists()) {
      throw new IllegalArgumentException(
          "파일이 이미 존재합니다. updateObject를 사용하세요: " + targetFile.getName());
    }
    writeObjectToFile(targetFile, obj);
  }

  @Override
  public Object readObject(UUID id) {
    File targetFile = resolveFile(id);
    if (!targetFile.exists()) {
      throw new RuntimeException("파일이 존재하지 않습니다: " + targetFile.getName());
    }
    return readObjectFromFile(targetFile);
  }

  @Override
  public void updateObject(UUID id, Object obj) {
    File targetFile = resolveFile(id);
    if (!targetFile.exists()) {
      throw new RuntimeException("수정할 파일이 존재하지 않습니다: " + targetFile.getName());
    }
    writeObjectToFile(targetFile, obj);
  }

  @Override
  public void deleteObject(UUID id) {
    File targetFile = resolveFile(id);
    if (!targetFile.exists()) {
      throw new RuntimeException("삭제할 파일이 존재하지 않습니다: " + targetFile.getName());
    }
    if (!targetFile.delete()) {
      throw new RuntimeException("파일 삭제에 실패했습니다: " + targetFile.getName());
    }
  }

  @Override
  public List<Object> readAll() {
    List<Object> objects = new ArrayList<>();
    File[] files = directory.listFiles((dir, name) -> name.endsWith(".ser"));
    if (files != null) {
      for (File file : files) {
        Object obj = readObjectFromFile(file);
        if (obj != null) {
          objects.add(obj);
        }
      }
    }
    return objects;
  }

  private void writeObjectToFile(File file, Object obj) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(obj);
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 중 오류 발생: " + file.getName(), e);
    }
  }

  private Object readObjectFromFile(File file) {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("파일 읽기 중 오류 발생: " + file.getName(), e);
    }
  }
}
