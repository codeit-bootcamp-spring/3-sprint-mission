package com.sprint.mission.discodeit.service.jcf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.*;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

public class JCFChannelService implements ChannelService {
    public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public final List<Channel> data;
    public JCFChannelService(){
        this.data = new ArrayList<>();
    }

    @Override
    public void create(User user) throws IOException {
        boolean isFinished = false;
        while (!isFinished) {
            System.out.println("새로 만들 채팅방 이름을 입력하십시오.");
            String name = reader.readLine();
            boolean isDuplicate;
            isDuplicate = this.readAll(user).stream()
                    .anyMatch(c -> c.getName().equals(name));
            if (isDuplicate) {
                try (Scanner scanner = new Scanner(System.in)) {
                    System.out.println("같은 이름의 채팅방이 있습니다." + name + "(으)로 생성하시겠습니까? (Y/N)");
                    String input = scanner.nextLine();
                    if (input.equals("N")) {
                        System.out.println("이름을 다시 입력하십시오.");
                    } else if (input.equals("Y")) {
                        this.data.add(new Channel(name, user));
                        System.out.println("채팅방이 생성되었습니다.");
                        isFinished = true;
                    } else {
                        System.out.println("Y 또는 N 을 입력해주십시오.");
                    }
                } catch (IllegalFormatException e) {
                    System.out.println("다시 시도해 주십시오.");
                }
            }
        }
    }

    @Override
    public Channel read(User user, UUID id) {
        return this.data.stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 없습니다."));
    }

    @Override
    public List<Channel> readAll(User user) {
        return this.data.stream()
                .collect(Collectors.toList());
    }

    public List<Channel> readByName(User user, String name) {
        return this.data.stream()
                .filter(c -> c.getName().contains(name))
                .collect(Collectors.toList());
    }

    @Override
    public void updateName(User user, UUID id) throws IOException {
        String name = reader.readLine();
        boolean isDuplicate;
        isDuplicate = this.readAll(user).stream()
                .anyMatch(c -> c.getName().equals(name));
        if (isDuplicate) {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("같은 이름의 채팅방이 있습니다." + name + "(으)로 수정하시겠습니까? (Y/N)");
                String input = scanner.nextLine();
                if (input.equals("N")) {
                    System.out.println("이름을 다시 입력하십시오.");
                } else if (input.equals("Y")) {
                    this.data.stream()
                            .filter(c -> c.getId().equals(id))
                            .forEach(c -> {
                                c.updateById(id, name);
                                c.updateDateTime();
                            });
                    System.out.println("수정되었습니다.");
                } else {
                    System.out.println("Y 또는 N 을 입력해주십시오.");
                }
            } catch (IllegalFormatException e) {
                System.out.println("다시 시도해 주십시오.");
            }
        }
    }

    @Override
    public void delete(User user, UUID id) {
        this.data.removeIf(u -> u.getId().equals(id));
        System.out.println(this.read(user, id).getName() + "채팅방이 삭제되었습니다.");
    }
}
