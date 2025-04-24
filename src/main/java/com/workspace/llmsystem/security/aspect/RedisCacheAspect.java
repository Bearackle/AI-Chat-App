package com.workspace.llmsystem.security.aspect;

import com.workspace.llmsystem.security.annotation.CacheException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class RedisCacheAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheAspect.class);
    @Pointcut("execution(public * com.workspace.llmsystem.service.*CacheService.*(..))")
    public void cacheAspect() {
    }
    @Around("cacheAspect()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Object result = null;
        try{
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            if (method.isAnnotationPresent(CacheException.class)) {
                throw throwable;
            } else {
                LOGGER.error(throwable.getMessage());
            }
        }
        return result;
    }
}
