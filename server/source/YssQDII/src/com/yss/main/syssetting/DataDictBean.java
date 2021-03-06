package com.yss.main.syssetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;
import com.yss.vsub.*;

/**
 *
 * <p>Title: DataDictBean </p>
 * <p>Description: 数据字典 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DataDictBean
    extends BaseDataSettingBean {
    private String strTabName = ""; //系统表名
    private String strFieldName = ""; //系统字段名
    private String strTableDesc = ""; //系统表描述
    private String strTableType = "";
    private String strIsNull = "";
    private String strFieldDesc = ""; //系统字段描述
    private String strFFieldType = "";
    private String FDefaultValue = "";
    private String strKey = ""; //是否为主键
    private String desc = "";
    private String IfCreate = "";
    private String ExNum = "";
    private String status = ""; //是否记入系统信息状态  lzp 11.29 add
    private boolean statu = true; //判断记入系统表的状态是否加载过 lzp 11.29 add
    //  private String FFIELDPRE = "";

    private String strOldTabName = "";
    private String strOldFieldName = "";
    private boolean bDesc = true; //保存FDesc的值
    private boolean bTabType = true; //保存FTabType的值. add liyu
    private boolean bCreate = true; //保存ifCreate的值
    private DataDictBean filterType;
    private String sSubData = ""; // 保存字段列表记录

    public void setDesc(String desc) {
        this.desc = desc;
    }

//下面这些 set get 方法要在接口参数处理时用到 add liyu 1114
    public void setStrTableType(String strTableType) {
        this.strTableType = strTableType;
    }

    public void setStrTableDesc(String strTableDesc) {
        this.strTableDesc = strTableDesc;
    }

    public void setStrKey(String strKey) {
        this.strKey = strKey;
    }

    public void setStrIsNull(String strIsNull) {
        this.strIsNull = strIsNull;
    }

    public void setStrFieldName(String strFieldName) {
        this.strFieldName = strFieldName;
    }

    public void setStrFFieldType(String strFFieldType) {
        this.strFFieldType = strFFieldType;
    }

    public void setBTabType(boolean bTabType) {
        this.bTabType = bTabType;
    }

    //sj modify 20081124 bug MS00037
    public void setSsubData(String SSubData) {
        this.sSubData = SSubData;
    }

    public void setFDefaultValue(String FDefaultValue) {
        this.FDefaultValue = FDefaultValue;
    }

    public void setExNum(String ExNum) {
        this.ExNum = ExNum;
    }

    public String getDesc() {
        return desc;
    }

    public String getStrTableType() {
        return strTableType;
    }

    public String getStrTableDesc() {
        return strTableDesc;
    }

    public String getStrKey() {
        return strKey;
    }

    public String getStrIsNull() {
        return strIsNull;
    }

    public String getStrFieldName() {
        return strFieldName;
    }

    public String getStrFFieldType() {
        return strFFieldType;
    }

    public boolean isBTabType() {
        return bTabType;
    }

    //sj modify 20081124 bug MS00037
    public String getSsubData() {
        return sSubData;
    }

    public String getFDefaultValue() {
        return FDefaultValue;
    }

    public String getExNum() {
        return ExNum;
    }

    public DataDictBean() {

    }

    public DataDictBean(YssPub pub) {
        setYssPub(pub);
    }

    public String getTabName() {
        return strTabName;
    }

    public void setTabName(String strTabName) {
        this.strTabName = strTabName;
    }

    public String getTableDesc() {
        return strTableDesc;
    }

    public void setTableDesc(String strTableDesc) {
        this.strTableDesc = strTableDesc;
    }

    public String getFieldName() {
        return strFieldName;
    }

    public void setFieldName(String strFieldName) {
        this.strFieldName = strFieldName;
    }

    public String getFieldDesc() {
        return strFieldDesc;
    }

    public void setFieldDesc(String strFieldDesc) {
        this.strFieldDesc = strFieldDesc;
    }

    public String getKey() {
        return strKey;
    }

    public void setKey(String strKey) {
        this.strKey = strKey;
    }

    public String getAllDataDict() throws YssException {
        String sql = "";
        try {
            sql = "select * from Tb_Fun_DataDict order by FTabName, FFieldName ";
            return buildSendStr(sql);
        } catch (Exception ex) {
            throw new YssException("获取所有数据字典信息出错", ex);
        }
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        String strSql = "", strTmp = "";
        if (btOper == YssCons.OP_ADD) {
            strSql =
                "select FFieldName from Tb_Fun_DataDict where FTabName = " +
                dbl.sqlString(this.strTabName.trim()) + " and FFieldName = " +
                dbl.sqlString(this.strFieldName.trim()) + "";
            strTmp = dbFun.GetValuebySql(strSql);
            if (strTmp.length() > 0) {
                throw new YssException("数据字典中表【" + this.strTabName.trim() +
                                       "】的字段【" + this.strFieldName.trim() +
                                       "】已经被占用，请重新输入");
            }
        } else if (btOper == YssCons.OP_EDIT) {
            if (!this.strTabName.trim().equalsIgnoreCase(this.strOldTabName)) { //彭鹏 2008.1.29 判断表是否以存在
                strSql =
                    "select FTabName from Tb_Fun_DataDict where FTabName = " +
                    dbl.sqlString(this.strTabName.trim());
                strTmp = dbFun.GetValuebySql(strSql);
                if (strTmp.length() > 0) {
                    throw new YssException("数据字典中表【" + this.strTabName.trim() +
                                           "】已经被占用，请重新输入");
                }
            } else if (!this.strTabName.trim().equalsIgnoreCase(this.strOldTabName) ||
                       !this.strFieldName.trim().equalsIgnoreCase(this.strOldFieldName)) {
                strSql =
                    "select FFieldName from Tb_Fun_DataDict where FTabName = " +
                    dbl.sqlString(this.strTabName.trim()) + " and FFieldName = " +
                    dbl.sqlString(this.strFieldName.trim()) + "";
                strTmp = dbFun.GetValuebySql(strSql);
                if (strTmp.length() > 0) {
                    throw new YssException("数据字典中表【" + this.strTabName.trim() +
                                           "】的字段【" + this.strFieldName.trim() +
                                           "】已经被占用，请重新输入");
                }
            }
        }
    }

    private String buildFilterSql() {
        String sResult = "";

        if (this.filterType != null) {
            sResult = " where 1=1 ";

            if (this.filterType.strTabName.trim().length() != 0) {
                sResult = sResult + " and a.ftabname like '" +
                    filterType.strTabName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strTableDesc.trim().length() != 0) {
                sResult = sResult + " and a.ftabledesc like '" +
                    filterType.strTableDesc.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.strFieldName.trim().length() != 0) {
                sResult = sResult + " and a.ffieldname like '" +
                    filterType.strFieldName.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.strFieldDesc.trim().length() != 0) {
                sResult = sResult + " and a.ffielddesc like '" +
                    filterType.strFieldDesc.replaceAll("'", "''") + "%'";
            }
            //alter by sunny 10.25 原因 当要配置接口的自定义关系的时候 会有此条件的限制
            //if (this.filterType.strKey.trim().length() != 0) {
            //   sResult = sResult + " and a.fkey like '" +
            //  filterType.strKey.replaceAll("'", "''") + "%'";
            //}
            if (this.filterType.strTableType.trim().length() != 0
                && !this.filterType.strTableType.equals("99")) {
                sResult = sResult + " and a.FTABleTYPE = " +
                    filterType.strTableType;
            }
        }
        return sResult;
    }

    public String addDataDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        //YssDbFun fun = new YssDbFun(pub);//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        ResultSet rs = null;
        String sqlStr = "";
        boolean showError = false;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //  PreparedStatement pst = conn.prepareStatement(
            sqlStr = "insert into Tb_Fun_DataDict" +
                "(FTabName,FFieldName,FTableDesc,FFieldDesc,FFieldType,FDefaultValue,FKey,FTABLETYPE ,FIsNull,FFIELDPRE,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" +
                dbl.sqlString(this.strTabName) + "," +
                dbl.sqlString(this.strFieldName) + "," +
                dbl.sqlString(this.strTableDesc) + "," +
                dbl.sqlString(this.strFieldDesc) + "," +
                dbl.sqlString(this.strFFieldType) + "," +
                dbl.sqlString(this.FDefaultValue) + "," +
                (this.strKey.length() > 0 ? (Integer.parseInt(this.strKey)) : 0) + "," + //彭鹏 2008.1.29 判断主键为空时设为默认0
                (strTableType.length() > 0 ? (YssFun.toInt(this.strTableType)) :
                 0) + "," + //liyu 修改 1009 若 strtabletype 为空,则为0
                (YssFun.toInt(this.strIsNull)) + "," +
                dbl.sqlString(this.ExNum) + "," +
                ( (pub.getSysCheckState() ? 0 : 1)) + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                dbl.sqlString( (pub.getSysCheckState() ? " " : this.creatorCode)) +
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("新增-数据字典设置");
                sysdata.setStrCode(this.strTabName);
                sysdata.setStrName(this.strFieldName);
                sysdata.setStrUpdateSql(sqlStr);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            if (showError) {
                throw new YssException("增加数据字典设置出错", e);
            } else {
                throw new YssException(e.getMessage());
            }
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 此类用于保存数据的添加与修改: 保存 1 主表.2 辅助表 liyu修改 0921
     * @throws YssException
     * @return String
     */

    public String editMultDataDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        // boolean bCreate=false;
        String[] sArr = this.sSubData.split("\f\f");
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (sArr.length > 0) {
                strSql = "delete from Tb_Fun_DataDict where FTabName = " +
                    dbl.sqlString(this.strOldTabName);
                dbl.executeSql(strSql);
                //---------lzp add 11.29
                if (this.status.equalsIgnoreCase("1")) {
                    com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                        funsetting.SysDataBean();
                    sysdata.setYssPub(pub);
                    sysdata.setStrAssetGroupCode("Common");
                    sysdata.setStrFunName("删除-数据字典设置");
                    sysdata.setStrCode(this.strOldTabName);
                    sysdata.setStrName(this.strFieldName);
                    sysdata.setStrUpdateSql(strSql);
                    sysdata.setStrCreator(pub.getUserName());
                    sysdata.addSetting();
                }
                //-----------------------
                for (int i = 0; i < sArr.length; i++) {
                    this.protocolParse(sArr[i]);
                    this.addDataDict();
                    //if(this.IfCreate.equalsIgnoreCase("true")) bCreate=true;
                }
            } else {
                this.addDataDict(); //保存只有表名与备注信息的表.
            }
            // dbl.executeSql(strSql);
            //------------update by guolongchao 20111216 STORY1903 QDV4赢时胜（上海）2011年11月18日01_A.xls  添加导入存储表类型------start
            if(IfCreate.equalsIgnoreCase("impStore")){//若是导入存储表类型，先将原来的临时表重命名           
            	if(dbl.yssTableExist(this.strOldTabName)&&!this.strOldTabName.equals(this.strTabName)){
            		strSql =" ALTER TABLE "+this.strOldTabName+" rename to "+this.strTabName;
               	    dbl.executeSql(strSql);
            	}            	
            	updateTableStructure(this.strTabName);//如果数据表类型为导入存储表，则根据最新数据字典设置更新表结构
            }                
            if (IfCreate.equalsIgnoreCase("true")) {//只有临时表需要删除旧表重新创建，其中IfCreate通过前台传入
                this.createTab(this.strTabName);
            }
            //------------update by guolongchao 20111216 STORY1903 QDV4赢时胜（上海）2011年11月18日01_A.xls  添加导入存储表类型------end
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {        
            throw new YssException("修改数据字典设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String editDataDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update Tb_Fun_DataDict set " +
                " FFieldName = " + dbl.sqlString(this.strFieldName) +
                ",FTableDesc = " + dbl.sqlString(this.strTableDesc) +
                ",FFieldDesc = " + dbl.sqlString(this.strFieldDesc) +
                ",FFieldType = " + dbl.sqlString(this.strFFieldType) +
                ",FDefaultValue = " + dbl.sqlString(this.FDefaultValue) +
                ",FKey = " + dbl.sqlString(this.strKey) +
                ",FTABLETYPE  = " +
                (strTableType.length() > 0 ? (YssFun.toInt(this.strTableType)) :
                 0) + // liyu 修改 1009
                ",FIsNull = " + this.strIsNull + "" +
                ",FDesc =" + dbl.sqlString(this.desc) +
                ",FFIELDPRE = " + dbl.sqlString(this.ExNum) +
                " where FTabName = " + dbl.sqlString(this.strOldTabName) +
                " and FFieldName = " + dbl.sqlString(this.strOldFieldName);

            System.out.println(strSql);
            dbl.executeSql(strSql);
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("修改-数据字典设置");
                sysdata.setStrCode(this.strOldTabName);
                sysdata.setStrName(this.strOldFieldName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改数据字典设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String delDataDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql =
                "delete from Tb_Fun_DataDict  where FTabName = " +
                dbl.sqlString(this.strTabName) + " and FFieldName = " +
                dbl.sqlString(this.strFieldName);
            dbl.executeSql(strSql);
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-数据字典设置");
                sysdata.setStrCode(this.strTabName);
                sysdata.setStrName(this.strFieldName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            conn.commit();
            bTrans = true;
            conn.setAutoCommit(true);
            return "true";
        } catch (Exception e) {
            throw new YssException("删除数据字典设置出错", e);
        }
    }

    public String delAllDataDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql =
                "delete from Tb_Fun_DataDict  where FTabName = " +
                dbl.sqlString(this.strTabName);
            // + " and FFieldName = " +
            // dbl.sqlString(this.strFieldName);
            dbl.executeSql(strSql);
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-数据字典设置");
                sysdata.setStrCode(this.strTabName);
                sysdata.setStrName(this.strFieldName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = true;
            conn.setAutoCommit(true);
            return "true";
        } catch (Exception e) {
            throw new YssException("删除数据字典设置出错", e);
        }
    }

    public void protocolParse(String sReq) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sReq.trim().length() == 0) {
                return;
            }
            if (sReq.indexOf("\r\t") >= 0) {
                sTmpStr = sReq.split("\r\t")[0];
                if (sReq.split("\r\t").length == 3) {
                    sSubData = sReq.split("\r\t")[2];
                }
            } else {
                sTmpStr = sReq;
            }
            reqAry = sTmpStr.split("\t");
            if (reqAry.length >= 7) {
                this.strTabName = reqAry[0];
                if (reqAry[0].length() == 0) {
                    this.strTabName = " ";
                }
                this.strFieldName = reqAry[1];
                if (reqAry[1].length() == 0) {
                    this.strFieldName = " ";
                }
                this.strTableDesc = reqAry[2];
                if (reqAry[2].length() == 0) {
                    this.strTableDesc = " ";
                }
                this.strFieldDesc = reqAry[3];
                if (reqAry[3].length() == 0) {
                    this.strFieldDesc = " ";
                }
                this.strFFieldType = reqAry[4];
                if (reqAry[4].length() == 0) {
                    this.strFFieldType = " ";
                }
                this.FDefaultValue = reqAry[5];
                if (reqAry[5].length() == 0) {
                    this.FDefaultValue = " ";
                }
                this.strKey = reqAry[6];
                //  if(reqAry[6].length()==0) this.strKey="0";
                if (bTabType) {
                    this.strTableType = reqAry[7];
                    //     if (reqAry[7].length() == 0)this.strTableType = "0";
                    bTabType = false;
                }
                this.strIsNull = reqAry[8];
                if (reqAry[8].length() == 0) {
                    this.strIsNull = "0";
                }
                this.strOldTabName = reqAry[9];
                if (reqAry[9].length() == 0) {
                    this.strOldTabName = "";
                }
                this.strOldFieldName = reqAry[10];
                if (reqAry[10].length() == 0) {
                    this.strOldFieldName = "";
                }
                if (bCreate) {
                    this.IfCreate = reqAry[11];
                    if (reqAry[11].length() == 0) {
                        this.IfCreate = "false";
                    }
                    bCreate = false;
                }
                this.ExNum = reqAry[12];
                if (reqAry[12].length() == 0) {
                    this.ExNum = " ";
                }
                if (bDesc) {
                    this.desc = reqAry[13];
                    bDesc = false; //只加载第一次
                }
                //lzp add 11.28
                if (statu) {
                    this.status = reqAry[15];
                    statu = false; //只加载第一次
                }

                super.parseRecLog();

                if (sReq.indexOf("\r\t") >= 0) {
                    if (this.filterType == null) {
                        this.filterType = new DataDictBean();
                        this.filterType.setYssPub(pub);
                    }
                    if (!sReq.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                        this.filterType.protocolParse(sReq.split("\r\t")[1]);
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("解析数据字典出错", e);
        }
    }

    public String getListViewData3() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        String strSql = "";
        ResultSet rs = null;
        try {
            //edited by zhouxiang MS01597 目标字段列表中备选项目和已选项目中2列放到字段描述后面 
        	sHeader = "系统表字段名称\t系统表字段描述\t系统表名称\t系统表描述";
            strSql = "select * from Tb_Fun_DataDict a" +
                " where a.FTabName=" + dbl.sqlString(this.strTabName) +
                " order by a.FTabName, a.FFieldName";
            System.out.println(strSql);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                
                bufShow.append(rs.getString("FFieldName")).append("\t");
                bufShow.append(rs.getString("FFieldDesc")).append("\t");
                bufShow.append(rs.getString("FTabName")).append("\t");
                bufShow.append(rs.getString("FTableDesc")).append(YssCons.
                    YSS_LINESPLITMARK);
            //end-- by zhouxiang MS01597 目标字段列表中备选项目和已选项目中2列放到字段描述后面    
                this.strTabName = rs.getString("FTabName") + "";
                this.strTableDesc = rs.getString("FTableDesc") + "";
                this.strFieldName = rs.getString("FFieldName") + "";
                this.strFieldDesc = rs.getString("FFieldDesc") + "";
                this.desc = rs.getString("FDesc");
                this.strKey = rs.getString("FKey") + "";
                this.checkStateId = 1; //审核状态
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            
            // rs.last();
            //System.out.println("最后的字段名："+ rs.getString("FFieldName"));

            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\fFTabName\tFTableDesc\tFFieldName\tFFieldDesc";
        } catch (Exception e) {
            throw new YssException("获取数据字典数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    //添加FTabType,为在第一次加载时出现 midify liyu 0924
    public String getListViewData() throws YssException {
        String sVocStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        String strSql = "";
        ResultSet rs = null;
        try {
            // lzp modify 20080103
            //sHeader = "系统表名称\t系统表描述\t字段名\t字段名描述";
            sHeader = "系统表名称\t系统表描述";
            strSql = "select distinct(a.FTabName),a.FTableDesc,a.FTableType from Tb_Fun_DataDict a" +
                buildFilterSql() +
                " order by FTabName";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FTabName")).append("\t");
                bufShow.append(rs.getString("FTableDesc")).append(YssCons.
                    YSS_LINESPLITMARK);

                this.strTabName = rs.getString("FTabName") + "";
                this.strTableDesc = rs.getString("FTableDesc") + "";
                //this.strFieldName = rs.getString("FFieldName") + "";
                //this.strFieldDesc = rs.getString("FFieldDesc") + "";
                this.strTableType = rs.getString("FTableType");
                this.checkStateId = 1; //审核状态
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            // rs.last();
            //System.out.println("最后的字段名："+ rs.getString("FFieldName"));

            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_FFieldType + "," +
                                        YssCons.YSS_FKey + "," +
                                        YssCons.YSS_DICT_TABTYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\fFTabName\tFTableDesc" + "\r\f" +
                "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取数据字典数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData2() throws YssException {
        String sHeader = "";
        String strSql = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "系统表名称\t系统表描述";
            strSql = "select DISTINCT FTabName,FTableDesc from Tb_Fun_DataDict group by FTabName,FTableDesc order by FTabName,FTableDesc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FTabName")).append("\t");
                bufShow.append(rs.getString("FTableDesc") +
                               "").append(YssCons.YSS_LINESPLITMARK);

                this.strTabName = rs.getString("FTabName") + "";
                this.strFieldName = "";
                this.strTableDesc = rs.getString("FTableDesc") + "";
                this.strFieldDesc = "";
                this.strKey = "";

                buf.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (buf.toString().length() > 2) {
                sAllDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取数据字典数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strTabName.trim()).append("\t");
        buf.append(this.strFieldName.trim()).append("\t");
        buf.append(this.strTableDesc.trim()).append("\t");
        buf.append(this.strFieldDesc.trim()).append("\t");
        buf.append(this.strFFieldType.trim()).append("\t");
        if (FDefaultValue != null) { //alter by sunny
            buf.append(this.FDefaultValue.trim()).append("\t");
        } else {
            buf.append("").append("\t");
        }
        buf.append(this.strKey.trim()).append("\t");
        buf.append(this.strTableType.trim()).append("\t");
        buf.append(this.strIsNull.trim()).append("\t");
        if (this.ExNum != null) {
            buf.append(this.ExNum.trim()).append("\t");
        } else {
            buf.append("").append("\t");
        }
        if (this.desc != null) {
            buf.append(this.desc).append("\t");
        } else {
            buf.append("").append("\t");
        }
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    private String buildSendStr(String strSql) throws YssException {
        //StringBuffer buf = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        ResultSet rs = null;
        String sResult = "";
        try {
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strTabName = rs.getString("FTabName") + "";
                this.strTableDesc = rs.getString("FTableDesc") + "";
                this.strFieldName = rs.getString("FFieldName") + "";
                this.strFieldDesc = rs.getString("FFieldDesc") + "";
                this.FDefaultValue = rs.getString("FDefaultValue") + "";
                this.strFFieldType = rs.getString("FFieldType") + "";
                this.strKey = rs.getString("FKey") + "";
                this.strTableType = rs.getInt("FTABLETYPE ") + "";
                this.strIsNull = rs.getInt("FIsNull") + "";
                this.desc = rs.getString("FDesc");
                sResult += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception ex) {
            throw new YssException("访问数据字典表出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData1() throws YssException {

        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer buf = new StringBuffer(); //用于显示的属性
        StringBuffer buf1 = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            // sHeader = this.getListView1Headers();
            sHeader = "字段名\t字段描述";
            String sql =
                "select * from " +
                "TB_FUN_DATADICT " +
                //" left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                // " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                " where FTabName = " +
                dbl.sqlString(this.filterType.getTabName()) +
                " and FFieldName<>' ' order by FTABNAME  desc";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {

                buf.append( (rs.getString("FFieldName") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FFieldDesc") + "").trim());
                // buf.append("\t");
                // buf.append( (rs.getString("FVocTypeCode") + "").trim());
                // buf.append("\t");
                // buf.append( (rs.getString("FDesc") + "").trim());
                buf.append(YssCons.YSS_LINESPLITMARK);

                this.strFieldName = rs.getString("FFIELDNAME") + "";
                this.strFieldDesc = rs.getString("FFieldDesc") + "";
                this.strTabName = rs.getString("FTABNAME") + "";
                this.strTableDesc = rs.getString("FTABLEDESC") + "";
                this.strFFieldType = rs.getString("FFieldType") + "";
                this.FDefaultValue = rs.getString("FDefaultValue") + "";
                this.strKey = rs.getInt("FKey") + "";
                this.strTableType = rs.getInt("FTABLETYPE") + "";
                this.strIsNull = rs.getInt("FIsNull") + "";
                this.ExNum = rs.getString("FFIELDPRE") + "";
                this.desc = rs.getString("FDesc");
                buf1.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0,
                    buf1.toString().length() - 2);
            }
            String temp = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            return temp;
        } catch (Exception e) {
            throw new YssException("获取数据字典信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getFieldType(String fieldtype) {
        String strFieldType = "";
        //--------2008.01.23 修改 蒋锦-----添加对不同数据库的判断----------------//
        if (fieldtype.equalsIgnoreCase("0")) {
            if (dbl.dbType == YssCons.DB_ORA) {
                strFieldType = "VARCHAR2";
            } else {
                strFieldType = "VARCHAR";
            }
        } else if (fieldtype.equalsIgnoreCase("1")) {
            if (dbl.dbType == YssCons.DB_ORA) {
                strFieldType = "NUMBER";
            } else {
                strFieldType = "DECIMAL";
            }
        } else if (fieldtype.equalsIgnoreCase("2")) {
            strFieldType = "DATE";
        } else if (fieldtype.equalsIgnoreCase("3")) {
            if (dbl.dbType == YssCons.DB_ORA) {
                strFieldType = "VARCHAR2";
            } else {
                strFieldType = "VARCHAR";
            }
        }
        //------------------------------------------------------------//
        /**shashijie 2011.03.29 STORY #814 招商证券每日需导出净值文件上传给中登,与我们系统导出的数字类型不一致*/
        else if (fieldtype.equalsIgnoreCase("4")) {
        	strFieldType = "NUMBER";//这里原先是Float，shashijie 2011.03.31 数据库里永远都是Number类型
        }
        else if (fieldtype.equalsIgnoreCase("5")) {//STORY #2236 panjunfang add 20120210
        	strFieldType = "Numeric";//增加Numeric 类型（导出DBF 2.x）
        }
        /**end*/
        return strFieldType;
    }

    /*
       public String getFields(String TabName) throws YssException {
          ResultSet rs = null;
          String strSql = "";
          StringBuffer buf = new StringBuffer();
          StringBuffer Cons = new StringBuffer();
          String fields = "";
          String constraint = "";
          String ReCons = "";
          try
          {
             constraint ="CONSTRAINT PK_" + this.strTabName + " PRIMARY KEY(";
             strSql = "select FFieldName,FFieldType,FFIELDPRE,FKey,FIsNull from TB_FUN_DATADICT where FTabName = " + dbl.sqlString(this.strTabName);
             rs = dbl.openResultSet(strSql);
             while(rs.next())
             {
                buf.append(rs.getString("FFieldName")).append("\t");
                buf.append(rs.getString("FFIELDTYPE")==null?" " :getFieldType(rs.getString("FFieldType"))).append("\t");
                buf.append(rs.getString("FFIELDPRE")==null?" ":("(" + rs.getString("FFIELDPRE") + ")")).append("\t");
     buf.append(rs.getInt("FIsNull")==0?"NULL":"NOT NULL").append("\r\n");
                if (rs.getInt("FKey") !=0)
                {
                   Cons.append(rs.getString("FFieldName") + ",");
                }

             }
             if (buf.length()>1)
             {
                fields = buf.toString();
             }

             if (Cons.length()>1)
             {
                 constraint  = constraint + Cons.toString().substring(0,Cons.length()-1)+ ") VALIDATE";
                 return fields + constraint;
             }
             else
             {
                   return fields ;
             }

          }
          catch(Exception e)
          {
              throw new YssException("创建临时表出错", e);
          }
       }*/
    /**
     * 本类用于编辑创建表的所有字段;修改 李钰 09-21
     * @param fields String 所有信息
     * @throws YssException
     * @return String 创建表的SQL语句,包括建表语句,主键等.
     */
    public String buildFields(String fields) throws YssException {
        String[] FieldsArr = null;
        String Field = ""; //编辑单个的字段;
        String sPK = ""; //primary key string
        String Fields = ""; //编辑所有的字段;
        FieldsArr = fields.split("\f\f");
        for (int i = 0; i < FieldsArr.length; i++) {
            this.protocolParse(FieldsArr[i]);
            if (this.strFieldName.trim().length() != 0) { //字段
                Field = this.strFieldName;
            }
            if (this.strFFieldType.trim().length() != 0) { //类型
                Field += " " + getFieldType(this.strFFieldType);
            }
            if (this.ExNum.trim().length() != 0 &&
                !this.ExNum.trim().equalsIgnoreCase("null") &&
                !getFieldType(this.strFFieldType).equalsIgnoreCase("DATE")) { //精度
                Field += "(" + this.ExNum + ")";
            }
            if (this.FDefaultValue.trim().length() != 0) { // default;
                Field +=
                    defaultStr(this.strFFieldType.trim(), this.FDefaultValue.trim());
            }
            Field += (this.strIsNull.trim().equals("0") ? " NULL" : " NOT NULL"); //Null or Not Null
            Fields += Field + ",";
            if (!this.strKey.trim().equals("0")) { //是否为主键
                sPK += this.strFieldName + ",";
            }
        }
        if (Fields.length() > 0) {
            Fields = Fields.substring(0, Fields.length() - 1);
        }
        if (sPK.length() > 0) {
            sPK = sPK.substring(0, sPK.length() - 1);
            sPK = " alter table " + strTabName + " add " + " constraint PK_" +
                strTabName +
                " primary key (" + sPK + ")";
        }
        if (Fields.trim().equalsIgnoreCase("NULL")) {
            return null;
        }
        return "create table " + strTabName + "(" + Fields + ")\t" + sPK;
    }

    public void createTab(String tabName) throws YssException {
        String strSql = "";
        String[] buildSQL = null;
        String getFields = "";
        try {
        	
            if (dbl.yssTableExist(tabName)) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table " + tabName));
                /**end*/
                //---------lzp add 2008-1-2
                /**shashijie ,2011-10-12 , STORY 1698*/
                String ss = dbl.doOperSqlDrop("drop table " + tabName);
                /**end*/
                if (this.status.equalsIgnoreCase("1")) {
                    com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                        funsetting.SysDataBean();
                    sysdata.setYssPub(pub);
                    sysdata.setStrAssetGroupCode("Common");
                    sysdata.setStrFunName("删表-数据字典");
                    sysdata.setStrCode(this.strTabName);
                    sysdata.setStrName(" ");
                    sysdata.setStrUpdateSql(ss);
                    sysdata.setStrCreator(pub.getUserName());
                    sysdata.addSetting();
                }
                //-----------------------

            }
            if (sSubData.length() != 0) {
                buildSQL = buildFields(this.sSubData).split("\t");
                if (buildSQL != null && buildSQL[0].length() != 0) {
                    dbl.executeSql(buildSQL[0]);
                }
                //---------lzp add 2008-1-2
                if (this.status.equalsIgnoreCase("1") && buildSQL != null &&
                    buildSQL[0].length() != 0) {
                    com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                        funsetting.SysDataBean();
                    sysdata.setYssPub(pub);
                    sysdata.setStrAssetGroupCode("Common");
                    sysdata.setStrFunName("建表-数据字典");
                    sysdata.setStrCode(this.strTabName);
                    sysdata.setStrName(" ");
                    sysdata.setStrUpdateSql(buildSQL[0]);
                    sysdata.setStrCreator(pub.getUserName());
                    sysdata.addSetting();
                }
                //-----------------------

                if (buildSQL.length == 2 && buildSQL[1].length() != 0) {
                    dbl.executeSql(buildSQL[1]);
                }

                //---------lzp add 2008-1-2
                if (this.status.equalsIgnoreCase("1") && buildSQL.length == 2 &&
                    buildSQL[1].length() != 0) {
                    com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                        funsetting.SysDataBean();
                    sysdata.setYssPub(pub);
                    sysdata.setStrAssetGroupCode("Common");
                    sysdata.setStrFunName("建表-数据字典");
                    sysdata.setStrCode(this.strTabName);
                    sysdata.setStrName(this.strFieldName);
                    sysdata.setStrUpdateSql(buildSQL[1]);
                    sysdata.setStrCreator(pub.getUserName());
                    sysdata.addSetting();
                }
                //-----------------------

            }
        } catch (Exception e) {
            throw new YssException("创建临时表出错！");
        }
    }

    /**
     * 将default值转换成相对应的值
     * @param sFieldType String 数据类型
     * @param sDefaultValue String  默认值
     * @return String
     */
    private String defaultStr(String sFieldType, String sDefaultValue) {
        String sRes = "";
        if (sDefaultValue.trim().length() != 0) {
            if (sFieldType.equalsIgnoreCase("0")) { //char
                sRes = " default " + dbl.sqlString(sDefaultValue);
            } else if (sFieldType.equalsIgnoreCase("1") && !sDefaultValue.equalsIgnoreCase("null")) { //number
                sRes = " default " + sDefaultValue;
            } else if (sFieldType.equalsIgnoreCase("2") && !sDefaultValue.equalsIgnoreCase("null")) { //date
                sRes = " default " + dbl.sqlDate(sDefaultValue);
            } else if (sFieldType.equalsIgnoreCase("3")) { //varchar2
                sRes = " default " + dbl.sqlString(sDefaultValue); //彭鹏 2008.3.20 BUG0000110
            }
        }
        return sRes;
    }

    public void getTableInfo(String strTabName) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String sResult = "";
        StringBuffer buf = null;
        StringBuffer bufAll = new StringBuffer();
        try {
            strSql = " select * from tb_fun_datadict " +
                " where FTABNAME=" + dbl.sqlString(strTabName);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf = new StringBuffer();
                //modify huangqirong 2012-01-04 story #2108处理系统表
                int tabType=rs.getInt("FTableType");
                String tabName=rs.getString("FTabName");
                if(tabType == 0)
                	buf.append(pub.yssGetTableName(tabName)).append("\t");
                else if(tabType == 1)
                	buf.append(tabName).append("\t");
                else if(tabType == 2)//update by guolongchao 20120310 STORY 2210 添加导入存储表类型
                	buf.append(tabName).append("\t");
                //---end---
                buf.append(rs.getString("FFieldName")).append("\t");
                buf.append(rs.getString("FTableDesc")).append("\t");
                buf.append(rs.getString("FFieldDesc")).append("\t");
                buf.append(rs.getString("FFieldType")).append("\t");
                buf.append("").append("\t");
                buf.append(rs.getInt("FKey")).append("\t");
                buf.append(rs.getInt("FTableType")).append("\t");
                buf.append(rs.getInt("FIsNull")).append("\t");
                buf.append("").append("\t");
                buf.append("").append("\t");
                buf.append(1).append("\t");
                buf.append(rs.getString("FFIELDPRE")).append("\t");
                buf.append("").append("\t1\tnull"); //这里增加一个字段往系统中 写参数 ,以前鲁志鹏改过字段的. by liyu

                bufAll.append(buf.toString()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufAll.toString().length() > 2) {
                sResult = bufAll.toString().substring(0,
                    bufAll.toString().length() -
                    2);
            }
            this.sSubData = sResult;
        } catch (Exception e) {
            throw new YssException("获取数据出错!");
        }
        //add by songjie 2010.01.04 QDII维护:MS00899 QDV4华夏2009年12月29日02_B 未关闭结果集 现已关闭
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        //add by songjie 2010.01.04 QDII维护:MS00899 QDV4华夏2009年12月29日02_B
    }

    /**
     * 用于判断表的类型 by ly 080324
     * 考虑很多地方都要用到当前表的类型
     * @param sTabName String 当前的表名
     * @return int 0:系统表,1:临时表 -1:当前表不存在
     * @throws YssException
     */
    public int getTabType(String sTabName) throws YssException {
        String strSql = "";
        int iType = -1;
        ResultSet rs = null;
        try {
            strSql = "select distinct(FTabName) as FTabName,FTableType from Tb_Fun_DataDict where FTabName=" +
                dbl.sqlString(sTabName);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                iType = rs.getInt("FTableType");
            }
            return iType;
        } catch (Exception e) {
            throw new YssException("获取表【" + sTabName + "】的类型出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //在finally中关闭结果集 QDV4海富通2009年05月11日03_AB MS00442 by leeyu 20090514
        }
    }
    
     /**
      * add by guolongchao 20111216 STORY1903 QDV4赢时胜（上海）2011年11月18日01_A.xls
      * @param sTabName
      * @return  返回true表示临时表中存在历史数据;false表示临时表不存在或者临时表中没有历史数据
      * @throws YssException
      */
     public  String getLines(String sTabName) throws YssException
     {
    	 String flag="false";
    	 if(dbl.yssTableExist(sTabName)){
    	 try {         
    	        int linesCount = dbl.executeSqlwithReturnRows("select * from " +sTabName);
    	        if(linesCount>0)
    	           flag="true";
    	     } catch (Exception e) {
    	         throw new YssException("获取表【" + sTabName + "】历史记录总行数出错", e);
    	     } 
    	 }
    	 return flag;
     }
     
     /**
      * add by guolongchao 20111219 STORY1903 QDV4赢时胜（上海）2011年11月18日01_A.xls
      * 更新表结构
      * @param tabName
      * @throws YssException
      */
     public boolean updateTableStructure(String tabName) throws YssException
     {
    	 String sql="";
    	 ResultSet rs = null;
    	 ResultSet rs1 = null;
    	 ResultSet rs2 = null;
    	 StringBuffer sb=new StringBuffer();    	
    	 boolean tableIsExist=false;//判断表tabName是否存在    	
    	 if(dbl.yssTableExist(tabName))
    	 {    	
    		tableIsExist=true;
    		Connection conn = dbl.loadConnection();
            boolean bTrans = false;
    		try 
     	    {  	
                conn.setAutoCommit(false);
                bTrans = true;       		
    			//删除表
     	    	sql=" select  lower(COLUMN_NAME) as COLUMN_NAME from user_tab_columns  where lower(Table_Name) = '"+tabName.toLowerCase()+"'"+
     	    	    " minus "+
     	    		" ( select  lower(COLUMN_NAME) as COLUMN_NAME from user_tab_columns  where lower(Table_Name) = '"+tabName.toLowerCase()+"'"+
                    "    minus "+
                    "   select  lower(a.ffieldname) as COLUMN_NAME from Tb_Fun_DataDict a where lower(a.ftabname)='"+tabName.toLowerCase()+"'" +
                    "  )";
     	    	rs = dbl.openResultSet(sql);
     	    	if(!rs.next())
     	    	{
     	    		dbl.executeSql(dbl.doOperSqlDrop("drop table " + tabName));
     	    		tableIsExist=false;
     	    		return false;
     	    	}
    			     
    			//删除表字段
     	    	sql=" select m.COLUMN_NAME,n.column_name as PK_COLUMN_NAME from "+
     	    		" (select  lower(COLUMN_NAME) as COLUMN_NAME from user_tab_columns  where lower(Table_Name) = '"+tabName.toLowerCase()+"'"+
                    " minus "+
                    " select  lower(a.ffieldname) as COLUMN_NAME from Tb_Fun_DataDict a where lower(a.ftabname)='"+tabName.toLowerCase()+"') m" +
                    " left join " +
                    " (select a.constraint_name,  a.column_name from user_cons_columns a, user_constraints b " +
                    "  where a.constraint_name = b.constraint_name "+
                    "  and b.constraint_type = 'P' "+
                     " and lower(a.table_name) = '"+tabName.toLowerCase()+"') n" +
                     " on lower(m.column_name)=lower(n.column_name)";
     	    	rs1 = dbl.openResultSet(sql);
     	    	while(rs1.next()){
     	    		if(rs1.getString("PK_COLUMN_NAME")!=null)
     	    			throw new YssException("主键【" + rs1.getString("PK_COLUMN_NAME")+ "】不能被删除");
     	    		sb.append(rs1.getString("COLUMN_NAME")+",");
     	    	}
     	    	if(sb.length()>1){
     	    		sql=(sb.delete(sb.length()-1, sb.length())).toString();
     	    		//dbl.executeSql("alter table "+tabName+" set unused (" + sql+")");
     	    		dbl.executeSql("alter table "+tabName+" drop (" + sql+")");
     	    		tableIsExist=true;
     	    		//alter table table1 set unused (column1,column2);
     	    	}
     	    	if(sb.length()>1)
 				   sb.delete(0,sb.length()-1);
 				
 				
 				String Field="";
	    		String strFFieldType="";
	    		String ExNum="";
	    		String FDefaultValue="";
	    		String strIsNull="";	    		
 				//添加表字段
 				sql=" select * from Tb_Fun_DataDict where lower(ftabname)='"+tabName.toLowerCase()+"' and lower(ffieldname) in "+
 					" (select  lower(a.ffieldname) as COLUMN_NAME from Tb_Fun_DataDict a where lower(a.ftabname)='"+tabName.toLowerCase()+"'"+
 				    "   minus "+
 				    "  select  lower(COLUMN_NAME) as COLUMN_NAME from user_tab_columns  where lower(Table_Name) = '"+tabName.toLowerCase()+"'" +
 				    "  )"; 				
    	    	rs2 = dbl.openResultSet(sql);
    	    	while(rs2.next())
    	    	{    	    		
    	    		int fkey=rs2.getInt("fkey");
    	    		if(fkey!=0)
    	    			throw new YssException("新增字段【" + rs2.getString("ffieldname")+ "】不能是主键");
    	    		
    	    		 Field=rs2.getString("ffieldname"); //字段
    	    		 strFFieldType=rs2.getString("ffieldtype");//类型
    	    		 ExNum=rs2.getString("ffieldpre");//精度    	    		
    	    		 FDefaultValue=(rs2.getString("fdefaultvalue")==null||rs2.getString("fdefaultvalue").trim().length()==0)?" ":rs2.getString("fdefaultvalue");//默认值	
    	    		 strIsNull=rs2.getString("fisnull");//是否为空
    	    		 
    	             if(Field!=null&&Field.trim().length()>0)//字段
    	            	 sb.append(Field.trim());
    	             if(strFFieldType!=null&&strFFieldType.trim().length()>0)//类型
    	            	 sb.append(" " + getFieldType(strFFieldType.trim()));
    	             if (ExNum!=null&&ExNum.trim().length() > 0 &&!ExNum.trim().equalsIgnoreCase("null") &&
        	                 !getFieldType(strFFieldType.trim()).equalsIgnoreCase("DATE")) { //精度
    	            	     sb.append("(" + ExNum + ")");
        	             }    	             
    	             if (FDefaultValue.trim().length() > 0) { // defaultValue;
    	            	 sb.append(defaultStr(strFFieldType.trim(), FDefaultValue.trim()));
    	             }
    	             sb.append((strIsNull!=null&&strIsNull.trim().equals("0"))?" NULL ,":" NOT NULL ,"); //Null or Not Null    	             
    	    	}
    	    	if(sb.length()>1){
     	    		sql=(sb.delete(sb.length()-1, sb.length())).toString();//删除最后的”,“
     	    		dbl.executeSql("alter table "+tabName+" add (" + sql+")");  
     	    		tableIsExist=true;
     	    	}    
    	    	if(sb.length()>1)
    	    	   sb.delete(0,sb.length()-1);   
    	    	conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);                
 			} catch (Exception e) {
 				throw new YssException(e);
 			} finally{
 	            dbl.closeResultSetFinal(rs);
 	            dbl.closeResultSetFinal(rs1);
 	            dbl.closeResultSetFinal(rs2);
 	            dbl.endTransFinal(conn, bTrans);
 	        }
    	 } 
    	 return tableIsExist;
     }
}
