package com.domin.annotation;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

//TODO 自定义注解 做权限验证
@Retention(RetentionPolicy.RUNTIME)  //TODO 运行时
@Target(ElementType.METHOD)          //TODO 注解可以放在哪里   (方法上
@Documented
@Component
public @interface DataLimited {


}
