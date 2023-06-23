package com.service;

import com.constant.AuthRoleConstant;
import com.domin.auth.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAuthService {


    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AuthRoleService authRoleService;


    //TODO 获取用户的权限列表
    public UserAuthorities getUserAuthorities(Long userId) {
        //TODO 根据用户ID  获取 该用户的角色 一个用户可以有多个角色 返回一个列表
        //TODO
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);

        //TODO 获取 角色id
        Set<Long> roleIdSet = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());

        //TODO 根据角色id  查询出所有的权限
        List<AuthRoleElementOperation> roleElementOperationList = authRoleService.getRoleElementOperationByRoleIds(roleIdSet);
        List<AuthRoleMenu> roleMenuList = authRoleService.getRoleMenuByRoleIds(roleIdSet);

        UserAuthorities result = new UserAuthorities();
        result.setAuthRoleMenuList(roleMenuList);
        result.setRoleElementOperationList(roleElementOperationList);
        return result;
    }

    public void addUserDefaultRole(Long userId) {
        UserRole userRole = new UserRole();
        AuthRole role = authRoleService.getRoleByCode(AuthRoleConstant.ROLE_CODE_LV0);
        userRole.setUserId(userId);
        userRole.setRoleId(role.getId());
        userRoleService.addUserRole(userRole);
    }
}
