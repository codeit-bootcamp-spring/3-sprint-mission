package com.sprint.mission.discodeit.config.custom;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * 캐시 데이터 타입 정보를 안전하게 포함하는 Wrapper 클래스
 * <p>
 * - 서비스에서 래핑/컨트롤러에서 언래핑
 */
public class CacheWrapper {

  public Object getData() {
    return cached;
  }

  @JsonTypeInfo(use = Id.CLASS, include = As.WRAPPER_ARRAY)
  public Object cached;

  public CacheWrapper(Object cached) {
    this.cached = cached;
  }

  public CacheWrapper() {
  }
}
