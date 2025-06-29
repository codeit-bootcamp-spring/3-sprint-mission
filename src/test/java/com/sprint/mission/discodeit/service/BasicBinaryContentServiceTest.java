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
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicBinaryContentServiceTest {

    @InjectMocks
    private BasicBinaryContentService service;

    @Mock
    private BinaryContentRepository repository;

    @Mock
    private BinaryContentMapper mapper;

    @Mock
    private BinaryContentStorage storage;

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
    @DisplayName("존재하지 않는 파일 조회 시 예외 발생")
    void shouldThrowBinaryContentNotFoundException_whenFileNotFound() {
        UUID id = UUID.randomUUID();
        given(repository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.find(id))
            .isInstanceOf(BinaryContentNotFoundException.class)
            .hasMessageContaining("BinaryContent not found");
    }


    @Test
    @DisplayName("존재하지 않는 파일 삭제 시 예외 발생")
    void shouldThrowBinaryContentNotFoundException_whenDeletingNonExistentFile() {
        UUID id = UUID.randomUUID();
        given(repository.existsById(id)).willReturn(false);

        assertThatThrownBy(() -> service.delete(id))
            .isInstanceOf(BinaryContentNotFoundException.class)
            .hasMessageContaining("BinaryContent not found");
    }
}
