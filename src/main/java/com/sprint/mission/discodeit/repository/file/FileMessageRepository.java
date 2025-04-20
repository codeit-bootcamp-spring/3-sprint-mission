package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {

    private final File file = new File("/data/messages.txt");


    public void save(Message message) { // entity를 받고 return 없음

        // 부모 디렉토리 없으면 생성
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs(); // data/ 폴더 생성
        }

        try (ObjectOutputStream objectOOS = new ObjectOutputStream(new FileOutputStream(file)) // FOS는 Path를 직접 파라미터로 받지 못함
        ) {
            objectOOS.writeObject(message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void save(List<Message> messageList) {

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {

            oos.writeObject(messageList);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Message> load() { // List 출력
        List<Message> objectList = new ArrayList();

        Path path = Paths.get("/data/messages.txt");
        try (ObjectInputStream objectOIS = new ObjectInputStream(new FileInputStream(path.toFile()))
        ) {
            if (!Files.exists(path)) { // 비즈니스 로직
                return new ArrayList<>();
            }
            for (Message object :(List<Message>) objectOIS.readObject()) {

                objectList.add(object);
            }
        } catch (FileNotFoundException e) {
            System.out.println("메시지 파일을 찾을 수 없습니다."); // 비즈니스 로직
            throw new RuntimeException(e);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("파일에 메시지가 없습니다."); // 비즈니스 로직
            throw new RuntimeException(e);
        }
        return objectList;
    }


    @Override
    public void create(Message message) {
        save(message);
    }

    @Override
    public Message findById(UUID id) {
        List<Message> objectList = load();
        for (Message object : objectList) {
            if (object.getId().equals(id)) {
                return object;
            }
        }
        return null;
    }

    @Override
    public List<Message> findAll() {
        List<Message> objectList = load();
        return objectList;
    }

    @Override
    public void updateMessage(UUID id, String newContent) {
        List<Message> objectList = load();
        for (Message object : objectList) {
            if (object.getId().equals(id)) {
                object.updateMessage(newContent);
                save(objectList);
                break;
            }
        }
    }

    @Override
    public void delete(UUID id) {
        List<Message> objectList = load();
        for (Message object : objectList) {
            if (object.getId().equals(id)) {
                objectList.remove(object);
                save(objectList);
                break;
            }
        }
    }
}
