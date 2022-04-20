package com.project.market.infra.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Aspect
@Service
public class SqlTraceAspect {

    @Around("@annotation(com.project.market.infra.aop.Trace)")
    public Object doSqlTrace(ProceedingJoinPoint joinPoint) throws Throwable {
        String uid = UUID.randomUUID().toString().substring(0, 8);
        log.info("[CODE : " + uid + "] ### 메서드를 실행합니다 ###");
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long end = System.currentTimeMillis();
        log.info("[CODE : " + uid + "] ### 메서드를 종료합니다 ###");
        log.info("[CODE : " + uid + "] 메서드 실행시간 = " + (end - start) + "ms");

        return proceed;
    }
}
