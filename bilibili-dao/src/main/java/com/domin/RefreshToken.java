package com.domin;

import lombok.Data;

import java.util.Date;

@Data
public class RefreshToken {

    private Long id;
    private Long userId;
    private String refreshToken;
    private Date createTime;
}
