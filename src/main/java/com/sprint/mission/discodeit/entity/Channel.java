package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.util.List;

public class Channel extends BaseUpdatableEntity {
  private ChannelType type;
  private String name;
  private String description;

  private List<User> participants;
  private List<Message> messages;
  private List<ReadStatus> readStatuses;

  public Channel(ChannelType channelType, String name, String description) {
    super();
  }

  public ChannelType getType() { return type; }
  public void setType(ChannelType type) { this.type = type; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public List<User> getParticipants() { return participants; }
  public void setParticipants(List<User> participants) { this.participants = participants; }

  public List<Message> getMessages() { return messages; }
  public void setMessages(List<Message> messages) { this.messages = messages; }

  public List<ReadStatus> getReadStatuses() { return readStatuses; }
  public void setReadStatuses(List<ReadStatus> readStatuses) { this.readStatuses = readStatuses; }

  public void update(String newName, String newDescription) {
  }
}