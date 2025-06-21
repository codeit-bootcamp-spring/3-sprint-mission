package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

public interface BinaryContentStorage {

  /**
   * UUID 키 정보를 바탕으로 byte[] 데이터를 저장합니다.
   * 
   * @param id    BinaryContent의 Id
   * @param bytes 저장할 바이너리 데이터
   * @return 저장된 파일의 UUID
   */
  UUID put(UUID id, byte[] bytes);

  /**
   * 키 정보를 바탕으로 byte[] 데이터를 읽어 InputStream 타입으로 반환합니다.
   * 
   * @param id BinaryContent의 Id
   * @return 파일의 InputStream
   */
  InputStream get(UUID id);

  /**
   * HTTP API로 다운로드 기능을 제공합니다.
   * 
   * @param binaryContentDto BinaryContentDto 정보
   * @return 파일을 다운로드할 수 있는 응답
   */
  ResponseEntity<?> download(BinaryContentDto binaryContentDto);
}
