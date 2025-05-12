package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entitiy.BinaryContent;
import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.ReadStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "File")
public class FileBinaryContentRepository implements BinaryContentRepository {

    @Value( "${discodeit.repository.fileDirectory}")
    private String FILE_Directory;
    private final String FILE_NAME = "binarycontent.ser";

    public Path getFilePath() {
        return Paths.get(FILE_Directory, FILE_NAME);
    }

    //File*Repository에서만 사용, 파일을 읽어들여 리스트 반환
    public List<BinaryContent> readFiles() {
        try {
            if (!Files.exists(getFilePath()) || Files.size(getFilePath()) == 0) {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<BinaryContent> binaryContents = new ArrayList<>();
        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(getFilePath().toFile()))) {
            while(true) {
                try {
                    binaryContents.add((BinaryContent) reader.readObject());
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return binaryContents;
    }


    //File*Repository에서만 사용, 만들어 놓은 리스트를 인자로 받아 파일에 쓰기
    public void writeFiles(List<BinaryContent> binaryContents) {
        try {
            Files.createDirectories(getFilePath().getParent());
            try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(getFilePath().toFile()))) {
                for (BinaryContent binaryContent : binaryContents) {
                    writer.writeObject(binaryContent);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        List<BinaryContent> binaryContents = readFiles();
        binaryContents.add(binaryContent);
        writeFiles(binaryContents);
        return binaryContent;
    }

    @Override
    public List<BinaryContent> read() {
        List<BinaryContent> binaryContents = readFiles();
        return binaryContents;
    }

    @Override
    public Optional<BinaryContent> readById(UUID id) {
        List<BinaryContent> binaryContents = readFiles();
        Optional<BinaryContent> binaryContent = binaryContents.stream()
                .filter((u)->u.getId().equals(id))
                .findAny();
        return binaryContent;
    }

    @Override
    public void update(UUID id, BinaryContent binaryContent) {
        List<BinaryContent> binaryContents = readFiles();
        binaryContents.stream()
                .filter((c)->c.getId().equals(id))
                .forEach((c)->{
                    c.setName(binaryContent.getName());
                    c.setContentType(binaryContent.getContentType());
                    c.setBytes(binaryContent.getBytes());
                });
        writeFiles(binaryContents);
    }

    @Override
    public void delete(UUID binaryContentId) {
        List<BinaryContent> binaryContents = readFiles();
        binaryContents.removeIf(binaryContent -> binaryContent.getId().equals(binaryContentId));
        writeFiles(binaryContents);
    }

}
