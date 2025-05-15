package com.sprint.mission.discodeit.dto.request;



/* DTO 생성 시 class와 record 차이
 * class : 일반 객체 생성, 보일러플레이트 수동 작성, 유연성 좋음
 * record : 불변 객체 생성, 자동 생성되나 유연성이 제한적임
 * */
// CRUD 메서드의 유동성으로 class가 적합하다 판단됨

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class UserCreateRequest{
    private String userName;
    private String pwd;
    private String email;
    private String phoneNumber;
    private String statusMessage;
}
