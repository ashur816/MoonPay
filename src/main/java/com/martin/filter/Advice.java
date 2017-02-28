package com.martin.filter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author ZXY
 * @ClassName: Advice
 * @Description:
 * @date 2017/2/28 10:00
 */
@Component
@Aspect
public class Advice {

    @Pointcut("execution(* com.martin.service.impl.PayAppCenter.*(..))")
    public void pay(){}

    @Before("pay()")
    public void before(JoinPoint jp){
        System.out.println("----------前置通知----------");
        System.out.println(jp.getSignature().getName());
    }

    @After("execution(* com.martin.service.impl.PayAppCenter.*(..))")
    public void after(JoinPoint jp){
        System.out.println("----------最终通知----------");
        System.out.println(jp.getSignature().getName());
    }
}
