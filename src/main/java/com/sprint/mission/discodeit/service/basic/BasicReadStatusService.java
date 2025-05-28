package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDTO;
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
import org.springframework.transaction.annotation.Transactional;

@Service("basicReadStatusService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  @Transactional
  public ReadStatus create(ReadStatusRequestDTO readStatusRequestDTO) {
    UUID userId = readStatusRequestDTO.userId();
    UUID channelId = readStatusRequestDTO.channelId();

    if (!userRepository.existsById(userId)) {
      throw new NotFoundUserException();
    }

    if (!channelRepository.existsById(channelId)) {
      throw new NotFoundChannelException();
    }

    if (readStatusRepository.existsByUserIdAndChannelId(userId, channelId)) {
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
  public List<ReadStatusResponseDTO> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::toDTO)
        .toList();
  }

  @Override
  @Transactional
  public ReadStatusResponseDTO update(UUID id, ReadStatusUpdateDTO readStatusUpdateDTO) {
    ReadStatus readStatus = findReadStatus(id);

    readStatus.updateLastReadAt(readStatusUpdateDTO.newLastAt());
    readStatusRepository.save(readStatus);

    return ReadStatus.toDTO(readStatus);
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    readStatusRepository.deleteById(id);
  }

  private ReadStatus findReadStatus(UUID id) {
    return readStatusRepository.findById(id)
        .orElseThrow(NotFoundReadStatusException::new);
  }
}
