package com.sprint.mission.discodeit.config.custom;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

/**
 * record 객체의 타입 정보를 자동으로 붙이는 커스텀 TypeResolver
 * <p>
 * - record는 FINAL 타입이지만 직렬화 시 타입 정보를 저장할 수 있게 커스터마이징
 */
public class RecordSupportingTypeResolver extends DefaultTypeResolverBuilder {

  public RecordSupportingTypeResolver(DefaultTyping t, PolymorphicTypeValidator ptv) {
    super(t, ptv);
  }

  @Override
  public boolean useForType(JavaType t) {
    boolean isRecord = t.getRawClass().isRecord();
    boolean superResult = super.useForType(t);
    if (isRecord) {
      return true;
    }
    return superResult;
  }
}
