package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service
 * fileName       : UserService
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */
public interface UserService {
    //    [ ] 등록
    public UUID registerUser(String username);


    //    [ ] 조회(단건, 다건)
    public List<User> findUserByUsername(UUID id);
    public List<User> findAllUsers();

    //    [ ] 수정
    public void updateUsername(UUID id,String newName);

    //    [ ] 삭제 - 없는 유저 조회시 오류 관련 메세지 표시
    public void deleteUser(UUID id);



}
