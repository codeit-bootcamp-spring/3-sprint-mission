package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFUserRepository implements UserRepository {

    private static final String FILE_PATH = "src/main/java/com/sprint/mission/discodeit/service/file/data/users.ser";

    @Override
    public void save(User user) {
        //기존의 user내용을 저장할 List 생성
        List<User> users = new ArrayList<>();
        try (ObjectInputStream reader= new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILE_PATH)))){
            while(true){
                try {
                    users.add((User) reader.readObject());
                }catch (EOFException e){
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        //새로 넣을 user를 List에 추가
        users.add(user);

        //OutputStream을 통해 파일에 출력
        try (ObjectOutputStream writer= new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(FILE_PATH)))){
            users.forEach((c)->{
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
    public void read() {
        //InputStream을 통해 파일에 접근
        try (ObjectInputStream reader= new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILE_PATH)))){
            while(true){
                try {
                    User user = (User) reader.readObject();
                    System.out.println(user);
                }catch (EOFException e){
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readById(UUID id) {
        //InputStream을 통해 파일에 접근하여 id가 일치하는경우 출력
        try (ObjectInputStream reader= new ObjectInputStream(new FileInputStream(FILE_PATH))){
            while(true){
                try {
                    User user = (User) reader.readObject();
                    if(user.getId().equals(id)){
                        System.out.println(user);
                    }
                }catch (EOFException e){
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(UUID id, User user) {
        //기존의 user내용을 저장할 List 생성
        List<User> users = new ArrayList<>();
        try (ObjectInputStream reader= new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILE_PATH)))){
            while(true){
                try {
                    users.add((User) reader.readObject());
                }catch (EOFException e){
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //수정할 user의 id와 일치하는경우 수정하여 List에 저장
        users.stream()
                .filter((c)->c.getId().equals(id))
                .forEach((c)->{
                    c.updateUserName(user.getUserName());
                    c.updatePassword(user.getPassword());
                    c.updateUpdatedAt(System.currentTimeMillis());
                    c.updateEmail(user.getEmail());
                    c.updatePhone(user.getPhone());
                    c.updateStatus(user.getStatus());
                    c.updateIsMikeOn(user.getIsMikeOn());
                    c.updateIsSpeakerOn(user.getIsSpeakerOn());
                    c.updateFriends(user.getFriends());
                });

        // OutputStream을 사용해 파일에 출력
        try (ObjectOutputStream writer= new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(FILE_PATH)))){
            users.forEach((c)->{
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
    public void delete(User user) {
        //기존의 user를 저장할 List생성
        List<User> users = new ArrayList<>();
        try (ObjectInputStream reader= new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILE_PATH)))){
            while(true){
                try {
                    users.add((User) reader.readObject());
                }catch (EOFException e){
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //삭제할 id의 user를 제외하고 새로운 리스트에 저장
        List<User> deleteUsers = users.stream()
                .filter((c) -> !c.getId().equals(user.getId()))
                .collect(Collectors.toList());

        //리스트를 OutputStream을 통해 파일에 출력
        try (ObjectOutputStream writer= new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(FILE_PATH)))){
            deleteUsers.forEach((c)->{
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
}
