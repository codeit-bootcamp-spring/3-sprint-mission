package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public class Converter {


  //MultipartFile 타입의 요청값을 CreateBinaryContentRequest 타입으로 변환하기 위한 메서드
  public static List<BinaryContentCreateRequest> resolveBinaryContentRequest(
      List<MultipartFile> image) {
    List<BinaryContentCreateRequest> binaryContentCreateRequests = new ArrayList<>();
    //컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
    try {
      for (MultipartFile image1 : image) {
        binaryContentCreateRequests.add(new BinaryContentCreateRequest(
            image1.getOriginalFilename(),
            image1.getContentType(),
            image1.getBytes())
        );
      }
      return binaryContentCreateRequests;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  //MultipartFile 타입의 요청값을 CreateBinaryContentRequest 타입으로 변환하기 위한 메서드
  public static Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
    if (profile.isEmpty()) {
      //컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
      return Optional.empty();
    } else {
      //컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profile.getOriginalFilename(),
            profile.getContentType(),
            profile.getBytes()
        );
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
