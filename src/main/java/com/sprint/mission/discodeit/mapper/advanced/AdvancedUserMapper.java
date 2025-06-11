package com.sprint.mission.discodeit.mapper.advanced;

import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Qualifier;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper.advanced
 * FileName     : AdvancedUserMapper
 * Author       : dounguk
 * Date         : 2025. 6. 3.
 */
@Mapper(uses = {AdvancedBinaryContentMapper.class}, componentModel = "spring")
public interface AdvancedUserMapper {
    AdvancedUserMapper INSTANCE = Mappers.getMapper(AdvancedUserMapper.class);

    @Mapping(source = "profile", target = "profile")
    @Mapping(target = "online", expression = "java(isOnline(user.getStatus()))")
    JpaUserResponse toDto(User user);


    default boolean isOnline(UserStatus userStatus) {
        if (userStatus == null || userStatus.getLastActiveAt() == null) return false;
        Instant now = Instant.now();
        return Duration.between(userStatus.getLastActiveAt(), now).toMinutes() < 5;
    }
}
