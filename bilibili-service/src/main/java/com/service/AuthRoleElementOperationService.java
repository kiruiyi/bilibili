package com.service;

import com.dao.AuthRoleElementOperationDao;
import com.domin.auth.AuthRoleElementOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthRoleElementOperationService {

    @Autowired
    private AuthRoleElementOperationDao authRoleElementOperationDao;

    public List<AuthRoleElementOperation> getRoleElementOperationByRoleIds( Set<Long> roleIdSet) {
        List<AuthRoleElementOperation> lists = authRoleElementOperationDao.getRoleElementOperationByRoleIds(roleIdSet);
            return lists;
    }
}
