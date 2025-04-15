package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUserRepository implements UserRepository {

    private static final String USER_FILE_REPOSITORY_PATH = "src/main/java/com/sprint/mission/discodeit/repository/data/User.txt";


    @Override
    public void save(User user) {
        File file = new File(USER_FILE_REPOSITORY_PATH);

        // 방어 코드
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // 객체 직렬화( 저장 )
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
