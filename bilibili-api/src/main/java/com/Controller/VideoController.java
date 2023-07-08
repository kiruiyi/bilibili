package com.Controller;

import com.domin.PageResult;
import com.domin.R;
import com.domin.Video;
import com.service.VideoService;
import com.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class VideoController {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private VideoService videoService;

    //TODO 视频投稿
    @PostMapping("/videos")
    public R addvideos(@RequestBody Video video) {
        Long userId = userSupport.getCurrentUserId();
        video.setUserId(userId);
        videoService.addVideos(video);
        return R.success();
    }

    //TODO 视频分页查询 通过分区筛选
    @GetMapping("/videos")
    public R pageListVideos(Integer size, Integer no, String area) {
        //TODO area 分区

        PageResult<Video> result = videoService.pageListVideos(size, no, area);

        return R.success(result);
    }


    //TODO 视频下载 播放 (分片)
    @GetMapping("/video-slice")
    public void viewVideoOnlineBySlice(HttpServletRequest request,
                                       HttpServletResponse response,
                                       String path) throws Exception {
        videoService.viewVideoOnlineBySlice(request, response, path);
    }

    //TODO  视频点赞
    @PostMapping("/video-likes")
    public R addVideoLike(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoLike(userId, videoId);
        return R.success();
    }


    //TODO  视频取消点赞
    @DeleteMapping("/video-likes")
    public R deleteVideoLike(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoLike(userId, videoId);
        return R.success();
    }

    //TODO 查询视频点赞数量
    //TODO 如果用户登录过 需要判断该用户是否点赞过
    @GetMapping("/video-likes")
    public R getVideoLikes(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        } catch (Exception e) {
        }
        Map<String, Object> result = videoService.getVideoLikes(userId, videoId);
        return R.success(result);
    }


}
