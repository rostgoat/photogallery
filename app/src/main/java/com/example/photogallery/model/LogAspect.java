package com.example.photogallery.model;

import android.graphics.Paint;
import android.util.CloseGuard;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.example.photogallery.LogAnnotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class LogAspect {
    private static final String TAG = LogAspect.class.getName();

 /*  @Pointcut("execution(void *.onClick(..))")
    public void onClickEntryPoint() {
    }*/

  /*  @Before("onClickEntryPoint()")
    public void onClickAdvice(JoinPoint joinPoint) {
        Log.d(TAG, "On Click ==> Clicked on : " + ((Button)joinPoint.getArgs()[0]).getText());
    }
*/
  /*  @Pointcut("call(* MainActivity.scrollPhotos(..) && args(i)")
    void onScrollPhotos(View view) {}

    @Before("onScrollPhotos(view)")
    public void onScrollPhotosAdvice(ProceedingJoinPoint thisJoinPoint, View view) {
        Log.d(TAG, "Scroll Photos Clicked on : " + view.toString());
    }*/

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

  /*  @Pointcut("execution(* android.view.View.OnClickListener.onClick(..))")
    public void onClickEntryPoint() {
    }

    @Before("onClickEntryPoint()")
    public void onClickBefore(JoinPoint joinPoint) {
        Log.d(TAG, "Before Advice ==> Clicked on : " + ((Button)joinPoint.getArgs()[0]).getText());
    }

    @Around("onClickEntryPoint()")
    public void onClickAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.d(TAG, "Around Advice ==> Clicked on : " + ((Button)joinPoint.getArgs()[0]).getText());

        joinPoint.proceed();
    }

    @After("onClickEntryPoint()")
    public void onClickAfter(JoinPoint joinPoint) {
        Log.d(TAG, "After Advice ==> Clicked on : " + ((Button)joinPoint.getArgs()[0]).getText());
    }

    @AfterReturning(pointcut = "onClickEntryPoint()")
    public void onClickAfterReturning() {
        Log.d(TAG, "AfterReturning Advice ==>");
    }*/

}
