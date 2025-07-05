package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ChannelTest {

  @Nested
  class Create {

    @Test
    void 채널이_생성되면_기본_정보가_올바르게_설정되어야_한다() {
      Channel channel = ChannelFixture.createPublic();

      assertAll(
          () -> assertThat(channel.getId()).isNotNull(),
          () -> assertThat(channel.getName()).isEqualTo(ChannelFixture.DEFAULT_CHANNEL_NAME),
          () -> assertThat(channel.getCreatedAt()).isNotNull()
      );
    }

    @Test
    void DTO_파라미터_사용과_채널_타입을_구분해서_생성할_수_있다() {
      PublicChannelCreateRequest publicDto = new PublicChannelCreateRequest("공개 채널", "공개 채널 설명");
      PrivateChannelCreateRequest privateDto = new PrivateChannelCreateRequest(List.of());

      Channel publicChannel = ChannelFixture.createPublic(publicDto);
      Channel privateChannel = ChannelFixture.createPrivate(privateDto);

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

      assertAll(
          () -> assertThat(channel.getName()).isEqualTo(newName),
          () -> assertThat(channel.getUpdatedAt()).isNotNull()
      );
    }

    @ParameterizedTest
    @ValueSource(strings = {"감자", "왕감자", "고구마"})
    void 채널_이름_수정_테스트_여러_데이터(String newName) {
      channel.updateName(newName);

      assertAll(
          () -> assertThat(channel.getName()).isEqualTo(newName),
          () -> assertThat(channel.getUpdatedAt()).isNotNull()
      );
    }
  }
}
