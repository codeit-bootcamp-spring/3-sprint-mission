package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(doNotUseGetters = true, callSuper = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor /* @Builder 때문에 넣어줌 */
@Builder
@Entity
@Table(name = "channels")
public class Channel extends BaseUpdatableEntity {

  //
  @Column(name = "name")
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private ChannelType type;

  @Column(name = "description")
  private String description;

//  // 채널 삭제될때 readStatus 모두 삭제 Q.이게 필요할까?
//  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
//  private List<ReadStatus> readStatuses;
//
//  // 채널 삭제될때 Message 모두 삭제 Q.이게 필요할까?
//  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
//  private List<Message> messages;

  /* FIXME: lastMessageAt는 DB에 없음, entity에서 값을 가지고 있고 message insert 될때마다 값 업데이트한다면? */
  @Transient
  @Setter
  private Instant lastMessageAt;

  /* XXX: ownerId는 DB에 없음 */
  @Transient
  private UUID ownerId;

  public Channel(ChannelType type, String name, String description) {
    this.name = name;
    this.type = type;
    this.description = description;
  }


  public Channel(ChannelType type, String name, String description, UUID ownerId) {
    this.name = name;
    this.type = type;
    this.description = description;
    this.ownerId = ownerId;
  }

  public void update(String newName, String newDescription) {
    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
    }
  }
}
