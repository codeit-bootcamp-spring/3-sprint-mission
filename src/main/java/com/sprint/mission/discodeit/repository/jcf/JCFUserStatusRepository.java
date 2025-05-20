package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@Profile("jcf")
public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> userStatuses = new HashMap<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        userStatuses.put(userStatus.getUserId(), userStatus);
        return userStatus;
    }

    @Override
    public UserStatus loadById(UUID id) { return userStatuses.get(id); }

    @Override
    public List<UserStatus> loadAll() { return userStatuses.values().stream().toList(); }

    @Override
    public void deleteByUserId(UUID id) {

    }
}
