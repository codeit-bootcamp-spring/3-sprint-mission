package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.channel.ChannelFindResponse;
import com.sprint.mission.discodeit.Dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.Dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.Dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.Dto.message.MessageAttachmentsCreateRequest;
import com.sprint.mission.discodeit.Dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.Dto.user.*;
import com.sprint.mission.discodeit.Dto.userStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.Dto.userStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.Dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.Dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.BinaryContentService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.Instant;
import java.util.*;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean("basicUserService", UserService.class);
		ChannelService channelService = context.getBean("basicChannelService", ChannelService.class);
		MessageService messageService = context.getBean("basicMessageService", MessageService.class);
		AuthService authService = context.getBean(AuthService.class);
		ReadStatusService readStatusService = context.getBean("basicReadStatusService", ReadStatusService.class);
		UserStatusService userStatusService = context.getBean("basicUserStatusService", UserStatusService.class);
		BinaryContentService binaryContentService = context.getBean("basicBinaryContentService", BinaryContentService.class);

		BinaryContentRepository binaryContentRepository = context.getBean(BinaryContentRepository.class);
		UserStatusRepository userStatusRepository = context.getBean(UserStatusRepository.class);

		// 빈 주입------------------------------------------------------------------------------------------------------------
		System.out.println("Start Test ====>");


// 1. create 테스트
		System.out.println("\n[Create] BinaryContent 생성");
		byte[] attachment1 = new byte[]{1, 2, 3, 4, 5};
		byte[] attachment2 = new byte[]{6, 7, 8, 9, 10};

		BinaryContent binaryContent1 = binaryContentService.create(new BinaryContentCreateRequest(attachment1));
		BinaryContent binaryContent2 = binaryContentService.create(new BinaryContentCreateRequest(attachment2));

		System.out.println("생성된 BinaryContent ID 1: " + binaryContent1.getId());
		System.out.println("생성된 BinaryContent ID 2: " + binaryContent2.getId());

// 2. find 테스트
		System.out.println("\n[Find] BinaryContent 단건 조회");
		BinaryContent found1 = binaryContentService.find(binaryContent1.getId());
		System.out.println("조회된 BinaryContent ID: " + found1.getId() + ", 데이터 길이: " + found1.getAttachment().length);

// 3. findAllByIdIn 테스트
		System.out.println("\n[FindAllByIdIn] 여러 BinaryContent 조회");
		List<BinaryContent> foundList = binaryContentService.findAllByIdIn(List.of(binaryContent1.getId(), binaryContent2.getId()));
		System.out.println("조회된 BinaryContent 개수: " + foundList.size());
		for (BinaryContent bc : foundList) {
			System.out.println("- BinaryContent ID: " + bc.getId() + ", 데이터 길이: " + bc.getAttachment().length);
		}

// 4. delete 테스트
		System.out.println("\n[Delete] BinaryContent 삭제");
		binaryContentService.delete(binaryContent1.getId());

// 삭제 확인 (find 하면 예외 터질 것)
		try {
			binaryContentService.find(binaryContent1.getId());
			System.out.println("삭제 실패: BinaryContent 여전히 존재함");
		} catch (Exception e) {
			System.out.println("삭제 성공: BinaryContent 존재하지 않음");
		}

		System.out.println("\n=== BinaryContentService 통합 테스트 종료 ===");
	}
}