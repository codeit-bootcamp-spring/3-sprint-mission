package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.entitiy.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUserStatusRepository implements UserStatusRepository {

    private static final String FILE_PATH = "src/main/java/com/sprint/mission/discodeit/repository/file/data/userstatus.ser";

    //File*Repository에서만 사용, 파일을 읽어들여 리스트 반환
    public List<UserStatus> readFiles(){
        List<UserStatus> userStatuses = new ArrayList<>();
        try (ObjectInputStream reader= new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILE_PATH)))){
            while(true){
                try {
                    userStatuses.add((UserStatus) reader.readObject());
                }catch (EOFException e){
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return userStatuses;
    }

    //File*Repository에서만 사용, 만들어 놓은 리스트를 인자로 받아 파일에 쓰기
    public void writeFiles(List<UserStatus> userStatuses){
        try (ObjectOutputStream writer= new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(FILE_PATH)))){
            userStatuses.forEach((c)->{
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
    public UserStatus save(UserStatus userStatus) {
        List<UserStatus> userStatuses = readFiles();
        userStatuses.add(userStatus);
        writeFiles(userStatuses);
        return userStatus;
    }

    @Override
    public List<UserStatus> read() {
        List<UserStatus> userStatuses = readFiles();
        return userStatuses;
    }

    @Override
    public Optional<UserStatus> readById(UUID id) {
        List<UserStatus> userStatuses = readFiles();
        Optional<UserStatus> userStatus = userStatuses.stream()
                .filter((u)->u.getId().equals(id))
                .findAny();
        return userStatus;
    }

    @Override
    public void update(UUID id, UserStatus userStatus) {
        List<UserStatus> userStatuses = readFiles();
        userStatuses.stream()
                .filter((c)->c.getId().equals(id))
                .forEach((c)->{
                    c.setUpdatedAt(Instant.now());
                });
        writeFiles(userStatuses);
    }

    @Override
    public void delete(UserStatus userStatus) {
        List<UserStatus> userStatuses = readFiles();
        List<UserStatus> UserStatus = userStatuses.stream()
                .filter((c) -> !c.getId().equals(userStatus.getId()))
                .toList();
        writeFiles(userStatuses);
    }
}
