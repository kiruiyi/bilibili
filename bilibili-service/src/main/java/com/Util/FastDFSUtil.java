package com.Util;

import com.exception.ConditionException;
import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.ConnectException;
import java.util.*;

@Component
public class FastDFSUtil {


    @Value("${fdfs.http.storage-addr}")
    private static String httpFdfsStorageAddr;
    private final static String DEFAULT_GROUP = "group1";

    private final static String PATH_KEY = "path-key:";
    private final static String UPLOADED_SIZE_KEY = "uploaded-size-key:";
    private final static String UPLOADED_NO_KEY = "uploaded-no-key:";
    private final static int SLICE_SIZE = 1024 * 1024 * 2;


    @Autowired
    private FastFileStorageClient fastFileStorageClient;


    //TODO 用于断点续传
    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    //TODO 获取文件类型

    public String getFileType(MultipartFile file) {
        if (file == null) {
            throw new ConditionException("非法文件！");
        }
        String filename = file.getOriginalFilename();
        int index = filename.lastIndexOf(".");
        return filename.substring(index + 1);
    }


    //TODO 文件上传
    //TODO 返回路劲
    public String uploadCommonFile(MultipartFile file) throws IOException {
        Set<MetaData> metaDataSet = new HashSet<>();
        String fileType = getFileType(file);
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
        return storePath.getPath();

    }

    //TODO 上传可以断点续传的文件
    public String uploadAppendFile(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String fileType = getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }


    //TODO 分片续传
    public void modifyAppenderFile(MultipartFile file, String filePath, long offset) throws IOException {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP, filePath, file.getInputStream(), file.getSize(), offset);
    }

    public String uploadFileBySlices(MultipartFile file, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws IOException {
        if (file == null || sliceNo == null || totalSliceNo == null) {
            throw new ConditionException("参数异常");
        }
        //TODO 当前文件路劲
        String pathKey = PATH_KEY + fileMd5;
        //TODO 当前已经传的大小
        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMd5;
        //TODO 当前处于第几个分片
        String uploadedNoKey = UPLOADED_NO_KEY + fileMd5;

        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);

        Long uploadedSize = 0L;
        if (!StringUtils.isNullOrEmpty(uploadedSizeStr)) {
            uploadedSize = Long.parseLong(uploadedSizeStr);
        }
        String fileType = getFileType(file);
        if (sliceNo == 1) {
            String path = uploadAppendFile(file);
            if (StringUtils.isNullOrEmpty(path)) {
                throw new ConditionException("上传失败!");
            }
            redisTemplate.opsForValue().set(pathKey, path);
            redisTemplate.opsForValue().set(uploadedNoKey, "1");
        } else {
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if (StringUtils.isNullOrEmpty(filePath)) {
                throw new ConnectException("上传失败！");
            }
            modifyAppenderFile(file, filePath, uploadedSize);
            redisTemplate.opsForValue().increment(uploadedNoKey);
        }
        uploadedSize += file.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey, String.valueOf(uploadedSize));


        //TODO 如果分片全部上传完毕  清空redis
        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);

        Integer uploadedNo = Integer.parseInt(uploadedNoStr);
        String resultPath = "";
        if (uploadedNo.equals(totalSliceNo)) {
            resultPath = redisTemplate.opsForValue().get(pathKey);
            List<String> keyList = Arrays.asList(uploadedNoKey, uploadedSizeKey, pathKey);
            redisTemplate.delete(keyList);
        }

        return resultPath;
    }

    public void convertFileToSlices(MultipartFile multipartFile) throws IOException {
        String filename = multipartFile.getOriginalFilename();
        String fileType = getFileType(multipartFile);
        File file = multipartFileToFile(multipartFile);
        int count = 1;
        for (int i = 0; i < file.length(); i += SLICE_SIZE) {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(i);
            byte[] bytes = new byte[SLICE_SIZE];
            int len = randomAccessFile.read(bytes);
            String path = "D:\\dfs\\" + count + "." + fileType;
            File slice = new File(path);
            FileOutputStream fos = new FileOutputStream(slice);
            fos.write(bytes, 0, len);
            fos.close();
            randomAccessFile.close();
            count++;
        }
        file.delete();
    }


    public File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String[] split = originalFilename.split("\\.");
        File file = File.createTempFile(split[0], "." + split[1]);
        multipartFile.transferTo(file);
        return file;
    }

    //TODO 文件删除
    public void deleteFile(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }

    public void viewVideoOnlineBySlice(HttpServletRequest request, HttpServletResponse response, String path) throws Exception {
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, path);
        long totalFileSize = fileInfo.getFileSize();
        //TODO 拼接地址
        String url = httpFdfsStorageAddr + path;
        //TODO 获取头信息的名字
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, Object> headrs = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            //TODO 获得头信息的值
            headrs.put(header, request.getHeader(header));
        }


        //TODO 设置请求头中的  Range
        String rangeStr = request.getHeader("Range");
        String[] range;
        if (StringUtils.isNullOrEmpty(rangeStr)) {
            rangeStr = "bytes=0-" + (totalFileSize - 1);
        }
        range = rangeStr.split("bytes=|-");

        long begin = 0;
        if (range.length >= 2) {
            begin = Long.parseLong(range[1]);
        }
        long end = totalFileSize - 1;
        if (range.length >= 3) {
            end = Long.parseLong(range[2]);
        }

        long len = (end - begin) + 1;

        //TODO 设置请求头中的参数
        String contentRange = "bytes" + begin + "-" + end + "/" + totalFileSize;

        response.setHeader("Content-Range", contentRange);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", "video/mp4");
        response.setContentLength((int) len);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        HttpUtil.get(url, headrs, response);

    }
}
