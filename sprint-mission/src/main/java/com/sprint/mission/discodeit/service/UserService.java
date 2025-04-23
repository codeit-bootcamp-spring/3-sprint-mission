package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(String username, UUID channelId);


   Map<UUID, User> readUsers();

   Optional<User> readUser(UUID id);

   User updateUser(UUID id, String username);
   User deleteUser(UUID id);


}
