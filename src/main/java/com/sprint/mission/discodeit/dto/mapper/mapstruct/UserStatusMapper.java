package com.sprint.mission.discodeit.dto.mapper.mapstruct;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.config.CommonMapperConfig;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * UserStatus Entity ↔ UserStatusDto 매핑
 * 
 * User 연관관계는 userId로만 매핑
 */
@Mapper(config = CommonMapperConfig.class)
public interface UserStatusMapper {

  /**
   * UserStatus Entity → UserStatusDto 변환
   * User 객체는 userId로만 매핑
   */
  @Mapping(target = "userId", source = "user.id")
  UserStatusDto toDto(UserStatus entity);

  /**
   * UserStatus Entity 리스트 → UserStatusDto 리스트 변환
   */
  List<UserStatusDto> toDtoList(List<UserStatus> entities);
}