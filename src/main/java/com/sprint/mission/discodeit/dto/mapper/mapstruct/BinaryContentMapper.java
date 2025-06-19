package com.sprint.mission.discodeit.dto.mapper.mapstruct;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.config.CommonMapperConfig;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * BinaryContent Entity ↔ BinaryContentDto 매핑
 * 
 * 가장 단순한 매핑 - 모든 필드가 1:1 대응
 */
@Mapper(config = CommonMapperConfig.class)
public interface BinaryContentMapper {

  /**
   * BinaryContent Entity → BinaryContentDto 변환
   */
  BinaryContentDto toDto(BinaryContent entity);

  /**
   * BinaryContent Entity 리스트 → BinaryContentDto 리스트 변환
   */
  List<BinaryContentDto> toDtoList(List<BinaryContent> entities);
}