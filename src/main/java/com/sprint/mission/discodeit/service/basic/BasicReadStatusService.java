package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.NotFoundReadStatusException;
import com.sprint.mission.discodeit.exception.NotFoundUserException;
import com.sprint.mission.discodeit.exception.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("basicReadStatusService")
@RequiredArgsConstructor
public class BasicReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public void save(ReadStatusRequestDTO readStatusRequestDTO) {
        if (userRepository.findById(readStatusRequestDTO.getUserId()).isEmpty()) {
            throw new NotFoundUserException();
        }

        if (channelRepository.findById(readStatusRequestDTO.getChannelId()).isEmpty()) {
            throw new NotFoundChannelException();
        }

        UUID channelId = readStatusRequestDTO.getChannelId();
        UUID userId = readStatusRequestDTO.getUserId();

        if (readStatusRepository.findByChannelIdAndUserId(channelId, userId).isPresent()) {
            throw new ReadStatusAlreadyExistsException();
        }

        ReadStatus readStatus = ReadStatusRequestDTO.toEntity(readStatusRequestDTO);

        readStatusRepository.save(readStatus);
    }

    public ReadStatusResponseDTO findById(UUID id) {
        ReadStatus readStatus = findReadStatus(id);

        return ReadStatusResponseDTO.toDTO(readStatus);
    }

    public List<ReadStatusResponseDTO> findAll() {
        return readStatusRepository.findAll().stream()
                .map(ReadStatusResponseDTO::toDTO)
                .toList();
    }

    public List<ReadStatusResponseDTO> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatusResponseDTO::toDTO)
                .toList();
    }

    public ReadStatusResponseDTO update(UUID id, ReadStatusRequestDTO readStatusRequestDTO) {
        ReadStatus readStatus = findReadStatus(id);

        readStatus.updateLastReadTime(readStatusRequestDTO.getLastReadTime());
        readStatusRepository.save(readStatus);

        return ReadStatusResponseDTO.toDTO(readStatus);
    }

    public void deleteById(UUID id) {
        readStatusRepository.deleteById(id);
    }

    private ReadStatus findReadStatus(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(NotFoundReadStatusException::new);
    }
}
