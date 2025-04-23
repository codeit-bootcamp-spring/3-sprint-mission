package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FileUserRepository implements UserRepository {

    private static final String FILE_PATH = "src/main/java/com/sprint/mission/discodeit/repository/file/data/users.ser";

    @Override
    public void save(User user) {
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

        users.add(user);

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

        List<User> deleteUsers = users.stream()
                .filter((c) -> !c.getId().equals(user.getId()))
                .collect(Collectors.toList());

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
