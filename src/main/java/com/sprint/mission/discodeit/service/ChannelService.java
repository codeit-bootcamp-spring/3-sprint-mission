package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto create(@Valid PublicChannelCreateRequest request);

  ChannelDto create(@Valid PrivateChannelCreateRequest request);

  ChannelDto find(@NotNull UUID channelId);

  List<ChannelDto> findAllByUserId(@NotNull UUID userId);

  ChannelDto update(@NotNull UUID channelId, @Valid PublicChannelUpdateRequest request);

  void delete(@NotNull UUID channelId);
}