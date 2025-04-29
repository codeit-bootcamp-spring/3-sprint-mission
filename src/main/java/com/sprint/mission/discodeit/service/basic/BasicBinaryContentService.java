package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    //TODO : 레포 생성하고 autowired 해줘야함
//    private final BinaryContentRepository binaryContentRepository;
    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest createRequest) {
        BinaryContent binaryContent = new BinaryContent(createRequest.byteArray(), createRequest.userId());
        // TODO : 레포 생성하고 실제 값 넣어주기

        //        this.binaryContentRepository.save(binaryContent);

        return new BinaryContentResponse(binaryContent);
    }

    @Override
    public BinaryContentResponse find(UUID binaryContentId) {
        // TODO : 레포 생성하고 실제 값 넣어주기
//        BinaryContent binaryContent = this.binaryContentRepository
//                .findById(binaryContentId)
//                .orElseThrow(() -> new NoSuchElementException("binaryContent with id " + binaryContentId + " not found"));
//
//        return new BinaryContentResponse(binaryContent);

        return null;
    }


    // Reference : https://www.baeldung.com/java-filter-collection-by-list
    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds) {
        // TODO : 레포 생성하고 실제 값 넣어주기
        // TODO : 잘 작동하는지 확인할것
//        List<BinaryContent> binaryContents = this.binaryContentRepository.findAll();
//        Set<UUID> binaryContentIdsSet = new HashSet<>(binaryContentIds);
//
//        List<BinaryContentResponse> filteredBinaryContents = binaryContents.stream().filter(binaryContentIdsSet::contains).map(BinaryContentResponse::new).toList();


        return List.of();
    }

    @Override
    public void delete(UUID binaryContentId) {
        // TODO : 레포 생성하고 실제 값 넣어주기
//        BinaryContent binaryContent = this.binaryContentRepository
//                .findById(binaryContentId)
//                .orElseThrow(() -> new NoSuchElementException("binaryContent with id " + binaryContentId + " not found"));
//

        //        this.binaryContentRepository.deleteById(binaryContentId);


    }
}
