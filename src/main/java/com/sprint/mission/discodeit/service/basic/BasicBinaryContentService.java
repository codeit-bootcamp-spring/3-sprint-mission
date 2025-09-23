package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        log.info("파일 업로드 요청: fileName={}, contentType={}, size={} bytes",
                request.fileName(), request.contentType(), request.bytes().length);

        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                (long) request.bytes().length,
                request.contentType()
        );
        binaryContentRepository.save(binaryContent);

        //  이벤트 발행
        publisher.publishEvent(new BinaryContentCreatedEvent(
                binaryContent.getId(), request.contentType(), request.bytes()
        ));

        log.info("파일 메타 저장 완료(이벤트 발행): id={}", binaryContent.getId());
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public BinaryContentDto find(UUID binaryContentId) {
        log.debug("파일 조회 요청: id={}", binaryContentId);

        return binaryContentRepository.findById(binaryContentId)
                .map(binaryContentMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("파일 조회 실패: 존재하지 않는 ID={}", binaryContentId);
                    return new BinaryContentNotFoundException(binaryContentId);
                });
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        log.debug("다중 파일 조회 요청: {}개 ID", binaryContentIds.size());

        return binaryContentRepository.findAllById(binaryContentIds).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public void delete(UUID binaryContentId) {
        log.info("파일 삭제 요청: id={}", binaryContentId);

        if (!binaryContentRepository.existsById(binaryContentId)) {
            log.error("삭제 실패: 존재하지 않는 파일 id={}", binaryContentId);
            throw new BinaryContentNotFoundException(binaryContentId);
        }

        BinaryContent profile = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));

        List<User> usersWithProfile = userRepository.findAllByProfile(profile);
        for (User user : usersWithProfile) {
            user.clearProfile();
            userRepository.save(user);
        }

        binaryContentRepository.deleteById(binaryContentId);
        log.info("파일 삭제 완료: id={}", binaryContentId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {
        BinaryContent entity = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));

        entity.updateStatus(status);
        binaryContentRepository.save(entity);

        log.info("BinaryContent 상태 업데이트: id={}, status={}", binaryContentId, status);
        return binaryContentMapper.toDto(entity);
    }
}