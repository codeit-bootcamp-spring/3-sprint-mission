package com.sprint.mission.discodeit.validator;

import java.util.ArrayList;
import java.util.List;

// User의 Password는 보안상 업데이트 기능을 분리함
// 이는 Web API 구축 중에서도 Update의 로직을 분리해서 적용
// 해당 기능의 보안성 향상을 위해서 비밀번호 생성 및 수정 시 비밀번호 규칙 적용
public class PasswordValidator {

    // 비밀번호 검증 : 대소문자, 숫자, 특수문자 포함, 최소 길이 8자 이상
    public static List<String> getPasswordValidationErrors(String password) {

        // 정규식 검증 시, 특정 에러 메시지 포함
        List<String> errors = new ArrayList<>();

        // 에러 메시지
        // 공란 불가
        if (password == null || password.isEmpty()) {
            errors.add("비밀번호는 공란일 수 없습니다, 비밀번호를 입력해주세요.");
            // 공란은 추가 검증 없이 실패 처리
            return errors;
        }

        // 검증할 대상.matches(검증할 정규식) : 대상의 정규식을 검토하여 확인하고 결과에 따라 true 혹은 false 반환
        // true : 정규식이 충족됨

        // 한글 미허용
        if (password.matches(".*[\\uAC00-\\uD7A3\\u3131-\\u318E].*")) {
            errors.add("비밀번호에 한글을 포함할 수 없습니다");
        }

        // (?=.*[a-z]) : 소문자 포함
        if (!password.matches(".*[a-z].*")) {
            errors.add("알파벳 소문자를 포함해야 합니다");
        }

        // (?=.*[A-Z]) : 대문자 포함
        if (!password.matches(".*[A-Z].*")) {
            errors.add("알파벳 대문자를 포함해야 합니다");
        }

        // (?=.*\\d) : 숫자 포함
        if (!password.matches(".*\\d.*")) {
            errors.add("숫자를 포함해야 합니다");
        }

        // (?=.*[$@$!%*?&]) : 특수문자 포함
        if (!password.matches(".*[$@$!%*?&].*")) {
            errors.add("특수문자를 포함해야 합니다. ( 예 : !, @, $, % 등 )");
        }

        // 최소 8자 이상
        if (password.length() < 8) {
            errors.add("비밀번호는 최소 8자 이상이어야 합니다");
        }

        return errors;
    }
}