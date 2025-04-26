package com.sprint.mission.discodeit.testutil;

public class MemoryUtil {

  /**
   * JVM 현재 메모리 사용량을 로깅한다.
   *
   * @param message 로그 메시지
   */
  public static void logMemoryUsage(String message) {
    long totalMemory = Runtime.getRuntime().totalMemory();
    long freeMemory = Runtime.getRuntime().freeMemory();
    long usedMemory = totalMemory - freeMemory;
    System.out.printf("[%s] Used Memory: %d bytes (%.2f MB)%n", message, usedMemory,
        usedMemory / (1024.0 * 1024.0));
  }

  /**
   * JVM의 메모리 사용량(바이트 단위)을 반환한다.
   *
   * @return 사용 중인 메모리 크기 (bytes)
   */
  public static long getUsedMemory() {
    return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
  }

  /**
   * JVM의 총 할당 메모리 크기(바이트 단위)
   *
   * @return 총 메모리 크기 (bytes)
   */
  public static long getTotalMemory() {
    return Runtime.getRuntime().totalMemory();
  }

  /**
   * JVM의 여유 메모리 크기(바이트 단위)
   *
   * @return 여유 메모리 크기 (bytes)
   */
  public static long getFreeMemory() {
    return Runtime.getRuntime().freeMemory();
  }

  /**
   * JVM이 사용 가능한 최대 메모리 크기(바이트 단위)
   *
   * @return 최대 메모리 크기 (bytes)
   */
  public static long getMaxMemory() {
    return Runtime.getRuntime().maxMemory();
  }
}
