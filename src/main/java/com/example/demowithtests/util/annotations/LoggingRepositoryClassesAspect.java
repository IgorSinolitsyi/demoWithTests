package com.example.demowithtests.util.annotations;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.demowithtests.util.annotations.LogColorConstants.ANSI_CYAN;
import static com.example.demowithtests.util.annotations.LogColorConstants.ANSI_RESET;

@Log4j2
@Aspect
@Component
public class LoggingRepositoryClassesAspect {
    LocalDateTime startExecutionTime;
    public  int queryCount = 0;
    public  long totalQueryDuration = 0;


    @Pointcut(value = "execution(* com.example.demowithtests.repository.EmployeeRepository.*(..))||execution(public *  org.springframework.data.repository.*.*(..))")
    public void callAtRepositoryMethods() {
    }
    @Before("callAtRepositoryMethods()")
    public void logRepositoryMethodsExecutionTimeBefore() {
        startExecutionTime = LocalDateTime.now();
    }

    @AfterReturning(value = "callAtRepositoryMethods()")
    public void logRepositoryAfter(JoinPoint joinPoint) {
        queryCount++;
        long queryDuration = Duration.between(startExecutionTime,LocalDateTime.now()).toMillis();
        totalQueryDuration = totalQueryDuration + queryDuration;

        String methodName = joinPoint.getSignature().toShortString();
        log.debug("\n"+ANSI_CYAN + "Repository: author - '"+ SecurityContextHolder.getContext().getAuthentication().getName()
                +"' called the method "+methodName + " at "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-DD hh:mm:ss,SSS"))+"\n"
                +ANSI_CYAN+"the request was executed for "+queryDuration +" mls\n"
                +ANSI_CYAN+"total duration for processing queries to the database "+totalQueryDuration +" mls\n"
                +ANSI_CYAN+"total number of queries to the database "+queryCount+ANSI_RESET);
    }
}
