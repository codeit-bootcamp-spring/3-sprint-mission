package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

public interface UserService {
    public User inputUserName();

    public void createNewUserNames(String oldName, String newName);

    public void outputAllUsersInfo();

    public void outputOneUserInfo(String name);

    public void updateUserName(User user, String newName);

    public void deleteUserName(int userNumber);

    public User changeUser(int userNumber);
}
