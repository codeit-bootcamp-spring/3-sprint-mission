package com.sprint.mission.discodeit.service.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BinaryContentServiceImpl implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    public BinaryContentServiceImpl(BinaryContentRepository binaryContentRepository) {
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public BinaryContent createBinaryContent(BinaryContentCreateRequestDto dto) {

        if (dto.getData() == null || dto.getData().isEmpty()) {
            throw new IllegalArgumentException("바이너리 컨텐츠 데이터가 없습니다.");
        }

        BinaryContent lastCreated = null;
        for (byte[] fileData : dto.getData()) {
            BinaryContent binaryContent = new BinaryContent(
                    dto.getOwnerId(),
                    dto.getContentType(),
                    dto.getFileContentType(),
                    fileData
            );
            lastCreated = binaryContentRepository.createBinaryContent(binaryContent);
        }

        return lastCreated;
    }


    @Override
    public BinaryContent findProfileImageByOwnerId(UUID ownerId) {
        BinaryContent profileImage = binaryContentRepository.findProfileImageByOwnerId(ownerId);
        if (profileImage == null) {
            throw new NoSuchElementException("프로필 이미지를 찾을 수 없습니다: " + ownerId);
        }
        return profileImage;
    }


    @Override
    public List<BinaryContent> findAllAttachmentsByOwnerId(UUID ownerId) {
        return binaryContentRepository.findAllAttachmentsByOwnerId(ownerId);
    }

    @Override
    public boolean deleteBinaryContentById(UUID id) {
        return binaryContentRepository.deleteBinaryContentById(id);
    }
}
