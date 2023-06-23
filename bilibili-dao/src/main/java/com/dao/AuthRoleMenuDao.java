package com.dao;

import com.domin.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface AuthRoleMenuDao {


    List<AuthRoleMenu> getRoleMenuByRoleIds(@Param("roleIdSet") Set<Long> roleIdSet);
}
