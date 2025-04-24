package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entitiy.BinaryContent;
import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileBinaryContentRepository implements BinaryContentRepository {

    private static final String FILE_PATH = "src/main/java/com/sprint/mission/discodeit/repository/file/data/binarycontents.ser";

    //File*Repository에서만 사용, 파일을 읽어들여 리스트 반환
    public List<BinaryContent> readFiles(){
        List<BinaryContent> binaryContents = new ArrayList<>();
        try (ObjectInputStream reader= new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILE_PATH)))){
            while(true){
                try {
                    binaryContents.add((BinaryContent) reader.readObject());
                }catch (EOFException e){
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return binaryContents;
    }

    //File*Repository에서만 사용, 만들어 놓은 리스트를 인자로 받아 파일에 쓰기
    public void writeFiles(List<BinaryContent> binaryContents){
        try (ObjectOutputStream writer= new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(FILE_PATH)))){
            binaryContents.forEach((c)->{
                try {
                    writer.writeObject(c);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.flush();
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
                    c.setContentType(binaryContent.getContentType());
                    c.setContent(binaryContent.getContent());
                });
        writeFiles(binaryContents);
    }

    @Override
    public void delete(BinaryContent binaryContent) {
        List<BinaryContent> binaryContents = readFiles();
        List<BinaryContent> deleteBinaryContents = binaryContents.stream()
                .filter((c) -> !c.getId().equals(binaryContent.getId()))
                .toList();
        writeFiles(deleteBinaryContents);
    }

}
