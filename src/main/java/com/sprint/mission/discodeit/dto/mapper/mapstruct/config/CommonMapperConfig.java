package com.sprint.mission.discodeit.dto.mapper.mapstruct.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct 매퍼들의 공통 설정
 * 
 * - componentModel = "spring": Spring Bean으로 자동 등록
 * - unmappedTargetPolicy = ERROR: 매핑되지 않은 필드가 있으면 컴파일 에러
 * - nullValuePropertyMappingStrategy = IGNORE: null 값은 매핑하지 않음
 */
@MapperConfig(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommonMapperConfig {
  // 공통 설정만 정의하는 인터페이스
  // 실제 매핑 메서드는 각각의 매퍼 인터페이스에서 정의
}