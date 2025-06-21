package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

  BinaryContentDTO toDTO(BinaryContent binaryContent);

}
