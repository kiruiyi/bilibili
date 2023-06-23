package com.Controller;


import com.domin.R;
import com.domin.auth.UserAuthorities;
import com.service.UserAuthService;
import com.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//TODO 权限相关  api
@RestController
public class UserAuthController {

    @Autowired
    private UserSupport userSupport;
    @Autowired
    private UserAuthService userAuthService;

    //TODO 获取用户的相关权限
    @GetMapping("/user-authorities")
    public R getUserAuthorities() {
        Long userId = userSupport.getCurrentUserId();
        UserAuthorities userAuthorities = userAuthService.getUserAuthorities(userId);
        return R.success(userAuthorities);
    }
}
