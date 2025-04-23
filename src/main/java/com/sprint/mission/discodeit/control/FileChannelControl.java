package com.sprint.mission.discodeit.control;

import com.sprint.mission.discodeit.FileJavaApplication;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

public class FileChannelControl extends FileJavaApplication {

    public static Channel joinChannel(User nowUser){  // 채널입장시 Channel 신규개설 판별 메서드. 있으면 있는 채널에 참가 / 없으면 없는채널 생성

        System.out.print(" ▶ 채널명을 입력해 주세요 : ");
        String chanName = scanner.nextLine();
        Channel channel = fchannelService.findChannelByName(chanName);


        if (channel != null) {
            System.out.println("[" + channel.getChannelName() + "] 에 접속합니다." + ", 채널 ID: " + channel.getId());

            return channel;
        } else {
            System.out.println("\n ▶ 존재하지 않는 채널입니다. 새로운 채널을 개설합니다.");
            System.out.println("\n ▶ 새로운 채널의 설명을 입력해주세요.");
            String chanDesc = scanner.nextLine();

            Channel newChannel = fchannelService.createChannel(chanName,chanDesc,nowUser.getName());
            System.out.println(" ▶ [" + newChannel.getChannelName() + "] 채널에 접속합니다." + "       채널 ID: " + newChannel.getId());

            return newChannel;
        }
    }
    public static void menuChannelMng(User currentUser){       // 2. 채널 관리 메서드
        while (true) {
            System.out.println(" *******************************************************\n"
                    + " ||   채널 관리 메뉴입니다. 원하는 기능을 선택하세요. ||\n"
                    + " ||      1 > 신규 채널 생성                           ||\n"
                    + " ||      2 > 개별 채널 정보 상세조회                  ||\n"
                    + " ||      3 > 개별 채널 이름,설명 변경                 ||\n"
                    + " ||      4 > 개별 채널 삭제                           ||\n"
                    + " ||      5 > 전체 채널 정보 상세조회                  ||\n"
                    + " ||      6 > 상위 메뉴로 돌아가기                     ||\n"
                    + " *******************************************************\n");
            int choice = scanInt(); // 숫자 입력 필터링 메서드 호출

            switch (choice) {
                case 1:                 // 2_1 신규 채널 생성, 채널설명 추가
                    System.out.println(" ▶ 새로운 채널을 개설합니다.");

                    while(true) {
                        System.out.print(" ▶ 채널명을 입력해 주세요 : ");
                        String chanName = scanner.nextLine();
                        Channel channel = fchannelService.findChannelByName(chanName);

                        if (channel == null) {
                            System.out.println("\n ▶ 새로운 채널의 설명을 입력해주세요.");
                            String chanDesc = scanner.nextLine();
                            Channel newChan = fchannelService.createChannel(chanName, chanDesc, currentUser.getName());

                            System.out.println("\n ▶ "+newChan.getChannelName()+" 채널 생성에 성공하였습니다.");

                            break;
                        } else {
                            System.out.println("\n ▶ 이미 존재하는 채널입니다. 다른 이름을 입력해주세요.");
                        }
                    }
                    break;
                case 2:                    // 2_2 채널 이름으로 검색하여 개별 채널 조회
                    System.out.print(" ▶ 조회할 채널의 이름을 입력해 주세요.\n >> ");
                    String chnName = scanner.nextLine();
                    Channel toPrintChannel = fchannelService.findChannelByName(chnName);

                    if (toPrintChannel != null) {
                        System.out.println(toPrintChannel);
                        break;
                    }else{
                        System.out.println("존재하지 않는 채널명입니다. 다시 입력해 주세요");
                    }
                    break;
                case 3:                 // 2_3 채널 이름으로 검색하여 해당 채널 이름,설명 변경
                    System.out.println("\n ▶  채널 이름을 변경합니다. 현재 개설된 채널은 아래와 같습니다. \n");
                    fchannelService.printAllChannels();

                    while(true) {
                        String newName;
                        String newDesc;

                        System.out.print(" ▶ 변경할 채널의 이름을 선택해 주세요.\n >> ");
                        String fndChnName = scanner.nextLine();
                        Channel toRenameChannel = fchannelService.findChannelByName(fndChnName);

                        if (toRenameChannel != null) {
                            System.out.println(" ▶ 선택하신 채널은 ["+toRenameChannel.getChannelName()+"] 입니다");
                            System.out.println(" ▶ 변경할 이름을 입력해 주세요.");

                            while(true) {
                                System.out.print(" >> ");
                                newName = scanner.nextLine();

                                if(fchannelService.findChannelByName(newName) == null){
                                    if (!newName.isEmpty()) {
                                        toRenameChannel.setChannelName(newName);

                                        break;
                                    } else {
                                        System.out.println(" ▶ 잘못된 입력입니다. 다시 입력해 주세요.");
                                    }
                                } else {
                                    System.out.println(" ▶ 이미 존재하는 채널명입니다. 다시 입력해 주세요.");
                                }
                            }

                            System.out.print(" ▶ 변경할 채널의 설명을 입력해 주세요.\n >> ");
                            newDesc = scanner.nextLine();

                            if (newDesc.isEmpty()) {
                                fchannelService.UpdateChannel(toRenameChannel,newName,"-");
                                System.out.println(" ▶ 채널 설명을 공백으로 설정했습니다.");
                                break;
                            }else{
                                fchannelService.UpdateChannel(toRenameChannel,newName,newDesc);
                                System.out.println(" ▶ 채널정보 수정에 성공했습니다.");
                                break;
                            }
                        }else{
                            System.out.println("존재하지 않는 채널명입니다. 다시 입력해 주세요");
                        }
                    }
                    break;
                case 4:                    // 2_4 개별 채널 삭제
                    System.out.println(" ▶ 현재 개설된 채널은 아래와 같습니다.");
                    fchannelService.printAllChannels();

                    System.out.print(" ▶ 삭제할 채널의 이름을 입력해 주세요.\n >> ");
                    String fndChnName = scanner.nextLine();
                    Channel toDeleteChannel = fchannelService.findChannelByName(fndChnName);

                    if (toDeleteChannel != null) {
                        System.out.print(" ▶ 정말로 삭제할까요? 삭제를 원하시면 [삭제]라고 입력해 주세요.\n >> ");
                        String deleteConfirm = scanner.nextLine();

                        if (deleteConfirm.isEmpty()) {
                            System.out.println(" ▶ 잘못된 입력입니다. 이전 메뉴로 돌아갑니다.");
                            break;

                        }else if(deleteConfirm.equals("삭제")) {
                            fchannelService.deleteChannel(toDeleteChannel);

                            System.out.println(" ▶ [" + fndChnName+ "]채널이 삭제되었습니다.");
                        }else{
                            System.out.println(" ▶ 잘못된 입력입니다. 채널 삭제를 취소합니다.");
                        }
                    } else {
                        System.out.println(" ▶ 잘못된 입력입니다. 채널 삭제를 취소합니다.");
                    }


                    break;
                case 5:                    // 2_5 개별 채널 정보 상세조회
                    fchannelService.printAllChannels();
                    break;
                case 6:                    // 2_6 상위메뉴 복귀
                    break;
                default:                   // 세부메뉴 입력예외처리
                    System.out.println(" ▶ 잘못된 접근입니다. 다시 입력해 주세요");
            }
            if (choice == 6) {
                break;
            }
        }


    }   // 2. 채널 관리 메서드

}
