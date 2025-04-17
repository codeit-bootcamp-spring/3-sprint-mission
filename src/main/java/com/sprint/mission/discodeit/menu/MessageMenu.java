package com.sprint.mission.discodeit.menu;


import com.sprint.mission.discodeit.service.MessageService;
import java.util.Scanner;

public class MessageMenu {
    private final Scanner scanner;
    private final MessageService messageService;

    public MessageMenu(Scanner scanner, MessageService messageService) {
        this.scanner = scanner;
        this.messageService = messageService;
    }

    public void run() {
        while (true) {
            System.out.println("1. 메시지 작성\t2. 작성한 메시지 출력\t3. 메시지 수정\t4. 메시지 삭제\t5. 이전 메뉴로 돌아가기");
            int n = Integer.parseInt(scanner.nextLine());

            switch (n) {
                case 1 -> {
                    System.out.println("작성하실 메시지를 입력해 주세요.");
                    String messageText = scanner.nextLine();
                    messageService.inputMessage(messageText);
                    System.out.println("메시지 작성 완료!");
                }
                case 2 -> messageService.outputUserMessage();
                case 3 -> {
                    System.out.println("수정할 메시지의 번호를 입력해 주세요.");
                    messageService.outputUserMessage();
                    int messageNumber = Integer.parseInt(scanner.nextLine());
                    System.out.println("수정할 메시지를 입력해 주세요.");
                    String newMessage = scanner.nextLine();
                    messageService.updateUserMessage(messageNumber, newMessage);
                }
                case 4 -> {
                    System.out.println("삭제할 메시지의 번호를 입력해 주세요.");
                    messageService.outputUserMessage();
                    int deleteMessageNumber = Integer.parseInt(scanner.nextLine());
                    messageService.deleteUserMessage(deleteMessageNumber);
                    System.out.println("삭제가 완료됐습니다.");
                }
                case 5 -> {
                    return;
                }
                default -> System.out.println("올바른 번호를 입력해 주세요.");
            }
        }
    }
}
