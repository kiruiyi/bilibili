package com.domin;

import lombok.Data;

import java.util.List;
@Data
public class PageResult<T> {

    private Integer total;
    private List<T> data;

    public PageResult(Integer total, List<T> list) {
        this.total = total;
        data = list;
    }
}
