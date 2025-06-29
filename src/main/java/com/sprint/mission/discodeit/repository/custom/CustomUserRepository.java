package com.sprint.mission.discodeit.repository.custom;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;

public interface CustomUserRepository {

    List<User> findAllWithProfileAndStatus();

}
