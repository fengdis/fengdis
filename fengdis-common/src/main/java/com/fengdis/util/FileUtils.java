package com.fengdis.util;

import com.fengdis.common.Constant;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @version 1.0
 * @Descrittion: 文件工具类
 * @author: fengdi
 * @since: 2018/8/26 0026 19:18
 */
public class FileUtils {

    private FileUtils(){
    }

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);


    public static String fileUploadPath = PropertiesUtils.propertiesMap.get("file-upload.dir");

    public static String test(MultipartFile file,String uploadPath) {
        if (file.isEmpty()) {
            logger.warn("上传文件为空");
            return null;
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadPath + file.getOriginalFilename());
            //file.transferTo(new File(path));
            if(!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.write(path, bytes);
            logger.info("文件上传成功：" + file.getOriginalFilename());
        } catch (IOException e) {
            logger.error("文件上传异常",e);
        }
        return null;
    }

    /**
     * 文件上传
     * @param multipartFile
     * @param uploadPath
     * @param newName
     * @param isWaterMark
     * @return
     */
    public static String upload(MultipartFile multipartFile,String uploadPath,String newName,boolean isWaterMark) {
        if ("".equals(multipartFile.getOriginalFilename()) || multipartFile.isEmpty()) {
            logger.warn("文件为空，无法上传");
            return null;
        }

        if (!multipartFile.getOriginalFilename().contains(".") || multipartFile.getOriginalFilename().indexOf(".") == -1) {
            logger.warn("文件格式不确定，无法上传");
            return null;
        }

        if (multipartFile.getSize() > 5242880) {//5m
            logger.warn("文件上传的大小限制，无法上传");
            return null;
        }

        String fileName = multipartFile.getOriginalFilename();

        String[] suffixs = fileName.split("\\.");
        String suffix = suffixs[suffixs.length - 1];
        String newFileName = newName + "." + suffix;

        File dir = new File(uploadPath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        File file = new File(uploadPath + newFileName);
        if(file.exists()){
            file.delete();
        }

        try {
            if(isWaterMark && ("png".equalsIgnoreCase(suffix) || "jpg".equalsIgnoreCase(suffix) || "jpeg".equalsIgnoreCase(suffix))){
                WaterMarkUtils.textWaterMark(multipartFile,uploadPath + newFileName,PropertiesUtils.propertiesMap.get("watermark.text"));
            }else {
                multipartFile.transferTo(new File(uploadPath + newFileName));
            }
            logger.info(String.format("文件%s上传成功",uploadPath + newFileName));
            return newFileName;
        } catch (Exception e) {
            logger.error(String.format("文件%s上传异常",uploadPath + newFileName),e);
            return null;
        }
    }

    /**
     * 多文件上传
     * @param files
     * @return
     */
    public static List<String> uploads(List<MultipartFile> files,String uploadPath,boolean isReName,boolean isWaterMark){
        List<String> names = new ArrayList<>();
        if(files !=null && files.size()>0){
            for (MultipartFile file : files) {
                String reName;
                if(isReName){
                    reName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                }else {
                    String[] suffixs = file.getOriginalFilename().split("\\.");
                    reName = suffixs[suffixs.length - 2];
                }
                String fileName = upload(file, uploadPath, reName, isWaterMark);
                if(fileName == null){
                    continue;
                }else {
                    names.add(fileName);
                }
            }
        }
        return names;
    }

    /**
     * 文件下载
     * @param name
     * @param filePath
     * @param response
     * @return
     */
    public static boolean download(String name,String filePath,HttpServletResponse response) {
        String[] fileNames = name.split("/");

        //String fileName = request.getSession().getServletContext().getRealPath("upload") + name;
        String fileName = filePath + name;

        File file = new File(fileName);
        if (!file.exists()) {
            logger.warn("文件不存在，无法下载");
            return false;
        }
        BufferedOutputStream out = null;
        try {
            // 获取输入流
            InputStream bis = new BufferedInputStream(new FileInputStream(new File(fileName)));
            // 获取去掉路径的文件名
            String filename = fileNames[fileNames.length - 1];
            // 转码，免得文件名中文乱码
            filename = URLEncoder.encode(filename, "UTF-8");
            // 设置文件下载头
            response.addHeader("Content-Disposition", "attachment;filename=" + filename);
            // 设置文件ContentType类型，这样设置，会自动判断下载文件类型
            //response.setContentType("multipart/form-data");
            //response.setContentType("application/force-download");
            response.setContentType("application/octet-stream");
            // 获取输出流
            out = new BufferedOutputStream(response.getOutputStream());
            int len = 0;
            while ((len = bis.read()) != -1) {
                out.write(len);
                out.flush();
            }
            logger.info(String.format("文件%s下载成功",fileName));
            return true;
        } catch (Exception e) {
            logger.error(String.format("文件%s下载异常",fileName),e);
            return false;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件预览
     * @param name
     * @param filePath
     * @param response
     * @return
     */
    public static boolean preview(String name,String filePath,HttpServletResponse response) {
        String[] fileNames = name.split("/");

        //String fileName = request.getSession().getServletContext().getRealPath("upload") + name;
        String fileName = filePath + name;

        File file = new File(fileName);
        if (!file.exists()) {
            logger.warn("文件不存在，无法预览");
            return false;
        }
        BufferedOutputStream out = null;
        try {
            // 获取输入流
            InputStream bis = new BufferedInputStream(new FileInputStream(new File(fileName)));
            // 获取去掉路径的文件名
            String filename = fileNames[fileNames.length - 1];
            // 转码，免得文件名中文乱码
            filename = URLEncoder.encode(filename, "UTF-8");
            // 设置文件下载头
            response.addHeader("Content-Disposition", "inline;filename=" + filename);
            // 设置文件ContentType类型，这样设置，会自动判断下载文件类型
            //response.setContentType("multipart/form-data");
            //response.setContentType("application/force-download");
            response.setContentType("application/octet-stream");
            // 获取输出流
            out = new BufferedOutputStream(response.getOutputStream());
            int len = 0;
            while ((len = bis.read()) != -1) {
                out.write(len);
                out.flush();
            }
            logger.info(String.format("文件%s预览成功",fileName));
            return true;
        } catch (Exception e) {
            logger.error(String.format("文件%s预览异常",fileName),e);
            return false;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取指定盘符文件列表
     * @param fileDir
     * @return
     */
    public static List<String> list(String fileDir) {
        File dir = new File(fileDir);
        String[] files = null;
        if(dir.isDirectory()){
            files = dir.list();
        }
        return Arrays.asList(files);
    }

    /**
     * 删除文件（全路径名）
     * @param fileName
     * @return
     */
    public static void delete(String fileName) {
        File file_ = new File(fileName);
        if (file_.exists()) {
            file_.delete();
        }
    }

    /**
     * 文件转base64字符串（主要针对图片处理）
     * @param file 文件
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(File file) throws Exception {
        byte[] buffer;
        FileInputStream inputFile = null;
        try {
            inputFile = new FileInputStream(file);
            buffer = new byte[(int)file.length()];
            inputFile.read(buffer);
        } finally {
            if (inputFile != null) inputFile.close();
        }
        return new BASE64Encoder().encode(buffer);
    }

    /**
     * 文件转base64字符串（主要针对图片处理）
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(InputStream inputStream) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int length = 0;
            byte[] buffer = new byte[1024];
            while((length = inputStream.read(buffer)) > 0){
                out.write(buffer,0,length);
            }
        } finally {
            if (inputStream != null) inputStream.close();
            if(out != null) out.close();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(out.toByteArray());
    }

    /**
     * base64字符串解码转字节流（主要针对图片处理）
     * @param base64Code
     * @throws Exception
     */
    public static byte[] decodeBase64File(String base64Code) throws Exception {
        //去除前缀
        //base64Code = base64Code.substring(base64Code.indexOf(",")+1);
        base64Code = base64Code.replaceAll("data:image/(jpg|png|jpeg);base64,","");
        return new BASE64Decoder().decodeBuffer(base64Code);
    }

    /**
     * base64码转文件并保存（主要针对图片处理）
     * @param base64
     * @param targetFileName 目标文件
     * @throws Exception
     */
    public static void base642file(String base64,String targetFileName) throws Exception{
        if (base64 == null){
            logger.warn("图片base64码为空");
            return;
        }
        //Base64解码
        OutputStream out = null;

        try {

            byte[] b = decodeBase64File(base64);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }

            File file = new File(targetFileName);
            if(file.isDirectory() && !file.exists()){
                file.mkdirs();
            }else if(file.isFile() && file.exists()){
                file.delete();
            }

            out = new FileOutputStream(targetFileName);
            out.write(b);
        } finally {
            out.flush();
            out.close();
        }
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

    /**
     * 判断当前文件是否是zip文件
     * @param fileName 文件名
     * @return
     */
    public static boolean isZip(String fileName) {
        return fileName.toLowerCase().endsWith(Constant.FilePostFix.ZIP_FILE);
    }


    public static void removeDocument(String fileName){
        File file=new File(fileName);
        if(file.exists() && file.isFile()) {
            file.delete();
        }
        if(file.isDirectory()){
            delDir(fileName);
        }
        if (fileName.lastIndexOf(Constant.FilePostFix.ZIP_FILE) > 0) {
            delDir(fileName.substring(0,fileName.lastIndexOf(Constant.FilePostFix.ZIP_FILE))+"/");
        }

    }

    /**
     * 解压zip
     * @param sourcePath
     * @return
     */
    public static boolean checkZipFile(String sourcePath){
        System.setProperty("sun.zip.encoding", System.getProperty("sun.jnu.encoding"));
        ZipFile zipFile =null;
        try {
            File sourceFile = new File(sourcePath);
            zipFile = new ZipFile(sourcePath, "gbk");
            if ((!sourceFile.exists()) && (sourceFile.length() <= 0)) {
                throw new Exception("要解压的文件不存在!");
            }
            Enumeration<?> e = zipFile.getEntries();
            while (e.hasMoreElements()) {
                ZipEntry zipEnt = (ZipEntry) e.nextElement();
                if (zipEnt.isDirectory()) {
                    return false;
                }
                if(zipEnt.getName().endsWith(".shp")){
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                if(null!=zipFile){
                    zipFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static List<String> listFile = new ArrayList<>();

    /**
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
     * (把指定文件夹下的所有文件目录和文件都压缩到指定文件夹下)
     * @param sourceFilePath
     *            :待压缩的文件路径
     * @param zipFilePath
     *            :压缩后存放路径
     * @param fileName
     *            :压缩后文件的名称
     * @return
     */
    public static boolean fileToZip(String sourceFilePath,String zipFilePath,String fileName)throws  Exception{
        boolean flag = false;
        FileOutputStream fos =null;
        ZipOutputStream zos =null;
        BufferedInputStream bis =null;
        FileInputStream  fis =null;
        BufferedOutputStream bufferedOutputStream =null;
        File sourceFile = new File(sourceFilePath);
        if(sourceFile.exists() == false){
            throw new Exception("待压缩的文件目录："+sourceFilePath+"不存在.");
        }else{
            try {
                File zipFile = new File(zipFilePath +fileName );
                if(zipFile.exists()){
                    throw new Exception(zipFilePath + "目录下存在名字为:" + fileName +Constant.FilePostFix.ZIP_FILE +"打包文件.");
                }else{
                    File[] sourceFiles = sourceFile.listFiles();
                    if(null == sourceFiles || sourceFiles.length<1){
                        throw new Exception("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.");
                    }else{
                        fos = new FileOutputStream(zipFile);
                        bufferedOutputStream = new BufferedOutputStream(fos);
                        zos = new ZipOutputStream(bufferedOutputStream);
                        byte[] bufs = new byte[1024*10];
                        for(int i=0;i<sourceFiles.length;i++){
                            //创建ZIP实体，并添加进压缩包
                            ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                            zos.putNextEntry(zipEntry);
                            //读取待压缩的文件并写进压缩包里
                            fis = new FileInputStream(sourceFiles[i]);
                            bis = new BufferedInputStream(fis, Constant.BYTE_BUFFER *Constant.BUFFER_MULTIPLE);
                            int read;
                            while((read=bis.read(bufs, 0, Constant.BYTE_BUFFER *Constant.BUFFER_MULTIPLE)) != -1){
                                zos.write(bufs,0,read);
                            }
                            fis.close();
                            bis.close();
                        }
                        flag = true;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally{
                //关闭流
                try {
                    if (null != bis) {
                        bis.close();
                    }
                    if (null != zos) {
                        zos.close();
                    }
                    if (null != bufferedOutputStream) {
                        bufferedOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    public static void getFile(String path) {
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (File f : tempList) {
            if (f.isFile()) {
                listFile.add(f.getPath());
                System.out.println(f.getPath());
                continue;
            }
            if (f.isDirectory()) {
                getFile(f.getPath());
            }
        }

    }

    /**
     * 保存文件到临时目录
     * @param inputStream 文件输入流
     * @param fileName 文件名
     */
    public static void savePic(InputStream inputStream, String fileName) {
        OutputStream os = null;
        try {
            // 保存到临时文件
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流保存到本地文件
            File tempFile = new File(fileUploadPath);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            os = new FileOutputStream(tempFile.getPath() + File.separator + fileName);
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 完毕，关闭所有链接
            try {
                os.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File createTemporaryFile(InputStream file, String name) throws Exception {
        File temp = new File(name);
        OutputStream out = new FileOutputStream(temp);
        try {
            int byteCount = 0;
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = file.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
        } finally {
            try {
                file.close();
            } catch (IOException e) {

            }
            try {
                out.close();
            } catch (IOException e) {

            }
        }
        return temp;
    }

    private static void copyFile(File newFile, FileInputStream file) throws Exception {
        FileOutputStream outFile = new FileOutputStream(newFile);
        FileChannel inC = file.getChannel();
        FileChannel outC = outFile.getChannel();
        int length = 2097152;
        while (true) {
            if (inC.position() == inC.size()) {
                inC.close();
                outC.close();
                outFile.close();
                file.close();
                return ;
            }
            if ((inC.size() - inC.position()) < 20971520) {
                length = (int) (inC.size() - inC.position());
            }else {
                length = 20971520;
            }
            inC.transferTo(inC.position(), length, outC);
            inC.position(inC.position() + length);
        }

    }

    private static String getFileName(Date time, int type, String name,String tag) {
        StringBuffer str = new StringBuffer();
        if (type== Constant.FileType.FILE_IMG) {
            str.append(Constant.FileType.FILE_IMG_DIR);
        }
        if (type==Constant.FileType.FILE_ZIP) {
            str.append(Constant.FileType.FILE_ZIP_DIR);
        }
        if (type==Constant.FileType.FILE_VEDIO) {
            str.append(Constant.FileType.FILE_VEDIO_DIR);
        }
        if (type==Constant.FileType.FILE_APK) {
            str.append(Constant.FileType.FILE_APK_DIR);
        }
        if (type==Constant.FileType.FIVE_OFFICE) {
            str.append(Constant.FileType.FIVE_OFFICE_DIR);
        }
        str.append(DateUtils.dateTime2String(time,DateUtils.FMT_yyyyMMddHHmmss));
        str.append("/");
        str.append(System.currentTimeMillis());
        if(!ComUtils.isEmpty(tag)) {
            str.append(tag);
        }
        str.append(name.substring(name.indexOf(".")));
        return str.toString();
    }

    public static int getFileType(String originalFilename) {
        String postFix = originalFilename.split("//.")[originalFilename.split("//.").length-1];
        if(Arrays.asList(Constant.FilePostFix.IMAGES).contains(postFix)){
            return Constant.FileType.FILE_IMG;
        }
        if(Arrays.asList(Constant.FilePostFix.ZIP).contains(postFix)){
            return Constant.FileType.FILE_ZIP;
        }
        if(Arrays.asList(Constant.FilePostFix.VIDEO).contains(postFix)){
            return Constant.FileType.FILE_VEDIO;
        }
        if(Arrays.asList(Constant.FilePostFix.APK).contains(postFix)){
            return Constant.FileType.FILE_APK;
        }
        if(Arrays.asList(Constant.FilePostFix.OFFICE).contains(postFix)){
            return Constant.FileType.FIVE_OFFICE;
        }
        return Constant.FileType.FILE_IMG;
    }

    /**
     * 删除文件目录
     * @param path
     */
    private static void delDir(String path){
        File dir=new File(path);
        if(dir.exists()){
            File[] tmp=dir.listFiles();
            for(int i=0;i<tmp.length;i++){
                if(tmp[i].isDirectory()){
                    delDir(path+File.separator+tmp[i].getName());
                }else{
                    tmp[i].delete();
                }
            }
            dir.delete();
        }
    }

    /**
     * 截取文件排除后缀名
     * @param fileName 文件名
     * @return
     */
    public static String cutNameSuffix(String fileName) {
        String suffix = fileName.substring(0,fileName.lastIndexOf("."));
        return suffix;
    }


    /**
     * 解压zip格式的压缩文件到指定位置
     *
     * @param sourcePath
     *            压缩文件
     * @param targetPath
     *            解压目录
     * @throws Exception
     */
    public static boolean unZipFiles(String sourcePath, String targetPath) throws Exception {
        System.setProperty("sun.zip.encoding", System.getProperty("sun.jnu.encoding"));
        InputStream is =null;
        BufferedInputStream bis =null;
        try {
            (new File(targetPath)).mkdirs();
            File sourceFile = new File(sourcePath);
            // 处理中文文件名乱码的问题
            ZipFile zipFile = new ZipFile(sourcePath, "UTF-8");
            if ((!sourceFile.exists()) && (sourceFile.length() <= 0)) {
                throw new Exception("要解压的文件不存在!");
            }
            String strPath, gbkPath, strtemp;
            File tempFile = new File(targetPath);
            strPath = tempFile.getAbsolutePath();
            Enumeration<?> e = zipFile.getEntries();
            while (e.hasMoreElements()) {
                ZipEntry zipEnt = (ZipEntry) e.nextElement();
                gbkPath = zipEnt.getName();
                if (zipEnt.isDirectory()) {
                    strtemp = strPath + File.separator + gbkPath;
                    File dir = new File(strtemp);
                    dir.mkdirs();
                    continue;
                } else {
                    // 读写文件
                    is = zipFile.getInputStream((ZipEntry) zipEnt);
                    bis = new BufferedInputStream(is);
                    gbkPath = zipEnt.getName();
                    strtemp = strPath + File.separator + gbkPath;
                    // 建目录
                    String strsubdir = gbkPath;
                    for (int i = 0; i < strsubdir.length(); i++) {
                        if ("/".equalsIgnoreCase(strsubdir.substring(i, i + 1))) {
                            String temp = strPath + File.separator + strsubdir.substring(0, i);
                            File subdir = new File(temp);
                            if (!subdir.exists()) {
                                subdir.mkdir();
                            }
                        }
                    }
                    FileOutputStream fos = new FileOutputStream(strtemp);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    int c;
                    while ((c = bis.read()) != -1) {
                        bos.write((byte) c);
                    }
                    bos.flush();
                    fos.close();
                    bos.close();
                }
            }
            zipFile.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回某个类所在包最顶层文件夹
     *
     * @param clazz 类
     * @return 顶层文件夹路径
     */
    public static String getTopClassPath(Class<?> clazz) {
        String path = StringUtils.utf8Decoding(clazz.getResource("/").getPath());
        return path;
    }

    /**
     * get the jars path
     *
     * @return
     */
    public static String getJarPath() {
        return FileUtils.getParent(FileUtils.getTopClassPath(FileUtils.class), 1) + File.separator + "lib";
    }

    public static String getClassPath(String folderName) {
        return getJarPath().replace("lib", folderName);
    }

    /**
     * 获得类所在文件路径
     *
     * @param clazz
     * @return
     */
    public static String getCurrPath(Class<?> clazz) {
        return StringUtils.utf8Decoding(clazz.getResource("/").getPath() + clazz.getName().replace(".", File.separator));
    }

    /**
     * 创建一个文件夹
     *
     * @param path
     * @return
     */
    public static boolean createDir(String path) {
        boolean flag = false;
        File file = new File(StringUtils.utf8Decoding(path));
        if (!file.exists()) {
            if (!file.isDirectory()) {
                flag = file.mkdir();
            }
        }
        return flag;
    }

    /**
     * 创建一个文件
     *
     * @param path 文件路径
     * @return
     * @throws IOException
     */
    public static boolean createFile(String path) throws IOException {
        return createFile(path, false);
    }

    /**
     * 是否强制新建文件
     *
     * @param path     文件路径
     * @param isDelete 文件存在后是否删除标记
     * @return 文件创建是否成功标记
     * @throws IOException
     * @see 1.原文件存在的情况下会删除原来的文件，重新创建一个新的同名文件，本方法返回文件创建成功标记
     * @see 2.原文件存在但isDelete参数设置为false，表示不删除源文件，本方法返回文件创建失败标记
     */
    public static boolean createFile(String path, boolean isDelete) throws IOException {
        // 加载文件
        File file = new File(StringUtils.utf8Decoding(path));
        // 文件是否创建成功
        boolean flag = true;
        // 判断文件是否存在
        if (file.exists()) {
            if (isDelete) { // 文件存在后删除文件
                // 删除原文件
                file.delete();
                // 创建新文件
                file.createNewFile();
            } else {
                flag = false;
            }
        } else {
            file.createNewFile();
        }

        return flag;
    }

    /**
     * 将oldFile移动到指定目录
     *
     * @param oldFile
     * @param newDir
     * @return
     */
    public static boolean moveFileTo(File oldFile, String newDir) {
        StringBuilder sb = new StringBuilder(newDir);
        sb.append(File.separator).append(oldFile.getName());
        File toDir = new File(StringUtils.utf8Decoding(sb.toString()));
        boolean flag = false;
        if (!toDir.exists()) {
            flag = oldFile.renameTo(toDir);
        }
        return flag;
    }

    /**
     * 不使用renameTo,如果文件(isFile)不存在则不复制.
     *
     * @param sourceFile
     * @param target
     * @throws Exception
     */
    public static void moveFile(File sourceFile, String target) throws Exception {
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            return;
        }
        InputStream inputStream = null;
        File targetFile = new File(target + File.separator + sourceFile.getName());
        OutputStream outputStream = null;
        inputStream = new FileInputStream(sourceFile);
        outputStream = new FileOutputStream(targetFile);
        int readBytes = 0;
        byte[] buffer = new byte[10000];
        while ((readBytes = inputStream.read(buffer, 0, 10000)) != -1) {
            outputStream.write(buffer, 0, readBytes);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    /**
     * 返回当前文件的上层文件夹路径（第几层由参数floor决定）
     *
     * @param f
     * @param floor
     * @return
     */
    public static String getParent(File f, int floor) {
        String result = "";
        if (f != null && f.exists()) {
            for (int i = 0; i < floor; ++i) {
                f = f.getParentFile();
            }

            if (f != null && f.exists()) {
                result = f.getPath();
            }
        }

        return StringUtils.utf8Decoding(result) + File.separator;
    }

    public static String getParent(String path, int floor) {
        return getParent(new File(path), floor);
    }

    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        boolean flag = false;
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    deleteFile(f);
                }
            }
            flag = file.delete();
        }

        return flag;
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param sPath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public static boolean DeleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) { // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) { // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else { // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查文件名是否合法
     *
     * @param fileName
     * @return
     */
    public static boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.length() > 255)
            return false;
        else {
            return fileName.matches(
                    "[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
        }
    }

    /**
     * 复制文件
     *
     * @param src
     * @param dst
     */
    public static void copy(File src, File dst) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        return;
    }

    /**
     * 取指定文件的扩展名
     *
     * @param filePathName 文件路径
     * @return 扩展名
     */
    public static String getFileExt(String filePathName) {
        int pos = 0;
        pos = filePathName.lastIndexOf('.');
        if (pos != -1) {
            return filePathName.substring(pos + 1, filePathName.length());
        }
        else {
            return "";
        }
    }

    /**
     * 去掉文件扩展名
     *
     * @param filename
     * @return
     */
    public static String trimExtension(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int i = filename.lastIndexOf('.');
            if ((i > -1) && (i < (filename.length()))) {
                return filename.substring(0, i);
            }
        }
        return filename;
    }

    /**
     * 读取文件大小
     *
     * @param filename 指定文件路径
     * @return 文件大小
     */
    public static int getFileSize(String filename) {
        try {
            File fl = new File(filename);
            int length = (int) fl.length();
            return length;
        } catch (Exception e) {
            return 0;
        }

    }

    /**
     * 判断是否是图片
     *
     * @param file
     * @return
     */
    public static boolean isImage(File file) {
        boolean flag = false;
        try {
            ImageInputStream is = ImageIO.createImageInputStream(file);
            if (null == is) {
                return flag;
            }
            is.close();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * TODO. 读取文件内容
     *
     * @param file
     * @param fullFilePath
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unused")
    public static String readFileContent(File file, String fullFilePath) throws IOException {
        String returnStr = "";
        if (ComUtils.isEmpty(file) && ComUtils.isEmpty(fullFilePath)) {
            return "";
        }
        if (ComUtils.isEmpty(file)) {
            file = new File(fullFilePath);
        }
        FileInputStream in = null;

        try {
            in = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = in.read(buf)) > 0) {
                returnStr += new String(buf, "utf-8");
                buf = new byte[1024];
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage() + ";" + file.getPath(), e);
            throw e;
        } catch (IOException e) {
            logger.error(e.getMessage() + ";" + file.getPath(), e);
            throw e;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return returnStr;
    }

    public static boolean writeToFile(String content, File file, String fullFilePath) throws IOException {
        if ((ComUtils.isEmpty(file) && ComUtils.isEmpty(fullFilePath)) || ComUtils.isEmpty(content)) {
            return false;
        }
        if (ComUtils.isEmpty(file)) {
            file = new File(fullFilePath);
        }
        FileOutputStream out = null;

        try {

            out = new FileOutputStream(file);

            out.write(content.getBytes("utf-8"));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage() + ";" + file.getPath(), e);
            throw e;
        } catch (IOException e) {
            logger.error(e.getMessage() + ";" + file.getPath(), e);
            throw e;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 在指定的目录下创建文件
     *
     * @param directory
     * @param fileName
     * @param content
     * @throws Exception
     */
    public static String createFile(String directory, String fileName, InputStream content) throws Exception {
        File currentDir = new File(directory);
        if (!currentDir.exists()) {
            currentDir.mkdirs();
        }
        FileOutputStream fileOut = null;
        String fullFilePath = directory + File.separator + fileName;
        try {
            fileOut = new FileOutputStream(fullFilePath);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = content.read(buffer)) > 0) {
                fileOut.write(buffer);
            }
        } finally {
            if (content != null) {
                content.close();
            }
            if (fileOut != null) {
                fileOut.close();
            }
        }
        return fullFilePath;
    }

    public static File mkdir(String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static String readFileContent(File file, Charset charset) throws Exception {
        if (!file.exists() || file.isDirectory()) throw new Exception("file is not exists or is a directory");
        StringWriter out = new StringWriter();
        FileInputStream in = new FileInputStream(file);
        try {
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = in.read(buffer)) > 0) {
                out.write(new String(buffer, 0, length, charset));
            }
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }
        return out.toString();
    }

}
