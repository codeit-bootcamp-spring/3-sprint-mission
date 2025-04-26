package com.sprint.mission.discodeit.fixture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileFixture {

  private static final String TEST_DATA_DIR = "data/test";
  private static final Logger log = LogManager.getLogger(FileFixture.class);

  /**
   * 테스트 파일을 생성한다. 디렉토리가 없다면 함께 생성한다.
   *
   * @param fileName 파일명
   * @return 생성된 파일 객체
   * @throws IOException 파일 생성 중 오류 발생 시
   */
  public static File createTestFile(String fileName) throws IOException {
    Path dirPath = Paths.get(TEST_DATA_DIR);

    // 디렉토리가 없으면 생성
    if (!Files.exists(dirPath)) {
      Files.createDirectories(dirPath);
      log.debug("테스트 디렉토리 생성: {}", dirPath);
    }

    Path filePath = dirPath.resolve(fileName);
    if (!Files.exists(filePath)) {
      Files.createFile(filePath);
      log.debug("테스트 파일 생성: {}", filePath);
    }

    return filePath.toFile();
  }

  /**
   * 테스트 디렉토리와 그 안의 모든 파일을 삭제한다.
   */
  public static void cleanupTestDirectory() {
    try {
      Path directory = Paths.get(TEST_DATA_DIR);
      if (Files.exists(directory)) {
        Files.walk(directory)
            .sorted((p1, p2) -> -p1.compareTo(p2)) // 역순으로 정렬하여 파일을 먼저 삭제
            .forEach(path -> {
              try {
                Files.delete(path);
                log.debug("삭제됨: {}", path);
              } catch (IOException e) {
                log.error("파일 삭제 중 오류 발생: {}", path, e);
              }
            });
      }
    } catch (IOException e) {
      log.error("디렉토리 삭제 중 오류 발생", e);
    }
  }
}