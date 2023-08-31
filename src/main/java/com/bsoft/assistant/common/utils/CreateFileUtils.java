package com.bsoft.assistant.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Map;

/**
 * /**
 *
 * @ClassName CreateFileUtils
 * @Description 创建文件工具类
 * @Author Administrator
 * @Date 2019/6/3 18:12
 * @Version 1.0
 **/
public class CreateFileUtils {
    private static final Logger logger = LoggerFactory.getLogger(CreateFileUtils.class);
    /***
     * @Description: 创建文件
     * @Author: Vee
     * @Param: [fileName：文件名, fileType：文件类型,filePath:文件路径，data:写入数据集]
     * @return: boolean
     * @Date: 2019/6/3
     **/
    public static boolean createFile(String fileName, String fileType, String filePath, Object data) {
        File file = new File(filePath, fileName + "." + fileType);
        OutputStream out = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
            bw.write(String.valueOf(data));
            bw.flush();
        } catch (IOException e) {
            logger.info("创建文件异常，异常信息为:  "+e.getMessage());
            //e.printStackTrace();
        } finally {
            try {
                if(out!=null){
                    out.close();
                }
                if(bw!=null){
                    bw.close();
                }

            } catch (IOException e) {
                logger.info("流关闭异常,异常信息为: "+e.getMessage());
               // e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 使用Clob类型生成文件
     *
     * @param fileName
     * @param fileType
     * @param filePath
     * @param clob
     * @return
     */
    public static boolean createFile(String fileName, String fileType, String filePath, Clob clob) {
        boolean flag = false;
        File file = new File(filePath, fileName + "." + fileType);
        OutputStream out = null;
        BufferedWriter bw = null;
        Reader reader = null;
        BufferedReader br = null;
        try {
            out = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
            reader = clob.getCharacterStream();
            br = new BufferedReader(reader);
            String str = br.readLine();
            while (null != str) {
                bw.write(str);
                bw.newLine();
                str = br.readLine();
            }
            bw.flush();
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                bw.close();
                reader.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static String getContextUrl(HttpServletRequest request, Map<String, Object> params) {
        StringBuffer url = request.getRequestURL();
        String contextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
        if (!contextUrl.endsWith("/")) {
            contextUrl += "/";
        }
        String path = params.get("cfgcode")==null?"":params.get("cfgcode").toString().toLowerCase();
        String code="";
        if(path.indexOf("ydd")>=0)
        {
            code = "ydd";
        }
        else if(path.indexOf("jsc")>=0)
        {
            code = "jsc";
        }
        else if(path.indexOf("tjfx")>=0)
        {
            code = "tjfx";
        }
        if("".equals(code)){
            return contextUrl + params.get("type") + "/html/" + params.get("fileName") + "." + params.get("fileType");
        }
        return contextUrl + params.get("type") + "/dist/"+ code +"/"+ params.get("fileName") + "." + params.get("fileType");
    }

    /**
     * @param file：上传的文件
     * @param path：文件要保存的路径
     * @param src：文件被引用的路径
     * @return
     * @throws Exception
     * @Description: 保存上传的文件到指定目录下
     */
    public static String writeFile(MultipartFile file, String path, String src)throws Exception {
        String fileName = getFileName(file.getOriginalFilename());
        String filePrefix = String.valueOf(System.currentTimeMillis());
        String fileSuffix = getFileSuffix(fileName);
        long fileSize=file.getSize();
        if (!"png".equals(fileSuffix) && !"jpg".equals(fileSuffix)) {
            throw new Exception("上传文件只能是png、jpg格式!");
        }
        if ((fileSize / 1024) > 1000) {
            throw new Exception("文件大小不能超过1000KB!");
        }
        //如果指定的路径不存在，则创建
        File desFile = new File(path);
        if (!desFile.exists() && !desFile.isDirectory()) {
            desFile.mkdirs();
        }
        fileName = filePrefix + "." + fileSuffix;
        File saveFile=new File(path,fileName);
        file.transferTo(saveFile);
        String filePath=src+ fileName;
        return filePath;
    }

    /**
     * @param path
     * @return
     * @throws Exception
     * @Description: 删除指定目录下的文件
     */
    public static boolean deleteFile(String path, String uploadPath) throws Exception {
        String fileName=getFileName(path);
        String filePath=uploadPath+fileName;
        File file = new File(filePath);
        boolean deleteStatus = false;
        if (file.exists() && file.isFile()) {
            deleteStatus = file.delete();
        } else {
            throw new Exception("该文件不存在！");
        }
        return deleteStatus;
    }

    /**
     * 读取文件内容
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        StringBuffer fileContext = new StringBuffer("");
        File file = new File(filePath);
        BufferedReader br=null;
        try {
            if (file.exists() && file.isFile()) {
                 br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
                 String str=br.readLine();;
                 while (str!=null){
                     fileContext.append(str).append(System.lineSeparator());
                     str=br.readLine();
                 }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileContext.toString();
    }

    /**
     *  * 注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：
     * c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt  * 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
     *  * @param fileName  * @return  
     */
    private static String getFileName(String fileName) {
        fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
        return fileName;
    }
    /**
     *  * 获得文件名前缀  
     */
    public static String getFilePrefix(String fileName) {
        String[] sArr = fileName.split("\\.");
        return sArr[0];
    }

    /**
     *  * 获得文件名后缀  
     */
    public static String getFileSuffix(String fileName) {
        String[] sArr = fileName.split("\\.");
        return sArr[1];
    }
    /**
     * 替换文件中的字符串
     * @param srcStr  原来的字符串
     * @param replaceStr   替换后的字符串
     * @param path      文件的全路径
     * @throws Exception
     */
    public static void replaceFileContent(String srcStr, String replaceStr, String path) throws IOException {
        File file = new File(path);
        StringBuffer fileContext = new StringBuffer();
        BufferedReader br = null;
        String str= null;
        BufferedWriter bw = null;
        FileOutputStream out = null;
        if (file.exists() && file.isFile()) {
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
                str = br.readLine();
                while (str!=null){
                    str = str.replaceAll(srcStr,replaceStr);
                    fileContext.append(str).append(System.lineSeparator());
                    str=br.readLine();
                }
                out = new FileOutputStream(file);
                bw = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
                bw.write(fileContext.toString());
                br.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
               throw e;
            }
        }
    }
    /**
     * 复制文件   html文件
     * @param sourceFilePath 源文件
     * @param copyFilePath    复制后的文件
     * @throws Exception
     */
    public static void copyFile(String sourceFilePath, String copyFilePath) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        // 定义一个字节数组，作为缓冲区
        byte[] buff = new byte[1024];
//        long begintime = System.currentTimeMillis(); // 获取拷贝文件前的系统时间
        int len;
        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilePath));
            bos = new BufferedOutputStream(new FileOutputStream(copyFilePath));
            while ((len = bis.read(buff)) != -1) {
                bos.write(buff, 0, len); // 从第一个字节开始，向文件写入len个字节
            }
            bis.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw  e;
        }
    }
}
