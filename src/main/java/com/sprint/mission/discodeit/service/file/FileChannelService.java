package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.Dto.channel.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.file
 * fileName       : FileChannelService2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
@Service
@Profile("file")
@RequiredArgsConstructor
public class FileChannelService {
    private final FileChannelRepository fileChannelRepository;

//
//    // empty
//    @Override
//    public List<ChannelFindResponse> findAllByUserId(ChannelFindByUserIdRequest request) {
//        return List.of();
//    }
//    // empty
//    @Override
//    public ChannelFindResponse find(ChannelFindRequest request) {
//        return null;
//    }
//    // empty
//    @Override
//    public ChannelCreateResponse createChannel(PrivateChannelCreateRequest request) {
//        return null;
//    }
//    // empty
//    @Override
//    public ChannelCreateResponse createChannel(PublicChannelCreateRequest request) {
//        return null;
//    }
//    // empty
//    @Override
//    public void update(ChannelUpdateRequest request) {}
//
//
//    @Override
//    public void deleteChannel(UUID channelId) {
//        Objects.requireNonNull(channelId, "no channelId: FileChannelService.deleteChannel");
//        fileChannelRepository.deleteChannel(channelId);
//    }
}
