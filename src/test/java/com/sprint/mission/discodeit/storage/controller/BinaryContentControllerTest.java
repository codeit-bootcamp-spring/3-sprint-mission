package com.sprint.mission.discodeit.storage.controller;

import com.sprint.mission.discodeit.controller.BinaryContentController;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BinaryContentController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = ".*\\.security\\.jwt\\..*"))
@AutoConfigureMockMvc(addFilters = false)
class BinaryContentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  // ObjectMapper는 현재 테스트에서 사용하지 않음

  @MockitoBean
  private BinaryContentService binaryContentService;

  @MockitoBean
  private BinaryContentStorage binaryContentStorage;

  @Test
  @DisplayName("바이너리 컨텐츠 조회 성공 테스트")
  void find_Success() throws Exception {
    // Given
    UUID binaryContentId = UUID.randomUUID();
    BinaryContentDto binaryContent = new BinaryContentDto(
        binaryContentId,
        "test.jpg",
        10240L,
        MediaType.IMAGE_JPEG_VALUE,
        BinaryContentStatus.SUCCESS
    );

    given(binaryContentService.find(binaryContentId)).willReturn(binaryContent);

    // When & Then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}", binaryContentId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(binaryContentId.toString()))
        .andExpect(jsonPath("$.fileName").value("test.jpg"))
        .andExpect(jsonPath("$.size").value(10240))
        .andExpect(jsonPath("$.contentType").value(MediaType.IMAGE_JPEG_VALUE));
  }

  @Test
  @DisplayName("바이너리 컨텐츠 조회 실패 테스트 - 존재하지 않는 컨텐츠")
  void find_Failure_BinaryContentNotFound() throws Exception {
    // Given
    UUID nonExistentId = UUID.randomUUID();

    given(binaryContentService.find(nonExistentId))
        .willThrow(new BinaryContentNotFoundException("nonExistentId"));

    // When & Then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}", nonExistentId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("바이너리 컨텐츠 다운로드 성공 테스트")
  void download_Success() throws Exception {
    // Given
    UUID binaryContentId = UUID.randomUUID();
    BinaryContentDto binaryContent = new BinaryContentDto(
        binaryContentId,
        "test.jpg",
        10240L,
        MediaType.IMAGE_JPEG_VALUE,
        BinaryContentStatus.SUCCESS
    );

    given(binaryContentService.find(binaryContentId)).willReturn(binaryContent);

    // doReturn 사용하여 타입 문제 우회
    ResponseEntity<ByteArrayResource> mockResponse = ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.jpg\"")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
        .body(new ByteArrayResource("test data".getBytes()));

    doReturn(mockResponse).when(binaryContentStorage).download(any(BinaryContentDto.class));

    // When & Then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", binaryContentId))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("바이너리 컨텐츠 다운로드 실패 테스트 - 존재하지 않는 컨텐츠")
  void download_Failure_BinaryContentNotFound() throws Exception {
    // Given
    UUID nonExistentId = UUID.randomUUID();

    given(binaryContentService.find(nonExistentId))
        .willThrow(new BinaryContentNotFoundException("nonExistentId"));

    // When & Then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", nonExistentId))
        .andExpect(status().isBadRequest());
  }
} 