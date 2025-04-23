package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private static final String fileName = "files/user.ser";
    private final Map<UUID, User> users;

    public FileUserRepository() {
        this.users = load();
    }

   @Override
   public void save(User user){
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))){
            out.writeObject(users);
        }
        catch(IOException e)
        {
            throw new RuntimeException("파일 저장 실패입니다 : " + e.getMessage(), e);
        }
   }

   @Override
   public Map<UUID, User> load() {
        File file = new File(fileName);

        if(!file.exists()){
            return new HashMap<>();
        }

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file)))
        {
            return (Map<UUID, User>) in.readObject();
        }
        catch(IOException | ClassNotFoundException e)
        {
            throw new RuntimeException("파일 로드 실패입니다 : " + e.getMessage(), e);
        }

   }

   @Override
    public void deleteUser(UUID id){
        users.remove(id);
   }


}
