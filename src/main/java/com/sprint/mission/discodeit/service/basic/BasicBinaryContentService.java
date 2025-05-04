package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.*;

public class BasicBinaryContentService {

    BinaryContentRepository binaryContentRepository;

    public BinaryContent create(CreateBinaryContentRequest request) {

        // 1. request에서 파라미터를 받아와 BinaryContent 생성
        if (request.fileName() == null || request.fileType() == null || request.content() == null) {
            throw new NoSuchElementException("BinaryContent parameters are invalid.");
        }

        // 2. BinaryContent 객체 생성
        BinaryContent binaryContent = new BinaryContent(request.fileName(), request.fileType(), request.content());

        // 3. Repository에 저장
        return binaryContentRepository.save(binaryContent);
    }


    public BinaryContent find(UUID id) {

        // 1. repository에서 find(UUID id)로 BinaryContent 찾아서 반환

        BinaryContent binaryContent = binaryContentRepository.findById(id);

        if (binaryContent == null) {
            throw new NoSuchElementException("BinaryContent not found.");
        }

        return binaryContent;
    }

    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {

        if (ids == null || ids.isEmpty()) {
            throw new NoSuchElementException("ids is empty.");
        }
        return binaryContentRepository.findAllByIdIn(ids);
//        List<BinaryContent> binaryContentList = binaryContentRepository.findAll();
//        List<BinaryContent> result = new ArrayList<>();
//        if (!binaryContentList.isEmpty()) {
//            for (BinaryContent binaryContent : binaryContentList) {
//                for (UUID id : ids) {
//                    if (binaryContent.getId().equals(id)) {
//                        result.add(binaryContent);
//                    }
//                }
//            }
//        }
//
//        return result;
    }

    public void delete(UUID id) {

        BinaryContent binaryContent = binaryContentRepository.findById(id);
        if (binaryContent == null) {
            throw new NoSuchElementException("BinaryContent not found.");
        }
        binaryContentRepository.delete(id);
    }

}
