package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<User, UUID> {

//    /* CrudRepository의 기본 메소드 */
//  public Channel save(Channel channel);
//
//    /* CrudRepository의 기본 메소드 */
//  public Optional<Channel> findById(UUID channelId);
//
//    /* JpaRepository의 기본 메소드 */
//  public List<Channel> findAll();
//
//    /* CrudRepository의 기본 메소드 */
//  public boolean existsById(UUID channelId);
//
//    /* CrudRepository의 기본 메소드 */
//  public void deleteById(UUID channelId);
}
