package com.fengdis.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @version 1.0
 * @Descrittion: 验证码生成工具类（包括图片、纯数字、字母数字混合）
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class CaptchaUtils {

    private CaptchaUtils(){
    }

    private static final Logger logger = LoggerFactory.getLogger(CaptchaUtils.class);

    private static int width = 100;// 定义图片的width
    private static int height = 45;// 定义图片的height
    private static int codeCount = 4;// 定义图片上显示验证码的个数
    private static int xx = 16;
    private static int fontHeight = 25;
    private static  int codeY = 30;
    private static char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z','a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    /**
     * 生成一个map集合
     * code为生成的验证码
     * img为生成的验证码BufferedImage对象
     * @return
     */
    public static Map<String,Object> generateCodePic() {
        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Graphics2D gd = buffImg.createGraphics();
        // Graphics2D gd = (Graphics2D) buffImg.getGraphics();
        Graphics gd = buffImg.getGraphics();
        // 创建一个随机数生成器类
        Random random = new Random();
        // 将图像填充为白色
        gd.setColor(Color.WHITE);
        gd.fillRect(0, 0, width, height);

        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.BOLD, fontHeight);
        // 设置字体。
        gd.setFont(font);

        // 画边框。
        gd.setColor(Color.BLACK);
        gd.drawRect(0, 0, width - 1, height - 1);

        // 随机产生40条干扰线，使图象中的认证码不易被其它程序探测到。
        gd.setColor(Color.BLACK);
        for (int i = 0; i < 30; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            gd.drawLine(x, y, x + xl, y + yl);
        }

        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;

        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < codeCount; i++) {
            // 得到随机产生的验证码数字。
            String code = String.valueOf(codeSequence[random.nextInt(62)]);
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);

            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(red, green, blue));
            gd.drawString(code, (i + 1) * xx, codeY);

            // 将产生的四个随机数组合在一起。
            randomCode.append(code);
        }

        Map<String,Object> map  =new HashMap<String,Object>();
        //存放验证码
        map.put("code", randomCode);
        //存放生成的验证码BufferedImage对象
        map.put("img", buffImg);
        return map;
    }


    /**
     * 输出验证码图片到本地磁盘上
     * @param filePath
     * @return
     */
    public static String getCodePic(String filePath){
        String code = "";
        //创建文件输出流对象
        try {
            OutputStream out = new FileOutputStream(filePath);
            Map<String,Object> map = generateCodePic();
            ImageIO.write((RenderedImage) map.get("img"), "jpg", out);
            code = map.get("code").toString();
            logger.info(String.format("验证码图片下载成功，验证码为%s",map.get("code")));
        } catch (IOException e) {
            logger.error("验证码图片下载异常",e);
        }
        return code;
    }

    /**
     * 输出验证码图片到浏览器上
     * @param response
     * @return
     */
    public static String getCodePic(HttpServletResponse response){
        String code = "";
        try {
            Map<String,Object> map = generateCodePic();
            Cookie cookie = new Cookie("code", map.get("code").toString());
            //cookie.setMaxAge(60*60*24*30);//如果不设置使用时间，那么将取不到Cookie的值
            cookie.setPath("/blog");
            response.addCookie(cookie);
            ImageIO.write((RenderedImage) map.get("img"), "jpg", response.getOutputStream());
            code = map.get("code").toString();
            logger.info(String.format("验证码图片下载成功，验证码为%s",map.get("code")));
        } catch (IOException e) {
            logger.error("验证码图片下载异常",e);
        }
        return code;
    }

    /**
     * 生成指定长度的纯数字验证码
     * @param length
     * @return
     */
    public static String getIntRandom(int length) {
        Random random = new Random();
        String code = random.nextInt((int) Math.pow(10,length)) + "";
        int randLength = code.length();
        if (randLength < length) {
            for (int i = 1; i <= 6 - randLength; i++)
                code = "0" + code;
        }
        return code;
    }


    /**
     * 生成指定长度的数字字母混合验证码
     * @param length
     * @return
     */
    public static String getStringRandom(int length) {
        String code = "";
        Random random = new Random();

        //参数length，表示生成几位随机数
        for(int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                code += (char)(random.nextInt(26) + temp);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                code += String.valueOf(random.nextInt(10));
            }
        }
        return code;
    }


    public static void main(String[] args) throws Exception {
        getCodePic("/Users/feng/Desktop/code.jpg");
    }
}