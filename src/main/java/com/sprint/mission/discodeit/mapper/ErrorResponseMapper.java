package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ErrorResponseMapper {

    @Mapping(target = "code",          expression = "java(e.getErrorCode().getCode())")
    @Mapping(target = "message",       expression = "java(e.getErrorCode().getMessage())")
    @Mapping(target = "exceptionType", expression = "java(e.getClass().getSimpleName())")
    @Mapping(target = "status",        expression = "java(e.getErrorCode().getStatus().value())")
    ErrorResponse toErrorResponse(DiscodeitException e);
}