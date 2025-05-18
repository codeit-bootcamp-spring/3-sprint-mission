package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class User implements Serializable {

  private static final Long serialVersionUID = 1L;
  //
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  //
  private String username;
  private String email;
  private String password;
  //
  private UUID profileId; // BinaryContent의 id

  public User(String username, String email, String password, UUID profileId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
    //
    this.username = username;
    this.email = email;
    this.password = password;
    //
    this.profileId = profileId;
  }

  public void update(String username, String email, String password, UUID profileId) {
    boolean anyValueUpdated = false;
    if (username != null && !username.equals(this.username)) {
      this.username = username;
      anyValueUpdated = true;
    }
    if (email != null && !email.equals(this.email)) {
      this.email = email;
      anyValueUpdated = true;
    }
    if (password != null && !password.equals(this.password)) {
      this.password = password;
      anyValueUpdated = true;
    }
    if (profileId != null && !profileId.equals(this.profileId)) {
      this.profileId = profileId;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();

    }
  }


  @Override
  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault());

    String createdAtFormatted = formatter.format(createdAt);
    String updatedAtFormatted = formatter.format(updatedAt);

    return "🙋‍♂️ User {\n" +
        "  id         = " + id + "\n" +
        "  createdAt  = " + createdAtFormatted + "\n" +
        "  updatedAt  = " + updatedAtFormatted + "\n" +
        "  username       = '" + username + "'\n" +
        "  email       = '" + email + "'\n" +
        "  password       = '" + password + "'\n" +
        "  profileId       = '" + profileId + "'\n" +
        "}";
  }

  // REF : https://www.baeldung.com/java-equals-hashcode-contracts
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof User)) {
      return false;
    }
    User other = (User) o;
    boolean idEquals = (this.id == null && other.id == null)
        || (this.id != null && this.id.equals(other.id));
    boolean usernameEquals = (this.username == null && other.username == null)
        || (this.username != null && this.username.equals(other.username));
    boolean emailEquals = (this.email == null && other.email == null)
        || (this.email != null && this.email.equals(other.email));

    return idEquals && usernameEquals && emailEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.email, this.username);
  }
}
