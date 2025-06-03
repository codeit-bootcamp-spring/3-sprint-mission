package com.sprint.mission.discodeit.mapper.original;

import com.sprint.mission.discodeit.dto.message.response.JpaMessageResponse;
import com.sprint.mission.discodeit.dto.message.response.JpaPageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.advanced.AdvancedMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper
 * FileName     : PageResponseMapper
 * Author       : dounguk
 * Date         : 2025. 6. 1.
 */
@RequiredArgsConstructor
//@Component
public class PageResponseMapper {

    //    private final MessageMapper messageMapper;
    private final AdvancedMessageMapper messageMapper;

    public JpaPageResponse fromSlice(Slice<Message> slice) {
        Set<JpaMessageResponse> pageResponses = slice.getContent().stream()
                .map(messageMapper::toDto).collect(Collectors.toSet());
        List<JpaMessageResponse> pageResponsesList = pageResponses.stream().toList();

        JpaPageResponse response = JpaPageResponse.builder()
                .content(pageResponsesList)
                .number(slice.getNumber())
                .size(slice.getSize())
                .hasNext(slice.hasNext())
                .build();
        return response;
    }

    public JpaPageResponse fromPage(Page<Message> page) {
        Set<JpaMessageResponse> pageResponse = page.getContent().stream()
                .map(messageMapper::toDto).collect(Collectors.toSet());
        List<JpaMessageResponse> pageResponsesList = pageResponse.stream().toList();


        JpaPageResponse response = JpaPageResponse.builder()
                .content(pageResponsesList)
                .number(page.getNumber())
                .size(page.getSize())
                .hasNext(page.hasNext())
                .totalElements(page.getTotalElements())
                .build();
        return response;
    }

}
