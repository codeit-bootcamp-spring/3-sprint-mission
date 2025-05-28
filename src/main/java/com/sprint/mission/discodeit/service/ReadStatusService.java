package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatus create(ReadStatusRequestDto readStatusRequestDTO);

  ReadStatusResponseDto findById(UUID id);

  List<ReadStatusResponseDto> findAllByUserId(UUID userId);

  ReadStatusResponseDto update(UUID id, ReadStatusUpdateDto readStatusUpdateDTO);

  void deleteById(UUID id);
}
