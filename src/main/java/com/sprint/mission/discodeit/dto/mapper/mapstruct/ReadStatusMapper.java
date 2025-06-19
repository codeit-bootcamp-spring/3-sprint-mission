package com.sprint.mission.discodeit.dto.mapper.mapstruct;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.config.CommonMapperConfig;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * ReadStatus Entity ↔ ReadStatusDto 매핑
 * 
 * User와 Channel 연관관계는 각각 userId, channelId로만 매핑
 */
@Mapper(config = CommonMapperConfig.class)
public interface ReadStatusMapper {

  /**
   * ReadStatus Entity → ReadStatusDto 변환
   * 연관관계는 ID로만 매핑
   */
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "channelId", source = "channel.id")
  ReadStatusDto toDto(ReadStatus entity);

  /**
   * ReadStatus Entity 리스트 → ReadStatusDto 리스트 변환
   */
  List<ReadStatusDto> toDtoList(List<ReadStatus> entities);
}