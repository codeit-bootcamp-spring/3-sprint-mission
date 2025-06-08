package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  public List<ReadStatus> findAllByUserId(UUID userId);

  public List<ReadStatus> findAllByChannelId(UUID channelId);

  public void deleteAllByChannelId(UUID channelId);

//      /* CrudRepository의 기본 메소드 */
//    public ReadStatus save(ReadStatus readStatus);
//
//      /* CrudRepository의 기본 메소드 */
//    public Optional<ReadStatus> findById(UUID readStatusId);
//
//      /* JpaRepository의 기본 메소드 */
//    public List<ReadStatus> findAll();
//
//      /* CrudRepository의 기본 메소드 */
//    public boolean existsById(UUID readStatusId);
//
//      /* CrudRepository의 기본 메소드 */
//    public void deleteById(UUID readStatusId);
}
