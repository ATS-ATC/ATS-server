package com.alucn.casemanager.server.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

public class FileUtil {
	/**
	 * FTP create folder
	 * @param ftp
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static boolean createFolder2Ftp(FTPClient ftp,String filePath) throws Exception{
			return ftp.makeDirectory(filePath);
	}
	
	/**
	 * upload file to FTP
	 * @param ftp
	 * @param fileName
	 * @param fileWholePath
	 * @param uploadPath
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public static boolean upload2Ftp(FTPClient ftp,String fileName,String fileWholePath,String uploadFile2TargetPath) throws Exception{
		FileInputStream fis = null;
		boolean uploadSucc = false;
		try {
			File file = new File(fileWholePath);
			fis = new FileInputStream(file);
			if(ftp.changeWorkingDirectory(uploadFile2TargetPath)){
				ftp.setBufferSize(2048);
				ftp.setControlEncoding("GBK");
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
				uploadSucc = ftp.storeFile(fileName, fis);
			}else{
				throw new Exception("[FTP upload file to folder exception]");
			}
		} catch (FileNotFoundException e) {
			throw new Exception("[FTP local folder not exist]",e);
		} catch (IOException e) {
			throw new Exception("[FTP upload exception]",e);
		}
		return uploadSucc;
	}
	/**
	 * login FTP
	 * @param ftp
	 * @param ip
	 * @param user
	 * @param pwd
	 * @return
	 * @throws SysException
	 */
	public static boolean loginFtp(FTPClient ftp,String ip,String user,String pwd) throws Exception{
		try {
			ftp.setConnectTimeout(10000);
			ftp.connect(ip);
			boolean loginSucc = ftp.login(user, pwd);
			if(!loginSucc || !FTPReply.isPositiveCompletion(ftp.getReplyCode())){
				closeFtp(ftp);
				throw new Exception(ip+"[FTP login exception 锛歖"+ user + "," + pwd);
			}
			return true;
		} catch (SocketException e) {
			throw new Exception(ip+"[FTP login exception锛歖",e);
		} catch (IOException e) {
			throw new Exception(ip+"[FTP login exception锛歖",e);
		}
	}
	
	/**
	 * delete file
	 * @param ftp
	 * @param fpath
	 * @return
	 * @throws SysException
	 */
	public static boolean deleteFtpFile(FTPClient ftp,String fpath) throws Exception{
		try {
			return ftp.deleteFile(fpath);
		} catch (IOException e) {
			throw new Exception("[FTP file delete exception]",e);
		}
	} 
	/**
	 * close ftp conn
	 * @param ftp
	 * @throws SysException
	 */
	public static void closeFtp(FTPClient ftp) throws Exception{
		if(null != ftp){
			if(ftp.isConnected()){
				try {
					ftp.disconnect();
				} catch (IOException e) {
					throw new Exception("[FTP close exception]",e);
				}
			}
			ftp = null;
		}
	}
	
	
	
