package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : BasicChannelService
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class BasicChannelService {
    private final FileChannelService fcs;
    private final JCFChannelService jcs;

    public BasicChannelService(FileChannelService fcs, JCFChannelService jcs) {
        this.fcs = fcs;
        this.jcs = jcs;
    }


}
