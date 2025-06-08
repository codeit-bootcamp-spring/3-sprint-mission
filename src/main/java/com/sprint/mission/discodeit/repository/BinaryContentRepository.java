package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

//      /* CrudRepository의 기본 메소드 */
//  public List<BinaryContent> findAllById(List<UUID> ids);
//      /* CrudRepository의 기본 메소드 */
//    public BinaryContent save(BinaryContent binaryContent);
//
//      /* CrudRepository의 기본 메소드 */
//    public Optional<BinaryContent> findById(UUID binaryContentId);
//
//      /* JpaRepository의 기본 메소드 */
//    public List<BinaryContent> findAll();
//
//      /* CrudRepository의 기본 메소드 */
//    public boolean existsById(UUID binaryContentId);
//
//      /* CrudRepository의 기본 메소드 */
//    public void deleteById(UUID binaryContentId);
}
