package com.sprint.mission.discodeit.service.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentType;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryOwnerType;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.List;
import java.util.UUID;

public class BinaryContentServiceImpl implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    public BinaryContentServiceImpl(BinaryContentRepository binaryContentRepository) {
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public BinaryContent createBinaryContent(BinaryContentCreateRequestDto dto) {
        if (dto.getOwnerType() == BinaryOwnerType.USER) {
            List<BinaryContent> existingList = binaryContentRepository.findAllBinaryContentById(dto.getOwnerId());
            for (BinaryContent content : existingList) {
                if (content.getContentType() == BinaryContentType.PROFILE_IMAGE) {
                    binaryContentRepository.deleteBinaryContentById(content.getId());
                    break;
                }
            }
            return binaryContentRepository.createBinaryContent(dto);

        } else if (dto.getOwnerType() == BinaryOwnerType.MESSAGE) {
            return binaryContentRepository.createBinaryContent(dto);
        }

        throw new IllegalArgumentException("해당 타입을 찾을 수 없습니다. : " + dto.getOwnerType());
    }

    @Override
    public BinaryContent findBinaryContentById(UUID id) {
        return binaryContentRepository.findBinaryContentById(id);
    }

    @Override
    public List<BinaryContent> findAllBinaryContentById(UUID id) {
        return binaryContentRepository.findAllBinaryContentById(id);
    }

    @Override
    public boolean deleteBinaryContentById(UUID id) {
        return binaryContentRepository.deleteBinaryContentById(id);
    }
}
