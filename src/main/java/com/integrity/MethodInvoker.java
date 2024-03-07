package com.integrity;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

//@Component
public class MethodInvoker implements Serializable {
    private Object targetObject;
    private final Map<String, Method> methodCache; // = new ConcurrentHashMap<>();

    public MethodInvoker(Object targetObject) {
        this.targetObject = targetObject;
        this.methodCache = new ConcurrentHashMap<>();
    }

    public void setCurrentTargetObject(Object targetObject){
        this.targetObject = targetObject;
    }

    public Object invokeMethod(String methodName, Boolean firstFlag, Object... args) throws Exception {
        // Check if the method is already cached
        if(firstFlag){
            Method method = getDeclaredMethod(methodName);
            methodCache.put(methodName, method);
            return method.invoke(targetObject, args);
        }

        Method method = methodCache.computeIfAbsent(methodName, this::getDeclaredMethod);

        // Invoke the method using reflection
        return method.invoke(targetObject, args);
    }

    private Method getDeclaredMethod(String methodName) {
        // Use reflection to get the declared method
        try {
            return targetObject.getClass().getDeclaredMethod(methodName, InputStream.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method not found: " + methodName, e);
        }
    }
}