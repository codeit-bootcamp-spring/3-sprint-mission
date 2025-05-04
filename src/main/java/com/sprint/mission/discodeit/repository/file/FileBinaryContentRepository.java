package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.io.*;
import java.util.*;

public class FileBinaryContentRepository implements BinaryContentRepository {
    private static final String FILE_PATH = "data/binary-contents.ser";
    private Map<UUID, BinaryContent> data;

    public FileBinaryContentRepository() {
        this.data = load();
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        data.put(binaryContent.getId(), binaryContent);
        saveToFile();
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
        saveToFile();
    }

    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> results = new ArrayList<>();
        for (UUID id : ids) {
            BinaryContent content = data.get(id);
            if (content != null) {
                results.add(content);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, BinaryContent> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
