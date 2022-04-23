package com.atstar.sell.aspect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Dawn
 * @Date: 2022/4/12 01:58
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Pointcut(value = "execution(* com.atstar.sell.controller..*.*(..)) && !execution(* com.atstar.sell.controller.seller.SellerUserController.*(..)) && !execution(* com.atstar.sell.controller.seller.WebSocket.*(..))")
    public void log() {

    }

    @Before(value = "log()")
    public void doBefore(JoinPoint joinPoint) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();

        String ip = request.getRemoteAddr();
        String url = request.getRequestURL().toString();
        String httpMethod = request.getMethod();
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        RequestInfo requestInfo = new RequestInfo();

        requestInfo.setUrl(url);
        requestInfo.setIp(ip);
        requestInfo.setClassMethod(classMethod);
        requestInfo.setHttpMethod(httpMethod);
        requestInfo.setRequestParams(getRequestParam(joinPoint));

        log.info("Request Info: {}", gson.toJson(requestInfo));
    }

    @Around(value = "log()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        log.info("===========================start=================================");
        long start = System.currentTimeMillis();

        Object result = proceedingJoinPoint.proceed();

        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setResult(result);
        resultInfo.setTimeCost(System.currentTimeMillis() - start);
        log.info("Result Info: {}", gson.toJson(resultInfo));

        log.info("===========================end=================================");
        return result;
    }


    private Map<String, Object> getRequestParam(JoinPoint joinPoint) {

        // 参数名
        String[] paramNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();

        // 参数值
        Object[] paramValues = joinPoint.getArgs();

        return buildRequestParam(paramNames, paramValues);
    }

    private Map<String, Object> buildRequestParam(String[] paramNames, Object[] paramValue) {
        Map<String, Object> requestParams = new HashMap<>();

        for (int i = 0; i < paramNames.length; i++) {
            Object value = paramValue[i];
            // 如果是文件对象
            if (value instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) value;
                value = file.getOriginalFilename(); // 获取文件名
            }

            requestParams.put(paramNames[i], value);
        }
        return requestParams;
    }

    @Data
    private class RequestInfo {
        private String ip;
        private String url;
        private String httpMethod;
        private String classMethod;
        private Object requestParams;
    }

    @Data
    private class ResultInfo {
        private Object result;
        private Long timeCost;
    }
}
