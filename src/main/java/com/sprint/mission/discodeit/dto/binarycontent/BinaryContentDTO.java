package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Getter;

@Getter
public class BinaryContentDTO {
    private final String fileName;
    private final String mimeType;
    private final byte[] data;

    public BinaryContentDTO(String fileName, String mimeType, byte[] data) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.data = data;
    }

    public static BinaryContent toEntity(BinaryContentDTO binaryContentDTO) {
        BinaryContent binaryContent = new BinaryContent();

        binaryContent.updateFileName(binaryContentDTO.getFileName());
        binaryContent.updateMimeType(binaryContentDTO.getMimeType());
        binaryContent.updateData(binaryContentDTO.getData());

        return binaryContent;
    }
}
