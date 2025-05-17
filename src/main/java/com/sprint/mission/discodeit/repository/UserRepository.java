package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface UserRepository {
    List<User> getUserslist();
    void saveUsersList();
}
