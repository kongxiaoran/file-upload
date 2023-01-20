package com.kxr;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: kongxr
 * @Date: 2023-01-20 13:50
 * @Description:
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @PostMapping("/upload")
    public String upload(@RequestParam("imgFiles") MultipartFile[] files, @RequestParam("imgNames") String name, @RequestParam(value = "path", required = false) String path) throws Exception {
        File dir = new File(path != null ? path : "/app/file-upload/uploadFile");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        List<String> filesPath = new ArrayList<>();
        for (MultipartFile file : files) {
            String filePath  = dir.getAbsolutePath() + File.separator + file.getOriginalFilename();
            file.transferTo(new File(filePath));
            filesPath.add(filePath);
        }
        return "上传完成！文件路径列表路径:" + filesPath.toString();
    }

    @RequestMapping("/download")
    public String fileDownLoad(HttpServletResponse response, @RequestParam("fileName") String fileName) {
        File file = new File("/app/file-upload/uploadFile/" + fileName);
        if (!file.exists()) {
            return "下载文件不存在";
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            return "下载失败";
        }
        return "下载成功";
    }
}
