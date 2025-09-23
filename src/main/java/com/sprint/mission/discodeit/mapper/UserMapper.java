package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.registry.JwtRegistry;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

    @Autowired
    private JwtRegistry jwtRegistry;

    @Mapping(target = "online", expression = "java(isOnline(user))")
    public abstract UserDto toDto(User user);

    protected boolean isOnline(User user) {
        boolean isOnline = jwtRegistry.hasActiveJwtInformationByUserId(user.getId());
        log.info("[UserMapper] 사용자 온라인 상태 확인: userId={}, username={}, isOnline={}", 
                user.getId(), user.getUsername(), isOnline);
        return isOnline;
    }
}
