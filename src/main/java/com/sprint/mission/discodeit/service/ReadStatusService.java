package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatus create(ReadStatusRequestDTO readStatusRequestDTO);

  ReadStatusResponseDTO findById(UUID id);

  List<ReadStatusResponseDTO> findAll();

  List<ReadStatusResponseDTO> findAllByUserId(UUID userId);

  ReadStatusResponseDTO update(UUID id, ReadStatusUpdateDTO readStatusUpdateDTO);

  void deleteById(UUID id);
}
