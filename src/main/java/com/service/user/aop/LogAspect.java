package com.service.user.aop;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/*
 * This Aspect class is used to write log before and after a joint point
 * for the method called
 */
@Aspect
@Component
public class LogAspect {
	
	// ===============================
	// Class variables
	// ===============================
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);
	
	// ===============================
	// inatance variables
	// ===============================
	
	@Pointcut("within(com.service.user.controller..*)"
			+"&& !@annotation(com.service.user.aop.NotLog)")
	public void controllerLoggingPointcut() {
	}
	
	@Pointcut("within(com.service.user.service..*)"
			+"&& !@annotation(com.service.user.aop.NotLog)")
	public void serviceLoggingPointcut() {
		
	}
	
	/**
	 * Aspect to log controller method invocation including response times
	 */
	@Around("controllerLoggingPointcut()")
	public Object logForController(final ProceedingJoinPoint joinPoint) throws Throwable {
		return log(joinPoint);
	}
	
	/**
	 * Aspect to log Service method invocation including response times
	 */
	@Around("serviceLoggingPointcut()")
	public Object logForservice(final ProceedingJoinPoint joinPoint) throws Throwable {
		return log(joinPoint);
	}

	private Object log(ProceedingJoinPoint joinPoint) throws Throwable {
		String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		String params = StringUtils.EMPTY;
		
		Object[] args = joinPoint.getArgs();
		if(args != null) {
			for (int i = 0; i < args.length; i++) {
				String arg = ArrayUtils.toString(args[i]);
				if(StringUtils.contains(arg, "[")) {
					arg = StringUtils.substringAfter(arg, "[");
					arg = StringUtils.substringBeforeLast(arg, "]");
				}
				params = params + (StringUtils.isBlank(params)?"":",")+arg;
			}
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("CLASS=\"{}\" METHOD=\"{}\" REQ_PARAMS=\"{}\"",className,methodName,params);
			}
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Object respObject = joinPoint.proceed();
		stopWatch.stop();
			LOGGER.info("CLASS=\"{}\" METHOD=\"{}\" TIME=\"{}\" RESP_OBJ=\"{}\"",className,methodName,stopWatch.getTime(),respObject);
		return respObject;
	}
}
