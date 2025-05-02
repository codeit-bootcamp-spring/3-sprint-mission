package com.sprint.mission.discodeit.repository.file;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFileRepository<K, V> {


    private final String filePath;

    protected AbstractFileRepository(String basePath, String path) {
        this.filePath = basePath + path;
    }

    public Map<K, V> loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<K, V>) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 실패: " + filePath, e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("데이터 형식 오류: " + filePath, e);
        }
    }

    public void saveToFile(Map<K, V> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패 : ", e);
        }
    }

    protected V save(K key, V value) {
        Map<K, V> data = loadFromFile();
        data.put(key, value);
        saveToFile(data);
        return value;
    }

}

