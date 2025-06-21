package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.ChannelFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
class ChannelTest {

  @Autowired
  TestEntityManager em;

  @Nested
  class Create {

    @Test
    void 채널이_생성되면_기본_정보가_올바르게_설정되어야_한다() {
      String name = "테스트 채널";
      String description = "테스트 채널임";
      Channel channel = Channel.createPublic(name, description);

      em.persist(channel);
      em.flush();
      em.clear();

      assertAll(
          () -> assertThat(channel.getId()).isNotNull(),
          () -> assertThat(channel.getName()).isEqualTo(name),
          () -> assertThat(channel.getDescription()).isEqualTo(description),
          () -> assertThat(channel.getCreatedAt()).isNotNull()
      );
    }

    @Test
    void 채널_타입을_구분해서_생성할_수_있다() {
      String name = "테스트 채널";
      String description = "테스트 채널임";

      Channel publicChannel = Channel.createPublic(name, description);
      Channel privateChannel = Channel.createPrivate();

      assertAll(
          () -> assertThat(publicChannel.getType()).isEqualTo(ChannelType.PUBLIC),
          () -> assertThat(privateChannel.getType()).isEqualTo(ChannelType.PRIVATE)
      );
    }
  }

  @Nested
  class Update {

    private Channel channel;

    @BeforeEach
    void setUp() {
      channel = ChannelFixture.createPublic();
    }

    @Test
    void 채널_이름_수정_시_이름과_수정_시간이_업데이트되어야_한다() {
      String newName = "변경된 채널명";
      channel.updateName(newName);

      em.persist(channel);
      em.flush();
      em.clear();

      assertAll(
          () -> assertThat(channel.getName()).isEqualTo(newName),
          () -> assertThat(channel.getUpdatedAt()).isNotNull()
      );
    }

    @ParameterizedTest
    @ValueSource(strings = {"감자", "왕감자", "고구마"})
    void 채널_이름_수정_테스트_여러_데이터(String newName) {
      channel.updateName(newName);

      em.persist(channel);
      em.flush();
      em.clear();

      assertAll(
          () -> assertThat(channel.getName()).isEqualTo(newName),
          () -> assertThat(channel.getUpdatedAt()).isNotNull()
      );
    }
  }
}
