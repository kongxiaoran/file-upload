package com.kxr;

import com.alibaba.fastjson.JSONObject;
import com.kxr.util.FileUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author: kongxr
 * @Date: 2023-01-20 13:50
 * @Description:
 */
@RestController
@RequestMapping("/file")
public class FileController {

    /*默认图片格式 jpg*/
    public static String DEFAULT_IMG_FORMAT = "jpg";
    /*图片格式 png*/
    public static String IMG_FORMAT_PNG = "png";
    /*图片格式 jpg*/
    public static String IMG_FORMAT_JPG = "jpg";

    public static final String HOST = "http://150.158.165.30:8035/";

    public static final String MESSAGE_IMG_PATH = "/app/file-upload/uploadFile";


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public JSONObject upload(@RequestParam("imgFiles") MultipartFile file, @RequestParam("imgNames") String name, @RequestParam(value = "path", required = false) String path) throws Exception {
        File dir = new File(path != null ? path : MESSAGE_IMG_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String type = file.getContentType().split("/")[1];
        String fileNameUUID = UUID.randomUUID().toString().replace("-", "") + "." + type;
        String filePath = dir.getAbsolutePath() + File.separator + fileNameUUID;
        file.transferTo(new File(filePath));

        JSONObject fileRes = new JSONObject();
        // 无论是图片的缩略图，还是视频封面的缩略图，都采用jpg格式（容易压缩，占用空间小）
        String preImageNameUUID = UUID.randomUUID().toString().replace("-", "") + ".jpg";
        String preImagePath = dir.getAbsolutePath() + File.separator + preImageNameUUID;
        if ("mp4".equals(type)) {
            FileUtil.getVideoPic(filePath, preImagePath);
            FileUtil.thumbnail(preImagePath, preImagePath);
            fileRes.put("src", HOST + preImageNameUUID);
            fileRes.put("videoUrl", HOST + fileNameUUID);
        } else {
            FileUtil.thumbnail(filePath, preImagePath);
            fileRes.put("src", HOST + preImageNameUUID);
            fileRes.put("imgUrl", HOST + fileNameUUID);
        }
        fileRes.put("type", type);
        System.out.println(fileRes);
        return fileRes;
    }

    @RequestMapping(value = "/uploads", method = RequestMethod.POST)
    public List<JSONObject> uploads(@RequestParam("imgFiles") MultipartFile[] files, @RequestParam("imgNames") String name, @RequestParam(value = "path", required = false) String path) throws Exception {
        File dir = new File(path != null ? path : "/app/file-upload/uploadFile");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        List<JSONObject> filesList = new ArrayList<>();
        for (MultipartFile file : files) {

            String type = file.getContentType().split("/")[1];
            String fileNameUUID = UUID.randomUUID().toString().replace("-", "") + "." + type;
            String filePath = dir.getAbsolutePath() + File.separator + fileNameUUID;
            file.transferTo(new File(filePath));

            JSONObject fileRes = new JSONObject();
            if ("mp4".equals(type)) {
                String preImagePath = dir.getAbsolutePath() + File.separator +
                        UUID.randomUUID().toString().replace("-", "") + ".jpg";
                fileRes.put("src", HOST + preImagePath);
                fileRes.put("videoUrl", filePath);
            } else {
                fileRes.put("src", HOST + fileNameUUID);
            }
            filesList.add(fileRes);
        }
        return filesList;
    }

    @RequestMapping("/download")
    public String fileDownLoad(HttpServletResponse response, @RequestParam("fileName") String fileName) {
        File file = new File(MESSAGE_IMG_PATH + fileName);
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
