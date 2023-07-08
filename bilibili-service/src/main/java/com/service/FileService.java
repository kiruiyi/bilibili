package com.service;

import com.Util.FastDFSUtil;
import com.Util.MD5Util;
import com.domin.File;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

@Service
public class FileService {

    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private com.dao.fileDao fileDao;

    public String uploadFileBySlices(MultipartFile slice, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws IOException {
        File dbFileByMd5 = fileDao.getFileByMd5(fileMd5);
        //TODO 秒传
        if (dbFileByMd5 != null) {
            return dbFileByMd5.getUrl();
        }
        //TODO 分片传  全部传完后 返回一个url地址
        String url = fastDFSUtil.uploadFileBySlices(slice, fileMd5, sliceNo, totalSliceNo);
        //TODO 若url地址不为空 记录下来 实现秒传
        if (!StringUtils.isNullOrEmpty(url)) {
            dbFileByMd5 = new File();
            dbFileByMd5.setMd5(fileMd5);
            dbFileByMd5.setType(fastDFSUtil.getFileType(slice));
            dbFileByMd5.setUrl(url);
            fileDao.addFile(dbFileByMd5);
        }
        return url;
    }

    public String getFileMd5(MultipartFile file) throws IOException {
        //TODO 获取文件的MD5加密  加密的是底层二进制数据 和名字无关
        return MD5Util.getFileMD5(file);
    }
}
