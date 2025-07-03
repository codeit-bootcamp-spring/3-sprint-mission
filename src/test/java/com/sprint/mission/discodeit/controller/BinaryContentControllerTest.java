package com.sprint.mission.discodeit.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.MapperFacade;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.testutil.TestConstants;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BinaryContentController.class)
@DisplayName("BinaryContentController 슬라이스 테스트")
class BinaryContentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private BinaryContentService binaryContentService;

  @MockitoBean
  private BinaryContentStorage binaryContentStorage;

  @MockitoBean
  private MapperFacade mapperFacade;

  private BinaryContent createBinaryContentWithId(UUID id, String fileName, Long size, String contentType) {
    try {
      BinaryContent binaryContent = new BinaryContent(fileName, size, contentType);
      Field idField = BinaryContent.class.getSuperclass().getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(binaryContent, id);

      Field createdAtField = BinaryContent.class.getSuperclass().getDeclaredField("createdAt");
      createdAtField.setAccessible(true);
      createdAtField.set(binaryContent, Instant.now());

      return binaryContent;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @DisplayName("바이너리 콘텐츠 조회 - 성공")
  void find_Success() throws Exception {
    // given
    UUID binaryContentId = UUID.randomUUID();
    BinaryContent binaryContent = createBinaryContentWithId(
        binaryContentId,
        TestConstants.TEST_FILE_NAME,
        TestConstants.TEST_FILE_SIZE,
        TestConstants.TEST_CONTENT_TYPE);

    when(binaryContentService.find(binaryContentId)).thenReturn(binaryContent);

    // when & then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}", binaryContentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(binaryContentId.toString()))
        .andExpect(jsonPath("$.fileName").value(TestConstants.TEST_FILE_NAME))
        .andExpect(jsonPath("$.size").value(TestConstants.TEST_FILE_SIZE))
        .andExpect(jsonPath("$.contentType").value(TestConstants.TEST_CONTENT_TYPE))
        .andExpect(jsonPath("$.createdAt").exists());
  }

  @Test
  @DisplayName("바이너리 콘텐츠 조회 - 실패 (존재하지 않는 ID)")
  void find_Fail_NotFound() throws Exception {
    // given
    UUID binaryContentId = TestConstants.NON_EXISTENT_USER_ID;

    when(binaryContentService.find(binaryContentId))
        .thenThrow(BinaryContentNotFoundException.withBinaryContentId(binaryContentId));

    // when & then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}", binaryContentId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("바이너리 콘텐츠 조회 - 실패 (유효하지 않은 UUID 형식)")
  void find_Fail_InvalidUuidFormat() throws Exception {
    // when & then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}", "invalid-uuid"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("다중 바이너리 콘텐츠 조회 - 성공")
  void findAllByIdIn_Success() throws Exception {
    // given
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    List<UUID> binaryContentIds = Arrays.asList(id1, id2);

    BinaryContent binaryContent1 = createBinaryContentWithId(
        id1, "file1.jpg", 1024L, "image/jpeg");
    BinaryContent binaryContent2 = createBinaryContentWithId(
        id2, "file2.png", 2048L, "image/png");
    List<BinaryContent> binaryContents = Arrays.asList(binaryContent1, binaryContent2);

    when(binaryContentService.findAllByIdIn(binaryContentIds)).thenReturn(binaryContents);

    // when & then
    mockMvc.perform(get("/api/binaryContents")
        .param("binaryContentIds", id1.toString())
        .param("binaryContentIds", id2.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(id1.toString()))
        .andExpect(jsonPath("$[0].fileName").value("file1.jpg"))
        .andExpect(jsonPath("$[0].size").value(1024))
        .andExpect(jsonPath("$[0].contentType").value("image/jpeg"))
        .andExpect(jsonPath("$[1].id").value(id2.toString()))
        .andExpect(jsonPath("$[1].fileName").value("file2.png"))
        .andExpect(jsonPath("$[1].size").value(2048))
        .andExpect(jsonPath("$[1].contentType").value("image/png"));
  }

  @Test
  @DisplayName("다중 바이너리 콘텐츠 조회 - 성공 (빈 목록)")
  void findAllByIdIn_Success_EmptyList() throws Exception {
    // given
    List<UUID> binaryContentIds = Arrays.asList();
    List<BinaryContent> emptyList = List.of();

    when(binaryContentService.findAllByIdIn(binaryContentIds)).thenReturn(emptyList);

    // when & then
    mockMvc.perform(get("/api/binaryContents")
        .param("binaryContentIds", "")) // 빈 파라미터 제공
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("다중 바이너리 콘텐츠 조회 - 실패 (파라미터 누락)")
  void findAllByIdIn_Fail_MissingParameter() throws Exception {
    // when & then
    mockMvc.perform(get("/api/binaryContents")
        .param("wrongParam", "someValue"))
        .andExpect(status().isBadRequest());
  }

  // 다운로드 기능은 ResponseEntity<?>를 반환하므로 Mock 설정이 복잡함
  // 따라서 통합 테스트에서 테스트하려 함

  @Test
  @DisplayName("파일 다운로드 - 실패 (존재하지 않는 파일)")
  void download_Fail_FileNotFound() throws Exception {
    // given
    UUID binaryContentId = TestConstants.NON_EXISTENT_USER_ID;

    when(binaryContentService.find(binaryContentId))
        .thenThrow(BinaryContentNotFoundException.withBinaryContentId(binaryContentId));

    // when & then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", binaryContentId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("파일 다운로드 - 실패 (유효하지 않은 UUID 형식)")
  void download_Fail_InvalidUuidFormat() throws Exception {
    // when & then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", "invalid-uuid"))
        .andExpect(status().isBadRequest());
  }
}
