package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * ChannelRepository의 JPA 슬라이스 테스트 클래스입니다.
 * <p>
 * DB와 연동하여 채널 관련 JPA 쿼리 동작을 검증합니다.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("ChannelRepository 슬라이스 테스트")
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    private Channel createChannel(String name) {
        Channel channel = new Channel(ChannelType.PUBLIC, name, "desc");
        return channelRepository.save(channel);
    }

    @Nested
    @DisplayName("기본 CRUD")
    class Describe_CRUD {
        /**
         * [성공] 채널을 저장하고 조회할 수 있는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] 채널 저장 및 조회 성공")
        void save_and_find_success() {
            Channel saved = createChannel("test");
            assertThat(channelRepository.findById(saved.getId())).isPresent();
        }
        /**
         * [실패] 존재하지 않는 id로 조회 시 빈 Optional을 반환하는지 검증합니다.
         */
        @Test
        @DisplayName("[실패] 존재하지 않는 id로 조회 시 빈 Optional 반환")
        void findById_fail() {
            assertThat(channelRepository.findById(UUID.randomUUID())).isEmpty();
        }
    }

    @Nested
    @DisplayName("페이징 및 정렬")
    class Describe_pagingAndSorting {
        /**
         * [성공] name 기준 오름차순 정렬 및 페이징이 동작하는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] name 오름차순 정렬 및 페이징")
        void paging_and_sorting() {
            for (int i = 1; i <= 10; i++) {
                createChannel("channel" + i);
            }
            Page<Channel> page = channelRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "name")));
            assertThat(page.getContent()).hasSize(5);
            assertThat(page.getContent().get(0).getName()).isEqualTo("channel1");
        }
    }
} 