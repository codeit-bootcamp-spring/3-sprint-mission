package com.sprint.mission.discodeit.entity;

/**
 * 채널의 유형을 나타내는 열거형
 */
public enum ChannelType {
  /**
   * 공개 채널 - 모든 사용자가 접근 가능
   */
  PUBLIC("공개 채널"),

  /**
   * 비공개 채널 - 초대받은 사용자만 접근 가능
   */
  PRIVATE("비공개 채널");

  private final String description;

  ChannelType(String description) {
    this.description = description;
  }

  /**
   * 채널 유형의 설명을 반환합니다.
   * 
   * @return 채널 유형 설명
   */
  public String getDescription() {
    return description;
  }

  /**
   * 공개 채널인지 확인합니다.
   * 
   * @return 공개 채널 여부
   */
  public boolean isPublic() {
    return this == PUBLIC;
  }

  /**
   * 비공개 채널인지 확인합니다.
   * 
   * @return 비공개 채널 여부
   */
  public boolean isPrivate() {
    return this == PRIVATE;
  }
}
