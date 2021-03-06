package com.yss.webServices.AccountClinkage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import net.sf.jftp.config.Settings;
import net.sf.jftp.net.BasicConnection;
import net.sf.jftp.net.ConnectionHandler;
import net.sf.jftp.net.ConnectionListener;
import net.sf.jftp.net.FtpConnection;
import net.sf.jftp.net.SftpConnection;

import com.sshtools.j2ssh.configuration.SshConnectionProperties;

/**
 * add by huangqirong 2013-06-03 story #3871
 * Jftp工具包
 * 主要支持ftp、sftp文件传输
 * 用于将交易数据文件从文件服务器传输到应用服务器
 * @author fly
 *
 */

public class FtpTool implements ConnectionListener {

    private boolean isThere = false;

    private ConnectionHandler handler = new ConnectionHandler();

    private HashMap connectPool = new HashMap();
    
    private Settings setting = new Settings();
    
    private BasicConnection conn =null;
    private Properties pro=null;
    private static final String DOWNLOADTYPE_FTP ="ftp";
    
    private static final String DOWNLOADTYPE_SFTP ="sftp";
    
    private Properties  parsePropertie(){
    	// 生成properties对象   
    	Properties p = new Properties(); 
    	InputStream in = null;
        // 生成输入流   
    	//InputStream ins=FtpTool.class.getResourceAsStream(File.separator + "acdatapath.properties");  
    	File file = new File(File.separator + "acdatapath.properties");
    	try {
			if(file.exists()){
				in = new BufferedInputStream(new FileInputStream(file));
				p.load(in);
			}
		} catch (IOException e) {
			System.out.println("读取.properties文件出错：" + e.getMessage());
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return p;
    }
    
    public FtpTool(){
    	pro = this.parsePropertie();
    	String ftpType = pro.getProperty("ftpType");
    	String ftpAddr = pro.getProperty("ftpAddr");
    	int ftpPort = Integer.parseInt(pro.getProperty("ftpPort"));
    	String ftpUser = pro.getProperty("ftpUser");
    	String ftpPassWd = pro.getProperty("ftpPassWd");
        conn = this.getConnection(ftpType, ftpAddr, ftpPort, ftpUser, ftpPassWd);
    }
    
    /**
     * FTP/SFTP服务器初始化连接
     * @param serverType 服务类型: ftp / sftp
     * @param host 服务器IP
     * @param port 端口号
     * @param user 用户名
     * @param passwd 密码
     * @return
     */
    public BasicConnection initConnection(String serverType, String host, int port, String user, String passwd) {
        setting.setProperty("jftp.disableLog", true);//设置Log功能关闭，关闭控制台msg
        if(serverType.equalsIgnoreCase(this.DOWNLOADTYPE_FTP)){//建立ftp链接
            FtpConnection con = new FtpConnection(host, port, "/");
            con.addConnectionListener(this);
            con.setConnectionHandler(handler);
            con.login(user, passwd);
            
            while (!isThere) {
                try {
                    Thread.sleep(10);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (isThere) {
                return con;
            } else {
                return null;
            }
        }else{//建立sftp连接
        	SftpConnection sftp_con = null;
            SshConnectionProperties pro = new SshConnectionProperties();
            pro.setHost(host);
            pro.setPort(port);
            sftp_con = new SftpConnection(pro);
            sftp_con.addConnectionListener(this);
            if(sftp_con.login(user, passwd)){
                return sftp_con;
            }else{
                return null;
            }
        }
    }
    
    /**
     * 获取ftp/sftp连接
     * @param serverType 
     * @param host
     * @param port
     * @param user
     * @param passwd
     * @return
     */
    public BasicConnection getConnection(String serverType, String host, int port, String user, String passwd){
        if (this.connectPool.isEmpty()) {
            BasicConnection tmp = this.initConnection(serverType, host, port, user, passwd);
                if(tmp == null || !tmp.isConnected()){
                    return null;
                }else{
                    this.connectPool.put(host, tmp);
                    return tmp;
                }
        } else {
            BasicConnection connection = (BasicConnection) this.connectPool.get(host);
            if (connection != null) {
                return connection;
            } else {
                BasicConnection tmp = this.initConnection(serverType, host, port, user, passwd);
                if(tmp == null || !tmp.isConnected()){
                    return null;
                }else{
                    return tmp;
                }
            }
        }
    }
    
    public boolean downLoad(String source, String file){
    	String ftpType = pro.getProperty("ftpType");
    	String ftpPath = pro.getProperty("ftpPath");
    	
    	if(this.DOWNLOADTYPE_FTP.equalsIgnoreCase(ftpType)){
    		return this.ftpDownload(ftpPath, source, file);
    	}else{
    		return this.sftpDownload(ftpPath, source, file);
    	}
    }
    
    public boolean upLoad(String ftpPath, String source ,String file){
    	String ftpType = pro.getProperty("ftpType");
    	//String ftpPath = pro.getProperty("ftpPath");
    	
    	if(this.DOWNLOADTYPE_FTP.equalsIgnoreCase(ftpType)){
    		return this.ftpUpload(ftpPath, file);
    	}else{
    		return this.sftpUpload(ftpPath, source, file);
    	}
    }
    
    /**
     * ftp 下载
     * @param dir 服务器文件目录
     * @param source 目标路径
     * @param file 下载的文件名 
     * @return
     */
    public boolean ftpDownload(String destination, String source, String file) {
        if(conn == null){
            return false;
        }else{
            conn.chdir(destination);
            conn.setLocalPath(source);
            conn.download(file);
            return true;
        }
    }
    
    /**
     * sftp 下载
     * @param destination 服务器路径
     * @param source 目标路径
     * @param file 文件名
     * @return
     */
    public boolean sftpDownload(String destination, String source, String file) {
        
        if(conn == null){
            return false;
        }else{

            conn.chdir(destination);
            conn.setLocalPath(source);
            conn.download(file);
            return true;
        }
    }
    
    /**
     * ftp 上传
     * @param dir 服务器文件目录
     * @param file 上传的文件名 
     * @return
     */
     public boolean ftpUpload(String dir, String file){
        if(conn == null){
            return false;
        }else{
            //make dirs
            String path = "";
            String[] paths = dir.split("/");
            for (int i = 0; i < paths.length; i++){
                path += "/" + paths[i];
                if (!conn.chdir(path)){
                	conn.mkdir(path);
                }
            }
            conn.chdir(dir);
            conn.upload(file);
            return true;
        }
    }

    /**
    * sftp 上传
    * @param dir 服务器文件目录
    * @param source 目标路径
    * @param file 上传的文件名 
    * @return
    */
     public boolean sftpUpload( String destination, String source, String file){
    	 if(conn == null){
    	     return false;
         }else{
        	    String path = "";
        	    String[] paths = destination.split("/");
        	    for (int i = 0; i < paths.length; i++) {
        	        path += "/" + paths[i];
        	        if (!conn.chdir(path)){
        	            conn.mkdir(path);
             }
         }
         conn.chdir(destination);
         conn.setLocalPath(source);
         conn.upload(file);
             return true;
         }
    }
  
    public void release() throws Exception {
        try {
            if(connectPool!=null && !connectPool.isEmpty()){
                Object[]obj = connectPool.values().toArray();
                for(int i=0;i<obj.length;i++){
                    BasicConnection connect = (BasicConnection)obj[i];                    
                    if(connect != null && connect.isConnected()){
                        connect.disconnect();
                    }
                }
                connectPool.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void updateRemoteDirectory(BasicConnection con) {
        System.out.println("new path is: " + con.getPWD());
    }

    public void connectionInitialized(BasicConnection con) {
        isThere = true;
    }

    
  

	public void actionFinished(BasicConnection arg0) {
		//arg0.disconnect();
		//arg0 = null;
	}

	public void connectionFailed(BasicConnection arg0, String arg1) {
		
		
	}

	public void updateProgress(String arg0, String arg1, long arg2) {
		
		
	}

	public static void main(String[] args) {

		FtpTool g = new FtpTool();
        BasicConnection conn =null;
        //conn = g.getConnection("sftp", "192.168.100.138", 22, "fly", "coding");
        //g.sftpDownload(conn, "/home", "F:/", "test.doc");
        conn = g.getConnection("ftp", "192.168.100.138", 21, "fly", "coding");
        g.ftpDownload("UserData","d:/", "test.doc");
        
        //g.ftpUpload(dir, file);
        try {
            g.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


