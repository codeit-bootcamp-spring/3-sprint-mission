package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  Optional<UserStatus> findByUser(User user);

  Optional<UserStatus> findByUserId(UUID userId);

  List<UserStatus> findByUserIdIn(List<UUID> userIds);

  void deleteById(@NonNull UUID userStatusId);
}
