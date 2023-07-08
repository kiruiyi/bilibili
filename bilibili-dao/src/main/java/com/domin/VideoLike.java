package com.domin;

import lombok.Data;

import java.util.Date;

@Data
public class VideoLike {

    private Long id;
    private Long userId;
    private Long videoId;
    private Date createTime;
}
