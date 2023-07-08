package com.service;

import com.Util.FastDFSUtil;
import com.dao.VideoDao;
import com.domin.PageResult;
import com.domin.Video;
import com.domin.VideoLike;
import com.domin.VideoTag;
import com.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoService {

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;


    @Transactional
    public void addVideos(Video video) {
        videoDao.addVideo(video);
        Long videoId = video.getId();
        List<VideoTag> tagList = video.getVideoTagList();
        tagList.forEach(item -> item.setVideoId(videoId));
        //TODO 批量添加视频标签
        videoDao.batchAddVideoTags(tagList);
    }


    //    TODO 分页获取视频
    public PageResult<Video> pageListVideos(Integer size, Integer no, String area) {
        if (size == null || no == null) {
            throw new ConditionException("参数异常");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("start", (no - 1) * size);
        map.put("limit", size);
        map.put("area", area);
        List<Video> list = new ArrayList<>();
        Integer total = videoDao.pageCountVideos(map);
        if (total > 0) {
            list = videoDao.pageListVideos(map);
        }
        return new PageResult<>(total, list);

    }

    //TODO 视频在线播放
    public void viewVideoOnlineBySlice(HttpServletRequest request, HttpServletResponse response, String path) throws Exception {
        fastDFSUtil.viewVideoOnlineBySlice(request, response, path);
    }

    public void addVideoLike(Long userId, Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(userId, videoId);
        if (videoLike != null) {
            throw new ConditionException("已经点赞过");
        }
        VideoLike vl = new VideoLike();
        vl.setUserId(userId);
        vl.setVideoId(videoId);
        videoDao.addVideoLike(vl);
    }

    public void deleteVideoLike(Long userId, Long videoId) {
        videoDao.deleteVideoLike(userId, videoId);
    }

    public Map<String, Object> getVideoLikes(Long userId, Long videoId) {
        Long count = videoDao.getVideoLikesCount(videoId);
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(userId, videoId);
        boolean like = videoLike != null;
        Map<String, Object> result = new HashMap<>();
        result.put("like", like);
        result.put("count", count);
        return result;
    }
}

