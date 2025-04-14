package com.sprint.mission.discodeit.service.file;

/**
 * packageName    : com.sprint.mission.discodeit.service.file
 * fileName       : FileMessageService
 * author         : doungukkim
 * date           : 2025. 4. 14.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 14.        doungukkim       최초 생성
 */
public class FileMessageService {
    public FileChannelService fileChannelService;

    public FileMessageService(FileChannelService fileChannelService) {
        this.fileChannelService = fileChannelService;
    }
}
