package com.sprint.mission.discodeit.mapper.advanced;

import com.sprint.mission.discodeit.dto.binaryContent.JpaBinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper.advanced
 * FileName     : AdvancedBinaryContentMapper
 * Author       : dounguk
 * Date         : 2025. 6. 3.
 */
@Mapper(componentModel = "spring")
public interface AdvancedBinaryContentMapper {
    AdvancedBinaryContentMapper INSTANCE = Mappers.getMapper(AdvancedBinaryContentMapper.class);

    JpaBinaryContentResponse toDto(BinaryContent binaryContent);
}
