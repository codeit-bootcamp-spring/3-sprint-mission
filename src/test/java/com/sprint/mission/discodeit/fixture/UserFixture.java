package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

public class UserFixture {

    public static User createUser(String userName, String email, String password) {
        User user = new User(userName, email, password, null, null);
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        return user;
    }

    public static UserDto createUserDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), null,
                true);
    }

    public static UserDto createUserDto(User user, BinaryContent profile) {
        BinaryContentDto profileDto = new BinaryContentDto(profile.getId(), profile.getFileName(),
                profile.getSize(), profile.getContentType());
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), profileDto, true);
    }
}
