package com.fengdis.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 *  try{
 *      try{
 *          doBefore();//对应@Before注解的方法切面逻辑
 *          method.invoke();
 *      }finally{
 *          doAfter();//对应@After注解的方法切面逻辑
 *      }
 *      doAfterReturning();//对应@AfterReturning注解的方法切面逻辑
 *  }catch(Exception e){
 *      doAfterThrowing();//对应@AfterThrowing注解的方法切面逻辑
 *  }
 */
@Aspect
@Component
public class SpringBootAspect {

    /**
     * 定义一个切入点
     * @author:SimpleWu
     * @Date:2018年10月12日
     */
    @Pointcut("execution(* com.fengdis..api.*(..))")
    public void aop(){}

    /**
     * 定义一个前置通知
     * @author:SimpleWu
     * @Date:2018年10月12日
     */
    @Before("aop()")
    public void aopBefore(){
        System.out.println("前置通知 SpringBootAspect....aopBefore");
    }

    /**
     * 定义一个后置通知  欢迎关注公众号 Web项目聚集地
     * @author:SimpleWu
     * @Date:2018年10月12日
     */
    @After("aop()")
    public void aopAfter(){
        System.out.println("后置通知  SpringBootAspect....aopAfter");
    }

    /**
     * 处理未处理的JAVA异常
     * @author:SimpleWu
     * @Date:2018年10月12日
     */
    @AfterThrowing(pointcut="aop()",throwing="e")
    public void exception(Exception e){
        System.out.println("异常通知 SpringBootAspect...exception .." + e);
    }

    /**
     * 环绕通知
     * @author:SimpleWu
     * @throws Throwable
     * @Date:2018年10月12日
     */
    @Around("aop()")
    public void around(ProceedingJoinPoint invocation) throws Throwable{
        System.out.println("SpringBootAspect..环绕通知 Before");
        invocation.proceed();
        System.out.println("SpringBootAspect..环绕通知 After");
    }
}