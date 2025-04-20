package com.sprint.mission.discodeit.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

// ChannelService 채널서비스 클래스
public class FileChannelService implements ChannelService {

    // 채널 정보를 저장할 파일 경로
    private static final String FILE_PATH = "channels.ser";

    // 메모리에 존재하는 채널 Map (프로그램 시작 시 파일에서 불러옴)
    private Map<UUID, Channel> channels = load();

    // 새로운 채널 생성 후 저장
    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);          // 새 채널 생성
        channels.put(channel.getId(), channel);       // Map에 저장
        save();                                        // 파일에도 저장
        return channel;
    }

    // ID로 채널 조회
    @Override
    public Channel findById(UUID id) {
        return channels.get(id);                      // Map에서 바로 꺼내옴
    }

    // 모든 채널 목록 반환
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels.values());    // Map의 value들을 리스트로 변환
    }

    // 채널 이름 수정
    @Override
    public Channel update(UUID id, String newName) {
        Channel channel = channels.get(id);
        if (channel != null) {
            channel.updateName(newName);              // 이름 변경
            save();                                   // 변경된 내용 저장
        }
        return channel;
    }

    // 채널 삭제
    @Override
    public void delete(UUID id) {
        channels.remove(id);                          // Map에서 삭제
        save();                                       // 저장소 반영
    }

    // 현재 Map 상태를 파일에 직렬화해서 저장
    private void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            out.writeObject(channels);                // Map 전체를 파일에 저장
        } catch (IOException e) {
            e.printStackTrace();                      // 예외 발생 시 콘솔 출력
        }
    }

    // 파일로부터 채널 Map을 복원
    private Map<UUID, Channel> load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<UUID, Channel>) in.readObject();  // 저장된 Map 불러오기
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();                      // 파일 없거나 실패 시 빈 Map 반환
        }
    }
}
