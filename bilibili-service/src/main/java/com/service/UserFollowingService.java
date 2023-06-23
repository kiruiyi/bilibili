package com.service;

import com.constant.UserConstant;

import com.dao.UserFollowingDao;
import com.domin.FollowingGroup;
import com.domin.User;
import com.domin.UserFollowing;
import com.domin.UserInfo;
import com.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private UserService userService;
    @Autowired
    private FollowingGroupService followingGroupService;


    @Transactional  //TODO 事务
    //TODO 添加用户关注
    public void addUserFollowings(UserFollowing userFollowing) {
        //TODO  检查分组
        Long groupId = userFollowing.getGroupId();
        if (groupId == null) {
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.DEFAULT_USER_FOLLOWING_GROUP_TYPE);
            userFollowing.setGroupId(followingGroup.getId());
        } else {
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if (followingGroup == null) {
                throw new ConditionException("关注分组不存在!");
            }
        }
        //TODO 检查关注人是否 存在
        Long followingId = userFollowing.getFollowingId();
        User user = userService.getUserById(followingId);
        if (user == null) {
            throw new ConditionException("关注的用户不存在!");
        }
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), userFollowing.getFollowingId());
        userFollowingDao.addUserFollowing(userFollowing);
    }

    //TODO 获取关注列表
    //TODO 查询关注列表中的用户信息
    //TODO 将用户按分组进行分类
    public List<FollowingGroup> getUserFollowings(Long userId) {
        //TODO 根据用户ID 查询出关注列表
        List<UserFollowing> list = userFollowingDao.getByUserId(userId);
        //TODO 根据列表 拿出所有的关注ID 根据关注ID查询 用户信息
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        //TODO 查出全部关注的用户信息
        if (followingIdSet.size() > 0) {
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }
        //TODO 将用户信息封装
        for (UserFollowing userFollowing : list) {
            for (UserInfo userInfo : userInfoList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }

        //TODO 查出当前用户的关注组  查看当前用户都有哪些组
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);


        //TODO 返回结果
        List<FollowingGroup> result = new ArrayList<>();
        //TODO 额外给前端返回一个全部分组
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        result.add(allGroup);
        //TODO 最后将 关注列表进行分组
        for (FollowingGroup group : groupList) {
            List<UserInfo> infoList = new ArrayList<>();
            for (UserFollowing userFollowing : list) {
                if (group.getId().equals(userFollowing.getGroupId()))
                    infoList.add(userFollowing.getUserInfo());
            }
            group.setFollowingUserInfoList(infoList);
            result.add(group);
        }

        return result;
    }

    //TODO 获取用户的粉丝列表
    public List<UserFollowing> getUserFans(Long userId) {
        //TODO 根据用户ID 查询出粉丝列表
        List<UserFollowing> fanList = userFollowingDao.getByfollowingId(userId);
        //TODO 根据列表 拿出所有的粉丝ID 根据粉丝ID查询 用户信息
        Set<Long> fanIdSet = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        //TODO 查出全部粉丝的用户信息
        if (fanIdSet.size() > 0) {
            userInfoList = userService.getUserInfoByUserIds(fanIdSet);
        }
        //TODO 根据当前用户 查出关注列表
        List<UserFollowing> followingList = userFollowingDao.getByUserId(userId);
        for (UserFollowing fan : fanList) {
            //TODO 设置 粉丝用户信息
            for (UserInfo userInfo : userInfoList) {
                if (fan.getUserId().equals(userInfo.getUserId()))
                    fan.setUserInfo(userInfo);
                userInfo.setFollowed(false);
            }

            //TODO 判断是否互关  互相关注
            for (UserFollowing following : followingList) {
                if (fan.getUserId().equals(following.getFollowingId())) {
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }

        return fanList;


    }

    public Long addUserFollowingGroups(FollowingGroup followingGroup) {
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE);
        followingGroupService.addUserFollowingGroup(followingGroup);
        return followingGroup.getId();
    }

    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupService.getUserFollowingGroups(userId);
    }

    public List<UserInfo> checkFollowingStatus(List<UserInfo> list, Long userId) {
        List<UserFollowing> userFollowingList = userFollowingDao.getByUserId(userId);
        for (UserInfo userInfo : list) {
            userInfo.setFollowed(false);
            for (UserFollowing userFollowing : userFollowingList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(true);
                }
            }
        }
        return list;
    }
}
