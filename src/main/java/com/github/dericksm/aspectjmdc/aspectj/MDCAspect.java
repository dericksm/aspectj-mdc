package com.github.dericksm.aspectjmdc.aspectj;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.dericksm.aspectjmdc.ItemSerializer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.IntStream;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Aspect
public class MDCAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MDCAspect() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Object.class, new ItemSerializer());
        objectMapper.registerModule(module);
    }

    @Around("@annotation(com.github.dericksm.aspectjmdc.aspectj.LogMethodEntryAndExit)")
    public Object aroundLogBeginAndEnd(final ProceedingJoinPoint jointPoint) throws Throwable {
        var method = ((MethodSignature) jointPoint.getSignature()).getMethod();
        var logger = LoggerFactory.getLogger(method.getDeclaringClass());

        logger.info("{} - start", method.getName());

        var start = System.currentTimeMillis();
        var response = jointPoint.proceed();
        var end = System.currentTimeMillis();

        logger.info("{} - finished in {} milliseconds", method.getName(), end - start);
        logger.info("response is {}", objectMapper.writeValueAsString(response));

        return response;
    }

    @Before("@annotation(LogMDC) && execution(* *(..))")
    public void beforeMethodAnnotation(JoinPoint joinPoint) {
        String[] argNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        Object[] values = joinPoint.getArgs();
        if (argNames.length != 0) {
            IntStream.range(0, values.length)
                .filter(i -> Objects.nonNull(values[i]))
                .forEach(i -> MDC.put(argNames[i], String.valueOf(values[i])));
        }
    }

    @After("@annotation(ClearMDC) && execution(* *(..))")
    public void afterMethodAnnotation() {
        MDC.clear();
    }

    @Before("execution(* *(.., @LogMDC (*), ..))")
    public void beforeParamsAnnotation(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] argNames = methodSignature.getParameterNames();
        Object[] values = joinPoint.getArgs();
        Method method = methodSignature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == LogMDC.class) {
                    MDC.put(argNames[i], String.valueOf(values[i]));
                }
            }
        }
    }
}