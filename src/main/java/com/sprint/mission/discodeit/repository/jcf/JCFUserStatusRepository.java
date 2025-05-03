package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("jcfUserStatusRepository")
public class JCFUserStatusRepository implements UserStatusRepository {
    @Override
    public void save(UserStatus userStatus) {

    }

    @Override
    public UserStatus loadById(UUID id) {
        return null;
    }

    @Override
    public List<UserStatus> loadAll() {
        return List.of();
    }

    @Override
    public void deleteById(UUID id) {

    }
}
