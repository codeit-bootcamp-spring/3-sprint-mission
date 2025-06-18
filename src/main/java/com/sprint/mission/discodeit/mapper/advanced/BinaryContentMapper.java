package com.sprint.mission.discodeit.mapper.advanced;

import com.sprint.mission.discodeit.dto.binaryContent.JpaBinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper.advanced
 * FileName     : AdvancedBinaryContentMapper
 * Author       : dounguk
 * Date         : 2025. 6. 3.
 */
@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

    JpaBinaryContentResponse toDto(BinaryContent binaryContent);
}
