package com.example.photogallery.model;

import android.util.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class LogAspect {
    private static final String TAG = LogAspect.class.getName();

    private static final String POINTCUT_METHOD =
            "execution(@com.example.photogallery.LogAnnotation * *(..))";

    @Pointcut(POINTCUT_METHOD)
    public void onLogPoint() {}

    @Before("onLogPoint()")
    public void onLogPointAdvice() {
        Log.d(TAG, "Do Something Before Logging Annotation Point");
    }

    @Around("execution(* *(..)) && @annotation(com.example.photogallery.LogAnnotation)")
    public void onLogPointAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature ms = ((MethodSignature) proceedingJoinPoint.getSignature());
        String methodName = ms.getMethod().getName();
        String methodClass = ms.getMethod().getDeclaringClass().toString();
        Log.d(TAG, "Log Point : " + methodClass + "()."+methodName+"()");

        proceedingJoinPoint.proceed();
    }

}
