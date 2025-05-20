package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileMessageRepository implements MessageRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"),fileDirectory, Message.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private Path resolvePath(UUID id){
        return DIRECTORY.resolve(id+EXTENSION);
    }


    @Override
    public Message save(Message message) {
        Path path = resolvePath(message.getId());
        try(
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ){
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public Message findById(UUID id) {
        Message message = null;
        Path targetMessageFile = resolvePath(id);
        if( Files.exists(targetMessageFile)){
            try (
                    FileInputStream fis = new FileInputStream(targetMessageFile.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                message = (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return message;
    }

    @Override
    public List<Message> findByChannel(UUID channelId) {
        return findAll().stream().filter(m -> m.getChannelId().equals(channelId)).collect(Collectors.toList());
    }

    @Override
    public List<Message> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Message) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path);
    }


    @Override
    public void delete(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByChannelId(UUID channelId){
        this.findByChannel(channelId).forEach(message-> this.delete(message.getId()));
    }
}
