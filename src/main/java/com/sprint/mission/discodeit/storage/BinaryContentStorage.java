package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

/**
 * 바이너리 콘텐츠의 저장 및 검색을 위한 스토리지 인터페이스입니다.
 * 
 * <p>파일 업로드, 다운로드, 스트리밍 등의 기능을 제공하며,
 * 로컬 파일 시스템, S3, 클라우드 스토리지 등 다양한 구현체를 지원합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>바이너리 데이터 저장</li>
 *   <li>바이너리 데이터 검색</li>
 *   <li>파일 다운로드 응답 생성</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
public interface BinaryContentStorage {

    /**
     * 바이너리 데이터를 스토리지에 저장합니다.
     * 
     * @param binaryContentId 저장할 바이너리 콘텐츠의 ID
     * @param bytes 저장할 바이트 데이터
     * @return 저장된 바이너리 콘텐츠의 ID
     */
    UUID put(UUID binaryContentId, byte[] bytes);

    /**
     * 스토리지에서 바이너리 데이터를 검색합니다.
     * 
     * @param binaryContentId 검색할 바이너리 콘텐츠의 ID
     * @return 바이너리 데이터를 읽을 수 있는 입력 스트림
     */
    InputStream get(UUID binaryContentId);

    /**
     * 바이너리 콘텐츠를 다운로드할 수 있는 HTTP 응답을 생성합니다.
     * 
     * @param binaryContentDto 다운로드할 바이너리 콘텐츠 정보
     * @return 파일 다운로드를 위한 HTTP 응답
     */
    ResponseEntity<?> download(BinaryContentDto binaryContentDto);

}
