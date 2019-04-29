package com.lyt.injectview.library;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyt.injectview.library.annotation.ContentView;
import com.lyt.injectview.library.annotation.EventBase;
import com.lyt.injectview.library.annotation.InjectBean;
import com.lyt.injectview.library.annotation.InjectView;
import com.lyt.injectview.library.handler.ListenerInvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class InjectManager {

    public static void inject(Activity activity) {
        injectLayout(activity);
        injectViews(activity);
        injectEvents(activity);
        injectBeans(activity);
    }


    public static View inject(Object object, LayoutInflater inflater, ViewGroup container) {
        View view = injectLayout(object, inflater, container);
        injectViews(object, view);
        injectEvents(object, view);
        injectBeans(object);
        return view;
    }

    public static void injectBeans(Object object) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            InjectBean injectBean = field.getAnnotation(InjectBean.class);
            if (injectBean != null) {
                Class<?> fieldClass = injectBean.value();
                try {
                    field.setAccessible(true);
                    if (fieldClass.isAssignableFrom(Object.class)) {
                        field.set(object, field.getType().newInstance());
                    } else {
                        field.set(object, fieldClass.newInstance());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void injectLayout(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        if (contentView == null) {
            throw new NullPointerException(clazz.getSimpleName() + " is must be @ContentView");
        }
        int layout = contentView.value();

        Method method = null;
        try {
            method = clazz.getMethod("setContentView", int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            method.invoke(activity, layout);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    private static void injectViews(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            InjectView injectView = field.getAnnotation(InjectView.class);
            if (injectView != null) {
                int id = injectView.value();
                Object view = null;
                try {
                    Method method = clazz.getMethod("findViewById", int.class);
                    view = method.invoke(activity, id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (view == null) {
                    throw new NullPointerException("not find the id of " + field.getName() + " please check the id or check the layout'id");
                }
                if (field.getType().isAssignableFrom(view.getClass())) {
                    try {
                        field.setAccessible(true);
                        field.set(activity, view);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new ClassCastException(view.getClass() + " cannot be cast to" + field.getType() + "at the field name = " + field.getName());
                }
            }

        }

    }

    private static void injectEvents(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (annotationType != null) {
                    EventBase eventBase = annotationType.getAnnotation(EventBase.class);
                    if (eventBase != null) {
                        //事件三要素
                        String listenerSetter = eventBase.listenerSetter();
                        String callBackListener = eventBase.callBackListener();
                        Class<?> listenerType = eventBase.listenerType();
                        try {
                            Method valueMethod = annotationType.getDeclaredMethod("value");
                            int[] viewIds = (int[]) valueMethod.invoke(annotation);
                            ListenerInvocationHandler handler = new ListenerInvocationHandler(activity);
                            //拦截方法，执行自定义的方法
                            handler.addMethod(callBackListener, method);
                            //代理方式完成
                            Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, handler);
                            for (int viewId : viewIds) {
                                View view = activity.findViewById(viewId);
                                Method setter = view.getClass().getMethod(listenerSetter, listenerType);
                                setter.invoke(view, listener);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    private static View injectLayout(Object inject, LayoutInflater inflater, ViewGroup container) {
        Class<?> clazz = inject.getClass();
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        if (contentView == null) {
            throw new NullPointerException(clazz.getSimpleName() + " is must be @ContentView");
        }
        int layout = contentView.value();
        View view = inflater.inflate(layout, container, false);
        if (view == null) {
            throw new NullPointerException("not find the layoutId from " + clazz.getSimpleName() + "");
        }
        return view;
    }

    private static void injectViews(Object inject, View view) {
        Class<?> clazz = inject.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            InjectView injectView = field.getAnnotation(InjectView.class);
            if (injectView != null) {
                int id = injectView.value();
                Object target = view.findViewById(id);
                if (target == null) {
                    throw new NullPointerException("not find the id of" + field.getName() + "");
                }
                if (field.getType().isAssignableFrom(target.getClass())) {
                    field.setAccessible(true);
                    try {
                        field.set(inject, target);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new ClassCastException(view.getClass() + " cannot be cast to" + field.getType() + "at the field name = " + field.getName());
                }
            }

        }
    }

    private static void injectEvents(Object inject, View view) {
        Class<?> clazz = inject.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (annotationType != null) {
                    EventBase eventBase = annotationType.getAnnotation(EventBase.class);
                    if (eventBase != null) {
                        //事件三要素
                        String listenerSetter = eventBase.listenerSetter();
                        String callBackListener = eventBase.callBackListener();
                        Class<?> listenerType = eventBase.listenerType();

                        try {
                            Method valueMethod = annotationType.getDeclaredMethod("value");
                            int[] viewIds = (int[]) valueMethod.invoke(annotation);
                            //拦截方法，执行自定义的方法
                            ListenerInvocationHandler handler = new ListenerInvocationHandler(inject);
                            handler.addMethod(callBackListener, method);
                            //代理方式完成
                            Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, handler);
                            for (int viewId : viewIds) {
                                View child = view.findViewById(viewId);
                                Method setter = child.getClass().getMethod(listenerSetter, listenerType);
                                setter.invoke(child, listener);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


}
