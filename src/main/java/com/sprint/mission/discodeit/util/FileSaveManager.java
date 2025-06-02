package com.sprint.mission.discodeit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class FileSaveManager {
  private static final Map<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

  // 경로별 락을 생성하거나 가져옴
  private static ReentrantLock getLockForFile(File file) {
    return lockMap.computeIfAbsent(file.getAbsolutePath(), k -> new ReentrantLock());
  }

  public static <T extends Serializable> void saveToFile(File file, Map<UUID , T> data) {
    ReentrantLock lock = getLockForFile(file);
    
    if (lock.tryLock()) { // 락이 바로 가능할 때만 저장
      try (FileOutputStream fos = new FileOutputStream(file);
          ObjectOutputStream out = new ObjectOutputStream(fos)) {
        out.writeObject(data);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        lock.unlock();
      }
    } else {
      System.out.println("Skipped saving: file is currently locked - " + file.getName());
    }
  }

  public static <T extends Serializable> T loadFromFile(File file) {
    if (!file.exists()) return null;

    ReentrantLock lock = getLockForFile(file);

    lock.lock(); // 로드 시에도 동기화
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
      return (T) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    } finally {
      lock.unlock();
    }
  }
}
