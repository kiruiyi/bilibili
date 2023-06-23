package com.Controller;


import com.Util.RSAUtil;
import com.domin.PageResult;
import com.domin.R;
import com.domin.User;
import com.domin.UserInfo;
import com.service.UserFollowingService;
import com.service.UserService;
import com.support.UserSupport;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserSupport userSupport;
    @Autowired
    private UserService userService;

    @Autowired
    private UserFollowingService userFollowingService;


    //TODO 通过头信息中的token  返回用户
    @GetMapping("/users")
    public R<User> getUser() {
        Long userId = userSupport.getCurrentUserId();
        User user = userService.getUserById(userId);
        return R.success(user);
    }

    @GetMapping("/rsa-pks")
    public R getRsaPulicKey() {
        return R.success(RSAUtil.getPublicKeyStr());
    }

    //TODO 用户注册
    @PostMapping("/user/register")
    public R addUser(@RequestBody User user) {
        userService.addUser(user);
        return R.success("注册成功");
    }

    //TODO 用户信息更新
    @PutMapping("/user-infos")
    public R updateUserInfos(@RequestBody UserInfo userInfo) {
        //TODO 根据token获取当前用户 进行更新
        Long userId = userSupport.getCurrentUserId();
        userInfo.setUserId(userId);
        userInfo.setUpdateTime(new Date());
        userService.updateUserInfosByUserId(userInfo);
        return R.success();
    }


    //TODO 用户登录
    //TODO  返回用户的token
    @PostMapping("/user/login-dantoken")
    public R login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return R.success(token);
    }

    //TODO 分页查询用户关注列表
    //TODO no 当前页码  size 当前页码的页数大小 nick用户昵称
    //TODO 根据用户昵称查询
    @GetMapping("/user-infos")
    public R getPageUserList(@RequestParam Integer no, @RequestParam Integer size, String nick) throws JSONException {
        Long userId = userSupport.getCurrentUserId();
        Map<String, Object> params = new HashMap<>();
        params.put("no", no);
        params.put("size", size);
        params.put("nick", nick);
        params.put("userId", userId);
        PageResult<UserInfo> result = userService.getPageUserList(params);
        if (result.getTotal() > 0) {
            List<UserInfo> checkedUserInfoList = userFollowingService.checkFollowingStatus(result.getData(), userId);
            result.setData(checkedUserInfoList);
        }
        return R.success(result);
    }

    //TODO 双 token 登录
    @PostMapping("/user/login")
    public R loginForDts(@RequestBody User user) throws Exception {
        Map<String, Object> map = userService.loginForDts(user);
        return R.success(map);
    }

    //TODO 双token登出
    @DeleteMapping("/user/logout")
    public R logout(HttpServletRequest request) {
        String refreshToken = request.getHeader("refreshToken");
        Long userId = userSupport.getCurrentUserId();
        userService.logout(refreshToken, userId);
        return R.success();
    }


    //TODO 刷新token
    @PostMapping("/access-token")
    public R refreshAccessToken(HttpServletRequest request) throws Exception {
        String refreshToken = request.getHeader("refreshToken");
        String accessToken = userService.refreshAccessToken(refreshToken);
        return R.success(accessToken);
    }


}
