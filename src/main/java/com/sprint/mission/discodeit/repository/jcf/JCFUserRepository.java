package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {

    // JCF 저장로직 ( 기존 Map 채택 >> map.put()기능 )
    private final Map<UUID, User> userMap = new HashMap<>();


    @Override
    public void save(User user) {
        userMap.put(user.getUserId(), user);
    }
}
