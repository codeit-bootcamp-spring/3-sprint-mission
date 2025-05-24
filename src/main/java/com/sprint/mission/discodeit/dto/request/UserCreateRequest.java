package com.sprint.mission.discodeit.dto.request;



/* DTO 생성 시 class와 record 차이
 * class : 일반 객체 생성, 보일러플레이트 수동 작성, 유연성 좋음
 * record : 불변 객체 생성, 자동 생성되나 유연성이 제한적임
 * */
// CRUD 메서드의 유동성으로 class가 적합하다 판단됨

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Data
@NoArgsConstructor
@Schema(description = "생성할 유저 정보")
public final class UserCreateRequest {

  @Schema(description = "생성할 사용자 이름", example = "홍길동")
  @NotBlank(message = "이름을 작성해주세요")
  private String userName;

  @Schema(description = "생성할 사용자 계정의 이메일", example = "test@example.com", type = "string", format = "email")
  @NotBlank(message = "이메일을 입력해주세요")
  private String email;

  @Schema(description = "생성할 사용자 계정의 비밀번호", example = "1234", type = "string", format = "password")
  @NotBlank(message = "비밀번호를 입력해주세요")
  private String password;
}
