package com.service;

import com.dao.AuthRoleMenuDao;
import com.domin.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthRoleMenuService {

    @Autowired
    private AuthRoleMenuDao authRoleMenuDao;

    public List<AuthRoleMenu> getRoleMenuByRoleIds(Set<Long> roleIdSet) {
        return authRoleMenuDao.getRoleMenuByRoleIds(roleIdSet);
    }
}
