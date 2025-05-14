package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    public User createUser(User user);

    public Optional<User> getUser(UUID id);

    public List<User> getAllUsers();

    public boolean modifyUser(User user);

    public boolean deleteUser(UUID id);

}
