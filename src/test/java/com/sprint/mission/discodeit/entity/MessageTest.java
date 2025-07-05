package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest

public class MessageTest {

  @Autowired
  TestEntityManager em;

  private User author;
  private Channel channel;

  @BeforeEach
  public void setUp() {
    author = UserFixture.createValidUser();
    channel = ChannelFixture.createPublic();
    em.persist(author);
    em.persist(channel);
  }

  @Nested
  class Create {

    @Test
    void 메시지가_생성되면_기본_정보가_올바르게_설정되어야_한다() {
      String content = "테스트 메시지";
      Message message = Message.create(content, author, channel);

      em.persist(message);
      em.flush();
      em.clear();

      assertAll(
          () -> assertThat(message.getId()).isNotNull(),
          () -> assertThat(message.getContent()).isEqualTo(content),
          () -> assertThat(message.getAuthor()).isNotNull(),
          () -> assertThat(message.getChannel()).isNotNull(),
          () -> assertThat(message.getCreatedAt()).isNotNull()
      );
    }

    @Test
    void 다양한_메시지_내용으로_생성해도_올바르게_설정되어야_한다() {
      String specialContent = "특수문자!@#$%^&*() 포함 메시지";
      var message = Message.create(specialContent, author, channel);

      em.persist(message);
      em.flush();
      em.clear();

      assertAll(
          () -> assertThat(message.getContent()).isEqualTo(specialContent),
          () -> assertThat(message.getAuthor()).isEqualTo(author),
          () -> assertThat(message.getChannel()).isEqualTo(channel)
      );
    }
  }

  @Nested
  class Read {

    @Test
    void 서로_다른_사용자의_메시지는_독립적이어야_한다() {
      var author1 = UserFixture.createValidUser();
      var author2 = UserFixture.createValidUser();

      em.persist(author1);
      em.persist(author2);

      var message1 = MessageFixture.createCustom("우왕굳", author1, channel);
      var message2 = MessageFixture.createCustom("우왕굳2", author2, channel);

      em.persist(message1);
      em.persist(message2);
      em.flush();
      em.clear();

      assertAll(
          () -> assertThat(message1.getId()).isNotEqualTo(message2.getId()),
          () -> assertThat(message1.getAuthor()).isNotEqualTo(message2.getAuthor())
      );
    }
  }
}
