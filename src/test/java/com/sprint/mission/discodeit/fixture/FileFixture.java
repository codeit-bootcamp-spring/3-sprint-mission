package com.sprint.mission.discodeit.fixture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileFixture {

  private static final String TEST_DATA_DIR = "data/test";
  private static final Logger log = LogManager.getLogger(FileFixture.class);

  /**
   * 기본 테스트 파일명.
   */
  public static final String DEFAULT_TEST_FILE_NAME = "test-file.txt";

  /**
   * 테스트 디렉토리를 반환한다.
   */
  public static Path getTestDirectory() {
    return Paths.get(TEST_DATA_DIR);
  }

  /**
   * 테스트 파일을 생성한다. 디렉토리가 없다면 함께 생성한다.
   *
   * @param fileName  파일명
   * @param overwrite true면 기존 파일이 존재할 경우 덮어쓴다
   * @return 생성된 파일 객체
   * @throws IOException 파일 생성 중 오류 발생 시
   */
  public static File createTestFile(String fileName, boolean overwrite) throws IOException {
    Path dirPath = getTestDirectory();

    // 디렉토리가 없으면 생성
    if (!Files.exists(dirPath)) {
      Files.createDirectories(dirPath);
      log.debug("테스트 디렉토리 생성: {}", dirPath);
    }

    Path filePath = dirPath.resolve(fileName);

    if (Files.exists(filePath)) {
      if (overwrite) {
        Files.delete(filePath);
        Files.createFile(filePath);
        log.debug("테스트 파일 덮어쓰기: {}", filePath);
      } else {
        log.debug("기존 테스트 파일이 존재함: {}", filePath);
      }
    } else {
      Files.createFile(filePath);
      log.debug("테스트 파일 생성: {}", filePath);
    }

    return filePath.toFile();
  }

  /**
   * 테스트 파일을 생성하고 샘플 데이터를 쓴다.
   *
   * @param fileName  파일명
   * @param content   쓸 내용 (byte 배열)
   * @param overwrite true면 기존 파일을 덮어쓴다
   * @return 생성된 파일 객체
   * @throws IOException 파일 생성 또는 쓰기 중 오류 발생 시
   */
  public static File createTestFileWithContent(String fileName, byte[] content, boolean overwrite)
      throws IOException {
    File file = createTestFile(fileName, overwrite);
    Path filePath = file.toPath();
    Files.write(filePath, content, StandardOpenOption.TRUNCATE_EXISTING);
    log.debug("테스트 파일에 데이터 작성: {}", filePath);
    return file;
  }

  public static void cleanupTestDirectory() {
    try {
      Path directory = getTestDirectory();
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

        // 디렉토리까지 삭제 (빈 디렉토리도 삭제)
        Path parentDirectory = directory.getParent();
        if (Files.exists(directory) && Files.isDirectory(directory)) {
          Files.delete(directory);
          log.debug("삭제됨: {}", directory);
        }

        // 상위 디렉토리인 `data`도 삭제
        if (parentDirectory != null && Files.exists(parentDirectory) && Files.isDirectory(
            parentDirectory) && isDirectoryEmpty(parentDirectory)) {
          Files.delete(parentDirectory);
          log.debug("삭제됨: {}", parentDirectory);
        }
      }
    } catch (IOException e) {
      log.error("디렉토리 삭제 중 오류 발생", e);
      throw new RuntimeException("테스트 디렉토리 정리 중 오류 발생", e);
    }
  }

  /**
   * 주어진 디렉토리가 비어 있는지 확인한다.
   *
   * @param directory 확인할 디렉토리
   * @return 디렉토리가 비어 있으면 true, 아니면 false
   */
  private static boolean isDirectoryEmpty(Path directory) throws IOException {
    try (var stream = Files.list(directory)) {
      return stream.findAny().isEmpty(); // 디렉토리 내에 파일이 없으면 true
    }
  }
}
