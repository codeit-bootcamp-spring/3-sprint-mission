package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BasicBinaryContentServiceTest {

    @InjectMocks
    private BasicBinaryContentService service;

    @Mock
    private BinaryContentRepository repository;

    @Mock
    private BinaryContentMapper mapper;

    @Mock
    private BinaryContentStorage storage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("파일 저장 성공")
    void shouldSaveFileSuccessfully() {
        byte[] bytes = new byte[5];
        BinaryContentCreateRequest request = new BinaryContentCreateRequest("test.png", "image/png",
            bytes);
        BinaryContent entity = new BinaryContent("test.png", 5L, "image/png");
        BinaryContentDto dto = new BinaryContentDto(UUID.randomUUID(), "test.png", 5L, "image/png");

        given(repository.save(any())).willReturn(entity);
        given(mapper.toDto(any())).willReturn(dto);

        BinaryContentDto result = service.create(request);

        assertThat(result.fileName()).isEqualTo("test.png");
        then(storage).should().put(any(), eq(bytes));
    }

    @Test
    @DisplayName("파일을 ID로 조회 성공")
    void shouldFindFileByIdSuccessfully() {
        UUID id = UUID.randomUUID();
        BinaryContent entity = new BinaryContent("file.txt", 3L, "text/plain");
        BinaryContentDto dto = new BinaryContentDto(id, "file.txt", 3L, "text/plain");

        given(repository.findById(id)).willReturn(Optional.of(entity));
        given(mapper.toDto(entity)).willReturn(dto);

        BinaryContentDto result = service.find(id);

        assertThat(result.fileName()).isEqualTo("file.txt");
    }

    @Test
    @DisplayName("존재하지 않는 파일 조회 시 예외 발생")
    void shouldThrowBinaryContentNotFoundException_whenFileNotFound() {
        UUID id = UUID.randomUUID();
        given(repository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.find(id))
            .isInstanceOf(BinaryContentNotFoundException.class);
    }

    @Test
    @DisplayName("여러 ID로 파일 목록 조회 성공")
    void shouldFindFilesByIdsSuccessfully() {
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        List<BinaryContent> entities = List.of(
            new BinaryContent("a.png", 10L, "image/png"),
            new BinaryContent("b.png", 20L, "image/png")
        );

        List<BinaryContentDto> dtos = List.of(
            new BinaryContentDto(UUID.randomUUID(), "a.png", 10L, "image/png"),
            new BinaryContentDto(UUID.randomUUID(), "b.png", 20L, "image/png")
        );

        given(repository.findAllById(ids)).willReturn(entities);
        given(mapper.toDto(entities.get(0))).willReturn(dtos.get(0));
        given(mapper.toDto(entities.get(1))).willReturn(dtos.get(1));

        List<BinaryContentDto> result = service.findAllByIdIn(ids);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("파일을 삭제 성공")
    void shouldDeleteFileSuccessfully() {
        UUID id = UUID.randomUUID();
        given(repository.existsById(id)).willReturn(true);

        service.delete(id);

        then(repository).should().deleteById(id);
    }

    @Test
    @DisplayName("존재하지 않는 파일 삭제 시 예외 발생")
    void shouldThrowBinaryContentNotFoundException_whenDeletingNonExistentFile() {
        UUID id = UUID.randomUUID();
        given(repository.existsById(id)).willReturn(false);

        assertThatThrownBy(() -> service.delete(id))
            .isInstanceOf(BinaryContentNotFoundException.class);
    }
}
