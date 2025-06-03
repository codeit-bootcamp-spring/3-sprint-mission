package com.sprint.mission.discodeit.mapper.advanced;

import com.sprint.mission.discodeit.dto.channel.response.JpaChannelResponse;
import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.original.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper.advanced
 * FileName     : AdvancedChannelMapper
 * Author       : dounguk
 * Date         : 2025. 6. 3.
 */
// 코드만 더 복잡해지는데 이게 맞을까?
//@Component
//@Mapper(componentModel = "spring",uses = {AdvancedUserMapper.class})
public interface AdvancedChannelMapper {
//    AdvancedChannelMapper INSTANCE = Mappers.getMapper(AdvancedChannelMapper.class);
//
//    @Mapping(source = "readStatus",target = "participants")
//    @Mapping(target = "lastMessageAt", ignore = true)
//    JpaChannelResponse toDto(Channel channel);
//
//
//    default List<JpaUserResponse> getParticipants(List<ReadStatus> readStatuses) {
//        if (readStatuses == null || readStatuses.isEmpty()) {
//
//            return Collections.emptyList();
//        }
//        return readStatuses.stream().map(ReadStatus::getUser).map(user -> userToDto(user)).toList();
//    }
//
//    @Mapping(target = "online", expression = "java(false)")
//    JpaUserResponse userToDto(User user);
}
