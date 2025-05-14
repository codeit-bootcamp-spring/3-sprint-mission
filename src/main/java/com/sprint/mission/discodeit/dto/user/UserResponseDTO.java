package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserResponseDTO {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String name;
    private String email;
    private UUID profileId;
    private String introduction;
    private boolean isLogin;
    private List<UUID> friends;
    private List<UUID> channels;
    private List<UUID> messages;

    public UserResponseDTO() {
    }

    public static UserResponseDTO toDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setId(user.getId());
        userResponseDTO.setCreatedAt(user.getCreatedAt());
        userResponseDTO.setUpdatedAt(user.getUpdatedAt());
        userResponseDTO.setName(user.getName());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setProfileId(user.getProfileId());
        userResponseDTO.setIntroduction(user.getIntroduction());
        userResponseDTO.setLogin(user.isLogin());
        userResponseDTO.setFriends(user.getFriends());
        userResponseDTO.setChannels(user.getChannels());
        userResponseDTO.setMessages(user.getMessages());

        return userResponseDTO;
    }

    @Override
    public String toString() {
        return "UserResponseDTO{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profileId=" + profileId +
                ", introduction='" + introduction + '\'' +
                ", isLogin=" + isLogin +
                ", friends=" + friends +
                ", channels=" + channels +
                ", messages=" + messages +
                '}';
    }
}
