package com.sprint.mission.discodeit.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class TestUtils {

  /**
   * JSON 요청을 위한 Content-Type 헤더를 반환합니다.
   * @return Content-Type이 application/json으로 설정된 HttpHeaders
   */
  public static HttpHeaders jsonHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  /**
   * 멀티파트 요청을 위한 Content-Type 헤더를 반환합니다.
   * @return Content-Type이 multipart/form-data로 설정된 HttpHeaders
   */
  public static HttpHeaders multipartHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    return headers;
  }

  /**
   * 텍스트 블록의 들여쓰기를 제거하여 JSON 문자열을 반환합니다.
   * (Java 15+ 텍스트 블록 지원)
   * @param textBlock 텍스트 블록
   * @return 들여쓰기가 제거된 JSON 문자열
   */
  public static String json(String textBlock) {
    return textBlock.stripIndent();
  }

  /**
   * 지정한 디렉터리와 그 하위 모든 파일 및 폴더를 삭제합니다.
   * @param root 삭제할 디렉터리의 경로
   * @throws IOException 파일 삭제 중 오류 발생 시
   */
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

  /**
   * 민감한 값을 마스킹하여 로그 등에 출력할 때 사용합니다.
   * 값이 null 또는 비어있으면 "[비어있음]"을 반환하고,
   * 값이 4글자 이하이면 "****"로 마스킹합니다.
   * 그 외에는 앞 4글자 + "****" + 뒤 4글자 형태로 반환합니다.
   * @param value 마스킹할 값
   * @return 마스킹된 문자열
   */
  public static String maskSensitiveValue(String value) {
    if (value == null || value.isEmpty()) {
      return "[비어있음]";
    }
    if (value.length() <= 4) {
      return "****";
    }
    return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
  }
}
