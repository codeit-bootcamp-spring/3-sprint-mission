package com.sprint.mission.discodeit.mapper.struct;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BinaryContentStructMapper {

    BinaryContentResponseDto toDto(BinaryContent binaryContent);

    @BeanMapping(ignoreUnmappedSourceProperties = {"bytes"})
    @Mapping(target = "id", ignore = true)
    BinaryContent toEntity(BinaryContentDto binaryContentDto);
}
