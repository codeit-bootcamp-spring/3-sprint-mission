package com.sprint.mission.discodeit.repository.file;


import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.util.FileioUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private Map<String, ReadStatus> readStatusData;
    private Path path;

    public FileReadStatusRepository(@Qualifier("readStatusFilePath") Path path) {
        this.path = path;
        if(!Files.exists(this.path)){
            try{
                Files.createFile(this.path);
                FileioUtil.save(this.path, new HashMap<>());
            } catch (Exception e) {
                throw new RuntimeException("메시지 읽은 시간을 저장한 파일 초기화 불가능",e);
            }
        }
        FileioUtil.init(this.path);
        this.readStatusData = FileioUtil.load(this.path, ReadStatus.class);

    }


    @Override
    public Optional<ReadStatus> findById(UUID id){
        if(!readStatusData.containsKey(id.toString())){
            throw new NoSuchElementException("ReadStatus not found with id " + id);
        }
        return Optional.ofNullable(readStatusData.get(id.toString()));
    }

    @Override
    public ReadStatus save(ReadStatus readStatus){
        readStatusData.put(readStatus.getId().toString(), readStatus);
        FileioUtil.save(this.path, readStatusData);
        return readStatus;
    }

    @Override
    public boolean existsById(UUID id){
        return readStatusData.containsKey(id.toString());
    }

    @Override
    public void deleteById(UUID id){
        readStatusData.remove(id.toString());
        FileioUtil.save(this.path, readStatusData);
    }

    @Override
    public void deleteByChannelId(UUID channelId){
        readStatusData.values().removeIf(readStatus -> readStatus.getChannelId().equals(channelId));
        FileioUtil.save(this.path, readStatusData);
    }

}
