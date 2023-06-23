package com.Controller;

import com.domin.FollowingGroup;
import com.domin.R;
import com.domin.UserFollowing;
import com.service.UserFollowingService;
import com.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserFollowingController {

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private UserSupport userSupport;

    //TODO 添加关注用户
    @PostMapping("/user-followings")
    public R addUserFollowings(@RequestBody UserFollowing userFollowing) {
        Long userId = userSupport.getCurrentUserId();
        userFollowing.setUserId(userId);
        userFollowingService.addUserFollowings(userFollowing);
        return R.success();
    }

    //TODO 获取关注列表
    @GetMapping("/user-followings")
    public R getUserFollowings() {
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> result = userFollowingService.getUserFollowings(userId);
        return R.success(result);
    }

    //TODO 获取粉丝
    @GetMapping("/user-fans")
    public R getUserFans() {
        Long userId = userSupport.getCurrentUserId();
        List<UserFollowing> fans = userFollowingService.getUserFans(userId);
        return R.success(fans);
    }


    //TODO 用户新建关注分组
    //TODO 返回一个分组Id
    @PostMapping("/user-following-group")
    public R addUserFollowingGroups(@RequestBody FollowingGroup followingGroup) {
        Long userId = userSupport.getCurrentUserId();
        followingGroup.setUserId(userId);
        Long groupId = userFollowingService.addUserFollowingGroups(followingGroup);
        return R.success(groupId);
    }


    //TODO 获取用户新建分组
    @GetMapping("/user-following-group")
    public R getUserFollowingGroups() {
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> list = userFollowingService.getUserFollowingGroups(userId);
        return R.success(list);
    }
}
