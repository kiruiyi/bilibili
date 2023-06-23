package com.dao;

import com.domin.UserMoment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMomentsDao {


    Integer addUserMoments(UserMoment userMoment);
}
