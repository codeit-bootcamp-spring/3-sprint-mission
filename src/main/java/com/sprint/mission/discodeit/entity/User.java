package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class User implements Serializable {
  private static final long serialVersionUID = 1L;

  private final UUID id;
  private final Long createdAt;
  private Long updatedAt;

  private String username;
  private String email;

  public User(String username, String email) {
    this.id = UUID.randomUUID();
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = System.currentTimeMillis();

    this.username = username;
    this.email = email;
  }

  public void updateName(String username) {
    this.username = username;
    updatedAt = System.currentTimeMillis();
  }

  public void updateEmail(String email) {
    this.email = email;
    updatedAt = System.currentTimeMillis();
  }

  @Override
  public String toString() {
    return "[" + "username='" + username + ", email='" + email + '\'' + ']';
  }
}
