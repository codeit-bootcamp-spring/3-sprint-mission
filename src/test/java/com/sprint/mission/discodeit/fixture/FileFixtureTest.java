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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileFixtureTest {

  private static final String TEST_FILE_NAME = "test.ser";
  private static final TestObject TEST_OBJECT = new TestObject("테스트테스트테스트테스트");
  private static final Logger log = LogManager.getLogger(FileFixtureTest.class);
  private static final String TEST_DATA_DIR = "data/test";

  @BeforeEach
  void setUp() {
    FileFixture.cleanupTestDirectory();
  }

  @AfterEach
  void tearDown() {
    FileFixture.cleanupTestDirectory();
  }

  @Test
  void writeAndReadSerializedObject() throws IOException, ClassNotFoundException {
    // given
    var file = createTestFile(TEST_FILE_NAME);

    // when
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(TEST_OBJECT);
    }

    TestObject readObject;
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      readObject = (TestObject) ois.readObject();
    }

    // then
    assertThat(readObject.content()).as("직렬화된 객체의 내용이 원본과 일치해야 한다.")
        .isEqualTo(TEST_OBJECT.content());
  }


  // 직렬화를 위한 테스트 클래스
  // record: 접근자, 생성자 등 자동으로 제공함 짱짱임
  record TestObject(String content) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1837564992930675588L;
  }
}