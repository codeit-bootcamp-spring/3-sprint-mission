package com.sprint.mission.discodeit.entity;

/**
 * 채널의 접근 가능 여부를 정의하는 열거형입니다.
 * 
 * <p>채널 생성 시 설정되며, 사용자의 채널 참여 권한을 결정하는 데 사용됩니다.</p>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
public enum ChannelType {
    /**
     * 공개 채널입니다.
     * 
     * <p>모든 사용자가 자유롭게 참여할 수 있으며, 검색을 통해 발견할 수 있습니다.</p>
     */
    PUBLIC,
    
    /**
     * 비공개 채널입니다.
     * 
     * <p>초대를 받은 사용자만 참여할 수 있으며, 검색 결과에 표시되지 않습니다.</p>
     */
    PRIVATE,
}
