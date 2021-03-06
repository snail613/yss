package com.yss.main.operdeal.report.navrep;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.operdeal.report.navrep.pojo.*;
import com.yss.util.*;

public class BaseNavRep
    extends BaseBean {
    protected String valDefine = ""; //分级字段
    protected java.util.Date dDate = null;
    protected String portCode = "";
    protected String invMgrCode = "";
    protected String tempViewName = ""; //临时视图名
    protected String[] fields = null; //group条件的字段
    protected ArrayList valuationBeans = null;
    protected boolean bETFVal = false;//20091013 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
    protected java.util.Date dEndDate = null;//====add by xxm,2010.01.29.MS00900 建议增加跨区间确认与反确认净值表的功能
    public BaseNavRep() {
    }

    protected void initReport(java.util.Date dDate, String sPortCode,
                              String sInvMgrCode) throws YssException {
    }

    /**
     * 将需要的数据并插入相应的视图,返回视图名
     * @param ptmt PreparedStatement
     * @throws YssException
     */
    protected String buildRepView() throws
        YssException {
        return "";
    }

    /**
     * 从初始明细视图中取数
     * @param tempTreeName String
     * @throws YssException
     * @return ArrayList
     */
    protected ArrayList getRepData(String tempViewName) throws YssException {
        //list = buildDefine(this.valDefine);
    	try
    	{
	        fields = this.valDefine.split(";");
	        String groupStr = "";
	        ArrayList allData = new ArrayList();
	        if (fields.length > 0) {
	            for (int Grade = fields.length; Grade > 0; Grade--) {
	                groupStr = buildGroupStr(fields, Grade);
	                allData.addAll(getGradeData(groupStr, Grade)); //获取各个分级的数据，放入一个总的ArrayList
	            }
	        }
	        return allData;
    	}
    	catch(Exception e)
    	{
    		throw new YssException(e.getMessage());
    	}
    }

    protected ArrayList getGradeData(String groupStr, int Grade) throws YssException {
        return null;
    }

    /**
     * 调用程序入口 获取向前台传输的数据
     * @param dDate Date
     * @param sPortCode String
     * @param sInvMgrCode String
     * @throws YssException
     * @return String
     */
    public ArrayList buildRepData(java.util.Date dDate, String sPortCode,
                                  String sInvMgrCode) throws YssException {
        try {
            initReport(dDate, sPortCode, sInvMgrCode); //初始化
            tempViewName = buildRepView(); //生成所需的视图，并返回视图名
            //------ modify by wangzuochun 2010.09.14  MS01705    视图问题没有完全解决，生成净值表时仍会创建视图    QDV4建行2010年09月08日02_B    
            ArrayList arrRepDate = getRepData(tempViewName); //从视图中获取数据并进行相应的处理，返回包含净值信息Bean的ArrayList
            if (tempViewName.startsWith("V_Temp_") && tempViewName.endsWith("_" + pub.getUserCode())){
            	if (dbl.yssViewExist(tempViewName)) {
                    dbl.executeSql("drop view " + tempViewName);
                }
            }
            return arrRepDate;
            //-----------------------MS01705--------------------//
        } catch (Exception e) {
            throw new YssException(e);
        }
    }
    
    /**
     * 调用程序入口 获取向前台传输的数据
     * @param dDate Date
     * @param sPortCode String
     * @param sborker String smortgage String 
     * @throws YssException
     * @return String add by zhouxiang 
     */
    public ArrayList buildRepData(java.util.Date dDate, String sPortCode,
                                  String sborker,String smortgage) throws YssException {
        try {
            initReport(dDate, sPortCode, sborker,smortgage); //初始化
            tempViewName = buildRepView(); //生成所需的视图，并返回视图名
          
            ArrayList arrRepDate = getRepData(tempViewName); //从视图中获取数据并进行相应的处理，返回包含净值信息Bean的ArrayList
            if (tempViewName.startsWith("V_Temp_") && tempViewName.endsWith("_" + pub.getUserCode())){
            	if (dbl.yssViewExist(tempViewName)) {
                    dbl.executeSql("drop view " + tempViewName);
                }
            }
            return arrRepDate;
            //-----------------------MS01705--------------------//
        } catch (Exception e) {
            throw new YssException(e);
        }
    }
    /**重写方法， 券商 和抵押物
     * @param dDate
     * @param sPortCode
     * @param sborker
     * @param smortgage
     * @throws YssException
     */
    public void initReport(java.util.Date dDate, String sPortCode,
            String sborker,String smortgage) throws YssException {
    	this.dDate=dDate;
    	this.portCode=sPortCode;
}

    
    /*protected ArrayList buildDefine(String Define) throws YssException {
       ArrayList reArray = new ArrayList();
       int endPos = 0;
       int pos = 0;
       int leng = 0;
       String subDefine = "";
       try {
          leng = Define.length();
          while (endPos < leng) {
             endPos = Define.indexOf(";");
             subDefine = Define.substring(pos, endPos);
             reArray.add(subDefine);
             pos = endPos + 1;
          }
          return reArray;
       }
       catch (Exception e) {
          throw new YssException("分级数据出错");
       }
       }*/

    protected String buildGroupStr(String[] fields, int grades) throws
        YssException {
        String reStr = "";
        try {
            for (int i = 0; i < grades; i++) {
                reStr = reStr + fields[i] + ",";
            }
            if (reStr.length() > 0) {
                reStr = reStr.substring(0, reStr.length() - 1);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException("设置group字段数据出错!+\n");
        }
    }

    protected String buildOrderStr(String OrderFields) throws YssException {
        String reStr = "";
        /**shashijie 2012-7-2 STORY 2475 */
        String[] fieldes = null;
        /**end*/
        try {
            //reStr = OrderFields.replaceAll(",", "||");
            if (OrderFields.length() > 0) {
                fieldes = OrderFields.split(",");
                if (fieldes != null) {
                    for (int i = 0; i < fieldes.length; i++) {
                        reStr += fieldes[i] + dbl.sqlJoinString().trim() + dbl.sqlString("##") + dbl.sqlJoinString().trim();
                    }
                    if (reStr.length() > 0) {
                        reStr = reStr.substring(0, reStr.length() - 8);
                    }
                }
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException("Order数据出错!\n");
        }
    }

    public void deleteData(String type) throws YssException {
        String deleteSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
        	//====add by xxm,2010.01.29.MS00900 建议增加跨区间确认与反确认净值表的功能
        	deleteSql = "delete from " + pub.yssGetTableName("tb_data_NavData") +
            " where FPortCode = " + dbl.sqlString(this.portCode);
        	if(this.dEndDate!=null){//此时需要根据区间来反确认净值表
        		deleteSql += " and FNAVDate between "+ dbl.sqlDate(this.dDate) +" and "+ dbl.sqlDate(this.dEndDate);
        	}else{//此时只根据单个日期来反确认指定日期的净值表
        		deleteSql += " and FNAVDate = "+ dbl.sqlDate(this.dDate);
        	}
        	deleteSql += " and FReTypeCode in (" + this.operSql.sqlCodes(type) + ")" + //sj edit "in" 为了在合计中能连头寸一起删除 20080530
            
        	/**Start 20130619 modified by liubo.Bug #8298.QDV4兴业银行2013年06月17日01_B*/
        	/**修改默认状态下投资经理的删除条件*/
//        	(this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " ");
        	
        	(this.invMgrCode.length() > 0 ? 
        			" and (FInvMgrCode = " + dbl.sqlString(this.invMgrCode) + " or FInvMgrCode = ' ') " 
        			: " FInvMgrCode = ' ')");
        	/**End 20130619 modified by liubo.Bug #8298.QDV4兴业银行2013年06月17日01_B*/
        	
            //=============end====================================================
//            deleteSql = "delete from " + pub.yssGetTableName("tb_data_NavData") +
//                " where FNAVDate = " + dbl.sqlDate(this.dDate) +
//                " and FPortCode = " + dbl.sqlString(this.portCode) +
//                " and FReTypeCode in (" + this.operSql.sqlCodes(type) + ")" + //sj edit "in" 为了在合计中能连头寸一起删除 20080530
//                (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " ");
//            
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(deleteSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            //throw new YssException("系统执行删除" + YssFun.formatDate(dbl.sqlDate(this.dDate)) +(this.dEndDate!=null?"到："+YssFun.formatDate(dbl.sqlDate(this.dEndDate)):" ")//MS00900
			throw new YssException("系统执行删除" + YssFun.formatDate(dbl.sqlDate(this.dDate)) +(this.dEndDate!=null?"到："+YssFun.formatDate(this.dEndDate):" ")//MS00900 调整日期 合并太平版本代码
            		+ "净值统计数据时出现异常!" + "\n", e); //by 曹丞 2009.02.01 删除净值统计数据异常信息 MS00004 QDV4.1-2009.2.1_09A
        }
    }
    /**
     * 删除数据
     *  20091013 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * @param type
     * @throws YssException
     */
    public void deleteETFData(String type) throws YssException {
        String deleteSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {

            deleteSql = "delete from " + pub.yssGetTableName("Tb_ETF_NavData") +
                " where FNAVDate = " + dbl.sqlDate(this.dDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " and FReTypeCode in (" + this.operSql.sqlCodes(type) + ")" +
                (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " ");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(deleteSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
	       //throw new YssException("系统执行删除"+YssFun.formatDate(dbl.sqlDate(this.dDate))+"净值统计数据时出现异常!"+"\n",e);//by 曹丞 2009.02.01 删除净值统计数据异常信息 MS00004 QDV4.1-2009.2.1_09A
	       throw new YssException("系统执行删除"+YssFun.formatDate(this.dDate,"yyyy-MM-dd")+"净值统计数据时出现异常!"+"\n",e);//系统并行处理优化，这里改正一个错误 by leeyu 20100601 合并太平版本代码
        }
    }

    public void insertTable(ArrayList arr) throws YssException {
        String insertSql = "";
        java.sql.Connection con = dbl.loadConnection();
        java.sql.PreparedStatement ptmt = null;
        NavRepBean navRep = null;
        try {
            insertSql = "insert into " + pub.yssGetTableName("tb_data_NavData") + "(FNAVDate" +
                ",FPortCode" +
                ",FKeyCode" +
                ",FKeyName" +
                ",FOrderCode" +
                ",FDetail" +
                ",FReTypeCode" +
                ",FCuryCode" +
                ",FPrice" +
                ",FOTPrice1" +
                ",FOTPrice2" +
                ",FOTPrice3" +
                ",FSEDOLCode" +
                ",FISINCode" +
                ",FSParAmt" +
                ",FBaseCuryRate" +
                ",FPortCuryRate" +
                ",FCost" +
                ",FPortCost" +
                ",FMarketValue" +
                ",FPortMarketValue" +
                ",FMVValue" +
                ",FPortMVValue" +
                ",FFXValue" +
                //------------------------------
                ",FInvMgrCode" +
                //------------------------------
                ",FGradeType1" +
                ",FGradeType2" +
                ",FGradeType3" +
                ",FGradeType4" +
                ",FGradeType5" +
                ",FGradeType6" +
                //MS00570 QDV4华安2009年07月16日01_AB sj --//
                ",FUnitCost" + //原币单位成本
                ",FChangeWithCost" + //原币涨跌
                //---------------------------------------//
                //=====by xuxuming,20090818.MS00637 QDV4华安2009年08月14日01_AB==
                ",FPortUnitCost"+//组合货币单位成本
                ",FPortChangeWithCost"+//组合货币涨跌
                //==============================================================
                ",FInOut" +
                ", FInvestType)" + //modify by fangjiang 2011.07.23 story 1176
                " values(?,?,?,?,?,?,?,?" +
                ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
                ",?,?,?,?,?,?,?,?,?,?,?,?)"; //modify by fangjiang 2011.07.23 story 1176
            ptmt = con.prepareStatement(insertSql);
            for (int i = 0; i < arr.size(); i++) {
                navRep = (NavRepBean) arr.get(i);
                if (navRep != null) {
                    ptmt.setDate(1, YssFun.toSqlDate(navRep.getNavDate()));
                    ptmt.setString(2, navRep.getPortCode());
                    ptmt.setString(3, navRep.getKeyCode());
                    ptmt.setString(4, navRep.getKeyName());
                    ptmt.setString(5, navRep.getOrderKeyCode());
                    ptmt.setDouble(6, navRep.getDetail());
                    ptmt.setString(7, navRep.getReTypeCode());
                    ptmt.setString(8, navRep.getCuryCode());
                    ptmt.setDouble(9, YssD.round(navRep.getPrice(), 12));
                    ptmt.setDouble(10, YssD.round(navRep.getOtPrice1(), 12));
                    ptmt.setDouble(11, YssD.round(navRep.getOtPrice2(), 12));
                    ptmt.setDouble(12, YssD.round(navRep.getOtPrice3(), 12));
                    ptmt.setString(13, navRep.getSedolCode());
                    ptmt.setString(14, navRep.getIsinCode());
                    //ptmt.setDouble(15, YssD.round(navRep.getSparAmt(), 4));
                    // 599 QDV4银华2011年02月15日05_A edit by qiuxufeng 20110317 录入交易数据的数量增加可保留至小数点后6位
                    ptmt.setDouble(15, YssD.round(navRep.getSparAmt(), 6));
                    ptmt.setDouble(16, navRep.getBaseCuryRate());
                    ptmt.setDouble(17, navRep.getPortCuryRate());
                    ptmt.setDouble(18, YssD.round(navRep.getBookCost(), 4));
                    ptmt.setDouble(19, YssD.round(navRep.getPortBookCost(), 4));
                    ptmt.setDouble(20, YssD.round(navRep.getMarketValue(), 4));
                    ptmt.setDouble(21, YssD.round(navRep.getPortMarketValue(), 4));
                    ptmt.setDouble(22, YssD.round(navRep.getPayValue(), 4));
                    ptmt.setDouble(23, YssD.round(navRep.getPortPayValue(), 4));
                    ptmt.setDouble(24, YssD.round(navRep.getPortexchangeValue(), 4));
                    ptmt.setString(25, navRep.getInvMgrCode().length() > 0 ? navRep.getInvMgrCode() : " ");
                    ptmt.setString(26, navRep.getGradeType1());
                    ptmt.setString(27, navRep.getGradeType2());
                    ptmt.setString(28, navRep.getGradeType3());
                    ptmt.setString(29, navRep.getGradeType4());
                    ptmt.setString(30, navRep.getGradeType5());
                    ptmt.setString(31, navRep.getGradeType6());
                    //MS00570 QDV4华安2009年07月16日01_AB sj --//
                    ptmt.setDouble(32,navRep.getUnitCost());//原币单位成本
                    ptmt.setDouble(33,navRep.getChangeWithCost());//原币涨跌
                    //=====by xuxuming,20090818.MS00637 QDV4华安2009年08月14日01_AB==
                    ptmt.setDouble(34,navRep.getPortUnitCost());//组合货币单位成本
                    ptmt.setDouble(35,navRep.getPortChangeWithCost());//组合货币涨跌
                    //=============================================================

                    ptmt.setDouble(36, navRep.getInOut()); //modify by fangjiang 2011.07.23 story 1176
                    //---------------------------------------//
                    ptmt.setString(37, navRep.getInvestType());
                    ptmt.executeUpdate();

                }
            }
            con.commit();
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("系统保存净值统计数据时出现异常!" + "\n", e); //by 曹丞 2009.02.01 保存净值统计数据异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeStatementFinal(ptmt);
        }
    }
    /**
     * 插入数据
     * 20091013 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * @param arr
     * @throws YssException
     */
    public void insertETFTable(ArrayList arr) throws YssException {
        String insertSql = "";
        java.sql.Connection con = dbl.loadConnection();
        java.sql.PreparedStatement ptmt = null;
        NavRepBean navRep = null;
        try {
            insertSql = "insert into " + pub.yssGetTableName("Tb_ETF_NavData") + "(FNAVDate" +
                ",FPortCode" +
                ",FKeyCode" +
                ",FKeyName" +
                ",FOrderCode" +
                ",FDetail" +
                ",FReTypeCode" +
                ",FCuryCode" +
                ",FPrice" +
                ",FOTPrice1" +
                ",FOTPrice2" +
                ",FOTPrice3" +
                ",FSEDOLCode" +
                ",FISINCode" +
                ",FSParAmt" +
                ",FBaseCuryRate" +
                ",FPortCuryRate" +
                ",FCost" +
                ",FPortCost" +
                ",FMarketValue" +
                ",FPortMarketValue" +
                ",FMVValue" +
                ",FPortMVValue" +
                ",FFXValue" +
                //------------------------------
                ",FInvMgrCode" +
                //------------------------------
                ",FGradeType1" +
                ",FGradeType2" +
                ",FGradeType3" +
                ",FGradeType4" +
                ",FGradeType5" +
                ",FGradeType6" +
                //MS00570 QDV4华安2009年07月16日01_AB sj --//
                ",FUnitCost" + //原币单位成本
                ",FChangeWithCost" + //原币涨跌
                //---------------------------------------//
                //=====by xuxuming,20090818.MS00637 QDV4华安2009年08月14日01_AB==
                ",FPortUnitCost"+//组合货币单位成本
                ",FPortChangeWithCost"+//组合货币涨跌
                //==============================================================
                ",FInOut)" +
                " values(?,?,?,?,?,?,?,?" +
                ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
                ",?,?,?,?,?,?,?,?,?,?,?)";
            ptmt = con.prepareStatement(insertSql);
            for (int i = 0; i < arr.size(); i++) {
                navRep = (NavRepBean) arr.get(i);
                if (navRep != null) {
                    ptmt.setDate(1, YssFun.toSqlDate(navRep.getNavDate()));
                    ptmt.setString(2, navRep.getPortCode());
                    ptmt.setString(3, navRep.getKeyCode());
                    ptmt.setString(4, navRep.getKeyName());
                    ptmt.setString(5, navRep.getOrderKeyCode());
                    ptmt.setDouble(6, navRep.getDetail());
                    ptmt.setString(7, navRep.getReTypeCode());
                    ptmt.setString(8, navRep.getCuryCode());
                    ptmt.setDouble(9, YssD.round(navRep.getPrice(), 12));
                    ptmt.setDouble(10, YssD.round(navRep.getOtPrice1(), 12));
                    ptmt.setDouble(11, YssD.round(navRep.getOtPrice2(), 12));
                    ptmt.setDouble(12, YssD.round(navRep.getOtPrice3(), 12));
                    ptmt.setString(13, navRep.getSedolCode());
                    ptmt.setString(14, navRep.getIsinCode());
                    ptmt.setDouble(15, YssD.round(navRep.getSparAmt(), 4));
                    ptmt.setDouble(16, navRep.getBaseCuryRate());
                    ptmt.setDouble(17, navRep.getPortCuryRate());
                    ptmt.setDouble(18, YssD.round(navRep.getBookCost(), 4));
                    ptmt.setDouble(19, YssD.round(navRep.getPortBookCost(), 4));
                    ptmt.setDouble(20, YssD.round(navRep.getMarketValue(), 4));
                    ptmt.setDouble(21, YssD.round(navRep.getPortMarketValue(), 4));
                    ptmt.setDouble(22, YssD.round(navRep.getPayValue(), 4));
                    ptmt.setDouble(23, YssD.round(navRep.getPortPayValue(), 4));
                    ptmt.setDouble(24, YssD.round(navRep.getPortexchangeValue(), 4));
                    ptmt.setString(25, navRep.getInvMgrCode().length() > 0 ? navRep.getInvMgrCode() : " ");
                    ptmt.setString(26, navRep.getGradeType1());
                    ptmt.setString(27, navRep.getGradeType2());
                    ptmt.setString(28, navRep.getGradeType3());
                    ptmt.setString(29, navRep.getGradeType4());
                    ptmt.setString(30, navRep.getGradeType5());
                    ptmt.setString(31, navRep.getGradeType6());
                    //MS00570 QDV4华安2009年07月16日01_AB sj --//
                    ptmt.setDouble(32,navRep.getUnitCost());//原币单位成本
                    ptmt.setDouble(33,navRep.getChangeWithCost());//原币涨跌
                    //=====by xuxuming,20090818.MS00637 QDV4华安2009年08月14日01_AB==
                    ptmt.setDouble(34,navRep.getPortUnitCost());//组合货币单位成本
                    ptmt.setDouble(35,navRep.getPortChangeWithCost());//组合货币涨跌
                    //=============================================================

                    ptmt.setDouble(36, navRep.getInOut());
                    //---------------------------------------//
                    ptmt.executeUpdate();

                }
            }
            con.commit();
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("系统保存ETF净值统计数据时出现异常!" + "\n", e);
        } finally {
            dbl.closeStatementFinal(ptmt);
        }
    }

    public void buildLeftSql(ArrayList leftArr, ArrayList fieldsArr) throws YssException {

    }

    public String setBlo(int Grade) throws YssException {
        String reStr = "";
        String initStr = "  "; //两个空格
        try {
            for (int i = 0; i < Grade; i++) {
                reStr += initStr;
            }
            if (reStr.length() > 0) {
                reStr = "." + reStr;
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e);
        }
    }
    
    /**add by zhouxiang 2010.12.27  抵押物净值表，删除操作
     * @param type
     * @throws YssException
     */
    public void deleteMortGageData(String type) throws YssException {
        String deleteSql = "";
        Connection conn = dbl.loadConnection();
        try {
      
        	deleteSql = "delete from " + pub.yssGetTableName("tb_data_navmortgdate") +
            " where FPortCode = " + dbl.sqlString(this.portCode);
        	if(this.dEndDate!=null){
        		deleteSql += " and FNAVDate between "+ dbl.sqlDate(this.dDate) +" and "+ dbl.sqlDate(this.dEndDate);
        	}else{
        		deleteSql += " and FNAVDate = "+ dbl.sqlDate(this.dDate);
        	}
        	deleteSql += " and FReTypeCode in (" + this.operSql.sqlCodes(type) + ")" + 
            (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " ");
        	conn.setAutoCommit(false);
            dbl.executeSql(deleteSql);
            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception e) {
           throw new YssException("系统执行删除" + YssFun.formatDate(dbl.sqlDate(this.dDate)) +(this.dEndDate!=null?"到："+YssFun.formatDate(this.dEndDate):" ")
            		+ "抵押物净值统计数据时出现异常!" + "\n", e);
        }
    }
    /**插入抵押物净值表
     * @param arr add by zhouxiang 2010.12.27 
     * @throws YssException
     */
    public void insertMortGageTable(ArrayList arr) throws YssException {
        String insertSql = "";
        java.sql.Connection con = dbl.loadConnection();
        java.sql.PreparedStatement ptmt = null;
        NavRepBean navRep = null;
        try {
            insertSql = "insert into " + pub.yssGetTableName("tb_data_navmortgdate") + "(FNAVDate" +
                ",FPortCode" +
                ",FKeyCode" +
                ",FKeyName" +
                ",FOrderCode" +
                ",FDetail" +
                ",FReTypeCode" +
                ",FCuryCode" +
                ",FPrice" +
                ",FOTPrice1" +
                ",FOTPrice2" +
                ",FOTPrice3" +
                ",FSEDOLCode" +
                ",FISINCode" +
                ",FSParAmt" +
                ",FBaseCuryRate" +
                ",FPortCuryRate" +
                ",FCost" +
                ",FPortCost" +
                ",FMarketValue" +
                ",FPortMarketValue" +
                ",FMVValue" +
                ",FPortMVValue" +
                ",FFXValue" +
                //------------------------------
                ",FInvMgrCode" +
                //------------------------------
                ",FGradeType1" +
                ",FGradeType2" +
                ",FGradeType3" +
                ",FGradeType4" +
                ",FGradeType5" +
                ",FGradeType6" +
              
                ",FUnitCost" + //原币单位成本
                ",FChangeWithCost" + //原币涨跌
                //---------------------------------------//
               
                ",FPortUnitCost"+//组合货币单位成本
                ",FPortChangeWithCost"+//组合货币涨跌
                //==============================================================
                ",FInOut)" +
                " values(?,?,?,?,?,?,?,?" +
                ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
                ",?,?,?,?,?,?,?,?,?,?,?)";
            ptmt = con.prepareStatement(insertSql);
            for (int i = 0; i < arr.size(); i++) {
                navRep = (NavRepBean) arr.get(i);
                if (navRep != null) {
                    ptmt.setDate(1, YssFun.toSqlDate(navRep.getNavDate()));
                    ptmt.setString(2, navRep.getPortCode());
                    ptmt.setString(3, navRep.getKeyCode());
                    ptmt.setString(4, navRep.getKeyName());
                    ptmt.setString(5, navRep.getOrderKeyCode());
                    ptmt.setDouble(6, navRep.getDetail());
                    ptmt.setString(7, navRep.getReTypeCode());
                    ptmt.setString(8, navRep.getCuryCode());
                    ptmt.setDouble(9, YssD.round(navRep.getPrice(), 12));
                    ptmt.setDouble(10, YssD.round(navRep.getOtPrice1(), 12));
                    ptmt.setDouble(11, YssD.round(navRep.getOtPrice2(), 12));
                    ptmt.setDouble(12, YssD.round(navRep.getOtPrice3(), 12));
                    ptmt.setString(13, navRep.getSedolCode());
                    ptmt.setString(14, navRep.getIsinCode());
                    ptmt.setDouble(15, YssD.round(navRep.getSparAmt(), 4));
                    ptmt.setDouble(16, navRep.getBaseCuryRate());
                    ptmt.setDouble(17, navRep.getPortCuryRate());
                    ptmt.setDouble(18, YssD.round(navRep.getBookCost(), 4));
                    ptmt.setDouble(19, YssD.round(navRep.getPortBookCost(), 4));
                    ptmt.setDouble(20, YssD.round(navRep.getMarketValue(), 4));
                    ptmt.setDouble(21, YssD.round(navRep.getPortMarketValue(), 4));
                    ptmt.setDouble(22, YssD.round(navRep.getPayValue(), 4));
                    ptmt.setDouble(23, YssD.round(navRep.getPortPayValue(), 4));
                    ptmt.setDouble(24, YssD.round(navRep.getPortexchangeValue(), 4));
                    ptmt.setString(25, navRep.getInvMgrCode().length() > 0 ? navRep.getInvMgrCode() : " ");
                    ptmt.setString(26, navRep.getGradeType1());
                    ptmt.setString(27, navRep.getGradeType2());
                    ptmt.setString(28, navRep.getGradeType3());
                    ptmt.setString(29, navRep.getGradeType4());
                    ptmt.setString(30, navRep.getGradeType5());
                    ptmt.setString(31, navRep.getGradeType6());
                  
                    ptmt.setDouble(32,navRep.getUnitCost());//原币单位成本
                    ptmt.setDouble(33,navRep.getChangeWithCost());//原币涨跌
              
                    ptmt.setDouble(34,navRep.getPortUnitCost());//组合货币单位成本
                    ptmt.setDouble(35,navRep.getPortChangeWithCost());//组合货币涨跌
                    //=============================================================

                    ptmt.setDouble(36, navRep.getInOut());
                    //---------------------------------------//
                    ptmt.executeUpdate();

                }
            }
            con.commit();
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("系统保存抵押物净值统计数据时出现异常!" + "\n", e); 
        } finally {
            dbl.closeStatementFinal(ptmt);
        }
    }

}
