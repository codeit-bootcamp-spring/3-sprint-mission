package com.sprint.mission.discodeit.fixture;

import static com.sprint.mission.discodeit.fixture.FileFixture.createTestFile;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileFixtureTest {

  private static final String TEST_FILE_NAME = "test.ser";
  private static final TestObject TEST_OBJECT = new TestObject("테스트 데이터입니다");
  private static final Logger log = LogManager.getLogger(FileFixtureTest.class);
  private static final Path TEST_DIRECTORY = FileFixture.getTestDirectory();

  @BeforeEach
  void setUp() {
    FileFixture.cleanupTestDirectory();
  }

  @AfterEach
  void tearDown() {
    FileFixture.cleanupTestDirectory();
  }

  @Test
  @DisplayName("직렬화 및 역직렬화된 객체가 원본과 동일해야 한다")
  void writeAndReadSerializedObject() throws IOException, ClassNotFoundException {
    // 테스트용 파일을 생성한다 (덮어쓰기 허용)
    var file = createTestFile(TEST_FILE_NAME, true);

    // 파일에 직렬화된 객체를 쓴다
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(TEST_OBJECT);
      log.debug("직렬화된 객체를 파일에 저장: {}", file.getAbsolutePath());
    } catch (IOException e) {
      log.error("직렬화 중 오류 발생", e);
      throw e;
    }

    // 파일에서 객체를 읽어온다
    TestObject readObject;
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      readObject = (TestObject) ois.readObject();
      log.debug("파일에서 객체를 읽음: {}", file.getAbsolutePath());
    } catch (IOException | ClassNotFoundException e) {
      log.error("역직렬화 중 오류 발생", e);
      throw e;
    }

    // 읽어온 객체의 내용이 원본과 동일해야 한다
    assertThat(readObject.content()).as("직렬화된 객체의 내용이 원본과 일치해야 한다.")
        .isEqualTo(TEST_OBJECT.content());
  }

  /**
   * 직렬화를 위한 테스트용 클래스
   */
  record TestObject(String content) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1837564992930675588L;
  }
}
