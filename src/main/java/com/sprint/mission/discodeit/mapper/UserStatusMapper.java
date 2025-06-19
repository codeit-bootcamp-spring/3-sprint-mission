package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {

    @Mapping(target = "userId", source = "user", qualifiedByName = "safeGetUserId")
    UserStatusDto toDto(UserStatus userStatus);

    @Named("safeGetUserId")
    default UUID safeGetUserId(User user) {
        try {
            return user != null ? user.getId() : null;
        } catch (Exception e) {
            return null; // 프록시 초기화 실패 시 null 반환
        }
    }
}
