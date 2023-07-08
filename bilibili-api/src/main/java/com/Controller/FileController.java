package com.Controller;

import com.domin.R;
import com.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/md5files")
    public R getFileMd5(MultipartFile file) throws IOException {
        String filemd5 = fileService.getFileMd5(file);
        return R.success(filemd5);
    }

    //TODO 断点续传   ()
    @PutMapping("/file-slices")
    public R uploadFileBySlices(MultipartFile slice, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws IOException {
        String filePath = fileService.uploadFileBySlices(slice, fileMd5, sliceNo, totalSliceNo);
        return R.success(filePath);
    }


}
