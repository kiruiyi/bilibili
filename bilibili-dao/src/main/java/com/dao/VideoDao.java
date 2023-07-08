package com.dao;

import com.domin.Video;
import com.domin.VideoLike;
import com.domin.VideoTag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface VideoDao {

    Integer addVideo(Video video);

    Integer batchAddVideoTags(List<VideoTag> tagList);

    Integer pageCountVideos(Map<String, Object> map);

    List<Video> pageListVideos(Map<String, Object> map);

    Video getVideoById(Long id);

    VideoLike getVideoLikeByVideoIdAndUserId(Long userId, Long videoId);

    Integer addVideoLike(VideoLike vl);

    Integer deleteVideoLike(Long userId, Long videoId);

    Long getVideoLikesCount(Long videoId);
}
