package com.sprint.mission.discodeit.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class User {
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

  public String getCreatedAtFormatted() {
    return formatDate(this.createdAt);
  }

  private String formatDate(Long timestamp) {
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
    return sdf.format(new Date(timestamp));
  }

  @Override
  public String toString() {
    return "[" +
        "createdAt= " + getCreatedAtFormatted() +
        ", updatedAt= " + updatedAt +
        ", username='" + username + '\'' +
        ", email='" + email + '\'' +
        ']';
  }
}
