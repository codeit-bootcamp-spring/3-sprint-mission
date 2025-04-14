package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.Map;
import java.util.UUID;

public interface UserService {
    User createUser(String username, UUID channelId);


   Map<UUID, User> readUsers();

   User readUser(UUID id);

   User updateUser(UUID id, String username);
   User deleteUser(UUID id);


}
