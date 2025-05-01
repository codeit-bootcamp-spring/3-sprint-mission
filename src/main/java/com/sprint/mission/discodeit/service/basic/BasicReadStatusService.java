package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
}
