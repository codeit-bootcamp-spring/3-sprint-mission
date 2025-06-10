package com.sprint.mission.discodeit.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PageResponse<T> {

    private List<?> content;
    private int number;
    private int size;
    private boolean hasNext;
    private Long totalElements;


    public PageResponse(List<?> content, int number, int size, boolean hasNext,
        Long totalElements) {
        this.content = content;
        this.number = number;
        this.size = size;
        this.hasNext = hasNext;
        this.totalElements = totalElements;
    }

    @Override
    public String toString() {
        return "PageResponse{" +
            "content=" + content +
            ", number=" + number +
            ", size=" + size +
            ", hasNext=" + hasNext +
            ", totalElements=" + totalElements +
            '}';
    }
}
