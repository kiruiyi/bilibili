package com.domin.auth;

import lombok.Data;

import java.util.List;

@Data
//TODO 两个关联表
public class UserAuthorities {

    List<AuthRoleElementOperation> roleElementOperationList;

    List<AuthRoleMenu> authRoleMenuList;

}
