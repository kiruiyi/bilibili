package com.dao;

import com.domin.RefreshToken;
import com.domin.User;
import com.domin.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface UserDao {
    User getUserById(Long id);

    User getUserByPhone(String phone);

    Integer addUser(User user);

    Integer addUserInfo(UserInfo userInfo);

    UserInfo getUserInfoByUserId(Long userId);

    Integer updateUserInfosByUserId(UserInfo userInfo);


    List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList);

    Integer pageCountUserInfo(Map<String,Object> params);

    List<UserInfo> pageUserListInfos(Map<String,Object> params);

    Integer deleteRefreshToken(@Param("refreshToken")String refreshToken, Long userId);

    Integer addRefreshToken(@Param("refreshToken") String refreshToken, Long userId);

    RefreshToken getRefreshToken(@Param("refreshToken") String refreshToken);
}
