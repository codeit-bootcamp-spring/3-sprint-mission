package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;

public interface UserService {
    public User registerUser();

    public void createNewUserNames(String existingName, String newName);

    public void outputAllUsersInfo();

    public void outputOneUserInfo(String name);

    public void updateUserName(User user, String newName);

    public void deleteUserName(int userNumber);

    public User changeUser(int userNumber);

    public void login(int loginNumber, List<User> users);
}
