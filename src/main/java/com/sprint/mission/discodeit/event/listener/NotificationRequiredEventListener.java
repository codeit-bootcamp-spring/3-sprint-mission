package com.sprint.mission.discodeit.event.listener;

//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class NotificationRequiredEventListener {
//
//    private final NotificationRepository notificationRepository;
//    private final ReadStatusRepository readStatusRepository;
//    private final UserRepository userRepository;
//    private final CacheManager cacheManager;
//    private static final String ROLE_UPDATE_TITLE = "권한이 변경되었습니다.";
//    private static final String PRIVATE_CHANNEL_NAME = "개인 메시지";
//    private static final String S3_UPLOAD_FAIL_TITLE = "S3 업로드 실패";
//
//    @Async("notificationExecutor")
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void onMessageCreated(MessageCreatedEvent event) {
//
//        User author = event.author();
//        Channel channel = event.channel();
//        String title = getTitle(author, channel);
//        String content = event.content();
//
//        // 알림 수신 여부가 true인 채널의 readStatus 조회
//        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdAndNotificationEnabled(
//            channel.getId(), true
//        );
//
//        // 캐시 무효화
//        Cache cache = cacheManager.getCache("notificationsByUser");
//        if (cache != null) {
//            readStatuses.stream()
//                .map(ReadStatus::getUser)
//                .map(User::getId)
//                .filter(userId -> !userId.equals(author.getId()))
//                .distinct()
//                .forEach(cache::evict);
//        }
//
//        log.debug("[NotificationRequiredEventListener] 알림 수신 가능 사용자 수: {}", readStatuses.size());
//
//        // 메시지를 보낸 사용자는 알림 대상에서 제외
//        List<Notification> notifications = readStatuses.stream()
//            .filter(readStatus -> !readStatus.getUser().equals(author))
//            .map(readStatus -> new Notification(title, content, readStatus.getUser()))
//            .toList();
//
//        notificationRepository.saveAll(notifications);
//
//        log.debug("[NotificationRequiredEventListener] 알림 {}개 생성 완료", notifications.size());
//    }
//
//    @Async("notificationExecutor")
//    @CacheEvict(value = "notificationsByUser", key = "#event.user().id")
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void onRoleUpdated(RoleUpdatedEvent event) {
//
//        User user = event.user();
//        Role oldRole = event.oldRole();
//        Role newRole = event.newRole();
//
//        String content = oldRole.name() + " -> " + newRole.name();
//
//        Notification notification = Notification.builder()
//            .title(ROLE_UPDATE_TITLE)
//            .content(content)
//            .user(user)
//            .build();
//
//        Notification savedNotification = notificationRepository.save(notification);
//
//        log.debug("[NotificationRequiredEventListener] 권한 변경 알림 생성 완료- id: {}",
//            savedNotification.getId());
//    }
//
//    @Async("notificationExecutor")
//    @EventListener
//    public void onS3UploadFailed(S3UploadFailedEvent event) {
//
//        String content =
//            "Request Id: " + event.requestId() + "\n BinaryContentId: " + event.binaryContentId()
//                + "\n Error: "
//                + event.errorMessage();
//
//        List<User> admins = userRepository.findByRole(Role.ADMIN);
//
//        List<Notification> notifications = admins.stream()
//            .map(user -> new Notification(S3_UPLOAD_FAIL_TITLE, content, user))
//            .toList();
//
//        // 캐시 무효화
//        Cache cache = cacheManager.getCache("notificationsByUser");
//        if (cache != null) {
//            admins.stream()
//                .map(User::getId)
//                .distinct()
//                .forEach(cache::evict);
//        }
//
//        notificationRepository.saveAll(notifications);
//
//        log.debug("[NotificationRequiredEventListener] S3 업로드 실패 알림 전송 완료- {}개",
//            notifications.size());
//    }
//
//    private String getTitle(User author, Channel channel) {
//        StringBuilder title = new StringBuilder(author.getUsername()).append(" (#");
//
//        if (channel.getType().equals(ChannelType.PUBLIC)) {
//            title.append(channel.getName());
//        } else {
//            title.append(PRIVATE_CHANNEL_NAME);
//        }
//        title.append(")");
//
//        return title.toString();
//    }
//}
