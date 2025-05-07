package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.entitiy.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    public BinaryContent create(CreateBinaryContentRequest request){
        return binaryContentRepository.save(new BinaryContent(request.contentType(), request.content()));
    }

    public BinaryContent find(UUID binaryContentId){
        Optional<BinaryContent> binaryContent = binaryContentRepository.readById(binaryContentId);
        try {
            if (binaryContent.isPresent()) {
                return binaryContent.get();
            } else {
                throw new NoSuchElementException("해당 binaryContentId는 존재하지 않습니다");
            }
        }catch (NoSuchElementException e){
            System.out.println(e);
            return null;
        }
    }

    public List<BinaryContent> findAllByIdIn(List<UUID> uuidList){
        List<BinaryContent> binaryContentList = new ArrayList<>();
        for(UUID uuid : uuidList){
            Optional<BinaryContent> binaryContent = binaryContentRepository.readById(uuid);
            if(binaryContent.isPresent()){
                binaryContentList.add(binaryContent.get());
            }
        }
        return binaryContentList;
    }

    public void delete(UUID binaryContentId){
        binaryContentRepository.delete(binaryContentId);
    }


}
