package com.domin;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Video {

    private Long id;
    private Long userId;
    private String url;  //TODO 视频链接
    private String thumbnail;  //TODO 封面链接
    private String title;  //TODO 视频标题
    private String type;   //TODO 视频类型
    private String duration; //TODO 视频时长
    private String area;  //TODO 所在分区
    private List<VideoTag> videoTagList; //TODO 视频标签
    private String description; //TODO 视频简介
    private Date createTime;
    private Date updateTime;


}
