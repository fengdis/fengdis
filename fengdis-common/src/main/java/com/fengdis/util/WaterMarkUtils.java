package com.fengdis.util;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @version 1.0
 * @Descrittion: 图片水印工具类
 * @author: fengdi
 * @since: 2018/8/26 0026 19:18
 */
public class WaterMarkUtils {

    private WaterMarkUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(WaterMarkUtils.class);

    //private static final String MARK_TEXT = PropertiesUtils.getProperty("watermark.text","");
    private static final String FONT_NAME = "微软雅黑";
    private static final int FONT_STYLE = Font.BOLD;
    private static final Color FONT_COLOR = Color.BLACK;
    //private static final int FONT_SIZE = 40;

    private static final float ALPHA = 0.3F;

    /**
     * 上传图片
     * @param myFile
     * @param filePath
     * @return
     */
    public static String uploadImage(MultipartFile myFile, String filePath) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = myFile.getInputStream();
            os = new FileOutputStream(filePath);
            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = is.read(buffer)) > 0) {
                os.write(buffer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }

        return "success";
    }

    /**
     * 添加单条文字水印
     * @param filePath
     * @return
     */
    public static void textWaterMark(MultipartFile myFile,String filePath,String waterMarkText) {
        //MultipartFile myFile = getMultipartFile(filePath);
        InputStream is = null;
        OutputStream os = null;
        int X = 0;
        int Y = 0;

        try {
            Image image = ImageIO.read(myFile.getInputStream());
            //计算原始图片宽度长度
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            int fontSize = getFontSize(width);
            //创建图片缓存对象
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            //创建java绘图工具对象
            Graphics2D graphics2d = bufferedImage.createGraphics();
            //参数主要是，原图，坐标，宽高
            graphics2d.drawImage(image, 0, 0, width, height, null);
            graphics2d.setFont(new Font(FONT_NAME, FONT_STYLE, fontSize));
            graphics2d.setColor(FONT_COLOR);

            //使用绘图工具将水印绘制到图片上
            //计算文字水印宽高值
            int waterWidth = fontSize * getTextLength(waterMarkText);
            int waterHeight = fontSize;
            //计算水印与原图高宽差
            int widthDiff = width - waterWidth;
            int heightDiff = height - waterHeight;
            //水印坐标设置
            if (widthDiff > 0) {
                X = widthDiff - fontSize;
            }
            if (heightDiff > 0) {
                Y = heightDiff;
            }
            //水印透明设置
            graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, ALPHA));
            graphics2d.drawString(waterMarkText, X, Y);

            graphics2d.dispose();
            os = new FileOutputStream(filePath);
            //创建图像编码工具类
            JPEGImageEncoder en = JPEGCodec.createJPEGEncoder(os);
            //使用图像编码工具类，输出缓存图像到目标文件
            en.encode(bufferedImage);
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            logger.info(String.format("图片%s单条文字水印成功",filePath));
        } catch (IOException e) {
            logger.error(String.format("图片%s单条文字水印异常",filePath),e);
        }
    }

    /**
     * 添加单图片水印
     * @param filePath
     * @param waterMarkPath
     * @return
     */
    public static void imageWaterMark(MultipartFile myFile,String filePath,String waterMarkPath) {
        //MultipartFile myFile = getMultipartFile(filePath);
        InputStream is = null;
        OutputStream os = null;
        int X = 0;
        int Y = 0;

        try {
            Image image = ImageIO.read(myFile.getInputStream());
            //计算原始图片宽度长度
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            int fontSize = getFontSize(width);

            //创建图片缓存对象
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            //创建java绘图工具对象
            Graphics2D graphics2d = bufferedImage.createGraphics();
            //参数主要是，原图，坐标，宽高
            graphics2d.drawImage(image, 0, 0, width, height, null);
            graphics2d.setFont(new Font(FONT_NAME, FONT_STYLE, fontSize));
            graphics2d.setColor(FONT_COLOR);

            //水印图片路径
            /*String logoPath = "/img/logo.png";
            String realPath = request.getSession().getServletContext().getRealPath(logoPath);*/
            File logo = new File(waterMarkPath);
            Image imageLogo = ImageIO.read(logo);
            int widthLogo = imageLogo.getWidth(null);
            int heightLogo = imageLogo.getHeight(null);
            int widthDiff = width - widthLogo;
            int heightDiff = height - heightLogo;
            //水印坐标设置
            if (widthDiff > 0) {
                X = widthDiff - fontSize;
            }
            if (heightDiff > 0) {
                Y = heightDiff;
            }
            //水印透明设置
            graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, ALPHA));
            graphics2d.drawImage(imageLogo, X, Y, null);

            graphics2d.dispose();
            os = new FileOutputStream(filePath);
            //创建图像编码工具类
            JPEGImageEncoder en = JPEGCodec.createJPEGEncoder(os);
            //使用图像编码工具类，输出缓存图像到目标文件
            en.encode(bufferedImage);
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            logger.info(String.format("图片%s单图片水印成功",filePath));
        } catch (IOException e) {
            logger.error(String.format("图片%s单图片水印异常",filePath),e);
        }
    }

    /**
     * 添加多条文字水印
     * @param filePath
     * @return
     */
    public static void moreTextWaterMark(MultipartFile myFile,String filePath,String waterMarkText) {
        //MultipartFile myFile = getMultipartFile(filePath);
        InputStream is = null;
        OutputStream os = null;
        try {
            Image image = ImageIO.read(myFile.getInputStream());
            //计算原始图片宽度长度
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            int fontSize = getFontSize(width);

            //创建图片缓存对象
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            //创建java绘图工具对象
            Graphics2D graphics2d = bufferedImage.createGraphics();
            //参数主要是，原图，坐标，宽高
            graphics2d.drawImage(image, 0, 0, width, height, null);
            graphics2d.setFont(new Font(FONT_NAME, FONT_STYLE, fontSize));
            graphics2d.setColor(FONT_COLOR);

            //使用绘图工具将水印绘制到图片上
            //计算文字水印宽高值
            int waterWidth = fontSize * getTextLength(waterMarkText);
            int waterHeight = fontSize;

            //水印透明设置
            graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, ALPHA));
            graphics2d.rotate(Math.toRadians(30), bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2);

            int x = -width / 2;
            int y = -height / 2;

            while (x < width * 1.5) {
                y = -height / 2;
                while (y < height * 1.5) {
                    graphics2d.drawString(waterMarkText, x, y);
                    y += waterHeight + 100;
                }
                x += waterWidth + 100;
            }
            graphics2d.dispose();

            os = new FileOutputStream(filePath);
            //创建图像编码工具类
            JPEGImageEncoder en = JPEGCodec.createJPEGEncoder(os);
            //使用图像编码工具类，输出缓存图像到目标文件
            en.encode(bufferedImage);
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            logger.info(String.format("图片%s多条文字水印成功",filePath));
        } catch (IOException e) {
            logger.error(String.format("图片%s多条文字水印异常",filePath),e);
        }
    }

    /**
     * 添加多图片水印
     * @param filePath
     * @param waterMarkPath
     * @return
     */
    public static void moreImageWaterMark(MultipartFile myFile,String filePath,String waterMarkPath) {
        //MultipartFile myFile = getMultipartFile(filePath);
        InputStream is = null;
        OutputStream os = null;
        try {
            Image image = ImageIO.read(myFile.getInputStream());
            //计算原始图片宽度长度
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            int fontSize = getFontSize(width);

            //创建图片缓存对象
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            //创建java绘图工具对象
            Graphics2D graphics2d = bufferedImage.createGraphics();
            //参数主要是，原图，坐标，宽高
            graphics2d.drawImage(image, 0, 0, width, height, null);
            graphics2d.setFont(new Font(FONT_NAME, FONT_STYLE, fontSize));
            graphics2d.setColor(FONT_COLOR);

            //水印图片路径
            /*String logoPath = "/img/logo.png";
            String realPath = request.getSession().getServletContext().getRealPath(logoPath);*/
            File logo = new File(waterMarkPath);
            Image imageLogo = ImageIO.read(logo);
            int widthLogo = imageLogo.getWidth(null);
            int heightLogo = imageLogo.getHeight(null);

            //水印透明设置
            graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, ALPHA));

            graphics2d.rotate(Math.toRadians(30), bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2);

            int x = -width / 2;
            int y = -height / 2;

            while (x < width * 1.5) {
                y = -height / 2;
                while (y < height * 1.5) {
                    graphics2d.drawImage(imageLogo, x, y, null);
                    y += heightLogo + 100;
                }
                x += widthLogo + 100;
            }
            graphics2d.dispose();
            os = new FileOutputStream(filePath);
            //创建图像编码工具类
            JPEGImageEncoder en = JPEGCodec.createJPEGEncoder(os);
            //使用图像编码工具类，输出缓存图像到目标文件
            en.encode(bufferedImage);
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            logger.info(String.format("图片%s多图片水印成功",filePath));
        } catch (IOException e) {
            logger.error(String.format("图片%s多图片水印异常",filePath),e);
        }
    }

    /**
     * 计算水印文本长度（1、中文长度即文本长度 2、英文长度为文本长度二分之一）
     * @param text
     * @return
     */
    public static int getTextLength(String text) {
        //水印文字长度
        int length = text.length();

        for (int i = 0; i < text.length(); i++) {
            String s = String.valueOf(text.charAt(i));
            if (s.getBytes().length > 1) {
                length++;
            }
        }
        length = length % 2 == 0 ? length / 2 : length / 2 + 1;
        return length;
    }

    /**
     * 根据图片大小自适应文字大小
     * @param picWidth
     * @return
     */
    public static int getFontSize(int picWidth) {
        int fontSize = 0;
        if(picWidth >= 1366){
            fontSize = 40;
        }else if(picWidth >= 960 && picWidth <= 1365){
            fontSize = 25;
        }else if(picWidth >= 480 && picWidth <= 959){
            fontSize = 20;
        }else if(picWidth <= 479){
            fontSize = 10;
        }
        return fontSize;
    }

    /**
     * 本地文件转化为MultipartFile
     * @param filePath
     * @return
     */
    public static MultipartFile File2MultipartFile(String filePath) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        String textFieldName = "textField";
        int num = filePath.lastIndexOf(".");
        String extFile = filePath.substring(num);
        FileItem fileItem = factory.createItem(textFieldName, "text/plain", true, "MyFileName" + extFile);
        File newfile = new File(filePath);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        FileInputStream fis = null;
        OutputStream os = null;
        try {
            fis = new FileInputStream(newfile);
            os = fileItem.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        return multipartFile;
    }

    public static void main(String[] args) {
        textWaterMark(File2MultipartFile("/Users/feng/Desktop/test.png"),"/Users/feng/Desktop/test.png","系统水印");
    }

}