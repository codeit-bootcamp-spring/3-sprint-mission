package com.sprint.mission.discodeit.dto.mapper.mapstruct;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.config.CommonMapperConfig;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Optional;

/**
 * User Entity ↔ UserDto 매핑
 * 
 * 복잡한 매핑:
 * - profile: BinaryContentMapper 사용
 * - online: UserStatus에서 계산
 * - password: 보안상 제외
 */
@Mapper(config = CommonMapperConfig.class, uses = { BinaryContentMapper.class })
public interface UserMapper {

  /**
   * User Entity → UserDto 변환
   * 
   * - profile: BinaryContentMapper가 자동으로 매핑
   * - online: UserStatus.isOnline() 값으로 매핑
   * - password: 보안상 제외 (DTO에 없음)
   */
  @Mapping(target = "online", expression = "java(getOnlineStatus(user))")
  UserDto toDto(User user);

  /**
   * User Entity 리스트 → UserDto 리스트 변환
   */
  List<UserDto> toDtoList(List<User> users);

  /**
   * 사용자 온라인 상태 계산
   * UserStatus가 있고 온라인이면 true, 그외는 false
   */
  default Boolean getOnlineStatus(User user) {
    return Optional.ofNullable(user.getUserStatus())
        .map(UserStatus::isOnline)
        .orElse(false);
  }
}