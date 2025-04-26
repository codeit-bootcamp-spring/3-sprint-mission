package com.sprint.mission.discodeit.repository.storage;

import com.sprint.mission.discodeit.common.exception.FileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class FileStorageImpl implements FileStorage {

  private final File file;
  private final List<Long> positions = new ArrayList<>();

  public FileStorageImpl(String filePath) throws FileException {
    try {
      this.file = new File(filePath);
      if (!file.exists()) {
        file.createNewFile();
      }
    } catch (IOException e) {
      throw FileException.writeError(new File(filePath), e);
    }
  }

  @Override
  public synchronized long saveObject(Object obj) {
    try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos)) {

      oos.writeObject(obj);
      oos.flush();
      byte[] serializedObject = baos.toByteArray();

      raf.seek(file.length());
      long position = raf.getFilePointer();
      raf.writeInt(serializedObject.length);
      raf.write(serializedObject);

      positions.add(position);
      return position;
    } catch (IOException e) {
      throw FileException.writeError(file, e);
    }
  }

  @Override
  public Object readObject(Long position) {
    try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
      raf.seek(position);
      int length = raf.readInt();
      byte[] serializedObject = new byte[length];
      raf.readFully(serializedObject);

      try (ObjectInputStream ois = new ObjectInputStream(
          new ByteArrayInputStream(serializedObject))) {
        return ois.readObject();
      }
    } catch (IOException | ClassNotFoundException e) {
      throw FileException.readError(file, e);
    }
  }

  @Override
  public void updateObject(Long position, Object obj) {
    deleteObject(position);
    long newPosition = saveObject(obj);
    positions.add(newPosition);
  }

  @Override
  public void deleteObject(Long position) {
    // 성능과 메모리 효율적이지 않을 수 있지만 파일에서 객체를 삭제하고 빈 공간을 제거하며 앞 당긴다.
    // TODO: 실행마다 빈 공간을 제거하며 앞 당기 않고 삭제된 객체의 위치를 빈 공간으로 변경하고 주기적인 트리거에 의한 optimize를 통해 개선한다.
    try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
      if (!positions.contains(position)) {
        throw FileException.invalidPosition(file, position);
      }

      long nextPositionIndex = positions.indexOf(position) + 1;
      long nextPosition = (nextPositionIndex < positions.size())
          ? positions.get((int) nextPositionIndex)
          : file.length(); // 다음 데이터 위치

      long lengthToShift = raf.length() - nextPosition;

      positions.remove(position);

      // 뒤에 나머지 데이터를 앞으로 이동
      if (lengthToShift > 0) {
        byte[] remainingData = new byte[(int) lengthToShift];
        raf.seek(nextPosition);
        raf.readFully(remainingData);
        raf.seek(position); // 삭제된 위치로 이동
        raf.write(remainingData);
      }

      // 파일의 길이 줄이기
      raf.setLength(raf.length() - (nextPosition - position));
    } catch (IOException e) {
      throw FileException.deleteError(file, position, e);
    }

  }

  @Override
  public synchronized List<Object> readAll() {
    List<Object> objects = new ArrayList<>();
    try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
      positions.clear();
      while (raf.getFilePointer() < file.length()) {
        long position = raf.getFilePointer();
        positions.add(position);

        int length = raf.readInt();
        if (length <= 0 || (raf.getFilePointer() + length > file.length())) {
          // 빈 공간이나 잘못된 데이터를 건너뜀
          raf.seek(raf.getFilePointer() + Math.max(length, 0));
          continue;
        }

        byte[] serializedObject = new byte[length];
        raf.readFully(serializedObject);
        try (ObjectInputStream ois = new ObjectInputStream(
            new ByteArrayInputStream(serializedObject))) {
          objects.add(ois.readObject());
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      throw FileException.readError(file, e);
    }
    return objects;
  }

  @Override
  public void optimize() {
    try {
      List<Object> objects = readAll();
      try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
        positions.clear();
        raf.setLength(0);
        for (Object obj : objects) {
          saveObject(obj);
        }
      }
    } catch (Exception e) {
      throw FileException.optimizationError(file, e);
    }
  }
}