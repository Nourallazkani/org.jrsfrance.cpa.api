package org.sjr.babel.web.interceptor;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component @Aspect
public class SecurityInterceptor {

	private @Autowired HttpServletRequest req;
	
	@Around("execution(@javax.annotation.security.RolesAllowed * org.sjr.babel.web.endpoint.*.*(..))")
	public Object checkIfCallerHasAccess(ProceedingJoinPoint pjp) throws Throwable{
		System.out.println("inside checkIfCallerHasAccess for "+pjp);
		//String accessKey = req.getHeader("accessKey");
		//System.out.println("accessKey for current user : "+accessKey);
		return pjp.proceed();
	}
}
