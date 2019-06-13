package org.dimensinfin.eveonline.neocom.annotations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LogTimeElapsed {
	//	@Component
	//	public class Logger
	//	{
	private static Logger logger = LoggerFactory.getLogger(Logger.class);

	@Around("execution(* *(..)) && @annotation(org.dimensinfin.eveonline.neocom.annotations.TimeElapsed)")
	public Object log( ProceedingJoinPoint point ) throws Throwable {
		final DateTime startTimePoint = DateTime.now();
		long start = System.currentTimeMillis();
		Object result = point.proceed();
		logger.info("<< [{}]> [TIMING]: {}"
				, this.stackCallerName()
				, new Duration(startTimePoint, DateTime.now()).getMillis() + "ms");
		//		logger.info("className={}, methodName={}, timeMs={},threadId={}", new Object[]{
		//				MethodSignature.class.cast(point.getSignature()).getDeclaringTypeName(),
		//				MethodSignature.class.cast(point.getSignature()).getMethod().getName(),
		//				System.currentTimeMillis() - start,
		//				Thread.currentThread().getId()}
		//		);
		return result;
	}

	private String stackCallerName() {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		StackTraceElement e = stacktrace[2];//maybe this number needs to be corrected
		final String methodName = e.getMethodName();
		final String className = e.getClassName();
		return className + "." + methodName;
	}
}
