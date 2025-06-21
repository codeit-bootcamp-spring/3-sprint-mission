package com.sprint.mission.discodeit.dto.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class PageMapper {

  /**
   * Slice 객체를 커서 기반 PageResponse로 변환 (totalElements는 null)
   */
  public <T, R> PageResponse<R> toPageResponse(Slice<T> slice, Function<T, R> mapper) {
    List<R> content = slice.getContent().stream()
        .map(mapper)
        .toList();

    // 다음 커서는 마지막 요소의 ID 또는 생성 시간을 사용
    Object nextCursor = null;
    if (slice.hasNext() && !content.isEmpty()) {
      // 실제 구현에서는 마지막 요소의 식별자(ID, createdAt 등)를 추출해야 함
      nextCursor = extractCursor(slice.getContent());
    }

    return new PageResponse<>(
        content,
        nextCursor,
        slice.getSize(),
        slice.hasNext(),
        null // Slice는 총 개수를 제공하지 않음
    );
  }

  /**
   * Page 객체를 커서 기반 PageResponse로 변환 (totalElements 포함)
   */
  public <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
    List<R> content = page.getContent().stream()
        .map(mapper)
        .toList();

    // 다음 커서는 마지막 요소의 ID 또는 생성 시간을 사용
    Object nextCursor = null;
    if (page.hasNext() && !content.isEmpty()) {
      nextCursor = extractCursor(page.getContent());
    }

    return new PageResponse<>(
        content,
        nextCursor,
        page.getSize(),
        page.hasNext(),
        page.getTotalElements());
  }

  /**
   * Slice 객체를 커서 기반 PageResponse로 변환 (이미 변환된 content 사용)
   */
  public <T> PageResponse<T> toPageResponse(Slice<?> slice, List<T> content) {
    Object nextCursor = null;
    if (slice.hasNext() && !content.isEmpty()) {
      nextCursor = extractCursor(slice.getContent());
    }

    return new PageResponse<>(
        content,
        nextCursor,
        slice.getSize(),
        slice.hasNext(),
        null);
  }

  /**
   * Page 객체를 커서 기반 PageResponse로 변환 (이미 변환된 content 사용)
   */
  public <T> PageResponse<T> toPageResponse(Page<?> page, List<T> content) {
    Object nextCursor = null;
    if (page.hasNext() && !content.isEmpty()) {
      nextCursor = extractCursor(page.getContent());
    }

    return new PageResponse<>(
        content,
        nextCursor,
        page.getSize(),
        page.hasNext(),
        page.getTotalElements());
  }

  /**
   * 커서 기반 PageResponse 생성 (직접 커서 지정)
   */
  public <T> PageResponse<T> toCursorPageResponse(List<T> content, Object nextCursor, int size, boolean hasNext,
      Long totalElements) {
    return new PageResponse<>(
        content,
        nextCursor,
        size,
        hasNext,
        totalElements);
  }

  /**
   * 엔티티 리스트에서 커서를 추출하는 헬퍼 메서드
   * createdAt을 우선적으로 추출하여 date-time 형식의 커서를 생성
   */
  private Object extractCursor(List<?> content) {
    if (content.isEmpty()) {
      return null;
    }

    Object lastElement = content.get(content.size() - 1);

    // 리플렉션을 사용하여 createdAt 필드를 우선 추출
    try {
      // createdAt 필드 우선 시도
      if (lastElement.getClass().getMethod("getCreatedAt") != null) {
        Object createdAt = lastElement.getClass().getMethod("getCreatedAt").invoke(lastElement);
        return createdAt != null ? createdAt.toString() : null;
      }
    } catch (Exception e) {
      // createdAt이 없으면 ID 시도
      try {
        if (lastElement.getClass().getMethod("getId") != null) {
          return lastElement.getClass().getMethod("getId").invoke(lastElement);
        }
      } catch (Exception ex) {
        // 추출 실패 시 null 반환
        return null;
      }
    }

    return null;
  }
}