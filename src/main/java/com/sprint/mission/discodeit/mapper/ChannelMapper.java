package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ReadStatusMapper.class,
    MessageMapper.class})
public abstract class ChannelMapper {

//  @Autowired
//  protected UserMapper self; // MapStruct에서 자기 자신 주입 필요시 사용

  public abstract ChannelDto toDto(Channel channel);

  public abstract Channel channelDtoToChannel(ChannelDto channelDto);

}
