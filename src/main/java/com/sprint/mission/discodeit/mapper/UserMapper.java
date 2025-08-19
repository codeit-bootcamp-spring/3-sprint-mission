package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  // 온라인 여부는 서비스 계층(SessionRegistry)에서 재계산
  @Mapping(target = "online", constant = "false")
  UserResponse toResponse(User user);

  List<UserResponse> toResponseList(List<User> users);
}
