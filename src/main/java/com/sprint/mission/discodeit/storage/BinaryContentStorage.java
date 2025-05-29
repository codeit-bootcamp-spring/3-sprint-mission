package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

// BinaryContentStorage : 바이너리 데이터의 저장 / 로드를 담당하는 컴포넌트
public interface BinaryContentStorage {

  // UUID 키( BinaryContentId ) 정보를 바탕으롤 byte[] data 저장
  UUID put(UUID id, byte[] data);

  // 키 정보를 바탕으로 byte[] data를 읽어 InputStream 타입으로 반환
  InputStream get(UUID id);

  // HTTP API로 다운로드 기능 제공 : dto 정보를 바탕으로 파일 다운로드 응답 반환
  ResponseEntity<?> download(BinaryContentDto dto);

}
