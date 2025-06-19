package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@EnableJpaAuditing
public class BinaryContentRepositoryTest {

  @Autowired
  TestEntityManager em;

  @Autowired
  private BinaryContentRepository binaryContentRepository;

  @Test
  void 첨부파일을_저장하고_조회할_수_있다() {
    // given
    BinaryContent content = BinaryContentFixture.createValid();

    // when
    binaryContentRepository.save(content);
    Optional<BinaryContent> result = binaryContentRepository.findById(content.getId());

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(content.getId());
    assertThat(result.get().getFileName()).isEqualTo(content.getFileName());
    assertThat(result.get().getContentType()).isEqualTo(content.getContentType());
    assertThat(result.get().getSize()).isEqualTo(content.getSize());
  }
}
