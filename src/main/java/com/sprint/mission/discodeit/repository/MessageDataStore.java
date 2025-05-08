package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class MessageDataStore implements Serializable {
  private static final long serialVersionUID = 1L;
  private Map<UUID, Message> messageMap = new HashMap<>();
  private Map<UUID, List<Message>> channelMessagesMap = new HashMap<>();
  private Map<UUID, List<Message>> userMessagesMap = new HashMap<>();
}
