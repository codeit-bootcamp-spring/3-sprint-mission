package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Entity
@Table(name = "channels")
@Getter
public class Channel extends BaseUpdatableEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

    @Column(nullable = false)
    private String name;

    private String description;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadStatus> readStatuses = new ArrayList<>();

    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Message> messages;

    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void update(String newName, String newDescription) {
        if (newName != null) {
            this.name = newName;
        }
        if (newDescription != null) {
            this.description = newDescription;
        }
    }

    protected Channel() {
    }
}
