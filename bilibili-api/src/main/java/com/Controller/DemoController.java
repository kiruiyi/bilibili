package com.Controller;

import com.Util.FastDFSUtil;
import com.domin.PageResult;
import com.domin.R;
import com.exception.ConditionException;
import com.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class DemoController {
    @Autowired
    private DemoService demoService;

    @Autowired
    private FastDFSUtil fastDFSUtil;


    @GetMapping("/slices")
    public void slice(MultipartFile file) throws IOException {
        fastDFSUtil.convertFileToSlices(file);
    }
    @GetMapping("/test")
    public R test() {
        int i = 1 / 0;
        return R.success();
    }


}
