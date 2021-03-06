package com.yss.main.taoperation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class TAFeeLink
    extends BaseDataSettingBean implements IDataSetting {
    private String sellNetCode; //销售网点代码
    private String sellNetName;
    private String portClsCode; //组合分级代码
    private String portClsName;
    private String portCode;
    private String portName;
    private String sellTypeCode;
    private String sellTypeName;
    private String curyCode;
    private String curyName;
    private Date startDate;
    private String desc;
    private String feeCode1;
    private String feeCode2;
    private String feeCode3;
    private String feeCode4;
    private String feeCode5;
    private String feeCode6;
    private String feeName1;
    private String feeName2;
    private String feeName3;
    private String feeName4;
    private String feeName5;
    private String feeName6;

    private String oldSellNetCode;
    private String oldPortClsCode;
    private String oldPortCode;
    private String oldSellTypeCode;
    private String oldCuryCode;
    private Date oldStartDate;
    private TAFeeLink filterType;
    private String sRecycled = null; //保存未解析前的字符串

    /**shashijie 2011-09-09 STORY 1580*/
    private String FeeType = "0";//费用类型
    private String OldFeeType;//旧费用类型
    /**end*/
    
    public TAFeeLink() {
    }

    public void checkInput(byte btOper) throws YssException {
    	/**shashijie 2011-09-15 STORY 1580*/
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_TA_FeeLink"),
                               "FSellNetCode,FPortClsCode,FPortCode,FSellTypeCode,FCuryCode,FStartDate,FFeeType",
                               sellNetCode + "," + portClsCode + "," +
                               portCode + "," + sellTypeCode + "," + curyCode +
                               "," + YssFun.formatDate(startDate, "yyyy-MM-dd")+","+FeeType
                               ,
                               oldSellNetCode + "," + oldPortClsCode + "," +
                               oldPortCode + "," + oldSellTypeCode + "," +
                               oldCuryCode + "," +
                               YssFun.formatDate(oldStartDate, "yyyy-MM-dd")+","+OldFeeType);
        /**end*/
    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " insert into " + pub.yssGetTableName("Tb_TA_FeeLink") +
                " (FSellNetCode,FPortClsCode,FDesc,FPortCode,FSellTypeCode,FCuryCode," +
                " FFeeCode1,FFeeCode2,FFeeCode3,FFeeCode4,FFeeCode5,FFeeCode6, " +
                " FCheckState,FCreator,FCreateTime,FStartDate" +
                /**shashijie 2011-09-09 STORY 1580 */
                " ,FFeeType " +
                /**end*/
                " ) values("
                + dbl.sqlString(this.sellNetCode) + ","
                + dbl.sqlString(this.portClsCode) + ","
                + dbl.sqlString(this.desc) + ","
                + dbl.sqlString(this.portCode) + ","
                + dbl.sqlString(this.sellTypeCode) + ","
                + dbl.sqlString(this.curyCode) + ","
                + dbl.sqlString(this.feeCode1) + ","
                + dbl.sqlString(this.feeCode2) + ","
                + dbl.sqlString(this.feeCode3) + ","
                + dbl.sqlString(this.feeCode4) + ","
                + dbl.sqlString(this.feeCode5) + ","
                + dbl.sqlString(this.feeCode6) + ","
                + (pub.getSysCheckState() ? "0" : "1") + ","
                + dbl.sqlString(this.creatorCode) + ","
                + dbl.sqlString(this.creatorTime) + //BugNo:0000310 4 edit by jc
                //(pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorTime)) +
                "," +
                dbl.sqlDate(this.startDate) +
                /**shashijie 2011-09-09 STORY 1580 */
                " , " +dbl.sqlString(this.FeeType) +//费用类型
                /**end*/
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加TA费用链接设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " update " + pub.yssGetTableName("Tb_TA_FeeLink") +
                " set FSellNetCode=" + dbl.sqlString(this.sellNetCode) + "," +
                " FPortClsCode=" + dbl.sqlString(this.portClsCode) + "," +
                " FPortCode=" + dbl.sqlString(this.portCode) + "," +
                " FSellTypeCode=" + dbl.sqlString(this.sellTypeCode) + "," +
                " FCuryCode =" + dbl.sqlString(this.curyCode) + "," +
                " FFeeCode1 =" + dbl.sqlString(this.feeCode1) + "," +
                " FFeeCode2 =" + dbl.sqlString(this.feeCode2) + "," +
                " FFeeCode3 =" + dbl.sqlString(this.feeCode3) + "," +
                " FFeeCode4 =" + dbl.sqlString(this.feeCode4) + "," +
                " FFeeCode5 =" + dbl.sqlString(this.feeCode5) + "," +
                " FFeeCode6 =" + dbl.sqlString(this.feeCode6) + "," +
                " FDesc=" + dbl.sqlString(this.desc) + "," +
                " FCheckState=" + this.checkStateId + "," +
                " FStartDate=" + dbl.sqlDate(this.startDate) +
                /**shashijie 2011-09-09 STORY 1580 */
                " , FFeeType = " + dbl.sqlString(this.FeeType) +//费用类型
                /**end*/
                //BugNo:0000310 4 edit by jc
                ", FCreateTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCreator = " +
                dbl.sqlString(pub.getUserCode()) +
                //------------------------jc
                " where FSellNetCode=" + dbl.sqlString(this.oldSellNetCode) +
                " and FPortClsCode=" + dbl.sqlString(this.oldPortClsCode) +
                " and FPortCode=" + dbl.sqlString(this.oldPortCode) +
                " and FSellTypeCode=" + dbl.sqlString(this.oldSellTypeCode) +
                " and FCuryCode=" + dbl.sqlString(this.oldCuryCode) +
                " and FStartDate=" + dbl.sqlDate(this.oldStartDate)+
            	/**shashijie 2011-09-15 STORY 1580*/
            	" and FFeeType = "+dbl.sqlString(this.OldFeeType);
            	/**end*/
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改TA费用链接设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " update " + pub.yssGetTableName("Tb_TA_FeeLink") +
                " set FCheckState=" + this.checkStateId +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FSellNetCode=" + dbl.sqlString(this.oldSellNetCode) +
                " and FPortClsCode=" + dbl.sqlString(this.oldPortClsCode) +
                " and FPortCode=" + dbl.sqlString(this.oldPortCode) +
                " and FSellTypeCode=" + dbl.sqlString(this.oldSellTypeCode) +
                " and FCuryCode=" + dbl.sqlString(this.oldCuryCode) +
                " and FStartDate=" + dbl.sqlDate(this.oldStartDate)+
                /**shashijie 2011-09-15 STORY 1580*/
            	" and FFeeType = "+dbl.sqlString(this.OldFeeType);
            	/**end*/
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除TA费用链接设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void checkSetting() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
			/**shashijie 2012-7-2 STORY 2475 */
            if (sRecycled != null || !sRecycled.equalsIgnoreCase("")) {
			/**end*/
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = " update " + pub.yssGetTableName("Tb_TA_FeeLink") +
                        " set FCheckState=" + this.checkStateId +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "', FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + //BugNo:0000310 4 edit by jc
//               修改 邱健，还原和清除功能不需要 oldXXX 成员
//               " where FSellNetCode=" + dbl.sqlString(this.oldSellNetCode) +
//               " and FPortClsCode=" + dbl.sqlString(this.oldPortClsCode) +
//               " and FPortCode=" + dbl.sqlString(this.oldPortCode) +
//               " and FSellTypeCode=" + dbl.sqlString(this.oldSellTypeCode) +
//               " and FCuryCode=" + dbl.sqlString(this.oldCuryCode) +
//               " and FStartDate=" + dbl.sqlDate(this.oldStartDate);

                        " where FSellNetCode=" + dbl.sqlString(this.sellNetCode) +
                        " and FPortClsCode=" + dbl.sqlString(this.portClsCode) +
                        " and FPortCode=" + dbl.sqlString(this.portCode) +
                        " and FSellTypeCode=" + dbl.sqlString(this.sellTypeCode) +
                        " and FCuryCode=" + dbl.sqlString(this.curyCode) +
                        " and FStartDate=" + dbl.sqlDate(this.startDate) +
	                    /**shashijie 2011-09-15 STORY 1580*/
	                	" and FFeeType = "+dbl.sqlString(this.FeeType);
	                	/**end*/
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("TA费用链接设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    private void setFeeLinkAttr(ResultSet rs) throws SQLException, YssException {
        this.sellNetCode = rs.getString("FSellNetCode");
        this.sellNetName = rs.getString("FSellNetName");
        this.desc = rs.getString("FDesc");
        this.portCode = rs.getString("FPortCode");
        this.portName = rs.getString("FPortName");
        this.sellTypeCode = rs.getString("FSellTypeCode");
        this.sellTypeName = rs.getString("FSellTypeName");
        this.curyCode = rs.getString("FCuryCode");
        this.curyName = rs.getString("FCuryName");
        this.portClsCode = rs.getString("FPortClsCode");
        this.portClsName = rs.getString("FPortClsName");
        this.startDate = rs.getDate("FStartDate");
        /**shashijie 2011-09-09 STORY 1580 */
        this.FeeType = rs.getString("FFeeType")==null? "0" : rs.getString("FFeeType");//费用类型
        /**end*/
        this.feeCode1 = rs.getString("FFeeCode1");
        this.feeCode2 = rs.getString("FFeeCode2");
        this.feeCode3 = rs.getString("FFeeCode3");
        this.feeCode4 = rs.getString("FFeeCode4");
        this.feeCode5 = rs.getString("FFeeCode5");
        this.feeCode6 = rs.getString("FFeeCode6");
        this.feeName1 = rs.getString("FFeeName1");
        this.feeName2 = rs.getString("FFeeName2");
        this.feeName3 = rs.getString("FFeeName3");
        this.feeName4 = rs.getString("FFeeName4");
        this.feeName5 = rs.getString("FFeeName5");
        this.feeName6 = rs.getString("FFeeName6");
        super.setRecLog(rs);
    }

    private String FilterStr() throws YssException {
        String str = "";
        if (this.filterType != null) {
            str = " where 1=1 ";
            /**shashijie 2011-09-16 STORY 1580 更改逻辑,否则查询不到记录like '%'会把空格也过滤掉*/
            if (this.filterType.sellNetCode != null &&
            		this.filterType.sellNetCode.trim().length() > 0) {
                str += " and FSellNetCode like '" +
                    filterType.sellNetCode.trim().replaceAll("'","''")+"%'";
            }
            /**shashijie 2011-09-16 STORY 1580 更改逻辑,否则查询不到记录like '%'会把空格也过滤掉*/
            if (this.filterType.desc != null && this.filterType.desc.trim().length() > 0) {
                str += " and FDesc like '" + filterType.desc.trim().replaceAll("'", "''") +
                    "%'";
            }
            /**shashijie 2011-09-16 STORY 1580 更改逻辑,否则查询不到记录like '%'会把空格也过滤掉*/
            //edited by zhouxiang MS01548	TA费用链接设置和TA现金帐户链接设置页面的筛选按钮不能进行筛选功能加上：.trim()				
            if (this.filterType.portCode != null &&
            		this.filterType.portCode.trim().length() != 0) {
                str += " and FPortCode like '" +
                    filterType.portCode.trim().replaceAll("'", "''") + "%'";
            }
            /**shashijie 2011-09-16 STORY 1580 更改逻辑,否则查询不到记录like '%'会把空格也过滤掉*/
            if (this.filterType.sellTypeCode != null &&
            		this.filterType.sellTypeCode.trim().length() != 0) {
                str += " and FSellTypeCode like '" +
                    filterType.sellTypeCode.trim().replaceAll("'", "''") + "%'";
            }
            /**shashijie 2011-09-16 STORY 1580 更改逻辑,否则查询不到记录like '%'会把空格也过滤掉*/
            if (this.filterType.curyCode != null && this.filterType.curyCode.trim().length() != 0) {
                str += " and FCuryCode like '" +
                    filterType.curyCode.trim().replaceAll("'", "''") + "%'";
            }
            if (this.filterType.startDate != null &&
                !this.filterType.startDate.equals(YssFun.toDate("9998-12-31"))) {
                str += " and FStartDate =" + filterType.startDate;
            }
            
            /**shashijie 2011-09-09 STORY 1580 增加费用类型*/
            if (this.filterType.FeeType != null && //费用类型
                    this.filterType.FeeType.trim().length() > 0 &&
                    //若选择全部测不添加查询条件
                    !this.filterType.FeeType.trim().equals("99")) {
                    str += " and FFeeType = " + filterType.FeeType;
                }
            /**end*/
            /**shashijie 2011-09-16 STORY 1580 更改逻辑,否则查询不到记录like '%'会把空格也过滤掉*/
            if (this.filterType.portClsCode != null &&
            		this.filterType.portClsCode.trim().length() != 0) {
                str += " and FPortClsCode like '" +
                    filterType.portClsCode.trim().replaceAll("'", "''") + "%'";
            }
            /**shashijie 2011-09-16 STORY 1580 更改逻辑,否则查询不到记录like '%'会把空格也过滤掉*/
            if (this.filterType.feeCode1 != null && this.filterType.feeCode1.trim().length() > 0) {
                str += " and FFeeCode1 like '" +
                    filterType.feeCode1.trim().replaceAll("'", "''") + "%'";
            }
            /**shashijie 2011-09-16 STORY 1580 更改逻辑,否则查询不到记录like '%'会把空格也过滤掉*/
            if (this.filterType.feeCode2 != null && this.filterType.feeCode2.trim().length() > 0) {
                str += " and FFeeCode2 like '" +
                    filterType.feeCode2.trim().replaceAll("'", "''") + "%'";
            }
            /**shashijie 2011-09-16 STORY 1580 更改逻辑,否则查询不到记录like '%'会把空格也过滤掉*/
            if (this.filterType.feeCode3 != null && this.filterType.feeCode3.trim().length() > 0) {
                str += " and FFeeCode3 like '" +
                    filterType.feeCode3.trim().replaceAll("'", "''") + "%'";
            }
            /**shashijie 2011-09-16 STORY 1580 更改逻辑,否则查询不到记录like '%'会把空格也过滤掉*/
            if (this.filterType.feeCode4 != null && this.filterType.feeCode4.trim().length() > 0) {
                str += " and FFeeCode4 like '" +
                    filterType.feeCode4.trim().replaceAll("'", "''") + "%'";
            }
            /**shashijie 2011-09-16 STORY 1580 更改逻辑,否则查询不到记录like '%'会把空格也过滤掉*/
            if (this.filterType.feeCode5 != null && this.filterType.feeCode5.trim().length() > 0) {
                str += " and FFeeCode5 like '" +
                    filterType.feeCode5.trim().replaceAll("'", "''") + "%'";
            }
            /**shashijie 2011-09-16 STORY 1580 更改逻辑,否则查询不到记录like '%'会把空格也过滤掉*/
            if (this.filterType.feeCode6 != null && this.filterType.feeCode6.trim().length() > 0) {
                str += " and FFeeCode6 like '" +
                    filterType.feeCode6.trim().replaceAll("'", "''") + "%'";
            }
        }
        return str;
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }

    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        /**shashijie 2011-09-09 STORY 1580 */
        String sVocStr = "";//常用词汇
        /**end*/
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FSellNetName as FSellNetName, " +
                " e.FPortClsName as FPortClsName,f.FPortName as FPortName,g.FSellTypeName as FSellTypeName," +
                " h.FCuryName as FCuryName,i.FFeeName as FFeeName1,j.FFeeName as FFeeName2,k.FFeeName as FFeeName3, " +
                " l.FFeeName as FFeeName4,m.FFeeName as FFeeName5,n.FFeeName as FFeeName6 " +
                " from " + pub.yssGetTableName("Tb_TA_FeeLink") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join (select FSellNetCode,FSellNetName from " +
                pub.yssGetTableName("Tb_TA_SellNet") +
                " where FCheckState = 1 ) d on a.FSellNetCode=d.FSellNetCode " +
                " left join (select FPortClsCode,FPortClsName from " +
                pub.yssGetTableName("Tb_TA_PortCls") +
                "  where FCheckState = 1 ) e on a.FPortClsCode = e.FPortClsCode " +          
                
                //------ modify by wangzuochun  2010.08.21  MS01604    启用日期不同的组合，导致新建产生多比数据    QDV4赢时胜(测试)2010年08月12日05_B    
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
       
                " left join (select FPortCode, FPortName, FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1) f on a.FPortCode = f.FPortCode " +
                
                
                //end by lidaolong
                //-------------------------------------------- MS01604 -------------------------------------------//
                              
                " left join (select FSellTypeCode,FSellTypeName from " +
                pub.yssGetTableName("Tb_TA_SellType") +
                "  where FCheckState = 1 ) g on g.FSellTypeCode= a.FSellTypeCode " +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1  ) h on h.FCuryCode=a.FCuryCode " +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                "  where FCheckState = 1 ) i on a.FFeeCode1 =i.FFeeCode " +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                "  where FCheckState = 1 ) j on a.FFeeCode2 =j.FFeeCode " +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                "  where FCheckState = 1 ) k on a.FFeeCode3 =k.FFeeCode " +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                "  where FCheckState = 1 ) l on a.FFeeCode4 =l.FFeeCode " +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                "  where FCheckState = 1 ) m on a.FFeeCode5 =m.FFeeCode " +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                "  where FCheckState = 1 ) n on a.FFeeCode6 =n.FFeeCode " +
                FilterStr() +
                " order by a.FCheckState,a.FCheckTime desc,a.FCreateTime desc ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols()))
                		.append(YssCons.YSS_LINESPLITMARK);
                setFeeLinkAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            /**shashijie 2011-09-15 STORY 1580 前台LIST前面显示费用类型*/
            //设置费用类型
            bufShow = setBufShowFeeType(bufShow);
            /**end*/
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            /**shashijie 2011-09-09 STORY 1580 */
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.yss_FEEMONEYJOINTYPE);
            /**end*/
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取TA现金结算链接设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

	public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*,i.FFeeName as FFeeName1,j.FFeeName as FFeeName2,k.FFeeName as FFeeName3, " +
                " l.FFeeName as FFeeName4,m.FFeeName as FFeeName5,n.FFeeName as FFeeName6 " +
                " from " + pub.yssGetTableName("Tb_TA_FeeLink") + " a" +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                " ) i on a.FFeeCode1 =i.FFeeCode " +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                " ) j on a.FFeeCode2 =j.FFeeCode " +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                " ) k on a.FFeeCode3 =k.FFeeCode " +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                " ) l on a.FFeeCode4 =l.FFeeCode " +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                " ) m on a.FFeeCode5 =m.FFeeCode " +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                " ) n on a.FFeeCode6 =n.FFeeCode " +
                FilterStr() +
                " order by a.FCheckState,a.FCheckTime desc,a.FCreateTime desc ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setFeeLinkAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取TA现金结算链接设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
                return;
            }
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.sellNetCode = reqAry[0];
            this.desc = reqAry[1];
            this.portCode = reqAry[2];
            this.sellTypeCode = reqAry[3];
            this.curyCode = reqAry[4];
            this.portClsCode = reqAry[5];
            if (reqAry[0].length() == 0) {
                this.sellNetCode = " ";
            }
            if (reqAry[2].length() == 0) {
                this.portCode = " ";
            }
            if (reqAry[5].length() == 0) {
                this.portClsCode = " ";
            }
            if (reqAry[4].length() == 0) {
                this.curyCode = " ";
            }
            if (reqAry[3].length() == 0) {
                this.sellTypeCode = " ";
            }
            this.startDate = YssFun.toDate(reqAry[6]);
            this.checkStateId = Integer.parseInt(reqAry[7]);
            this.oldSellNetCode = reqAry[8];
            this.oldPortClsCode = reqAry[9];
            this.oldPortCode = reqAry[10];
            this.oldSellTypeCode = reqAry[11];
            this.oldCuryCode = reqAry[12];
            this.oldStartDate = YssFun.toDate(reqAry[13]);
            this.feeCode1 = reqAry[14];
            this.feeName1 = reqAry[15];
            this.feeCode2 = reqAry[16];
            this.feeName2 = reqAry[17];
            this.feeCode3 = reqAry[18];
            this.feeName3 = reqAry[19];
            this.feeCode4 = reqAry[20];
            this.feeName4 = reqAry[21];
            this.feeCode5 = reqAry[22];
            this.feeName5 = reqAry[23];
            this.feeCode6 = reqAry[24];
            this.feeName6 = reqAry[25];
            /**shashijie 2011-09-09 STORY 1580 */
            this.FeeType = reqAry[26];//费用类型
            /**end*/
            /**shashijie 2011-09-15 STORY 1580*/
            this.OldFeeType = reqAry[27];//旧费用类型
        	/**end*/
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TAFeeLink();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析TA现金结算链接设置出错", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sellNetCode).append("\t");
        buf.append(this.sellNetName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.sellTypeCode).append("\t");
        buf.append(this.sellTypeName).append("\t");
        buf.append(this.curyCode).append("\t");
        buf.append(this.curyName).append("\t");
        buf.append(this.portClsCode).append("\t");
        buf.append(this.portClsName).append("\t");
        buf.append(YssFun.formatDate(this.startDate)).append("\t"); //modify by fangjiang 2010.10.28 BUG #194 QDV4赢时胜(测试)2010年10月25日04_B.xls
        buf.append(this.feeCode1).append("\t");
        buf.append(this.feeName1).append("\t");
        buf.append(this.feeCode2).append("\t");
        buf.append(this.feeName2).append("\t");
        buf.append(this.feeCode3).append("\t");
        buf.append(this.feeName3).append("\t");
        buf.append(this.feeCode4).append("\t");
        buf.append(this.feeName4).append("\t");
        buf.append(this.feeCode5).append("\t");
        buf.append(this.feeName5).append("\t");
        buf.append(this.feeCode6).append("\t");
        buf.append(this.feeName6).append("\t");
        /**shashijie 2011-09-09 STORY 1580*/
        buf.append(this.FeeType).append("\t");//费用类型
        /*if (this.FeeType.trim().equals("1")) {
        	buf.append("TA赎回款手续费").append("\t");//费用类型
		} else {
			buf.append("交易续费").append("\t");//费用类型
		}*/
        /**end*/
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public void setSellNetCode(String sellNetCode) {
        this.sellNetCode = sellNetCode;
    }

    public void setSellNetName(String sellNetName) {
        this.sellNetName = sellNetName;
    }

    public void setPortClsName(String portClsName) {
        this.portClsName = portClsName;
    }

    public void setPortTypeCode(String portCode) {
        this.portCode = portCode;
    }

    public void setPortTypeName(String portName) {
        this.portName = portName;
    }

    public void setSellTypeCode(String sellTypeCode) {
        this.sellTypeCode = sellTypeCode;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setCuryName(String curyName) {
        this.curyName = curyName;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOldSellNetCode(String oldSellNetCode) {
        this.oldSellNetCode = oldSellNetCode;
    }

    public void setOldPortClsCode(String oldPortClsCode) {
        this.oldPortClsCode = oldPortClsCode;
    }

    public void setOldPortCode(String oldPortCode) {
        this.oldPortCode = oldPortCode;
    }

    public void setOldSellTypeCode(String oldSellTypeCode) {
        this.oldSellTypeCode = oldSellTypeCode;
    }

    public void setOldCuryCode(String oldCuryCode) {
        this.oldCuryCode = oldCuryCode;
    }

    public void setOldStartDate(Date oldStartDate) {
        this.oldStartDate = oldStartDate;
    }

    public void setSellTypeName(String FSellTypeName) {
        this.sellTypeName = FSellTypeName;
    }

    public void setFilterType(TAFeeLink filterType) {
        this.filterType = filterType;
    }

    public void setFeeCode1(String feeCode1) {
        this.feeCode1 = feeCode1;
    }

    public void setFeeCode2(String feeCode2) {
        this.feeCode2 = feeCode2;
    }

    public void setFeeCode3(String feeCode3) {
        this.feeCode3 = feeCode3;
    }

    public void setFeeCode4(String feeCode4) {
        this.feeCode4 = feeCode4;
    }

    public void setFeeCode5(String feeCode5) {
        this.feeCode5 = feeCode5;
    }

    public void setFeeCode6(String feeCode6) {
        this.feeCode6 = feeCode6;
    }

    public void setFeeName1(String feeName1) {
        this.feeName1 = feeName1;
    }

    public void setFeeName2(String feeName2) {
        this.feeName2 = feeName2;
    }

    public void setFeeName3(String feeName3) {
        this.feeName3 = feeName3;
    }

    public void setFeeName4(String feeName4) {
        this.feeName4 = feeName4;
    }

    public void setFeeName5(String feeName5) {
        this.feeName5 = feeName5;
    }

    public void setFeeName6(String feeName6) {
        this.feeName6 = feeName6;
    }

    public String getSellNetCode() {
        return sellNetCode;
    }

    public String getSellNetName() {
        return sellNetName;
    }

    public String getPortClsCode() {
        return portClsCode;
    }

    public String getPortClsName() {
        return portClsName;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getPortName() {
        return portName;
    }

    public String getSellTypeCode() {
        return sellTypeCode;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public String getCuryName() {
        return curyName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getDesc() {
        return desc;
    }

    public String getOldSellNetCode() {
        return oldSellNetCode;
    }

    public String getOldPortClsCode() {
        return oldPortClsCode;
    }

    public String getOldPortCode() {
        return oldPortCode;
    }

    public String getOldSellTypeCode() {
        return oldSellTypeCode;
    }

    public String getOldCuryCode() {
        return oldCuryCode;
    }

    public Date getOldStartDate() {
        return oldStartDate;
    }

    public String getSellTypeName() {
        return sellTypeName;
    }

    public TAFeeLink getFilterType() {
        return filterType;
    }

    public String getFeeCode1() {
        return feeCode1;
    }

    public String getFeeCode2() {
        return feeCode2;
    }

    public String getFeeCode3() {
        return feeCode3;
    }

    public String getFeeCode4() {
        return feeCode4;
    }

    public String getFeeCode5() {
        return feeCode5;
    }

    public String getFeeCode6() {
        return feeCode6;
    }

    public String getFeeName1() {
        return feeName1;
    }

    public String getFeeName2() {
        return feeName2;
    }

    public String getFeeName3() {
        return feeName3;
    }

    public String getFeeName4() {
        return feeName4;
    }

    public String getFeeName5() {
        return feeName5;
    }

    public String getFeeName6() {
        return feeName6;
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_TA_FeeLink") +
                        " where FSellNetCode=" + dbl.sqlString(this.sellNetCode) +
                        " and FPortClsCode=" + dbl.sqlString(this.portClsCode) +
                        " and FPortCode=" + dbl.sqlString(this.portCode) +
                        " and FSellTypeCode=" + dbl.sqlString(this.sellTypeCode) +
                        " and FCuryCode=" + dbl.sqlString(this.curyCode) +
                        " and FStartDate=" + dbl.sqlDate(this.startDate) +
	                    /**shashijie 2011-09-15 STORY 1580*/
	                	" and FFeeType = "+dbl.sqlString(this.FeeType);
	                	/**end*/
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("TA费用链接清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData1() throws YssException {
        return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }

	public String getFeeType() {
		return FeeType;
	}

	public void setFeeType(String feeType) {
		FeeType = feeType;
	}

    /**设置费用类型
     * @param bufShow
     * @return
     * @author shashijie ,2011-9-15 , STORY 1580
     * @modified 
     */
    private StringBuffer setBufShowFeeType(StringBuffer bufShow) {
    	StringBuffer value = new StringBuffer("");
    	if (bufShow==null || bufShow.toString().trim().length()<1) {
			return value;
		}
    	String[] bufStrings = bufShow.toString().split(YssCons.YSS_LINESPLITMARK);
    	for (int i = 0; i < bufStrings.length; i++) {
    		String tempString = "";
    		String[] showStrings = bufStrings[i].toString().split("\t");
    		//更换第十八个字符,费用类型
    		if (showStrings[17].trim().equals("1")) {
    			showStrings[17] = "TA赎回款手续费";//费用类型
    		} else {
    			showStrings[17] = "交易费用";//费用类型
    		}
    		for (int j = 0; j < showStrings.length; j++) {
    			tempString += showStrings[j] + ("\t");
			}
    		//去掉最后一个\t
    		if (tempString.length() > 2) {
    			tempString = tempString.substring(0,tempString.length() - 1);
            }
    		value.append(tempString).append(YssCons.YSS_LINESPLITMARK);
		}
		return value;
	}

	public String getOldFeeType() {
		return OldFeeType;
	}

	public void setOldFeeType(String oldFeeType) {
		OldFeeType = oldFeeType;
	}

}
