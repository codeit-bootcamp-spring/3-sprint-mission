package com.sprint.mission.discodeit.entity.dto;

import com.sprint.mission.discodeit.entity.FileType;

import java.util.UUID;

public record CreateBinaryContentRequest (
        UUID id,
        String fileName,
        FileType fileType,
        byte[] content
) { }
