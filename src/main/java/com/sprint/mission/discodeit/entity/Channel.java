package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Channel implements Serializable {

  private final UUID id;
  private final long createdAt;
  private long updatedAt;
  private String name;

  public Channel(String name) {
    this.id = UUID.randomUUID();
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;
    this.name = name;
  }


  public void updateName(String name) {
    this.name = name;
    this.updatedAt = System.currentTimeMillis();
  }
}
