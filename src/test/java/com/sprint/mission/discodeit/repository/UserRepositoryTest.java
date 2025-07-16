package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.AppConfig;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository 슬라이스 테스트")
@Import(AppConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("유저 저장하고 조회 - 성공(프로필이미지 있음)")
    void save() {
        // Given
        BinaryContent profile = new BinaryContent("profile", null, "png");
        User user = new User("조현아", "zzo@email.com", "password123!", profile);

        // When
        userRepository.save(user);
        Optional<User> result = userRepository.findById(user.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("조현아");
        assertThat(result.get().getEmail()).isEqualTo("zzo@email.com");
        assertThat(result.get().getPassword()).isEqualTo("password123!");
        assertThat(result.get().getProfile()).isEqualTo(profile);
    }
}