package com.sprint.mission.discodeit.service.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JcfChannelService;
import com.sprint.mission.discodeit.service.jcf.JcfUserService;

public class ServiceFactory {
  // 의존성 주입을 수동으로 관리하는 DI 컨테이너
  // ServiceFactory를 쓰는이유: JcfUserService와 JcfChannelService의 순환 참조 (A->B->A) 문제를 해결하고자
  // userService와 channelService를 프로그램 실행 하는 동안 하나의 인스턴스만 유지 - 싱글톤패턴
  // 어디서든 ServiceFactory.getUserService()로 같은 인스턴스를 사용할 수 있다.
  private static UserService userService;
  private static ChannelService channelService;

  public static void initializeServices() {
    FileUserService user = new FileUserService();  //  먼저 Jcf(File)UserService를 비워서 생성한다.
    channelService = new FileChannelService(user); // Jcf(File)ChannelService에 유저서비스를 넣는다.
    user.setChannelService(channelService); // 생성된 channelService를 setter로 주입한다.
    userService = user;
  }

  public static UserService getUserService() {
    return userService;
  }

  public static ChannelService getChannelService() {
    return channelService;
  }
}
