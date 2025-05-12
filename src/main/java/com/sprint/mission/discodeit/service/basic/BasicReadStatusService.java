package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.notfound.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundReadStatusException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.exception.alreadyexist.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("basicReadStatusService")
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusRequestDTO readStatusRequestDTO) {
        if (userRepository.findById(readStatusRequestDTO.userId()).isEmpty()) {
            throw new NotFoundUserException();
        }

        if (channelRepository.findById(readStatusRequestDTO.channelId()).isEmpty()) {
            throw new NotFoundChannelException();
        }

        UUID channelId = readStatusRequestDTO.channelId();
        UUID userId = readStatusRequestDTO.userId();

        if (readStatusRepository.findByChannelIdAndUserId(channelId, userId).isPresent()) {
            throw new ReadStatusAlreadyExistsException();
        }

        ReadStatus readStatus = ReadStatusRequestDTO.toEntity(readStatusRequestDTO);

        readStatusRepository.save(readStatus);

        return readStatus;
    }

    @Override
    public ReadStatusResponseDTO findById(UUID id) {
        ReadStatus readStatus = findReadStatus(id);

        return ReadStatus.toDTO(readStatus);
    }

    @Override
    public List<ReadStatusResponseDTO> findAll() {
        return readStatusRepository.findAll().stream()
                .map(ReadStatus::toDTO)
                .toList();
    }

    @Override
    public List<ReadStatusResponseDTO> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::toDTO)
                .toList();
    }

    @Override
    public ReadStatusResponseDTO update(UUID id, ReadStatusRequestDTO readStatusRequestDTO) {
        ReadStatus readStatus = findReadStatus(id);

        readStatus.updateLastReadTime(readStatusRequestDTO.lastReadTime());
        readStatusRepository.save(readStatus);

        return ReadStatus.toDTO(readStatus);
    }

    @Override
    public void deleteById(UUID id) {
        readStatusRepository.deleteById(id);
    }

    private ReadStatus findReadStatus(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(NotFoundReadStatusException::new);
    }
}
