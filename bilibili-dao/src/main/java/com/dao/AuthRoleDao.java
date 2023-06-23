package com.dao;

import com.domin.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthRoleDao {


    AuthRole getRoleByCode(String code);
}
