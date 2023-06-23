package com.Controller;

import com.constant.AuthRoleConstant;
import com.domin.R;
import com.domin.UserMoment;
import com.domin.annotation.ApiLimitedRole;
import com.domin.annotation.DataLimited;
import com.exception.ConditionException;
import com.service.UserMomentsService;
import com.support.UserSupport;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


//TODO   用户信息发布 RocketMQ
@RestController
public class UserMomentsController {
    @Autowired
    private UserMomentsService userMomentsService;

    @Autowired
    private UserSupport userSupport;


    //TODO @ApiLimitedRole 自定义注解 里面的值表示该用户禁止访问
    //TODO @DataLimited 自定义注解 针对传入的数据进行控制


    //TODO 用户发布动态
    @DataLimited    //TODO 数据权限控制
    @ApiLimitedRole(limitRoleCodeList = {AuthRoleConstant.ROLE_CODE_LV0})
    @PostMapping("/user-moments")
    public R addUserMoments(@RequestBody UserMoment userMoment) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Long userId = userSupport.getCurrentUserId();
        userMoment.setUserId(userId);
        userMomentsService.addUserMoments(userMoment);
        return R.success();
    }


    //TODO 根据用户查询 关注人发布的信息
    @GetMapping("/user-subscribed-moments")
    public R getUserSubscribedMoments() {
        Long userId = userSupport.getCurrentUserId();
        List<UserMoment> list = userMomentsService.getUserSubscribedMoments(userId);
        return R.success(list);
    }
}
