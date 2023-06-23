package com.service;

import com.Util.MD5Util;
import com.Util.RSAUtil;
import com.Util.TokenUtil;
import com.constant.UserConstant;
import com.dao.UserDao;

import com.domin.PageResult;
import com.domin.RefreshToken;
import com.domin.User;
import com.domin.UserInfo;
import com.exception.ConditionException;
import com.mysql.cj.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthService userAuthService;


    /**
     * id phone email password salt
     */

    public void addUser(User user) {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空");
        }
        if (getUserByPhone(phone) != null) {
            throw new ConditionException("手机号已注册");
        }

        String salt = String.valueOf(new Date().getTime());

        //TODO 前端返回的密码是通过raw加密过的 需要解密一下
        //TODO  然后再通过MD5加密放到数据库里

        String rawpassword;
        try {
            rawpassword = RSAUtil.decrypt(user.getPassword());
        } catch (Exception e) {
            throw new ConditionException("密码解密失败");
        }
        //TODO 此时得到一个明文密码  rawpassword

        String md5Password = MD5Util.sign(rawpassword, salt, "UTF-8");

        user.setSalt(salt);
        user.setPassword(md5Password);

        userDao.addUser(user);

        //TODO 添加用户之后 对应添加用户信息
        UserInfo userInfo = new UserInfo();

        userInfo.setUserId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.DEFAULT_GENDER);

        //TODO 添加用户信息
        userDao.addUserInfo(userInfo);


        //TODO 添加用户默认角色
        userAuthService.addUserDefaultRole(user.getId());
    }

    public User getUserByPhone(String phone) {
        return userDao.getUserByPhone(phone);
    }


    //TODO 登录通过 手机号和密码
    //TODO 判断用户是否存在
    //TODO 返回一个登录令牌 token
    public String login(User user) throws Exception {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空");
        }
        User dbuser = getUserByPhone(phone);
        if (dbuser == null) {
            throw new ConditionException("当前用户不存在");
        }
        String rawpassword;
        try {
            rawpassword = RSAUtil.decrypt(user.getPassword());
        } catch (Exception e) {
            throw new ConditionException("密码解密失败");
        }

        String salt = dbuser.getSalt();
        String md5Password = MD5Util.sign(rawpassword, salt, "UTF-8");

        if (!md5Password.equals(dbuser.getPassword())) {
            throw new ConditionException("密码错误");
        }

        return TokenUtil.generateToken(dbuser.getId());
    }

    public User getUserById(Long userId) {
        User user = userDao.getUserById(userId);
        if (user == null) {
            throw new ConditionException("用户不存在");
        }
        UserInfo userinfo = userDao.getUserInfoByUserId(userId);
        user.setUserInfo(userinfo);
        return user;
    }

    public void updateUserInfosByUserId(UserInfo userInfo) {
        userDao.updateUserInfosByUserId(userInfo);

    }

    public List<UserInfo> getUserInfoByUserIds(Set<Long> followingIdSet) {
        return userDao.getUserInfoByUserIds(followingIdSet);
    }

    public PageResult<UserInfo> getPageUserList(Map<String, Object> params) throws JSONException {
        Integer no = (Integer) params.get("no");
        Integer size = (Integer) params.get("size");
        //TODO 从第几条数据开始查
        params.put("start", (no - 1) * size);
        //TODO 要查多少条
        params.put("limit", size);
        Integer total = userDao.pageCountUserInfo(params);
        List<UserInfo> userInfoList = new ArrayList<>();
        if (total > 0) {
            userInfoList = userDao.pageUserListInfos(params);
        }

        return new PageResult<>(total, userInfoList);
    }

    public Map<String, Object> loginForDts(User user) throws Exception {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空");
        }
        User dbuser = getUserByPhone(phone);
        if (dbuser == null) {
            throw new ConditionException("当前用户不存在");
        }
        String rawpassword;
        try {
            rawpassword = RSAUtil.decrypt(user.getPassword());
        } catch (Exception e) {
            throw new ConditionException("密码解密失败");
        }

        String salt = dbuser.getSalt();
        String md5Password = MD5Util.sign(rawpassword, salt, "UTF-8");

        if (!md5Password.equals(dbuser.getPassword())) {
            throw new ConditionException("密码错误");
        }

        //TODO 获取接入token
        String accessToken = TokenUtil.generateToken(dbuser.getId());


        //TODO 获取刷新token
        String refreshToken = TokenUtil.generateRefreshToken(dbuser.getId());

        //TODO 将refreshToken 保存在数据库  方便后续用户登录以及刷新token

        userDao.deleteRefreshToken(refreshToken, dbuser.getId());
        userDao.addRefreshToken(refreshToken, dbuser.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    public void logout(String refreshToken, Long userId) {
        userDao.deleteRefreshToken(refreshToken, userId);

    }

    public String refreshAccessToken(String refreshToken) throws Exception {

        RefreshToken refresh = userDao.getRefreshToken(refreshToken);
        if (refresh == null) {
            throw new ConditionException("555", "token过期!");
        }
        Long userId = refresh.getUserId();
        return TokenUtil.generateToken(userId);

    }
}
