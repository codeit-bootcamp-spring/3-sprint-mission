package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

    private final String FILENAME = "src/main/java/com/sprint/mission/discodeit/data/binaryContentRepo.ser";
    private final File file = new File(FILENAME);
    
    @Override
    public void save(BinaryContent binaryContent) {
        // 파일에서 읽어오기
        Map<UUID, BinaryContent> data = loadFile();

        data.put(binaryContent.getId(), binaryContent);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        // 파일에서 읽어오기
        Map<UUID, BinaryContent> data = loadFile();

        Optional<BinaryContent> foundBinaryContent = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundBinaryContent;
    }

    @Override
    public List<BinaryContent> findAll() {
        // 파일에서 읽어오기
        Map<UUID, BinaryContent> data = loadFile();

        return new ArrayList<>(data.values());
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        // 파일에서 읽어오기
        Map<UUID, BinaryContent> data = loadFile();

        return data.entrySet().stream()
                .filter(entry -> ids.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        // 파일에서 읽어오기
        Map<UUID, BinaryContent> data = loadFile();

        data.remove(id);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<UUID, BinaryContent> loadFile() {
        Map<UUID, BinaryContent> data = new HashMap<>();

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream in = new ObjectInputStream(fis)) {
                data = (Map<UUID, BinaryContent>)in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}
