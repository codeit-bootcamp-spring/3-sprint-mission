package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;

public interface UserRepository {

    void saveUser(User user);

    void updateUser(User user);

    void deleteUser(int userNumber);

    User findUser(int userNumber);

    List<User> findAllUser();
}