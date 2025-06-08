package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor
@AllArgsConstructor /* @Builder 때문에 넣어줌 */
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User extends BaseUpdatableEntity implements Serializable {

  private static final Long serialVersionUID = 1L;
  //
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  // 유저 삭제될때 profile 삭제
  @OneToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;

  @OneToOne(mappedBy = "user")
  private UserStatus status;

  // 유저 삭제될때 readStatus 모두 삭제
  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<ReadStatus> readStatuses;

  // 유저 삭제될때 message 는 삭제 하면 안됨. null값 허용함.
  @OneToMany(mappedBy = "user")
  private List<Message> Messages;


  public User(String username, String email, String password, BinaryContent profile) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }

  public void update(String username, String email, String password, BinaryContent profile) {
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
    if (profile != null && !profile.equals(this.profile)) {
      this.profile = profile;
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

    String createdAtFormatted = (createdAt != null) ? formatter.format(createdAt) : null;
    String updatedAtFormatted = (updatedAt != null) ? formatter.format(updatedAt) : null;

    return "🙋‍♂️ User {\n" +
        "  id         = " + id + "\n" +
        "  createdAt  = " + createdAtFormatted + "\n" +
        "  updatedAt  = " + updatedAtFormatted + "\n" +
        "  username       = '" + username + "'\n" +
        "  email       = '" + email + "'\n" +
        "  password       = '" + password + "'\n" +
        "  profile       = '" + profile + "'\n" +
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
