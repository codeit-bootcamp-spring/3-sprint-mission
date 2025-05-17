package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUserRepository implements UserRepository {
    private final List<User> users;
    private static final String USER_FILE_PATH = "src/files/user.ser";

    public FileUserRepository() {
        this.users = fileLoadUsers();
    }
    // 파일 로드 메서드
    @SuppressWarnings("unchecked")
    public List<User> fileLoadUsers(){
        try (FileInputStream fis = new FileInputStream(USER_FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)){
            return (List<User>)ois.readObject();
        } catch (IOException | ClassNotFoundException e){
            return new ArrayList<>();
        }
    }
    // 파일 세이브 메서드
    public void saveUsersList(){
        try(FileOutputStream fos = new FileOutputStream(USER_FILE_PATH);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(users);
        } catch (IOException e){
            System.out.println("파일생성에실패하였습니다 ///user.ser");
        }
    }
    public List<User> getUserslist() {
        return users;
    }
}
