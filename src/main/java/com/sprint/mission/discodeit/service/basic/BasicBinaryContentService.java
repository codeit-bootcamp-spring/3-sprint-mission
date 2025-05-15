package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.entitiy.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(CreateBinaryContentRequest request){
        return binaryContentRepository.save(new BinaryContent(request.filename(), request.contentType(), request.bytes()));
    }

    @Override
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

    @Override
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

    @Override
    public void delete(UUID binaryContentId){
        binaryContentRepository.delete(binaryContentId);
    }


}
