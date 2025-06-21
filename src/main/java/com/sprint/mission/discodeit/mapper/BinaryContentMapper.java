package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

  public BinaryContentDto toDto(BinaryContent binaryContent);

  public BinaryContent toEntity(BinaryContentDto binaryContentDto);

}
