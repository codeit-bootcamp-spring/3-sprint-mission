package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatusResponseDto create(ReadStatusRequestDto readStatusRequestDto);

  ReadStatusResponseDto findById(UUID id);

  List<ReadStatusResponseDto> findAllByUserId(UUID userId);

  ReadStatusResponseDto update(UUID id, ReadStatusUpdateDto readStatusUpdateDto);

  void deleteById(UUID id);
}
