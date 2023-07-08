package com.Config;


import com.domin.R;
import com.exception.ConditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)  // 表示优先级最高 优先处理这个
public class CommnGlobalExceptionHandler {

    //TODO 全局异常处理器

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public R<String> commonExceptionHandler(Exception e) {
        String msg = e.getMessage();
        if (e instanceof ConditionException) {
            String errcode = ((ConditionException) e).getCode();
            return new R<>(errcode, msg);
        }
        return new R<>("500", msg);
    }


}
