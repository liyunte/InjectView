package com.lyt.injectview.library.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ListenerInvocationHandler implements InvocationHandler {
    private Object target;
    private HashMap<String, Method> methodMap = new HashMap<>();
    private long lastTime;
    public static long TIME = 1000;//点击事件的时间间隔
    public static boolean intercepted = true;//是否开启拦截多次点击事件

    public ListenerInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (target != null) {
            String methodName = method.getName();
            method = methodMap.get(methodName);
            if (method != null) {
                if (intercepted) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime >= TIME) {
                        lastTime = currentTime;
                        method.invoke(target, args);
                    }
                } else {
                    method.invoke(target, args);
                }
            }
        }
        return true;
    }

    /**
     * 拦截的添加
     *
     * @param methodName 本应该执行的方法，onClick(),拦截
     * @param method     执行自定义的方法 ，show() click()
     */
    public void addMethod(String methodName, Method method) {
        methodMap.put(methodName, method);
    }
}
