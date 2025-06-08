package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

    ReadStatusDTO toDTO(ReadStatus readStatus);
    ReadStatus toEntity(ReadStatusDTO dto);
}
