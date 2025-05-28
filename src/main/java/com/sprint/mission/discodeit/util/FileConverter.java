package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class FileConverter {

  public static BinaryContentDto resolveFileRequest(MultipartFile multipartFile) {
    if (multipartFile == null || multipartFile.isEmpty()) {
      // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
      return null;
    } else {
      // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
      try {
        BinaryContentDto binaryContentDTO = new BinaryContentDto(
            multipartFile.getOriginalFilename(),
            multipartFile.getContentType(),
            multipartFile.getBytes());

        return binaryContentDTO;
      } catch (IOException e) {
        throw new RuntimeException("파일 '" + multipartFile.getOriginalFilename() + "' 처리 중 오류 발생",
            e);
      }
    }
  }

  public static List<BinaryContentDto> resolveFileRequest(List<MultipartFile> attachedFiles) {
    if (attachedFiles == null || attachedFiles.isEmpty()) {
      return Collections.emptyList();
    }

    List<BinaryContentDto> binaryContentList = new ArrayList<>();
    for (MultipartFile file : attachedFiles) {
      if (file.isEmpty()) {
        continue;
      }
      try {
        binaryContentList.add(new BinaryContentDto(
            file.getOriginalFilename(),
            file.getContentType(),
            file.getBytes()
        ));
      } catch (IOException e) {
        throw new RuntimeException("파일 '" + file.getOriginalFilename() + "' 처리 중 오류 발생", e);
      }
    }

    return binaryContentList;
  }
}
