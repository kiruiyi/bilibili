package com.domin.auth;

import lombok.Data;

import java.util.Date;

@Data
public class AuthRoleMenu {

    private Long id;
    private Long roleId;
    private Long menuId;
    private Date createTime;

    private AuthMenu authMenu;
}
