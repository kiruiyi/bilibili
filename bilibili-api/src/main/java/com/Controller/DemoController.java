package com.Controller;

import com.domin.R;
import com.exception.ConditionException;
import com.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @Autowired
    private DemoService demoService;

    @GetMapping("/test")
    public R test() {
        int i = 1 / 0;
        return R.success();
    }


}
