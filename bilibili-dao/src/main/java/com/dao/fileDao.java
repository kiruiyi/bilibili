package com.dao;

import com.domin.File;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface fileDao {

    Integer addFile(File file);


    File getFileByMd5(String md5);
}
