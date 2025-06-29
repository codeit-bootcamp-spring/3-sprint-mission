import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

  @Around("execution(* com.sprint.mission.discodeit.controller..*(..)) || execution(* com.sprint.mission.discodeit.service..*(..))")
  public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().toShortString();
    log.info("▶️ Start: {}", methodName);
    Object result = joinPoint.proceed();
    log.info("✅ End: {}", methodName);
    return result;
  }
}