package com.fengdis.util;

import ch.ethz.ssh2.*;
import com.fengdis.common.BaseExServiceException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @version 1.0
 * @Descrittion: 远程shell工具类（注意连接的关闭，避免占用资源）
 * @author: fengdi
 * @since: 2019/09/07 17:26
 */
public class RemoteShellUtils {

    public static void main(String[] args)throws Exception {
        RemoteShellUtils remoteShellUtils = new RemoteShellUtils("39.105.155.44",22,"root","admin@aliyun");
        System.out.println(remoteShellUtils.exec1("cd && ll"));
    }

    private static final Logger logger = LoggerFactory.getLogger(RemoteShellUtils.class);

    private Connection conn;

    private String hostname;
    private int port;
    private String username;
    private String password;

    private String charset = Charset.defaultCharset().toString();

    private static final int SSH_PORT = 22;

    private static final int TIME_OUT = 1000 * 5 * 60;

    public RemoteShellUtils(String hostname,int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.init(hostname,port,username,password);
    }

    public RemoteShellUtils(String hostname, String username, String password) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.init(hostname,SSH_PORT,username,password);
    }

    /**
     * 获取连接并登录
     * @param hostname
     * @param port
     * @param username
     * @param password
     * @return
     */
    private Connection init(String hostname,int port,String username,String password){
        conn = new Connection(hostname, port);
        try {
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(username, password);
            if(isAuthenticated == false){
                throw new IOException("isAuthentication failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭连接（避免占用资源）
     */
    public void close(){
        this.conn.close();
    }

    /**
     * 用方法execCommand执行Shell命令的时候，会遇到获取不全环境变量的问题，
     * 　　　　　　　　　　比如执行 hadoop fs -ls 可能会报找不到hadoop 命令的异常
     * 　　　　　　　　　　试着用execCommand执行打印环境变量信息的时候，输出的环境变量不完整
     * 　　　　　　　　　　与Linux主机建立连接的时候会默认读取环境变量等信息
     * 　　　　　　　　　　可能是因为session刚刚建立还没有读取完默认信息的时候，execCommand就执行了Shell命令
     * @param cmds
     * @return
     * @throws Exception
     */
    @Deprecated
    public int exec1(String cmds) throws Exception {
        InputStream stdOut = null;
        InputStream stdErr = null;
        String outRes = "";
        String outErr = "";
        int exitStatus = -1;
        try {
            if (conn != null) {
                Session session = conn.openSession();
                session.execCommand(cmds);

                stdOut = new StreamGobbler(session.getStdout());
                outRes = processStream(stdOut, charset);

                stdErr = new StreamGobbler(session.getStderr());
                outErr = processStream(stdErr, charset);

                session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS, TIME_OUT);

                logger.info("outRes=" + outRes);
                logger.info("outErr=" + outErr);

                exitStatus = session.getExitStatus();
                session.close();
            } else {
                throw new BaseExServiceException(BaseExServiceException.SERVICE_EXCEPTION,"登录远程机器失败" + hostname);
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            IOUtils.closeQuietly(stdOut);
            IOUtils.closeQuietly(stdErr);
        }
        return exitStatus;
    }

    public int exec(String cmds) throws Exception {
        InputStream stdOut = null;
        InputStream stdErr = null;
        String outRes = "";
        String outErr = "";
        int exitStatus = -1;
        try {
            if (conn != null) {
                Session session = conn.openSession();
                // 建立虚拟终端
                session.requestPTY("bash");
                // 打开一个Shell
                session.startShell();
                stdOut = new StreamGobbler(session.getStdout());
                stdErr = new StreamGobbler(session.getStderr());
                // 准备输入命令
                PrintWriter out = new PrintWriter(session.getStdin());
                // 输入待执行命令
                out.println(cmds);
                // 退出
                //out.println("exit");
                // 关闭输入流
                out.close();
                // 等待，除非1.连接关闭；2.输出数据传送完毕；3.进程状态为退出；4.超时
                session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS , TIME_OUT);

                logger.info("**********output from stdout**********");
                outRes = processStream(stdOut, charset);
                logger.info(outRes);
                logger.info("**********output from stdout**********");

                logger.info("**********output from stderr**********");
                outErr = processStream(stdErr, charset);
                logger.info(outErr);
                logger.info("**********output from stderr**********");

                //Show exit status, if available (otherwise "null")
                exitStatus = session.getExitStatus();
                session.close();
            } else {
                throw new BaseExServiceException(BaseExServiceException.SERVICE_EXCEPTION,"登录远程机器失败" + hostname);
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            IOUtils.closeQuietly(stdOut);
            IOUtils.closeQuietly(stdErr);
        }
        return exitStatus;
    }

    @Deprecated
    private String processStream1(InputStream in, String charset) throws IOException {
        byte[] buf = new byte[1024];
        StringBuilder sb = new StringBuilder();
        while (in.read(buf) != -1) {
            sb.append(new String(buf, charset));
        }
        return sb.toString();
    }

    private String processStream(InputStream in, String charset) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, charset));
        StringBuffer sb = new StringBuffer();
        if (in.available() != 0) {
            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                sb.append(line).append(System.getProperty("line.separator"));//换行，等同于\n
            }
        }
        return sb.toString();
    }

    /**
     * 本地文件上传至指定目录
     * @param localFile
     * @param remoteTargetDirectory
     * @throws Exception
     */
    public void transferFile(String localFile, String remoteTargetDirectory) throws Exception {
        File file = new File(localFile);
        if (file.isDirectory()) {
            throw new BaseExServiceException(BaseExServiceException.SERVICE_EXCEPTION,localFile + " is not a file");
        }

        try {
            Session session = conn.openSession();
            session.execCommand("mkdir -p " + remoteTargetDirectory);

            SCPClient sCPClient = conn.createSCPClient();
            sCPClient.put(localFile, remoteTargetDirectory, "0600");

            logger.info("{}上传成功",localFile);
            session.close();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void transferDirectory(String localDirectory, String remoteTargetDirectory) throws Exception {
        File dir = new File(localDirectory);
        if (!dir.isDirectory()) {
            throw new BaseExServiceException(BaseExServiceException.SERVICE_EXCEPTION,localDirectory + " is not directory");
        }

        try {
            String[] files = dir.list();
            Session session = conn.openSession();
            for (String file : files) {
                if (file.startsWith(".")) {
                    continue;
                }
                String fullName = localDirectory + "/" + file;
                if (new File(fullName).isDirectory()) {
                    String rdir = remoteTargetDirectory + "/" + file;
                    session.execCommand("mkdir -p " + remoteTargetDirectory);
                    transferDirectory(fullName, rdir);
                    logger.info("{}上传成功",fullName);
                } else {
                    transferFile(fullName, remoteTargetDirectory);
                }
            }
            session.close();
        }finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

}