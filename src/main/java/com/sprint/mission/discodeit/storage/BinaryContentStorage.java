package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentStorage {

  /* UUID 키 정보를 바탕으로 byte[] 데이터를 저장합니다. */
  public UUID put(UUID binaryContentId, byte[] bytes);

  /*키 정보를 바탕으로 byte[] 데이터를 읽어 InputStream 타입으로 반환합니다. */
  public InputStream get(UUID binaryContentId);

  /* HTTP API로 다운로드 기능을 제공,BinaryContentDto 정보를 바탕으로 파일을 다운로드할 수 있는 응답을 반환 */
  public ResponseEntity<?> download(BinaryContentDto metaData);

}
