package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    /**
     * 새 ReadStatus 생성
     * - 관련된 Channel이나 User가 존재하지 않으면 예외 발생
     * - 같은 Channel과 User와 관련된 객체가 이미 존재하면 예외 발생
     * @param request ReadStatus 생성 요청 DTO
     * @return 생성된 ReadStatus 객체
     */
    ReadStatus createReadStatus(ReadStatusCreateRequest request);

    /**
     * ID로 ReadStatus 조회
     * @param readStatusId 조회할 ReadStatus의 ID
     * @return 조회된 ReadStatus 객체 (없으면 예외 발생)
     */
    ReadStatus findReadStatusById(UUID readStatusId);

    /**
     * 특정 사용자의 모든 ReadStatus 조회
     * @param userId 사용자 ID
     * @return 해당 사용자의 ReadStatus 목록
     */
    List<ReadStatus> findAllReadStatusesByUserId(UUID userId);

    /**
     * ReadStatus 정보 업데이트 (마지막 읽은 시간 등)
     * @param readStatusId 수정할 ReadStatus의 ID
     * @param request ReadStatus 업데이트 요청 DTO
     * @return 수정된 ReadStatus 객체
     */
    ReadStatus updateReadStatus(UUID readStatusId, ReadStatusUpdateRequest request);

    /**
     * ID로 ReadStatus 삭제
     * @param readStatusId 삭제할 ReadStatus의 ID
     */
    void deleteReadStatus(UUID readStatusId);
}
