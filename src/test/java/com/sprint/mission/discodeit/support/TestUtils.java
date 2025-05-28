package com.sprint.mission.discodeit.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class TestUtils {

  public static HttpHeaders jsonHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  public static String json(String textBlock) {
    return textBlock.stripIndent(); // Java 15+ 텍스트 블록 지원
  }

  public static void deleteDirectory(Path root) throws IOException {
    if (Files.exists(root)) {
      try (Stream<Path> paths = Files.walk(root)) {
        paths.sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(file -> {
              if (!file.delete()) {
                System.err.println("파일 삭제 실패: " + file.getAbsolutePath());
              }
            });
      }
    }
  }
}
