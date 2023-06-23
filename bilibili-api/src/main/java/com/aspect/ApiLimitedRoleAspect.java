package com.aspect;

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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Order(1)   //TODO 优先级
@Aspect
@Component
public class ApiLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.domin.annotation.ApiLimitedRole)")
    public void check() {

    }

    @Before("check() && @annotation(apiLimitedRole)")
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole) {
        Long userId = userSupport.getCurrentUserId();
        //TODO 根据用户Id  查角色
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        // TODO 获取注解中的值
        String[] limitRoleCodeList = apiLimitedRole.limitRoleCodeList();

        Set<String> limitRoleCodeSet = Arrays.stream(limitRoleCodeList).collect(Collectors.toSet());
        //TODO 获取角色中的 Code
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());

        //TODO 取交集  roleCodeSet剩余的就是交集
        roleCodeSet.retainAll(limitRoleCodeSet);

        if (roleCodeSet.size() > 0) {
            throw new ConditionException("权限不足!");
        }


    }


}
