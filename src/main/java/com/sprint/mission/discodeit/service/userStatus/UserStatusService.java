package com.sprint.mission.discodeit.service.userStatus;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.dto.userStatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.userStatus.UserStatusUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    public UserStatus createUserStatus(UserStatusCreateRequestDto userStatusCreateRequestDto);

    public UserStatus findStatusById(UUID statusId);

    public List<UserStatus> findAllStatus();

    public boolean updateUserStatus(UserStatusUpdateRequestDto userStatusUpdateRequestDto);

    public boolean updateByUserId(UUID userId);
}
