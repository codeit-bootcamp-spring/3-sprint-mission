package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Getter
@Setter
public class BinaryContentResponseDTO {

    private UUID id;
    private Instant createdAt;
    private String fileName;
    private String mimeType;
    private byte[] data;

    public BinaryContentResponseDTO() {
    }

    public static BinaryContentResponseDTO toDTO(BinaryContent binaryContent) {
        BinaryContentResponseDTO binaryContentResponseDTO = new BinaryContentResponseDTO();

        binaryContentResponseDTO.setId(binaryContent.getId());
        binaryContentResponseDTO.setCreatedAt(binaryContent.getCreatedAt());
        binaryContentResponseDTO.setFileName(binaryContent.getFileName());
        binaryContentResponseDTO.setMimeType(binaryContent.getMimeType());
        binaryContentResponseDTO.setData(binaryContent.getData());


        return binaryContentResponseDTO;
    }

    @Override
    public String toString() {
        return "BinaryContentResponseDTO{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", fileName='" + fileName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
