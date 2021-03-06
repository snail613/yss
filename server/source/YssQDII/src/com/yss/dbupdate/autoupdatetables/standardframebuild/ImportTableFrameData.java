package com.yss.dbupdate.autoupdatetables.standardframebuild;

import java.io.*;
import java.security.*;
import java.sql.*;
import java.util.*;
import java.util.zip.*;

import org.dom4j.*;
import org.dom4j.io.*;
import com.yss.dbupdate.autoupdatetables.sqlstringbuild.*;
import com.yss.dsub.*;
import com.yss.util.*;

class ImportTableFrameData
    extends BaseBean {
    ImportTableFrameData() {
    }

//   public static void main(String[] arges){
//      ImportTableFrameData fd = new ImportTableFrameData();
//      try{
//         fd.importFrameDataToFrameTables("1.0.1.0000");
//      }
//      catch(Exception e){
//
//      }
//   }

    /**
     * 导入数据的入口：分两步完成，先创建 TB_FUN_AllTableName、TB_Fun_Columns、TB_FUN_ConsCols、TB_Fun_CONSTRAINTS 四张表，
     * 然后将更新文件中的数据，倒入表中
     * @param sVerNum：当前更新版本号
     * @throws YssException
     */
    public void importData(String sVerNum) throws YssException {
        //创建表
        createFrameTables();
        //导入数据
        importFrameDataToFrameTables(sVerNum);
    }

    /**
     * Create 标准表结构表
     * @throws YssException
     */
    private void createFrameTables() throws YssException {
        ArrayList alSqlStr = new ArrayList();
        SqlStringBuilder sqlBild = null;
        try {
            //获取 SQL 语句，逐个执行
            sqlBild = new SqlStringBuilder(dbl.dbType);
            sqlBild.setYssPub(pub);
            alSqlStr.addAll(sqlBild.getDropMyFrameDataTableStr());
            alSqlStr.addAll(sqlBild.getCreateMyFrameDataTableStr());

            for (int i = 0; i < alSqlStr.size(); i++) {
                dbl.executeSql( (String) alSqlStr.get(i));
            }
        } catch (Exception e) {
            throw new YssException("创建自定义数据字典表出错！\r\n", e);
        }
    }

    /**
     * 解析和验证更新文件内容
     * @param updateFile FileInputStream：储存有更新文件的 Stream
     * @param sVerNum String：当前更新版本号
     * @return String：将更新文件中的 XML 内容返回
     * @throws YssException
     */
    private String parseAndCheckUpdateFile(FileInputStream updateFile, String sVerNum) throws YssException {
        BufferedReader reader = null;
        String sSHA1 = "";
        String sFileVer = "";
        String sXmlContent = "";
        MessageDigest md = null;
        String[] oldSha1CodeStr = null;
        byte[] oldSha1Code = new byte[20];
        byte[] newSha1Code = null;
        String sLine = "";
        try {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(updateFile), "utf-8"));
            sSHA1 = reader.readLine();
            sFileVer = reader.readLine();

            while ( (sLine = reader.readLine()) != null) {
                sXmlContent += sLine;
            }

            if (sSHA1 == null || sSHA1.trim().length() == 0 ||
                sFileVer == null || sFileVer.trim().length() == 0 ||
                sXmlContent == null || sXmlContent.trim().length() == 0) {
                throw new YssException("更新文件已被损坏无法进行表结构更新！");
            }

            if (!sFileVer.equalsIgnoreCase(sVerNum)) {
                throw new YssException("文件名与版本号不统一，无法进行表结构更新！");
            }

            oldSha1CodeStr = sSHA1.split(" ");
            for (int i = 0; i < oldSha1Code.length; i++) {
                oldSha1Code[i] = Byte.parseByte(oldSha1CodeStr[i]);
            }
            md = MessageDigest.getInstance("sha1");
            newSha1Code = md.digest(sXmlContent.getBytes("utf-8"));
            if (!md.isEqual(oldSha1Code, newSha1Code)) {
                throw new YssException("更新文件已被修改无法进行表结构更新！");
            }

        } catch (Exception e) {
            throw new YssException("读取和验证更新文件出错！\n", e);
        }
        return sXmlContent;
    }

    /**
     * 将标准表结构数据导入表中
     * @param sVerNum：当前更新版本号
     * @throws YssException
     */
    private void importFrameDataToFrameTables(String sVerNum) throws YssException {
        SAXReader reader = null;
        Document doc = null;
        String sPath = "";
        String fileContent = "";
        FileInputStream fis = null;
        String tpath = "";
        try {
        	//BUG NO.894  在新建组合群时，登录创建表结构时报错（WebSphere6.1）  modify by jiangshichao 2011.01.18 
        	//WebSphere容器用Thread.currentThread().getContextClassLoader().getResources("/")获取当前WEB应用程序的物理根路径有问题
        	if(Thread.currentThread().getContextClassLoader().getClass().toString().indexOf("ibm")>0){
        		
        		Enumeration e = Thread.currentThread().getContextClassLoader().getResources("/");
        		while(e.hasMoreElements()){
        			tpath = e.nextElement().toString();
        			if(tpath.length()>0&&tpath.indexOf("WEB-INF")>0){
        				tpath = tpath.split("file:")[1];
        				break;
        			}
        		}
        	}else{
        		tpath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();//modify by jiangshichao  获取绝对路径路径 2010.04.13
        	}
        	//--- BUG NO.894  在新建组合群时，登录创建表结构时报错（WebSphere6.1）  modify by jiangshichao 2011.01.18 END ---------------------------
            //fanghaoln 20091221 MS00852 QDV4南方2009年12月09日01_B 
            if(tpath.indexOf(":")>0){//当在windows环境下对路径的处理
            	sPath = tpath.substring(1, tpath.length() - 16) + "dbset/" + sVerNum.replaceAll("[.]", "_") + ".dbupd";
            }else{//当在linux环境下对路径的处理
            sPath = tpath.substring(0, tpath.length() - 16) + "dbset/" + sVerNum.replaceAll("[.]", "_") + ".dbupd";
            }
            //---------------------------------------end MS00852-----------------------------------------------------
//         sPath = "E:/测试用代码/DomoTestAboutAdjest/server/source/YssQDII/dbset/1_0_1_0000.dbupd";
            //将文件读入流
            sPath = java.net.URLDecoder.decode(sPath, "utf-8");
            fis = new FileInputStream(sPath);
            //文件内容读取和验证
            fileContent = parseAndCheckUpdateFile(fis, sVerNum);

            reader = new SAXReader();
            doc = reader.read(new ByteArrayInputStream(fileContent.getBytes("utf-8")));

            importAllTableName(doc);
            importColumns(doc);
            importConstraints(doc);
            importConsCols(doc);
        } catch (Exception e) {
            throw new YssException("将标准表结构数据导入自定义表结构数据字典表出错！\r\n", e);
        }
    }

    /**
     * 将数据导入表名表
     * @param doc Document
     * @throws YssException
     */
    private void importAllTableName(Document doc) throws YssException {
        String strSql = "";
        List list = null;
        Iterator iter = null;
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "INSERT INTO TB_FUN_AllTableName VALUES(?,?)";
            pst = conn.prepareStatement(strSql);
            list = doc.selectNodes("/ysstech/USER_ALL_TABLES/ROWS");
            iter = list.iterator();
            while (iter.hasNext()) {
                Element tableEle = (Element) iter.next();
                pst.setString(1, pub.yssGetTableName(tableEle.attribute(0).getValue()));
                pst.setString(2, "");
                pst.addBatch();
            }
            pst.executeBatch();
        } catch (Exception e) {
            throw new YssException("将数据导入 TB_FUN_AllTableName 表出错！\r\n", e);
        } finally {
            dbl.closeStatementFinal(pst);
        }
    }

    /**
     * 导入表列表数据
     * @param doc Document
     * @throws YssException
     */
    private void importColumns(Document doc) throws YssException {
        String strSql = "";
        List list = null;
        Iterator iter = null;
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        int i = 0;
        try {
            strSql = "INSERT INTO TB_Fun_Columns VALUES(?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);
            list = doc.selectNodes("/ysstech/USER_TAB_COLUMNS/ROWS");
            iter = list.iterator();
            while (iter.hasNext()) {
                Element tableEle = (Element) iter.next();
                //列数据，Oracle 和 DB2 有很大不同，所以要判断，分开处理
                if (dbl.dbType == YssCons.DB_ORA) {
                    pst.setString(1,
                                  pub.yssGetTableName(tableEle.attribute(0).getValue()));
                    pst.setString(2, tableEle.attribute(1).getValue());
                    pst.setInt(3, Integer.parseInt(tableEle.attribute(2).getValue()));
                    pst.setString(4, tableEle.attribute(3).getValue());
                    pst.setString(5, tableEle.attribute(4).getValue());
                    pst.setString(6, tableEle.attribute(5).getValue());
                    pst.setString(7, tableEle.attribute(6).getValue());
                    pst.setString(8, tableEle.attribute(7).getValue());
                    pst.setString(9, tableEle.attribute(8).getValue());
                    pst.setString(10, tableEle.attribute(9).getValue());
                    pst.addBatch();
                } else {
                    String s1 = tableEle.attribute(0).getValue();
                    String s2 = tableEle.attribute(1).getValue();
                    String s3 = tableEle.attribute(2).getValue();
                    String s4 = tableEle.attribute(3).getValue();
                    String s5 = tableEle.attribute(4).getValue();
                    String s6 = tableEle.attribute(5).getValue();
                    String s7 = tableEle.attribute(6).getValue();
                    String s8 = tableEle.attribute(7).getValue();
                    String s9 = tableEle.attribute(8).getValue().trim();
                    String s10 = tableEle.attribute(9).getValue().trim();
                    //文件中的表名是没有组合群编号的，先获取一下
                    pst.setString(1, pub.yssGetTableName(s1));
                    pst.setString(2, s2);
                    pst.setDouble(3, Double.parseDouble(s3));
                    //把Varchar2 和 Char 都换成 Varchar
                    if (s4.equalsIgnoreCase("VARCHAR2") ||
                        s4.equalsIgnoreCase("CHAR")) {
                        pst.setString(4, "VARCHAR");
                        pst.setDouble(5, Double.parseDouble(s5));
                    }
                    //Oracle 保存用户密码用的 RAW 类型 DB2 用的 CHARACTER
                    else if (s4.equalsIgnoreCase("RAW")) {
                        pst.setString(4, "CHARACTER");
                        pst.setDouble(5, Double.parseDouble(s5));
                    } else if (s4.equalsIgnoreCase("NUMBER")) {
                        //如果类型为 NUMBER 但是长度为为 null，说明是 INTEGER 类型
                        if (s5.trim().length() == 0) {
                            pst.setString(4, "INTEGER");
                            pst.setDouble(5, 4);
                        } else {
                            //NUMBER 型换成 DECIMAL
                            pst.setString(4, "DECIMAL");
                            pst.setDouble(5, Double.parseDouble(s5));
                        }
                    } else {
                        pst.setString(4, s4);
                        pst.setDouble(5, Double.parseDouble(s5));
                    }
                    //如果要插入 null 值，就需要处理一下
                    if (s6.trim().length() == 0) {
                        pst.setNull(6, java.sql.Types.DECIMAL);
                    } else {
                        //DB2 的 DESCIMAL 类型的长度不能大于 30
                        if (Double.parseDouble(s6) > 30) {
                            s6 = "30";
                        }
                        pst.setDouble(6, Double.parseDouble(s6));
                    }
                    if (s7.trim().length() == 0) {
                        pst.setNull(7, java.sql.Types.DECIMAL);
                    } else {
                        pst.setDouble(7, Double.parseDouble(s7));
                    }

                    pst.setString(8, s8);
                    pst.setString(9, s9.trim());
                    pst.setString(10, s10.trim());
                    //没查出为什么，使用 addBatch 就会报 java.unit.lang 错误，换成 executeUpdate 就没事了。
                    //用 executeUpdate 速度慢了不少
                    //不再浪费时间去查了，很郁闷！！！
                    //pst.addBatch();
                    pst.executeUpdate();
                }
            }
            pst.executeBatch();
        } catch (Exception e) {
            throw new YssException("将数据导入 TB_Fun_Columns 表出错！\r\n", e);
        } finally {
            dbl.closeStatementFinal(pst);
        }
    }

    /**
     * 导入约束数据，现在只导入主键约束
     * @param doc Document
     * @throws YssException
     */
    private void importConstraints(Document doc) throws YssException {
        String strSql = "";
        List list = null;
        Iterator iter = null;
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "INSERT INTO TB_Fun_CONSTRAINTS VALUES(?,?,?)";
            pst = conn.prepareStatement(strSql);
            list = doc.selectNodes("/ysstech/USER_CONSTRAINTS/ROWS");
            iter = list.iterator();
            while (iter.hasNext()) {
                Element tableEle = (Element) iter.next();
                //约束名称要处理一下
                //直接传入主键名，不使用表名进行转换 蒋锦 2009-07-07
                pst.setString(1, getPKName(tableEle.attribute(0).getValue()));
                pst.setString(2, pub.yssGetTableName(tableEle.attribute(1).getValue()));
                pst.setString(3, tableEle.attribute(2).getValue());
                pst.addBatch();
            }
            pst.executeBatch();
        } catch (Exception e) {
            throw new YssException("将数据导入 TB_Fun_CONSTRAINTS 表出错！\r\n", e);
        } finally {
            dbl.closeStatementFinal(pst);
        }
    }

    /**
     * 导入约束列
     * @param doc Document
     * @throws YssException
     */
    private void importConsCols(Document doc) throws YssException {
        String strSql = "";
        List list = null;
        Iterator iter = null;
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "INSERT INTO TB_FUN_ConsCols VALUES(?,?,?,?)";
            pst = conn.prepareStatement(strSql);
            list = doc.selectNodes("/ysstech/USER_CONS_COLUMNS/ROWS");
            iter = list.iterator();
            while (iter.hasNext()) {
                Element tableEle = (Element) iter.next();
                //直接传入主键名，不使用表名进行转换 蒋锦 2009-07-07
                pst.setString(1, getPKName(tableEle.attribute(0).getValue()));
                pst.setString(2, pub.yssGetTableName(tableEle.attribute(1).getValue()));
                pst.setString(3, tableEle.attribute(2).getValue());
                pst.setInt(4, Integer.parseInt(tableEle.attribute(3).getValue()));
                pst.addBatch();
            }
            pst.executeBatch();
        } catch (Exception e) {
            throw new YssException("将数据导入 TB_FUN_ConsCols 表出错！\r\n", e);
        } finally {
            dbl.closeStatementFinal(pst);
        }
    }

    /**
     * 给生成主键名称，因为表结构数据是从 Oracle 中导出的，所以主键名称是不一样的，另外防止 Oracle 中主键名称大于30个字符
     * @param name String：表名
     * @return String
     * @throws YssException
     */
    private String getPKName(String name) throws YssException {
        //如果主键名不以 PK 开头，直接返回
        if (name.startsWith("PK_")) {
            //截取主键名中属于表名的部分，获取组合群前缀
            name = pub.yssGetTableName(name.substring(3));
            if (dbl.dbType == YssCons.DB_DB2) {
                if (name.length() > 14) {
                    name = "PK_" + name.substring(0, 7) +
                        name.substring(name.length() - 7);
                } else {
                    name = "PK_" + name;
                }
            } else {
                name = "PK_" + name;
                if (name.length() > 30) {
                    name = name.substring(0, 30);
                }
            }
        }
        return name;
    }

}
