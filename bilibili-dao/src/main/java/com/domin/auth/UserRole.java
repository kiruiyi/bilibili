package com.domin.auth;


import lombok.Data;

import java.util.Date;

@Data
public class UserRole {
    private Long id;
    private Long userId;
    private Long roleId;
    private Date createTime;

    private String roleName;
    private String roleCode;

}
