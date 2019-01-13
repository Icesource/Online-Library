package com.ice.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * ftp上传下载工具类
 **/
@Component
@Slf4j
public class FtpUtil {

    @Value("${library.ftp.host}")
    private String host;

    @Value("${library.ftp.port}")
    private int port;

    @Value("${library.ftp.username}")
    private String username;

    @Value("${library.ftp.password}")
    private String password;

    @Value("${library.ftp.basePath}")
    private String basePath;

    /**
     * Description: 向FTP服务器上传文件
     *
     * @param filePath FTP服务器文件存放路径。例如分日期存放：/2018/01/01。文件的路径为basePath+filePath
     * @param filename 上传到FTP服务器上的文件名
     * @param input    输入流
     * @return 成功返回true，否则返回false
     */
    public boolean uploadFile(String filePath, String filename, InputStream input) {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host, port);
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return result;
            }
            // 切换到被动模式
            ftp.enterLocalPassiveMode();
            ftp.setControlEncoding("UTF-8");
            //切换到上传目录
            if (!ftp.changeWorkingDirectory(basePath + filePath)) {
                //如果目录不存在创建目录
                String[] dirs = filePath.split("/");
                String tempPath = basePath;
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) continue;
                    tempPath += "/" + dir;
                    if (!ftp.changeWorkingDirectory(tempPath)) {
                        if (!ftp.makeDirectory(tempPath)) {
                            return result;
                        } else {
                            ftp.changeWorkingDirectory(tempPath);
                        }
                    }
                }
            }
            //为了加大上传文件速度，将InputStream转成BufferInputStream
            BufferedInputStream in = new BufferedInputStream(input);
            //加大缓存区
            ftp.setBufferSize(1024 * 1024);
            //设置上传文件的类型为二进制类型
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            //上传文件
            if (!ftp.storeFile(filename, in)) {
                return result;
            }
            in.close();
            ftp.logout();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Description: 从FTP服务器下载文件
     *
     * @param remotePath FTP服务器上的相对路径
     * @param fileName 要下载的文件名
     * @return
     */
    public byte[] downloadFile(String remotePath, String fileName) {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host, port);
            ftp.login(username, password);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return swapStream.toByteArray();
            }
            // 切换到被动模式
            ftp.enterLocalPassiveMode();
            ftp.setControlEncoding("UTF-8");
            if(remotePath.startsWith("/")){
                remotePath = remotePath.substring(1);
            }
            ftp.changeWorkingDirectory(remotePath);
            ftp.retrieveFile(fileName, swapStream);
            ftp.logout();
            return swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            try {
                swapStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return swapStream.toByteArray();
    }
}