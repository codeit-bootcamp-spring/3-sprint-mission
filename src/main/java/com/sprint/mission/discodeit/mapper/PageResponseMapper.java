package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

/*
 * 확장성을 위해 제네릭 메소드로 구현하세요.
 * 제네릭 메소드 사용 이유
 * 1. 유연성 : 다양한 타입에 대해 한번만 정의 가능
 * 2. 타입 안정성 : 컴파일시 타입 체크됨(안전 , 깔끔)
 * 3. 반복제거 : 같은 로직을 타입마다 다시만들필요 없음.
 *
 * Page<User> userPage = ...;
 * PageResponse<User> userResponse = pageResponseMapper.fromPage(userPage);
 * 호출 시 T는 자동 추론되어 사용
 * */
@Component
public class PageResponseMapper {

    //다음페이지
    public <T> PageResponse<T> fromSlice(Slice<T> slice) {
        return new PageResponse<>(
            slice.getContent(),
            calculateNextCursor(slice),
            slice.getSize(),
            slice.hasNext(),
            null
        );
    }

    //전체 페이지
    public <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.hasNext(),
            page.getTotalElements()
        );
    }

    private <T> Object calculateNextCursor(Slice<T> slice) {
        if (!slice.hasNext() || slice.getContent().isEmpty()) {
            return null;
        }

        T lastElement = slice.getContent().get(slice.getContent().size() - 1);

        if (lastElement instanceof MessageDto) {
            return ((MessageDto) lastElement).createdAt();
        }
        return null;
    }
}

