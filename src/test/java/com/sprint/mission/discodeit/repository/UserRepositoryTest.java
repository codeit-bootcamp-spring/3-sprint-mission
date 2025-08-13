package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepository의 JPA 슬라이스 테스트 클래스입니다.
 * <p>
 * DB와 연동하여 사용자 관련 JPA 쿼리 동작을 검증합니다.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("UserRepository 슬라이스 테스트")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User createUser(String username, String email) {
        BinaryContent profile = new BinaryContent("profile.png", 100L, "image/png");
        User user = new User(username, email, "pw1234", profile);
        UserStatus status = new UserStatus(user, Instant.now());
        user.setStatus(status);
        return userRepository.save(user);
    }

    @Nested
    @DisplayName("findByUsername")
    class Describe_findByUsername {
        /**
         * [성공] 존재하는 username이면 User를 반환하는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] username 존재 시 User 반환")
        void when_username_exists_then_return_user() {
            // given
            createUser("testuser", "test@email.com");
            // when
            Optional<User> found = userRepository.findByUsername("testuser");
            // then
            assertThat(found).isPresent();
            assertThat(found.get().getUsername()).isEqualTo("testuser");
        }
        /**
         * [실패] 존재하지 않는 username이면 빈 Optional을 반환하는지 검증합니다.
         */
        @Test
        @DisplayName("[실패] username 미존재 시 빈 Optional 반환")
        void when_username_not_exists_then_return_empty() {
            // when
            Optional<User> found = userRepository.findByUsername("notfound");
            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByEmail & existsByUsername")
    class Describe_exists {
        /**
         * [성공] 존재하는 email/username이면 true를 반환하는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] email/username 존재 시 true 반환")
        void when_exists_then_true() {
            createUser("user1", "user1@email.com");
            assertThat(userRepository.existsByEmail("user1@email.com")).isTrue();
            assertThat(userRepository.existsByUsername("user1")).isTrue();
        }
        /**
         * [실패] 존재하지 않으면 false를 반환하는지 검증합니다.
         */
        @Test
        @DisplayName("[실패] email/username 미존재 시 false 반환")
        void when_not_exists_then_false() {
            assertThat(userRepository.existsByEmail("no@email.com")).isFalse();
            assertThat(userRepository.existsByUsername("nope")).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllWithProfileAndStatus")
    class Describe_findAllWithProfileAndStatus {
        /**
         * [성공] 유저가 있으면 리스트를 반환하는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] 유저 존재 시 리스트 반환")
        void when_users_exist_then_return_list() {
            createUser("userA", "a@email.com");
            createUser("userB", "b@email.com");
            assertThat(userRepository.findAllWithProfileAndStatus()).hasSize(2);
        }
        /**
         * [실패] 유저가 없으면 빈 리스트를 반환하는지 검증합니다.
         */
        @Test
        @DisplayName("[실패] 유저 없음 시 빈 리스트 반환")
        void when_no_users_then_return_empty() {
            assertThat(userRepository.findAllWithProfileAndStatus()).isEmpty();
        }
    }

    @Nested
    @DisplayName("페이징 및 정렬")
    class Describe_pagingAndSorting {
        /**
         * [성공] username 기준 내림차순 정렬 및 페이징이 동작하는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] username 내림차순 정렬 및 페이징")
        void paging_and_sorting() {
            for (int i = 1; i <= 10; i++) {
                createUser("user" + i, "user" + i + "@email.com");
            }
            Page<User> page = userRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "username")));
            assertThat(page.getContent()).hasSize(5);
            assertThat(page.getContent().get(0).getUsername()).isEqualTo("user9");
        }
    }
} 