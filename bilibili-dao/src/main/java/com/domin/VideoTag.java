package com.domin;

import lombok.Data;

import java.util.Date;

@Data
public class VideoTag {

    private Long id;
    private Long videoId;
    private Long tagId;
    private Date createTime;
}
