package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;


import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileUserService implements UserService{
 private final Map<UUID, User> users;
 private static final String fileName = "files/user.ser";
 private final ChannelService channelService;

 public FileUserService(ChannelService channelService) {
     this.users = loadFromFile();
     this.channelService = channelService;
 }

 // 파일에서 데이터를 읽어오는 역할
    private Map<UUID, User> loadFromFile() {
     File file = new File(fileName);

     if(!file.exists()){
         return new HashMap<>();
     }

     try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file)))
     {
         return (Map<UUID, User>) in.readObject();
     }
     catch(IOException | ClassNotFoundException e)
     {
         throw new RuntimeException("파일 로드 실패입니다 : " + e.getMessage(), e);
     }
 }

 // 데이터를 파일에 저장하는 역할
    private void saveToFile() {
     try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))){
         out.writeObject(users);
     }
     catch (IOException e){
         throw new RuntimeException("파일 저장 실패입니다." + e.getMessage(), e);
     }
 }


    @Override
public User createUser(String username, UUID channelId){
        User user = new User(username);
        users.put(user.getId(),user);
        channelService.addUserToChannel(channelId, user.getId());
        saveToFile();
        return user;
}


    // readUsers랑 read는 읽어오는 역할만 하기에 파일에 저장할 일은 없는거 같음
@Override
public Map<UUID, User> readUsers() {
        return users;
    }

@Override
public  Optional<User> readUser(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

@Override
public User updateUser(UUID id,String username) {
        User user = users.get(id);
        user.updateUserName(username);
        saveToFile();
        return user;
}

@Override
public User deleteUser(UUID id) {
       saveToFile();
       return users.remove(id);
    }
}




