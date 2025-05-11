package com.sprint.mission.discodeit.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class BinaryContentCreateRequest {
    private String fileName;
    private byte[] fileData;
    private String fileType;
}
