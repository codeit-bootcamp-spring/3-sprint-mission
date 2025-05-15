package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/read-status")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  /* 특정 채널의 메세지 수신 정보 생성 */
  @RequestMapping(method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<ReadStatus> createReadStatus(
      @RequestBody ReadStatusCreateRequest readStatusCreateRequest
  ) {
    ReadStatus createdReadStatus = readStatusService.create(readStatusCreateRequest);
    return ResponseEntity.created(URI.create(createdReadStatus.getId().toString()))
        .body(createdReadStatus);
  }


  /* 특정 채널의 메세지 수신 정보 수정 */
  @RequestMapping(path = "{readStatusId}", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<ReadStatus> update(
      @PathVariable String readStatusId,
      @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest
  ) {
    ReadStatus updatedReadStatus = readStatusService.update(parseStringToUuid(readStatusId),
        readStatusUpdateRequest);
    return ResponseEntity.ok().body(updatedReadStatus);
  }

  /* 특정 사용자의 메세지 수신 정보 조회 */
  @RequestMapping(path = "{userId}", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<ReadStatus>> findAllByUserId(
      @PathVariable String userId
  ) {
    List<ReadStatus> ReadStatusList = readStatusService.findAllByUserId(parseStringToUuid(userId));
    return ResponseEntity.ok().body(ReadStatusList);
  }

  //FIXME : util로 빼기 (모든 컨트롤러에서 중복됨)
  /* MultipartFile 타입 -> BinaryContentCreateRequest 타입으로 변경 */
  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
    if (profile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profile.getOriginalFilename(), profile.getContentType(), profile.getBytes());
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException();
      }
    }
  }

  //FIXME : util로 빼기 (모든 컨트롤러에서 중복됨)
  /* String 타입 -> UUID 타입으로 변경 */
  private UUID parseStringToUuid(String id) {
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("올바른 파라미터 형식이 아닙니다.");
    }
  }

}
