package com.yss.main.operdeal.datainterface.swift;

import com.yss.dsub.*;
import com.yss.util.*;
import com.yss.main.dao.*;
import com.yss.main.datainterface.DaoCusConfigureBean;
import com.yss.main.datainterface.DaoFileNameBean;
import com.yss.main.datainterface.swift.DaoSwiftEntitySet;
import com.yss.main.operdeal.datainterface.*;

import java.util.*;
import java.sql.ResultSet;
import com.yss.main.syssetting.DataDictBean;

/**
 * QDV4赢时胜（深圳）2009年5月12日01_A MS00455
 * SWIFT报文操作基类
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company:YssTech </p>
 *
 * @author by leeyu create
 * @version 1.0
 */
public abstract class BaseSwiftOper
    extends BaseBean {
    protected String headerCode = ""; //列代码 code1\tcode2
    protected String headerName = ""; //列名称 name1\tname2
    protected Object[] rowData; //存放一行数据用
    protected String swiftType = ""; //SWIFT类型 如MT303、MT545、MT910
    protected String swiftStatus ="";//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
    protected String swiftStandard = ""; //SWIFT标准 ISO15022、ISO29992
    protected String swiftReflow = ""; //流入与流出
    protected String swiftOperType = ""; //操作类型
    protected String operDatas = ""; //操作数据。
    protected HashMap hmSwiftTemp; //保存SWIFT模板信息 key=swiftType,value =ArrayList
    protected HashMap tradeNums = new HashMap();
    protected String sRelanum = "";//交易编号
    protected SaveSwiftContentBean saveSwift; //保存SWIFT原文数据用的JAVABEAN
    protected BaseDaoOperDeal baseOperDeal; //导入与导出操作的需调用此类的实现类
    
    protected java.util.Date startDate; //起始日期
	protected java.util.Date endDate; //结束日期
    protected String portCode = ""; //组合代码
    public BaseSwiftOper() {
    }

  //--------add by jiangshichao 2010.02.28-------
	public HashMap getTradeNums() {
		return tradeNums;
	}

	public void setTradeNums(HashMap tradeNums) {
		this.tradeNums = tradeNums;
	}
	
	public String getsRelanum(){
		
		return sRelanum;
	}
	
	public void setsRelanum(String sRelanum){
		this.sRelanum = sRelanum;
	}
	//------------------------------------------//
    
    public void setSwiftType(String swifttype) {
        swiftType = swifttype;
    }

    public void setSwiftStandard(String standard) {
        swiftStandard = standard;
    }

    public void setReflow(String reflow) {
        swiftReflow = reflow;
    }

    public void setOperType(String opertype) {
        swiftOperType = opertype;
    }

    /**
     * 初始化Bean
     * @throws YssException
     */
    public abstract void initBean() throws YssException;

    /**
     * 返回一条Header列的报文信息
     * @return String
     * @throws YssException
     */
    public abstract String buildRowStr() throws YssException;

    /**
     * 按type类型解析数据
     * @param sResStr String
     * @param type String
     * @throws YssException
     */
    public abstract void parseReqsRow(String sResStr, String type) throws YssException;

    /**
     * 导入报文时：加载导入流向报文信息(包括报文类型、业务类型、报文路径、导入报文名),返回前台并显示的列信息,以便前台根据文件名读数据
     * 导出报文时：加载导出流向报文信息(包括报文类型、业务类型、报文路径、导入报文名、报文原文、关联交易编号),返回前台并显示的列信息
     * @return String
     * @throws YssException
     */
    public abstract String loadSWIFTListView() throws YssException;

    /**
     * 获取文件名信息
     * @param swiftType SWIFT类型
     * @return String
     * @throws YssException
     */
    protected String getFileNameStr(String swiftCode) throws YssException {
        return getFilePathName(swiftCode);
    }

    /**
     * 初始前台窗体的方法
     * 可用于初始化前台窗体的下拉框的值、加载LISTVIEW、TREEVIEW的初始列等信息
     * @return String
     * @throws YssException
     */
    public abstract String initSWIFTListView() throws YssException;

    /*------------------------其他功能的处理---------------------*/
    /**
     * 解析数据,将一行报文原文解析成报文实体相关数据
     * 可用于导入SWIFT报文时的处理
     * @param objData 一行报文原文数据
     * @throws YssException
     */
    public abstract IDataSetting parseEntityStr(Object objData) throws YssException;

    /**
     * 根据一行报文模板信息生成一行报文数据
     * 可用于导出SWIFT报文时的处理
     * @param entity 报文模板
     * @param objData 数据信息
     * @return String 返回一条完整的报文数据，包括必要的报文开头，报文数据
     * @throws YssException
     */
    public abstract String buildEntityStr(IDataSetting entity, Object objData) throws YssException;

    /**
     * 数据检查处理 若检查不通过，抛出异常提示信息
     *
     * 检查数据完整性;
     * 检查数据格式;
     * 检查数据长度；
     * 检查数据类型；
     * @throws YssException
     */
    protected void check(Object objData, String formatCode) throws YssException {
        byte[] bFormat;
        try {

        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        }
    }

    /**
     * 获取SWIFT某类型的模板信息
     * @param swiftType String
     * @return ArrayList
     * @throws YssException
     */
    protected ArrayList getSwiftTemp(String swiftType,String swiftStatus,String swiftCode) throws YssException {
        try {
            if (hmSwiftTemp == null) {
                hmSwiftTemp = new HashMap();
            }
            if (hmSwiftTemp.get(swiftType+swiftStatus) == null) {
                hmSwiftTemp.put(swiftType+swiftStatus, buildSwiftTemp(swiftType,swiftStatus,swiftCode));
            }
        } catch (Exception ex) {
            throw new YssException(ex);
        }
        return (ArrayList) ( (ArrayList) hmSwiftTemp.get(swiftType+swiftStatus)).clone();
    }

    /**
     * 通过系统数据字典的表类型（系统表与临时表来判断表）来为表加前缀。
     * @param tableName String
     * @return String
     * @throws YssException
     */
    protected String getYssTableName(String tableName) throws YssException {
        DataDictBean dict = new DataDictBean(pub);
        int type = dict.getTabType(tableName);
        if (type == 0) { //系统表
            tableName = pub.yssGetTableName(tableName);
        }
        return tableName;
    }

    /**
     * 检查或是创建临时表的方法
     * @param tableName String
     * @throws YssException
     */
    protected void checkOrCreateTable(String tableName) throws YssException {
        DataDictBean dict = new DataDictBean(pub);
        int type = dict.getTabType(tableName);
        if (type == 1) { //临时表
            dict.getTableInfo(tableName);
            if (!dbl.yssTableExist(tableName)) { //如果表存在的话，就不用建了
                dict.createTab(tableName);
            }
        }
    }

    /**
     * 删除临时表的数据
     * @param tabName String
     * @throws YssException
     */
    protected void deleteSWIFTTab(String tabName) throws YssException {
        String sqlStr = "";
        try {
            sqlStr = "truncate table " + getYssTableName(tabName);
            dbl.executeSql(sqlStr);
        } catch (Exception ex) {
            throw new YssException("删除表【" + tabName + "】数据出错", ex);
        }
    }
    
    /**
     * 获取SWIFT某类型的模板信息
     * @param swiftType String　报文类型
     * @param status String 报文状态　
     * @return ArrayList
     * @throws YssException
     */
    private ArrayList buildSwiftTemp(String swiftType,String status,String swiftCode) throws YssException {
        ArrayList alTemp = new ArrayList();
        DaoSwiftEntitySet entity;
        ResultSet rs = null;
        String sqlStr = "";
        try {
            sqlStr = "select FSwiftType,FStatus,FSwiftStatus,FIndex,FContent,FOption,FTag,FQualifier,FFieldName,FFieldFullName,FTableField from " +
                pub.yssGetTableName("Tb_Dao_Swiftentity") +
                " where FSwiftType=" + dbl.sqlString(swiftType) +
                " and FSwiftStatus="+dbl.sqlString(status)+
                " and FSwiftCode="+dbl.sqlString(swiftCode)+ //add by jiangshichao 获取报文的模板信息时添加swiftcode字段来过滤
                " and FCheckState=1 order by FIndex,FStatus ";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                entity = new DaoSwiftEntitySet();
                entity.setSWiftType(rs.getString("FSwiftType") == null ? "" : rs.getString("FSwiftType"));
                entity.setSSwiftStatus(rs.getString("FSwiftStatus")==null?"":rs.getString("FSwiftStatus"));
                entity.setSStatus(rs.getString("FStatus") == null ? "" : rs.getString("FStatus"));
                entity.setSIndex(rs.getString("FIndex") == null ? "" : rs.getString("FIndex"));
                entity.setSContent(rs.getString("FContent") == null ? "" : rs.getString("FContent"));
                entity.setSOption(rs.getString("FOption") == null ? "" : rs.getString("FOption"));
                entity.setSTag(rs.getString("FTag") == null ? "" : rs.getString("FTag"));
                entity.setSQualifier(rs.getString("FQualifier") == null ? "" : rs.getString("FQualifier"));
                entity.setSFieldName(rs.getString("FFieldName") == null ? "" : rs.getString("FFieldName"));
                entity.setSFieldFullName(rs.getString("FFieldFullName") == null ? "" : rs.getString("FFieldFullName"));
                entity.setSTableField(rs.getString("FTableField") == null ? "" : rs.getString("FTableField"));
                alTemp.add(entity);
            }
        } catch (Exception ex) {
            throw new YssException("获取模板【" + swiftType + "】信息出错!", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return alTemp;
    }    
    

    /**
     * 这里采用ImpCusInterface类的相关方法获取文件名相关信息
     * @return String
     * @throws YssException
     */
    private String getFilePathName(String swiftCode) throws YssException {
        StringBuffer buf = new StringBuffer();
        ArrayList alFileNameSet = null;
        ImpCusInterface impcus = (ImpCusInterface) baseOperDeal;
        impcus.setYssPub(pub);
        //调用 DaoCusConfigureBean 作为中间过度类,辅助完成相关处理
        DaoCusConfigureBean cuscfg = new DaoCusConfigureBean();
        //cuscfg.setCusCfgCode(swiftType);
        cuscfg.setCusCfgCode(swiftCode); //这里更改为用报文类型加上报文状态的方式来取 by leeyu 20091111 //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
        //cuscfg.setFileType("out");//已此文件类型结尾的文件
        //alFileNameSet = getFileNameSet(swiftType);
        alFileNameSet = getFileNameSet(swiftCode);//这里更改为用报文类型加上报文状态的方式来取 by leeyu 20091111//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
        if (alFileNameSet.size() > 0) {
            impcus.recuFileNames(alFileNameSet, 0, "", buf, cuscfg);
        }
        return buf.toString();
    }

    private ArrayList getFileNameSet(String swiftType) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        ArrayList alFileName = new ArrayList();
        DaoFileNameBean fileName = null;
        try {
            strSql = " select * from " + pub.yssGetTableName("Tb_Dao_FileName") +
                " where FCusCfgCode=" + dbl.sqlString(swiftType) +
                " and FCheckState=1 order by FOrderNum";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                fileName = new DaoFileNameBean();
                fileName.setCusCfgCode(swiftType);
                fileName.setFormat(rs.getString("FFormat"));
                fileName.setFileNameConent(rs.getString("FFIleNameConent"));
                fileName.setFileNameCls(rs.getString("FFileNameCls"));
                fileName.setFileNameDictCode(rs.getString("FFileNameDict"));
                alFileName.add(fileName);
            }
            return alFileName;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    public java.util.Date getStartDate() {
		return startDate;
	}

	public void setStartDate(java.util.Date startDate) {
		this.startDate = startDate;
	}

	public java.util.Date getEndDate() {
		return endDate;
	}

	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	public void setOperDatas(String operDatas){
		this.operDatas =operDatas;
	}
	//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
    public String getSwiftStatus() {
		return swiftStatus;
	}
    //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
	public void setSwiftStatus(String swiftStatus) {
		this.swiftStatus = swiftStatus;
	}
}
