package com.lyt.injectview.library.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)//元注解 作用于注解之上 注解的注解
@Retention(RetentionPolicy.RUNTIME)
public  @interface EventBase {
    //事件的三个重要成员
   // 1、setOnxxxListener() 方法名
   String listenerSetter();
   // 2、监听的对象   View.OnXXXListener

   Class<?> listenerType();
   //3、回调方法 onClick(View v)
   String callBackListener();

}
