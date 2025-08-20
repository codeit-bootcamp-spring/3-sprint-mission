package com.sprint.mission.discodeit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect // 이 클래스가 AOP임을 명시
@Component // Bean으로 등록
public class GlobalLogger {

  // 1. 모든 com.example.demo 패키지 이하의 모든 클래스의 모든 메서드에 적용 (패키지명은 프로젝트에 맞게!)
  @Around("execution(* com.sprint.mission.discodeit..*(..)) && !execution(* com.sprint.mission.discodeit..*find*(..))")
  public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
    // 2. 메서드 이름과 파라미터 가져오기
    String methodName = joinPoint.getSignature().toShortString();
    Object[] args = joinPoint.getArgs();

    // 3. 호출 전 로그 출력
    System.out.println("[LOG] 호출: " + methodName + " 파라미터: " + Arrays.toString(args));

    // 4. 실제 메서드 실행
    Object result = joinPoint.proceed();

    // 5. 반환값 로그 출력
    System.out.println("[LOG] 반환값: " + result);

    return result;
  }
}