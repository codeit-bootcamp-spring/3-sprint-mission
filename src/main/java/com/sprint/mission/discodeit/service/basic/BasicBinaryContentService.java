package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundBinaryContentException;
import com.sprint.mission.discodeit.exception.NotFoundMessageException;
import com.sprint.mission.discodeit.exception.NotFoundUserException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("basicBinaryContentService")
@RequiredArgsConstructor
public class BasicBinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    public void save(BinaryContentDTO binaryContentDTO) {
        BinaryContent binaryContent = BinaryContentDTO.toEntity(binaryContentDTO);

        binaryContentRepository.save(binaryContent);
    }

    public BinaryContentResponseDTO findById(UUID id) {
        BinaryContent foundBinaryContent = binaryContentRepository.findById(id)
                .orElseThrow(NotFoundBinaryContentException::new);

        return BinaryContentResponseDTO.toDTO(foundBinaryContent);
    }


    public List<BinaryContentResponseDTO> findAll() {
        return binaryContentRepository.findAll().stream()
                .map(BinaryContentResponseDTO::toDTO)
                .toList();
    }

    public List<BinaryContentResponseDTO> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(BinaryContentResponseDTO::toDTO)
                .toList();
    }

    public void deleteById(UUID id) {
        binaryContentRepository.deleteById(id);
    }
}