	 /**
	  * 2017年6月7日 下午2:14:31
	  * @author haiqiw
	  * FileUtil.java
	  * @return void
	  * @param ip
	  * @param user
	  * @param port
	  * @param privateKey
	  * @param passphrase
	  * @param sPath
	  * @param dPath
	  */
    public static void createSession2(String ip, String user, int port,String privateKey, 
    		String passphrase, String sPath, String dPath) {
        Session session = null;
        JSch jsch = new JSch();
        try {
            if (privateKey != null && !"".equals(privateKey)) {
                if (passphrase != null && "".equals(passphrase)) {
                    jsch.addIdentity(privateKey, passphrase);
                } else {
                    jsch.addIdentity(privateKey);
                }
            }
            if (port <= 0) {
                session = jsch.getSession(user, ip);
            } else {
                session = jsch.getSession(user, ip, port);
            }
            if (session == null) {
                throw new Exception("session is null");
            }
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(300000);
            upLoadFile(session, sPath, dPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 2017年6月7日 下午2:34:49
     * @author haiqiw
     * FileUtil.java
     * @return void
     * @param ip
     * @param user
     * @param psw
     * @param port
     * @param sPath
     * @param dPath
     */
    public static Session createSession(String ip, String user, String psw, int port)  throws Exception{
        Session session = null;
        JSch jsch = new JSch();
        if (port <= 0) {
            session = jsch.getSession(user, ip);
        } else {
            session = jsch.getSession(user, ip, port);
        }
        if (session == null) {
            throw new Exception("session is null");
        }
        session.setPassword(psw);
        session.setConfig("StrictHostKeyChecking", "no");//(ask | yes | no)
        session.connect(300000);
        return session;
    }
	/**
	 * 2017年6月7日 下午2:36:15
	 * @author haiqiw
	 * FileUtil.java
	 * @return void
	 * @param session
	 * @param sPath
	 * @param dPath
	 */
    public static void upLoadFile(Session session, String sPath, String dPath) throws Exception{
        Channel channel = null;
        try {
            channel = (Channel) session.openChannel("sftp");
            channel.connect(10000000);
            ChannelSftp sftp = (ChannelSftp) channel;
            try {
                sftp.cd(dPath);
            } catch (SftpException e) {
                sftp.mkdir(dPath);
                sftp.cd(dPath);
            }
            File file = new File(sPath);
            copyFile(sftp, file, sftp.pwd());
        } finally {
            session.disconnect();
            channel.disconnect();
        }
    }
    /**
     * 2017年6月7日 下午2:44:31
     * @author haiqiw
     * FileUtil.java
     * @return void
     * @param sftp
     * @param file
     * @param pwd
     */
    public static void copyFile(ChannelSftp sftp, File file, String pwd) throws Exception{

        if (file.isDirectory()) {
            File[] list = file.listFiles();
            String fileName = file.getName();
            sftp.cd(pwd);
            sftp.mkdir(fileName);
            pwd = pwd + "/" + file.getName();
            sftp.cd(file.getName());
            for (int i = 0; i < list.length; i++) {
                copyFile(sftp, list[i], pwd);
            }
        } else {
            sftp.cd(pwd);
            InputStream instream = null;
            OutputStream outstream = null;
            try {
                outstream = sftp.put(file.getName());
                instream = new FileInputStream(file);
                byte b[] = new byte[1024];
                int n;
                while ((n = instream.read(b)) != -1) {
                    outstream.write(b, 0, n);
                }
            } finally {
                outstream.flush();
                outstream.close();
                instream.close();
            }
        }
    }

    /**
     * 2017年6月7日 下午3:21:16
     * @author haiqiw
     * FileUtil.java
     * @return String[]
     * @param dstIp
     * @param dstport
     * @param localIp
     * @param localPort
     * @param timeOut
     * @param userName
     * @param password
     * @param cmds
     * @return
     * @throws Exception
     */
    public static String[] execShellCmdBySSH(String dstIp, int dstport, String userName, String password, String... cmds) throws Exception {
    	Session session = null;
    	Channel channel = null;
    	InputStream is = null;
    	OutputStream os = null;
      try {
        session = createSession(dstIp, userName, password, dstport);
        channel = session.openChannel("shell");
        channel.connect();
        is = channel.getInputStream();
        os = channel.getOutputStream();
        String[] result = new String[cmds.length];
        for (int i = 0; i < cmds.length; i++) {
          result[i] = sendCommand(is, os, cmds[i]);
        }
        return result;
      } catch (JSchException e) {
        if (e.getMessage().contains("Auth fail")) {
          throw new Exception("Auth error");
        } else {
          throw new Exception("Connect error");
        }
      } catch (Exception e) {
        throw e;
      } finally {
    	  is.close();
    	  os.close();
    	  channel.disconnect();
    	  session.disconnect();
      }
    }
    
    /**
     * 2017年6月7日 下午3:29:08
     * @author haiqiw
     * FileUtil.java
     * @return String
     * @param is
     * @param os
     * @param cmd
     * @return
     * @throws IOException
     */
    private static String sendCommand(InputStream is, OutputStream os,
        String cmd) throws IOException {
    	os.write(cmd.getBytes());
    	os.flush();
    	StringBuffer sb = new StringBuffer();
    	int beat = 0;
    	while (true) {
    		if (beat > 3) {
    			break;
    		}
    		if (is.available() > 0) {
    			byte[] b = new byte[is.available()];
    			is.read(b);
    			sb.append(new String(b));
    			beat = 0;
    		} else {
    			if (sb.length() > 0) {
    				beat++;
    			}
    			try {
    				Thread.sleep(sb.toString().trim().length() == 0 ? 1000: 300);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    	}
      return sb.toString();
    }
    
    
    
	public static void main(String[] args) throws Exception {
//		Session session = createSession("135.242.106.201", "root", "r00t", 22);
//		upLoadFile(session, "D:/test/test.txt", "/test/");
		 String[] cmds = new String[] {"sh /whq/test.sh"};
		 String[] result = execShellCmdBySSH("135.242.106.201", 22, "root", "r00t", cmds);
		 for(String str : result){
			 System.out.println(str);
		 }
	}
}