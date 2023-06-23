package com.domin.auth;

import lombok.Data;

import java.util.Date;
@Data
public class AuthRole {
    private Long id;
    private String name;
    private String code;
    private Date createTime;
    private Date updateTime;
}
