package com.dao;

import com.domin.UserFollowing;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserFollowingDao {


    Integer deleteUserFollowing(Long userId, Long followingId);

    Integer addUserFollowing(UserFollowing userFollowing);

    List<UserFollowing> getByUserId(Long userId);

    List<UserFollowing> getByfollowingId(Long userId);
}
