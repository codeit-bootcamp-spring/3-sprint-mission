package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;


import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileUserService implements UserService{
    private static final String FILE_PATH = "src/main/java/com/sprint.mission/discodeit/users.ser";
    private final ChannelService channelService;
    private Map<UUID,User> users;

    public FileUserService(ChannelService channelService) {
        this.channelService = channelService;
        this.users = new HashMap<>();
    }


@Override
public User createUser(String username, UUID channelId){

        User user = new User(username);
        users.put(user.getId(),user);
        //repo 생성 후 파일에 저장하는 구문추가
        return user;
}

// readUsers랑 read는 읽어오는 역할만 하기에 파일에 저장할 일은 없는거 같음
@Override
public Map<UUID, User> readUsers() {
        return users;
    }

@Override
public User readUser(UUID id) {
        return users.get(id);
    }

@Override
public User updateUser(UUID id,String username) {
        User user = users.get(id);
        user.updateUserName(username);
        //repo 생성 후 파일에 저장하는 구문추가
        return user;
}

@Override
public User deleteUser(UUID id) {
        //repo 생성 후 파일에 저장하는 구문추가
        return users.get(id);
    }
}

