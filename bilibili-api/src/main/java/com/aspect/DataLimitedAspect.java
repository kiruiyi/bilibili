package com.aspect;

import com.constant.AuthRoleConstant;
import com.domin.UserMoment;
import com.domin.annotation.ApiLimitedRole;
import com.domin.auth.UserRole;
import com.exception.ConditionException;
import com.service.UserRoleService;
import com.support.UserSupport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Aspect
@Component
public class DataLimitedAspect {
    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;


    //TODO 意思是 使用了该注解的都会被代理 @DataLimited
    @Pointcut("@annotation(com.domin.annotation.DataLimited)")
    public void check() {

    }


    @Before("check()")
    public void doBefore(JoinPoint joinPoint) {
        Long userId = userSupport.getCurrentUserId();
        //TODO 根据用户Id  查角色
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        // TODO 获取注解中的值

        //TODO 获取角色中的 Code
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());

        //TODO 获取方法的参数
        Object[] args = joinPoint.getArgs();

        //TODO 如果方法的参数含有 UserMoment 查看是否违规数据 限制参数传入

        for (Object arg : args) {
            if (arg instanceof UserMoment) {
                UserMoment userMoment = (UserMoment) arg;
                String type = userMoment.getType();
                //TODO 如果用户的角色为Lv0  且发布的类型 不是 0  权限不足
                //TODO 因为角色Lv0只能发布类型为0的
                if (roleCodeSet.contains(AuthRoleConstant.ROLE_CODE_LV1) && (!"0".equals(type))) {
                    throw new ConditionException("参数异常！");
                }
            }
        }
    }

}
