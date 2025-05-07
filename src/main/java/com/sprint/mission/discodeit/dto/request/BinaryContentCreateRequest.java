package com.sprint.mission.discodeit.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BinaryContentCreateRequest {
    private String fileName;
    private byte[] fileData;
    private String fileType;
    private long fileSize;
}
