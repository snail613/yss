package com.yss.manager;

import com.yss.dsub.*;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.*;
import java.sql.*;
import java.util.*;

public class SecRecPayAdmin
    extends BaseBean {
    ArrayList addList = new ArrayList();
    String insertNum = "";
    String inComeNum = ""; //获取应收应付类型为收入的编号。sj 20081222 MS00114
    public SecRecPayAdmin() {
    }

    public void addList(SecPecPayBean secpecpay) {
        this.addList.add(secpecpay);
    }

    public void addList(ArrayList list) {
        this.addList = list;
    }

    public String getInsertNum() {
        return insertNum;
    }

    public String getIncomeNum() { //获取收入类的编号 sj 20081222 MS00114
        return inComeNum;
    }

    public ArrayList getList() {
        return addList;
    }

    //根据条件审核证券应收应付 by sunny
    public void checkSecRecPay(String sNums, java.util.Date beginDate,
                               java.util.Date endDate,
                               String sTsfTypeCode, String sSubTsfTypeCode,
                               String sSecurityCode, String sCuryCode,
                               String sPortCode, String sAnalysisCode1,
                               String sAnalysisCode2, String sAnalysisCode3,
                               int iDsInd, int checkStateId
        ) throws YssException {
        String strSql = "";
        try {

            strSql = "update  " + pub.yssGetTableName("tb_data_secrecpay") +
                " set FCheckState=" + checkStateId +
                this.buildWhereSql(sNums, beginDate, endDate, sTsfTypeCode,
                                   sSubTsfTypeCode, sSecurityCode,
                                   sCuryCode, sPortCode, sAnalysisCode1,
                                   sAnalysisCode2, sAnalysisCode3,
                                   iDsInd, 0, "", "", ""); //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---

            dbl.executeSql(strSql);

        } catch (Exception e) {
            throw new YssException("审核证券应收应付信息出错", e);
        } finally {
            // dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 根据传入的条件生成sql语句的where条件，如果条件全部不符合则生成where 1=1
     * modify by xuqiji 20090505
     * MS00429 : QDV4赢时胜（上海）2009年4月30日02_B 通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误
     * 新增参数exitNum，生成not in 语句
     * @param sNums String   包含的应收应付编号
     * @param beginDate Date 业务开始日期
     * @param endDate Date   业务结束日期
     * @param sTsfTypeCode String    业务类型
     * @param sSubTsfTypeCode String 业务子类型
     * @param sSecurityCode String   证券代码
     * @param sCuryCode String       币种代码
     * @param sPortCode String       组合代码
     * @param sAnalysisCode1 String  分析代码1
     * @param sAnalysisCode2 String  分析代码2
     * @param sAnalysisCode3 String  分析代码3
     * @param iDsInd int             来源状态
     * @param inout int              流入流出方向
     * @param exitNum String         不包含的应收应付编号
     * @param sRelaNum String        关联编号 2009.06.29 蒋锦 添加 关联编号 关联编号类型 MS00013   QDV4.1赢时胜（上海）2009年4月20日13_A
     * @param sRelaType String    关联类型
     * @return String                生成的where条件语句
     */
    public String buildWhereSql(String sNums, java.util.Date beginDate,
                                java.util.Date endDate,
                                String sTsfTypeCode, String sSubTsfTypeCode,
                                String sSecurityCode, String sCuryCode,
                                String sPortCode, String sAnalysisCode1,
                                String sAnalysisCode2, String sAnalysisCode3,
                                int iDsInd, int inout, String exitNum, String sRelaNums, String sRelaTypes) throws YssException
    {
        //edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B FDataOrigin = 0 表示为自动生成的数据
    	String sResult = " where 1=1 and FDataOrigin = 0 ";
        if (sNums != null && sNums.length() > 0) {
        	/**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
        	* 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//            sResult += " and FNum in(" + operSql.sqlCodes(sNums) + ")";
        	sResult += " and (" + operSql.getNumsDetail(sNums,"FNum",500) + ")";
        	/**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
        }
        if (beginDate != null && endDate != null) {
            sResult += " and (FTransDate between " + dbl.sqlDate(beginDate) +
                " and " + dbl.sqlDate(endDate) + ")";
        }
        if (sTsfTypeCode != null && sTsfTypeCode.length() > 0) {
            if (sTsfTypeCode.indexOf(",") > 0) {
                sResult += " and FTsfTypeCode in (" + operSql.sqlCodes(sTsfTypeCode) +
                    ")";
            } else {
                sResult += " and FTsfTypeCode = " + dbl.sqlString(sTsfTypeCode);
            }
        }
        if (sSubTsfTypeCode != null && sSubTsfTypeCode.length() > 0) {
            if (sSubTsfTypeCode.indexOf(",") > 0) {
                sResult += " and FSubTsfTypeCode in (" +
                    operSql.sqlCodes(sSubTsfTypeCode) + ")";
            } else if (sSubTsfTypeCode.indexOf("%") > 0) { //加入like的处理 20070918 胡昆
                sResult += " and FSubTsfTypeCode like " +
                    dbl.sqlString(sSubTsfTypeCode);
            } else {
                sResult += " and FSubTsfTypeCode = " +
                    dbl.sqlString(sSubTsfTypeCode);
            }
        }
        if (iDsInd > -1) {
            sResult += " and FDataSource = " + iDsInd;
        }
        //edit by jc  控制删除流入或流出方向的数据
        if (inout != 0) {
            sResult += " and FInOut = " + inout;
        }
        //-------------------jc
      //==========add by xuxuming,20091231.增加删除条件，排除非估值时自动生成的数据===========
      //==MS00902    估值增值和汇兑损益未转到新的库存中去    QDV4国泰2010年1月5日01_B    =========
      sResult += " and FStockInd <> '9'";//在估值之前添加的应收应付数据，标识为9,此处不删除它们
      //================end=================================
        if (sSecurityCode != null && sSecurityCode.length() > 0) {
        	/**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
        	* 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//            sResult += " and FSecurityCode in (" + operSql.sqlCodes(sSecurityCode) + ")";
        	sResult += " and (" + operSql.getNumsDetail(sSecurityCode,"FSecurityCode",500) + ")" ;
        	/**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
        }
        if (sCuryCode != null && sCuryCode.length() > 0) {
            sResult += " and FCuryCode = " + dbl.sqlString(sCuryCode);
        }
        if (sPortCode != null && sPortCode.length() > 0) {
            sResult += " and FPortCode in (" +
                operSql.sqlCodes(sPortCode.replaceAll("'", "")) + ")";
        }
        //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
        // 传入得分析代码可能有多个
        if (sAnalysisCode1 != null && sAnalysisCode1.length() > 0) {
            sResult += " and FAnalysisCode1 in (" + operSql.sqlCodes(sAnalysisCode1) + ")";
        }
        if (sAnalysisCode2 != null && sAnalysisCode2.length() > 0) {
            sResult += " and FAnalysisCode2 in (" + operSql.sqlCodes(sAnalysisCode2) + ")";
        }
        //------------------------------end------------------------------------------------------------------------------------//

        if (sAnalysisCode3 != null && sAnalysisCode3.length() > 0) {
            sResult += " and FAnalysisCode3 = " + dbl.sqlString(sAnalysisCode3);
        }
        //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429
        //如果传入的编号不为空，则不对符合传入条件的应收应付进行更新
        if (null != exitNum && exitNum.length() > 0) {
            sResult += " and FNum not in (" + operSql.sqlCodes(exitNum) + ")";
        }
        //======================End MS00429===============================
        //2009.06.29 蒋锦 添加 MS00013   QDV4.1赢时胜（上海）2009年4月20日13_A
        //当输入的关联编号和关联编号类型都为空时查询这两字段位空的数据删除，
        //否则将他们所谓查询条件代入
        if((sRelaTypes == null || sRelaTypes.length() == 0) &&
           (sRelaNums == null || sRelaNums.length() == 0)){
            sResult += " and (FRelaType is null or FRelaType = '') and (FRelaNum is null or FRelaNum = '')";
        } else {
            if (!(sRelaTypes == null || sRelaTypes.length() == 0)) {
                sResult += " and FRelaType in (" + operSql.sqlCodes(sRelaTypes) + ")";
            }
            if (!(sRelaNums == null || sRelaNums.length() == 0)) {
                sResult += " and FRelaNum in (" + operSql.sqlCodes(sRelaNums) + ")";
            }
        }
        return sResult;
    }

    public String loadSecPRNums(java.util.Date transferDate,
                                String sTsfTypeCode, String sSubTsfTypeCode,
                                String sSecurityCode,
                                String sPortCode, String sAnalysisCode1,
                                String sAnalysisCode2) throws YssException {
        return loadSecPRNums("", transferDate, sTsfTypeCode, sSubTsfTypeCode,
                             sSecurityCode, "",
                             sPortCode, sAnalysisCode1, sAnalysisCode2, "", -99); //MS00275 QDV4中保2009年02月27日01_B 将数据来源设置为-99的目的是在获取时不论是手工还是自动，一并获取。
    }

    public String loadSecPRNums(String sNums, java.util.Date transferDate,
                                String sTsfTypeCode, String sSubTsfTypeCode,
                                String sSecurityCode, String sCuryCode,
                                String sPortCode, String sAnalysisCode1,
                                String sAnalysisCode2, String sAnalysisCode3,
                                int iDsInd) throws YssException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select FNum from " +
                pub.yssGetTableName("tb_data_secrecpay") +
                this.buildWhereSql(sNums, transferDate, transferDate,
                                   sTsfTypeCode, sSubTsfTypeCode, sSecurityCode,
                                   sCuryCode, sPortCode, sAnalysisCode1,
                                   sAnalysisCode2, sAnalysisCode3,
                                   iDsInd, 0, "", "", ""); //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult += rs.getString("FNum") + ",";
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 删除方法的重载
     * @param sNums String 交易编号
     * @param beginDate Date 开始日期
     * @param endDate Date 结束日期
     * @param sTsfTypeCode String 调拨类型
     * @param sSubTsfTypeCode String 调拨子类型
     * @param sSecurityCode String 证券代码
     * @param sCuryCode String 基础货币代码
     * @param sPortCode String 组合
     * @param sAnalysisCode1 String 分析代码1
     * @param sAnalysisCode2 String 分析代码2
     * @param sAnalysisCode3 String 分析代码3
     * @param iDsInd int 数据源状态
     * @param inout int 流入或流出
     * @throws YssException 异常
     * add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
     */
    public void delete(String sNums, java.util.Date beginDate,
                       java.util.Date endDate,
                       String sTsfTypeCode, String sSubTsfTypeCode,
                       String sSecurityCode, String sCuryCode,
                       String sPortCode, String sAnalysisCode1,
                       String sAnalysisCode2, String sAnalysisCode3,
                       int iDsInd, int inout) throws YssException {
        delete(sNums, beginDate, endDate,
               sTsfTypeCode, sSubTsfTypeCode,
               sSecurityCode, sCuryCode,
               sPortCode, sAnalysisCode1,
               sAnalysisCode2, sAnalysisCode3,
               iDsInd, inout, "", "", "");
    }

//------------------------------------end --------------------------------------------------------------------------------------//
    //edit by jc 重载一个delete方法，添加参数inout
    /**
     *
     * @param sNums String
     * @param beginDate Date
     * @param endDate Date
     * @param sTsfTypeCode String
     * @param sSubTsfTypeCode String
     * @param sSecurityCode String
     * @param sCuryCode String
     * @param sPortCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @param iDsInd int
     * @param inout int
     * @param exitNum String
     * @param sRelaNum String     关联编号 2009.06.29 蒋锦 添加 关联编号 关联编号类型 MS00013   QDV4.1赢时胜（上海）2009年4月20日13_A
     * @param sRelaType String    关联类型
     * @throws YssException
     */
    public void delete(String sNums, java.util.Date beginDate,
                       java.util.Date endDate,
                       String sTsfTypeCode, String sSubTsfTypeCode,
                       String sSecurityCode, String sCuryCode,
                       String sPortCode, String sAnalysisCode1,
                       String sAnalysisCode2, String sAnalysisCode3,
                       int iDsInd, int inout, String exitNum, String sRelaNums, String sRelaTypes) throws //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
        YssException {
        String strSql = "";
        try {
            strSql = "delete from " +
                pub.yssGetTableName("tb_data_secrecpay") +
                this.buildWhereSql(sNums, beginDate, endDate, sTsfTypeCode,
                                   sSubTsfTypeCode, sSecurityCode,
                                   sCuryCode, sPortCode, sAnalysisCode1,
                                   sAnalysisCode2, sAnalysisCode3,
                                   iDsInd, inout, exitNum, sRelaNums, sRelaTypes); //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    public void delete(String sNums, java.util.Date beginDate,
                       java.util.Date endDate,
                       String sTsfTypeCode, String sSubTsfTypeCode,
                       String sSecurityCode, String sCuryCode,
                       String sPortCode, String sAnalysisCode1,
                       String sAnalysisCode2, String sAnalysisCode3,
                       int iDsInd) throws
        YssException {
        delete(sNums, beginDate, endDate, sTsfTypeCode, sSubTsfTypeCode,
               sSecurityCode, sCuryCode, sPortCode, sAnalysisCode1,
               sAnalysisCode2, sAnalysisCode3, iDsInd, 0);
    }

    //----------------------jc

    public void delete(String sNums) throws YssException {
        delete(sNums, null, null, "", "", "", "", "", "", "", "", -1);
    }

   public void insert(java.util.Date transDate, String tsfType,
                      String subTsfType, String port, String invMgr,
                      String broker, String security, String cury,
                      int datasource) throws YssException {
      insert("", transDate, transDate, tsfType, subTsfType, port, invMgr,
             broker, security, cury,
             datasource,true,0,false);
   }
    //xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持
    public void insert(java.util.Date transDate, String tsfType,
                       String subTsfType, String port, String invMgr,
                       String broker, String security, String cury,int inout,
                       int datasource) throws YssException {
        insert("", transDate, transDate, tsfType, subTsfType, port, invMgr,
               broker, security, cury,
               datasource, true, inout, false);
    }
    //--------------------------------------end-----------------------------------//

    public void insert(java.util.Date beginDate, java.util.Date endDate,
                       String tsfType,
                       String subTsfType, String port, String invMgr,
                       String broker, String security, String cury,
                       int datasource) throws YssException {
        insert("", beginDate, beginDate, tsfType, subTsfType, port, invMgr,
               broker, security, cury,
               datasource, true, 0, false);
    }

//------------------------------------------------------------------------------
   /**合并太平版本代码
    * 重载insert方法，添加证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
    */
   	public void insert(java.util.Date beginDate, java.util.Date endDate,
          String tsfType,String subTsfType, String port,
          int datasource,boolean bdigit4,String security) throws YssException{
		  insert("",beginDate, beginDate, tsfType, subTsfType, port, "",
		   "", security, "",datasource,true,0,bdigit4);
   	}
   	/**
     * 重载的方法，用于对小数位数进行判断
     * @param beginDate Date
     * @param endDate Date
     * @param tsfType String
     * @param subTsfType String
     * @param port String
     * @param datasource int
     * @param bdigit4 boolean
     * @throws YssException
     * MS00269 QDV4中保2009年02月24日02_B
     */
    public void insert(java.util.Date beginDate, java.util.Date endDate,
                       String tsfType, String subTsfType, String port,
                       int datasource, boolean bdigit4) throws YssException {
        insert("", beginDate, beginDate, tsfType, subTsfType, port, "",
               "", "", "", datasource, true, 0, bdigit4);
    }

//------------------------------------------------------------------------------

    public void insert(java.util.Date beginDate, java.util.Date endDate,
                       String tsfType, String subTsfType, String port,
                       int datasource) throws YssException {
        insert("", beginDate, beginDate, tsfType, subTsfType, port, "",
               "", "", "", datasource, true, 0, false);
    }

    public void insert(java.util.Date beginDate, java.util.Date endDate,
                       String tsfType, String subTsfType, String port,
                       int datasource, int inout) throws YssException {
        insert("", beginDate, endDate, tsfType, subTsfType, port, "",
               "", "", "", datasource, true, inout, false);
    }

    public void insert(String sNums, java.util.Date beginDate,
                       java.util.Date endDate, String tsfType,
                       String subTsfType, String port, String invMgr,
                       String broker, String security, String cury,
                       int datasource, boolean bAutoDel) throws
        YssException {
        insert(sNums, beginDate, endDate, tsfType, subTsfType, port, invMgr,
               broker, security, cury, datasource, bAutoDel, 1, false);
    }

    /**
     * 重载方法做插入证券应收应付数据入库方法
     * @param sNums String 交易编号
     * @param beginDate Date 起始日期
     * @param endDate Date 结束日期
     * @param tsfType String 调拨类型
     * @param subTsfType String 调拨子类型
     * @param port String 组合代码
     * @param invMgr String 投资经理代码
     * @param broker String 券商代码
     * @param security String 证券代码
     * @param cury String  基础货币代码
     * @param datasource int  来源
     * @param bAutoDel boolean 做拼接SQL语句的条件
     * @param inout int 流入或流出
     * @param bdigit4 boolean 布尔值
     * @throws YssException 异常
     * add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误
     */
    public void insert(String sNums, java.util.Date beginDate,
                       java.util.Date endDate, String tsfType,
                       String subTsfType, String port, String invMgr,
                       String broker, String security, String cury,
                       int datasource, boolean bAutoDel, int inout, boolean bdigit4) throws
        YssException {
        insert(sNums, beginDate,
               endDate, tsfType,
               subTsfType, port, invMgr,
               broker, security, cury,
               datasource, bAutoDel, inout, bdigit4, "", "", "");
    }

//------------------------------------------------end-----------------------------------------------------------------------------------//
    /**
     *
     * @param sNums String
     * @param beginDate Date
     * @param endDate Date
     * @param tsfType String
     * @param subTsfType String
     * @param port String
     * @param invMgr String
     * @param broker String
     * @param security String
     * @param cury String
     * @param datasource int
     * @param bAutoDel boolean
     * @param inout int
     * @param bdigit4 boolean
     * @param exitNum String
     * @param sRelaNum String     关联编号 2009.06.29 蒋锦 添加 关联编号 关联编号类型 MS00013   QDV4.1赢时胜（上海）2009年4月20日13_A
     * @param sRelaType String    关联类型
     * @throws YssException
     */
    public void insert(String sNums, java.util.Date beginDate,
                       java.util.Date endDate, String tsfType,
                       String subTsfType, String port, String invMgr,
                       String broker, String security, String cury,
                       int datasource, boolean bAutoDel, int inout, boolean bdigit4, String exitNum,
                       String sRelaNums, String sRelaTypes) throws //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
        YssException {
	   	synchronized(YssGlobal.objSecRecLock){//添加锁，将此部分锁起来，原因是防止生成重复的编号 by leeyu 20100521
	   		String strSql = "";
	   		SecPecPayBean secpecpay = null;
	        //modified by liubo.Story #1757
	        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
	        //=================================
//	        PreparedStatement pst = null;
	        YssPreparedStatement yssPst = null;
	        //===============end==================
	   		//Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
	   		String sFNum = "";
	   		int i = 0;
	   		int iFNum = 0;
	   		//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
			//HashMap htDiffDate = new HashMap(); // 根据不同的日期 存放 max FNum
	   		//fanghaoln 20100301 MS00808 QDV4建行2009年11月12日01_B
	   		boolean stateBdigit4=bdigit4;//保留原有的小数位数
        
	   		//---MS01021  QDV4南方2010年3月11日01_B 财务估值表里的净值与余额表里的成本加估值增值的和有尾差  add by jiangshichao 2010.03.26-----
	   		CtlPubPara pubpara = null;
	   		pubpara = new CtlPubPara();
	   		pubpara.setYssPub(pub);
	   		String para = pubpara.getSecRecRound();
	   		int digit = para.equalsIgnoreCase("0")?2:4;//默认小数点后保留2位有效数字
	   		//---MS01021  QDV4南方2010年3月11日01_B 财务估值表里的净值与余额表里的成本加估值增值的和有尾差 end----------------------------------
	   		//----------end--------------
	   		try {
            /* strSql = "delete from " + pub.yssGetTableName("Tb_Data_SecRecPay") +
             " where FTransDate between " + dbl.sqlDate(beginDate) + " and " +
                   dbl.sqlDate(endDate) +
                   ( (tsfType == null || tsfType.length() == 0) ? " " :
                    " and FTsfTypeCode = " + dbl.sqlString(tsfType)) +
                   ( (subTsfType == null || subTsfType.length() == 0) ? " " :
                    " and FSubTsfTypeCode in (" +
                    operSql.sqlCodes(subTsfType) + ")") +
                   ( (port == null || port.length() == 0) ? " " :
                    " and FPortCode in (" +
                    operSql.sqlCodes(port) + ")") +
                   ( (invMgr == null || invMgr.length() == 0) ? " " :
                    " and FAnalysisCode1 = " + dbl.sqlString(invMgr)) +
                   ( (broker == null || broker.length() == 0) ? " " :
                    " and FAnalysisCode2 = " +
                    dbl.sqlString(broker)) +
                   ( (security == null || security.length() == 0) ? " " :
                    " and FSecurityCode = " +
                    dbl.sqlString(security)) +
                   ( (cury == null || cury.length() == 0) ? " " :
                    " and FCuryCode = " + dbl.sqlString(cury)) +
                   " and FDataSource = " + datasource;
             dbl.executeSql(strSql); */

            //2009.04.27 蒋锦 添加 为Tb_Data_SecRecPay加锁，避免多用户同时获取最大编号时出现编号重复
            //MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
            //dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_SecRecPay"));//这里不用加锁 合并太平版本调整

            //2009.06.29 蒋锦 添加 关联编号 关联编号类型 MS00013   QDV4.1赢时胜（上海）2009年4月20日13_A 便于删除时作为查询条件
	   			if (bAutoDel) {
	   				delete(sNums, beginDate, endDate, tsfType,
	   						subTsfType, security,
	   						cury, port, invMgr,
	   						broker, "",
	   						datasource, inout, exitNum,
	   						sRelaNums, sRelaTypes); //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误
	   			}

	   			strSql = "insert into " + pub.yssGetTableName("Tb_Data_SecRecPay") +
	   				"(FNum,FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FSecurityCode,FTsfTypeCode" +
	   				",FSubTsfTypeCode,FCuryCode,FMoney,FMMoney,FVMoney,FBaseCuryRate,FBaseCuryMoney,FMBaseCuryMoney,FVBaseCuryMoney" +
	   				",FPortCuryRate,FPortCuryMoney,FMPortCuryMoney,FVPortCuryMoney" +
	   				",FMoneyF,FBaseCuryMoneyF,FPortCuryMoneyF" +//2008.11.13 蒋锦 添加 保留8位小数的原币、基础货币、本位币 编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
	   				",FDataSource,FStockInd,FCatType,FAttrClsCode" + //add 所属分类,品种类型 sj 20071202
	   				//---edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B 添加 FDataOrigin = 0 表示为自动生成 start---//
	   				",FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FInOut,FRelaNum,FRelaType,FINVESTTYPE,FDataOrigin)" + //add FInOut 方向 ly 080213
	   				" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0)";
	   			    //---edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B end---//
//	   			pst = conn.prepareStatement(strSql);
	   			yssPst = dbl.getYssPreparedStatement(strSql);

	   			for (i = 0; i < this.addList.size(); i++) {
	   				secpecpay = (SecPecPayBean) addList.get(i);
            //if (i == 0) {
               //sFNum = "SRP" +
               //      YssFun.formatDatetime(secpecpay.getTransDate()).
               //      substring(0, 8) +
               //      dbFun.getNextInnerCode(pub.yssGetTableName(
               //            "Tb_Data_SecRecPay"),
               //                             dbl.sqlRight("FNUM", 9),
               //                             "000000001",
               //                             " where FTransDate = " +
               //                             dbl.sqlDate(secpecpay.getTransDate()));
               //htDiffDate.put(secpecpay.getTransDate(), sFNum);
            //} //这里只取一次
	   				//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//	   				if (YssGlobal.hmSecRecNums.get(secpecpay.getTransDate()) == null) {
//		               //如果证券的交易日期与上一次的日期不同的话就得重新取一次最大编号 by leeyu 080616
//	   					sFNum = "SRP" +
//		                     	YssFun.formatDatetime(secpecpay.getTransDate()).
//		                     	substring(0, 8) +
//		                     	dbFun.getNextInnerCode(pub.yssGetTableName(
//		                           	"Tb_Data_SecRecPay"),
//		                                            dbl.sqlRight("FNUM", 9),
//		                                            "000000001",
//		                                            " where FTransDate = " +
//		                                            dbl.sqlDate(secpecpay.getTransDate()));
//		               YssGlobal.hmSecRecNums.put(secpecpay.getTransDate(),sFNum);
//	   				}
//	   				sFNum =(String)YssGlobal.hmSecRecNums.get(secpecpay.getTransDate());
	   				//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
            //if (secpecpay.getStrSubTsfTypeCode().equalsIgnoreCase("9906")) {
            //   int iii = 0;
            //}
            //如果金额全部为零时  就不进行保存
            //8-2
	   				if (secpecpay.getMoney() == 0 &&
	   						secpecpay.getBaseCuryMoney() == 0 &&
	   						secpecpay.getPortCuryMoney() == 0) {
	   					continue;
	   				}
	   				//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//	   				if(sFNum.trim().length()>0 && sFNum.length()>11){
//	   					iFNum = YssFun.toInt(YssFun.right(sFNum,9));
//	   					sFNum = YssFun.left(sFNum,11);
//	   					iFNum++;
//	   					sFNum += YssFun.formatNumber(iFNum,"000000000");
//	   					YssGlobal.hmSecRecNums.put(secpecpay.getTransDate(),sFNum);
//	   				}
	   				//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
	   				//add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
	   				sFNum = getKeyNum();
	   				insertNum = sFNum;
	   				if (secpecpay.getStrTsfTypeCode().trim().equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Income)){//若调拨类型为收入的,则将其编号获取.sj MS00114
	   					inComeNum = sFNum;
	   				}
	   				//fanghaoln 20100301 MS00808 QDV4建行2009年11月12日01_B
	   				if(secpecpay.getStrSubTsfTypeCode().equalsIgnoreCase("09OP")){//判断是否是权证
	   					bdigit4=true;//如果的权证的把它改成保留4位
	   				}else{
	   					bdigit4=stateBdigit4;//还原其它的设置
	   				}
	   				//-----------------------------end -MS00808-----------------------------	   			    
	   				yssPst.setString(1, sFNum);	   				
	   				yssPst.setDate(2, YssFun.toSqlDate(secpecpay.getTransDate()));
	   				yssPst.setString(3, secpecpay.getStrPortCode());
	   				yssPst.setString(4, (secpecpay.getInvMgrCode() != null && secpecpay.getInvMgrCode().length() > 0) ? secpecpay.getInvMgrCode() : " "); //modify by fangjiang 2011.05.20 story 845
	   				yssPst.setString(5, (secpecpay.getBrokerCode() != null && secpecpay.getBrokerCode ().length() > 0) ? secpecpay.getBrokerCode () : " "); //modify by fangjiang 2011.05.20 story 845
	   				/** modified by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
	   				//yssPst.setString(6, " ");
	   				yssPst.setString(6,(secpecpay.getCatTypeCode()).length()==0?" ":secpecpay.getCatTypeCode());
	   				/** -----end----- */
	   				yssPst.setString(7, secpecpay.getStrSecurityCode());
	   				yssPst.setString(8, secpecpay.getStrTsfTypeCode());
	   				yssPst.setString(9, secpecpay.getStrSubTsfTypeCode());
	   				yssPst.setString(10, secpecpay.getStrCuryCode());
	   				yssPst.setDouble(11,
	   						bdigit4?YssFun.roundIt(secpecpay.getMoney(), 4):YssFun.roundIt(secpecpay.getMoney(), 2));//当bdigit4为true时，保留4位小数。sj edit 20080702
	   				yssPst.setDouble(12,
	   						bdigit4?YssFun.roundIt(secpecpay.getMMoney(), 4):YssFun.roundIt(secpecpay.getMMoney(), 2));
	   				yssPst.setDouble(13,
	   						bdigit4?YssFun.roundIt(secpecpay.getVMoney(), 4):YssFun.roundIt(secpecpay.getVMoney(), 2));
	   				yssPst.setDouble(14,
	   						YssFun.roundIt(secpecpay.getBaseCuryRate(), 15));   //hxqdii
	   				yssPst.setDouble(15,
	   						bdigit4?YssFun.roundIt(secpecpay.getBaseCuryMoney(),
	   								4):YssFun.roundIt(secpecpay.getBaseCuryMoney(),2));
	   				yssPst.setDouble(16,
	   						bdigit4?YssFun.roundIt(secpecpay.getMBaseCuryMoney(),
	   								4):YssFun.roundIt(secpecpay.getMBaseCuryMoney(),2));
	   				yssPst.setDouble(17,
	   						bdigit4?YssFun.roundIt(secpecpay.getVBaseCuryMoney(),
	   								4):YssFun.roundIt(secpecpay.getVBaseCuryMoney(),2));
	   				yssPst.setDouble(18,
	   						YssFun.roundIt(secpecpay.getPortCuryRate(), 15));   //hxqdii
	   				
	   				yssPst.setDouble(19,
	   						bdigit4?YssFun.roundIt(secpecpay.getPortCuryMoney(),
	   								4):YssFun.roundIt(secpecpay.getPortCuryMoney(),digit));
	   				yssPst.setDouble(20,
	   						bdigit4?YssFun.roundIt(secpecpay.getMPortCuryMoney(),
	   								4):YssFun.roundIt(secpecpay.getMPortCuryMoney(),digit));
	   				yssPst.setDouble(21,
	   						bdigit4?YssFun.roundIt(secpecpay.getVPortCuryMoney(),
	   								4):YssFun.roundIt(secpecpay.getVPortCuryMoney(),digit));
                                         
            
	   				//-----------2008.11.13 蒋锦 添加-------------//
	   				//储存保留8位小数的原币，基础货币，本位币金额
	   				//编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
	   				yssPst.setDouble(22, secpecpay.getMoneyF());
	   				yssPst.setDouble(23, secpecpay.getBaseCuryMoneyF());
	   				yssPst.setDouble(24, secpecpay.getPortCuryMoneyF());
	   				//-------------------------------------------//
	   				//---MS00275 QDV4中保2009年02月27日01_B ------//
//            pst.setInt(25, 0);
	   				//yssPst.setInt(25, 1); //将自动类型的标记修改成正确的值。 数据源自动改为0  delete by jsc
	   				yssPst.setInt(25, secpecpay.getDataSource()); //add by jiangshichao 20120308
	   				//------------------------------------------//
	   				yssPst.setInt(26, 0);
	   				yssPst.setString(27, (secpecpay.getCatTypeCode() != null && secpecpay.getCatTypeCode().length() > 0) ? secpecpay.getCatTypeCode() : " "); //modify by fangjiang 2011.05.20 story 845
	   				yssPst.setString(28,(secpecpay.getAttrClsCode() != null && secpecpay.getAttrClsCode().length() > 0)?secpecpay.getAttrClsCode():" ");
	   				yssPst.setInt(29, secpecpay.checkStateId);
	   				yssPst.setString(30, pub.getUserCode());
	   				yssPst.setString(31, YssFun.formatDatetime(new java.util.Date()));
	   				yssPst.setString(32, pub.getUserCode());
	   				yssPst.setString(33, YssFun.formatDatetime(new java.util.Date()));
	   				yssPst.setInt(34, secpecpay.getInOutType());
	   				yssPst.setString(35, secpecpay.getRelaNum());
	   				yssPst.setString(36, secpecpay.getRelaNumType());
	   				yssPst.setString(37, (secpecpay.getInvestType() != null && secpecpay.getInvestType().length() > 0) ? secpecpay.getInvestType() : "C");//panjunfang add 20110514 区分投资类型
	   				yssPst.executeUpdate();

	   			}
	   		} catch (Exception e) {
	   			throw new YssException("系统保存证券应收应付金额时出现异常!" + "\n", e); //by 曹丞 2009.02.01 保存证券应收应付异常信息 MS00004 QDV4.1-2009.2.1_09A
	   		} finally {
	   			dbl.closeStatementFinal(yssPst);
	   		}
		}//合并太平版本代码
    }

	/**
	 * add by songjie 2012.12.20 
	 * BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
	 * 获取主键编号
	 * @return
	 * @throws YssException
	 */
	public String getKeyNum() throws YssException{
		String num = "";
		String strSql = "";
		ResultSet rs = null;
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		int maxNum = 0;
		try{
			conn.setAutoCommit(false);
			
			if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Data_SecRecPay")){
				strSql = " select max(FNum) as FNum from " + pub.yssGetTableName("Tb_Data_SecRecPay") + 
				" where subStr(FNum,0,3) <> 'SRP' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FNum") != null && YssFun.isNumeric(rs.getString("FNum"))){
						maxNum = Integer.parseInt(rs.getString("FNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Data_SecRecPay " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
				
				dbl.executeSql(strSql);
			}
			
			strSql = " select trim(to_char(SEQ_" + pub.getPrefixTB() + 
			"_Data_SecRecPay.NextVal,'00000000000000000000')) as FNum from dual ";
			rs = dbl.openResultSet(strSql);
			if(rs.next()){
    			num = rs.getString("FNum");
    		}
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			
			return num;
		}catch(Exception e){
			throw new YssException("获取最大编号出错!\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
