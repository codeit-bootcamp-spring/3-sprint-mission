package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.util.List;

public class User extends BaseUpdatableEntity {
  public String username;
  public String email;
  public String password;
  public BinaryContent profile;
  public UserStatus status;

  public List<Channel> channels;
  public List<ReadStatus> readStatuses;
  public List<Message> messages;

  public String getUsername() {
    return username;
  }
}
