package com.kxr.util;

import net.coobird.thumbnailator.Thumbnails;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author kxr
 * @date 2023/1/23 11:22 AM
 * @description
 */
public class FileUtil {

    public static void main(String[] args) throws Exception {
        try {
//            getVideoPic("/Users/kxr/Project/IdeaProjects/file-upload/facd6d5e44d34ed492dc1739bd9f7609.mp4", "/Users/kxr/Project/IdeaProjects/file-upload/test.png");
            thumbnail("/Users/kxr/Project/IdeaProjects/file-upload/900.png", "/Users/kxr/Project/IdeaProjects/file-upload/test1.png");
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定视频的帧并保存为图片至指定目录
     *
     * @param videofile 源视频文件路径
     * @param picPath   截取帧的图片存放路径
     * @throws Exception
     * @throws IOException
     */
    public static File getVideoPic(String videofile, String picPath) {
        File video = new File(videofile);
        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(video);
        try {
            ff.start();
            int lenght = ff.getLengthInFrames();
            int i = 0;
            Frame f = null;
            while (i < lenght) {
                // 过滤前5帧，避免出现全黑的图片，依自己情况而定
                f = ff.grabFrame();
                if ((i > 5) && (f.image != null)) {
                    break;
                }
                i++;
            }

            // 截取的帧图片
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage srcImage = converter.getBufferedImage(f);

//            int srcImageWidth = srcImage.getWidth();
//            int srcImageHeight = srcImage.getHeight();
//            // 对截图进行等比例缩放(缩略图)
//            int width = 480;
//            int height = (int) (((double) width / srcImageWidth) * srcImageHeight);
//            BufferedImage thumbnailImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
//            thumbnailImage.getGraphics().drawImage(srcImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

            File picFile = new File(picPath);
            ImageIO.write(srcImage, "jpg", picFile);

            ff.stop();
            return picFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    /**
//     * 压缩图片
//     *
//     * @param srcImagePath 源图片路径
//     * @param desImagePath 目标路径
//     * @param scale        压缩率
//     */
//    public static void thumbnail(String srcImagePath, String desImagePath, double scale) {
//        //读取图像到矩阵中,取灰度图像
//        Mat src = Imgcodecs.imread(srcImagePath);
//        //复制矩阵进入dst
//        Mat dst = src.clone();
//        Imgproc.resize(src, dst, new Size(src.width() * scale, src.height() * scale));
//        Imgcodecs.imwrite(desImagePath, dst);
//    }

    /**
     * 压缩图片
     *
     * @param srcImagePath 源图片路径
     * @param desImagePath 目标路径
     * @throws IOException the io exception
     */
    public static void thumbnail(String srcImagePath, String desImagePath) throws IOException {
        double scale = 1d;
        File srcFile = new File(srcImagePath);
        long srcFilesize = srcFile.length() / 1024;     //kb
        if (srcFilesize < 80) {
            return;
        } else if (srcFilesize < 200) {
            scale = 0.9;
        } else if (srcFilesize < 500) {
            scale = 0.8;
        } else if (srcFilesize < 1000) {
            scale = 0.6;
        } else if (srcFilesize < 2000) {
            scale = 0.5;
        } else if (srcFilesize < 4000) {
            scale = 0.3;
        } else if (srcFilesize < 6000) {
            scale = 0.2;
        } else {
            scale = 0.1;
        }
        Thumbnails.of(srcImagePath).scale(scale).toFile(desImagePath);
    }
}
