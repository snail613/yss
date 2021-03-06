package com.yss.log;

import java.sql.*;

import org.apache.log4j.Logger;

import com.yss.util.YssException;
import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.datainterface.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;
import oracle.jdbc.*;
import oracle.sql.*;

public class OperateLogBean
    extends BaseDataSettingBean implements IYssLog, IDataSetting {
	private String logCode = "";
    private java.util.Date logDate;
    private String logTime = "";
    private String operUserCode = "";
    private String operUserName = "";
    private String moduleCode = "";
    private String moduleName = "";
    private String funCode = "";
    private String funName = "";
    private int operType;
    private String operTypeName = "";
    private String operResultCode = "";
    private String operResultName = "";
    private String refInvokeCode = "";
    private String refInvokeParam = "";
    private String logData1 = "";
    private String logData2 = "";
    private String strartDate = ""; //起始日期
    private String endDate = ""; //结束日期
    private String delList = "";
    private String sLogType = "";	//20130305 added by liubo.Story #2839.日志类别
    
    public String getLogType() {
		return sLogType;
	}

	public void setLogType(String sLogType) {
		this.sLogType = sLogType;
	}

	//20130110 added by liubo.Story #2839
    //==========================
    private String sChangeContent = "";	//变更内容
    private String sCategory = "";		//日志类别
    
    public String getCategory() {
		return sCategory;
	}

	public void setsategory(String sCategory) {
		this.sCategory = sCategory;
	}

	public String getChangeContent() {
		return sChangeContent;
	}

	public void setsChangeContent(String sChangeContent) {
		this.sChangeContent = sChangeContent;
	}
    //==============end============

	/**
     * insertLog
     *
     * @param obj Object
     *
     *
     */
    private int operateType;
    private String operateResult = "";
    private String bData = ""; //修改前的数据

    private String bSubData = ""; //修改前的子表数据

    private String aSubData = ""; //修改后的子表数据
    private String onlyShowColumn;//story 1840 by zhouwei 20111114 0代表只显示列
    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public void setOperateResult(String operateResult) {
        this.operateResult = operateResult;
    }

    public void setBData(String bData) {
        this.bData = bData;
    }

    public int getOperateType() {
        return operateType;
    }

    public String getOperateResult() {
        return operateResult;
    }

    public String getBData() {
        return bData;
    }

    /**
     * 插入日志到日志表
     * @param obj Object
     * @throws YssException
     */
    public void insertLog(Object obj) throws YssException {
        ResultSet rs = null;
        ResultSet rs1 = null;//add by songjie 2012.06.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
      	boolean bTrans = false;//by leeyu 20100504 处理死锁 合并太平版本
      	Connection con = null;//by leeyu 20100504 处理死锁 合并太平版本
      	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
      	PreparedStatement pstmt = null;
      	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        try {
         	//Connection con = null;//并发优化 合并太平版本 防止死锁
            String strSql = "";//并发优化 合并太平版本 防止死锁
         	//boolean bTrans = false;
            
            //---add by songjie 2012.09.06 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            if(obj == null){
            	return;
            }
            //---add by songjie 2012.09.06 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            
            BaseBean baseBean = (BaseBean) obj;
            if (baseBean instanceof BaseDataSettingBean) {
                int checkId = ( (BaseDataSettingBean) baseBean).checkStateId;
                if (this.operateType == 3) {
                    if (checkId == 1) {
                        this.operateType = 3;
                    }
                    if (checkId == 0) {
                        this.operateType = 11;
                    }
                }
            }
            // add by jsc 20120705 使用Sequence 来作为logCode
            if(!dbl.yssSequenceExist("SEQ_SYS_LOG")){
            	dbl.createSequence("Tb_Sys_OperLog");
            }
            IYssConvert convert = (IYssConvert) obj;
            //delete by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
            //dbl.lockTableInExclusiveMode("Tb_Sys_OperLog");//添加表级独占锁  合并太平版本调整
            //xuqiji 20100712 MS01431    后台日志表达到了6位，系统报错    QDV4建行2010年07月12日01_B    
//         	String logCode =//这里用局部变量，方便多线程处理 by leeyu 20100526 //并发优化 合并太平版本 防止死锁
//                dbFun.getNextInnerCode("Tb_Sys_OperLog",
//                                       dbl.sqlRight("FLogCode", 10), "0000000001",
//                                       " where 1=1", 1);
            

            
            String str = "\t\t\t\t\t\t\t\t";
            con = dbl.loadConnection();
            String bTemp = "";
            String aTemp = "";
            bTemp = baseBean.getBSubData().replaceAll("\t", "[-]"); //把操作前子表中的\t改成[-]
            bSubData = bTemp.replaceAll("\f\f", "[--]"); //把操作前子表中的\f\f改成[--]
            aTemp = baseBean.getASubData().replaceAll("\t", "[-]"); //把操作后子表中的\t改成[-]
            aSubData = aTemp.replaceAll("\f\f", "[--]"); //把操作后子表中的\f\f改成[--]
            if(this.bData==null)this.bData="";
            String bLog = this.bData.replaceAll("\t", "[-]") + "/bsub/" +
                bSubData.replaceAll("\r\f", "[---]");
            String aLog = "";
            if (obj instanceof ImpExpDataBean) {
                aLog = str.replaceAll("\t", "[-]") + "/asub/" +
                    aSubData.replaceAll("\r\f", "[---]");
            } else {
                aLog = convert.buildRowStr().replaceAll("\t", "[-]") + "/asub/" +
                    aSubData.replaceAll("\r\f", "[---]");
            }
            
            //20130109 added by liubo.Story #2839
            //某个窗体在做parseRowStr的操作时，会将<Logging>标签后的数据，复制给BaseBean的sLoggingPositionData变量
            //这个变量表示前台某个窗体的收日志管控的控件的差异值的集合
            //================================
//            String sTheDataChanged = baseBean.sLoggingPositionData.replaceAll("<next one>", "\r\r");
          String sTheDataChanged = baseBean.sLoggingPositionData;
//            sTheDataChanged = sTheDataChanged.replaceAll("[null]", "");
            //===============end=================
            
            logCode = this.getLogCode();
            
            strSql =
                " insert into Tb_Sys_OperLog " +
                "(FLogCode,FLogDate,FLogTime,FOperUser,FModuleCode,FFunCode,FMacClientIP,FMacClientName," +
				//edit by songjie 2012.12.17 STORY #2343 QDV4建行2012年3月2日04_A 添加 FLogData3
                "FMacNetCardAddr,FOperType,FRefInvokeCode,FOperResult,FLogData1,FLogData2,FLogData3" +
                ",FLogData4) " +	//20130109 added by liubo.Story #2839
                " values( " +
                dbl.sqlString(logCode) + "," +
                dbl.sqlDate(YssFun.toDate(YssFun.formatDate(new java.util.Date(),
                "yyyy-MM-dd"))) + "," +
                dbl.sqlString(YssFun.formatDate(new java.util.Date(), "HH:mm:ss")) +
                "," +
                dbl.sqlString(pub.getUserCode()) + "," +
                //---edit by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
                dbl.sqlString((baseBean == null ||(baseBean.getModuleName() == null)) ? " " : baseBean.getModuleName()) + "," +
                dbl.sqlString((baseBean == null ||(baseBean.getFunName() == null)) ? " " : baseBean.getFunName()) + "," +
                //---edit by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
                dbl.sqlString(pub.getClientPCAddr()) + "," +
                dbl.sqlString(pub.getClientPCName()) + "," +
                dbl.sqlString(pub.getClientMacAddr()) + "," +
                this.operateType + "," +
                //edit by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
                dbl.sqlString((baseBean == null ||(baseBean.getRefName() == null)) ? " " :baseBean.getRefName()) + "," +
                dbl.sqlString(this.operateResult) + ",";

            if (dbl.getDBType() == YssCons.DB_ORA) { //2007.11.29 添加判断 根据数据库的不同对大对象进行不同的处理
            	//edit by songjie 2012.12.17 STORY #2343 QDV4建行2012年3月2日04_A 添加 FLogData3
                strSql = strSql + "EMPTY_CLOB()" + "," + "EMPTY_CLOB()" + ",";
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                //DB2数据库执行语句过长会报错，这里先不对DB2数据库插入日志  edit by jc
                //strSql = strSql + dbl.sqlString(bLog) + "," + dbl.sqlString(aLog) +
                //      ")";
//                return;
            	/**yeshenghong 2013-6-25 BUG8319*/
            	strSql = strSql + "EMPTY_CLOB()" + "," + "EMPTY_CLOB()" + ",";
            	/**yeshenghong 2013-6-25 BUG8319 end*/
                //---------------------------------------------------------jc
            } else {
                throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
            }
//            strSql += dbl.sqlString(sTheDataChanged) + ")";
//            //---add by songjie 2012.12.17 STORY #2343 QDV4建行2012年3月2日04_A start---//
            if(this.operateType == 15){//添加 FLogData3
            	strSql += dbl.sqlString(baseBean.getASubData()) + ",";
            }else{
            	strSql += " '',";
            }
            /**yeshenghong 2013-6-25 BUG8319*/
            strSql += "EMPTY_CLOB())";
            /**yeshenghong 2013-6-25 BUG8319 end*/
            
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            
            //---add by songjie 2012.06.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            Logger log = Logger.getLogger("D");
            log.info("--------------------------------- OperLogID : " + logCode + " ---------------------------------");
            //---add by songjie 2012.06.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞 add by jsc
            CLOB bLogData = null;
            CLOB aLogData = null;
            CLOB changeContent = null;
            OracleResultSet oracleResultSet = null;
         	if(this.operateType==0 
        		||this.operateType==1 
        		||this.operateType==2
        		||this.operateType==3
        		||this.operateType==11){//只更新，增、删、改、审核与反审核的大字段的数据,系统优化 by leeyu 20100519 //并发优化 合并太平版本 防止死锁
	            if (dbl.getDBType() == YssCons.DB_ORA) { //2007.11.29 添加 DB2 数据库不需要执行以下部分
	        		String str2 = "select FLogData1,FLogData2,FLogData4 from " +
        	            " Tb_Sys_OperLog where FLogCode=" +
    	                dbl.sqlString(logCode) + " for update";

	                rs = dbl.openResultSet(str2);

                	if (rs.next()) {
                		//STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞 add by jsc
                		bLogData = dbl.CastToCLOB(rs.getClob("FLogData1"));
            	        aLogData = dbl.CastToCLOB(rs.getClob("FLogData2"));
            	        changeContent = dbl.CastToCLOB(rs.getClob("FLogData4"));	//20130109 added by liubo.Story #2839
        	                
                		bLogData.putString(1, bLog);
	                    aLogData.putString(2, aLog);
	                    changeContent.putString(3, sTheDataChanged);

    	                //----------------------------------
	                    String sql =
                        	"update Tb_Sys_OperLog set FLogData1=? ,FLogData2=?,FLogData4=? where FLogCode=" +
                    	    dbl.sqlString(logCode);
                	    //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
	                    pstmt = con.prepareStatement(sql);
	                    //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
            	        pstmt.setClob(1, bLogData);
        	            pstmt.setClob(2, aLogData);
        	            pstmt.setClob(3, changeContent);		//20130109 added by liubo.Story #2839
    	                pstmt.executeUpdate();
	                    pstmt.close();

                	}
        		}
         	}//end if //并发优化 合并太平版本 防止死锁
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
    	 	bTrans = true;//添加对事务的处理，防止表死锁，主要是对异常的处理 by leeyu 20100504 
    	 	e.printStackTrace();
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
         	dbl.endTransFinal(con,bTrans);//添加对事务的处理，防止表死锁，主要是对异常的处理 by leeyu 20100504
         	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
         	dbl.closeStatementFinal(pstmt);
         	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        }
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            strSql =
                "select a.*,b.FBarName as FModuleName,c.FBarName as FFunName , d.FParams as FInvokeParam , " +
                "e.FVocName as FOperTypeValue,f.FVocName as FOperResultValue" +
                " from Tb_Sys_OperLog a " +
                " left join (select FBarCode,FBarName from TB_FUN_MENUBAR) b on a.FModuleCode = b.FBarCode" +
                " left join (select FBarCode,FBarName from TB_FUN_MENUBAR) c on a.FFunCode = c.FBarCode" +
                " left join (select FRefInvokeCode,FParams from TB_FUN_REFINVOKE) d on a.FRefInvokeCode=d.FRefInvokeCode" +
                " left join Tb_Fun_Vocabulary e on a.FOperType =e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_OPERTYPE) +
                " left join Tb_Fun_Vocabulary f on a.FOperResult = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_OPERRESULT) +
                "where 1=2";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.logDate = rs.getDate("FLogDate");
                this.logTime = rs.getString("FLogTime");
                this.operUserCode = rs.getString("FOperUser");
                this.moduleCode = rs.getString("FModuleCode");
                this.moduleName = rs.getString("FModuleName");
                this.funCode = rs.getString("FFunCode");
                this.funName = rs.getString("FFunName");
                this.operType = rs.getInt("FOperType");
                this.refInvokeCode = rs.getString("FRefInvokeCode");
                this.refInvokeParam = rs.getString("FInvokeParam");
                this.logData1 = dbl.clobStrValue(rs.getClob("FLogData1")).
                    replaceAll(
                        "\t", "   ");
                this.logData2 = dbl.clobStrValue(rs.getClob("FLogData2")).
                    replaceAll(
                        "\t", "   ");
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
            String sVocStr = getVoc();
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
    //story 1840 by zhouwei 查询日志具有分页的功能
    public String getListViewData2() throws YssException {
      String sHeader = "";
      String sShowCols = "";
      String sShowDataStr = "";
      String strSql = "";
      ResultSet rs = null;
      String sAllDataStr = "";
      StringBuffer bufShow = new StringBuffer();
      StringBuffer bufAll = new StringBuffer();
      try {
          sHeader = this.getListView1Headers();
          sShowCols = this.getListView1ShowCols();
          if(this.onlyShowColumn.equals("0")){
        	  String sVocStr = getVoc();
              return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
              "\r\f" + sShowCols + "\r\f"+ yssPageInationBean.buildRowStr()+ "\r\f" + "voc" + sVocStr;
          }
          strSql =
                  "select a.*,Nvl(b.FBarName,' ') as FModuleName," +
                  //20130317 added by liubo.Story #2839
                  //用户权限设置的操作日志浏览，需要通过功能名称区分
                  //===============================
                  "(case when a.FFunCode = 'rightSet_Public' then '权限设置-公共级权限' " +
                  " when a.FFunCode = 'rightSet_Port' then '权限设置-组合级权限' " +
                  " when a.FFunCode = 'rightSet_Report' then '权限设置-报表权限' " +
                  " when a.FFunCode = 'rightSet_Dao' then '权限设置-接口权限'" +
                  " when a.FFunCode = 'rightSet_Role' then '权限设置-角色权限'" +
                  " else c.FBarName end) as FFunName ," +
                  //================end===============
                  " Nvl(d.FParams,' ') as FInvokeParam , " +
                  " Nvl(e.FVocName,' ') as FOperTypeValue,f.FVocName as FOperResultValue, 0 as fcheckstate," +
                  " Nvl(g.Ffuntype, ' ') as FCategory,a.FLogData4 as FChangeContent " +
                  " from Tb_Sys_OperLog a " +
                  " left join (select FBarCode,FBarName from TB_FUN_MENUBAR) b on a.FModuleCode = b.FBarCode" +
                  " left join (select FBarCode,FBarName from TB_FUN_MENUBAR) c on a.FFunCode = c.FBarCode" +
                  " left join (select FRefInvokeCode,FParams from TB_FUN_REFINVOKE) d on a.FRefInvokeCode=d.FRefInvokeCode" +
                  " left join Tb_Fun_Vocabulary e on " +
                  dbl.sqlToChar("a.FOperType") +
                  " =e.FVocCode and e.FVocTypeCode = " +
                  dbl.sqlString(YssCons.YSS_OPERTYPE) +
                  " left join Tb_Fun_Vocabulary f on a.FOperResult = f.FVocCode and f.FVocTypeCode = " +
                  dbl.sqlString(YssCons.YSS_OPERRESULT) +
                  " left join Tb_Fun_LogType g on a.FFunCode = g.FFunCode " +
                  buildFilterSql() + 
                  //-----edit by maxin STORY #14624 需求上海-[YSS_SH]QDIIV4.0[高]20131227001（操作日志查询导出功能变更） 
//                  " and a.Fopertype in (0,1) " +
                  " order by FLogDate desc, FlogTime desc";
          yssPageInationBean.setsQuerySQL(strSql);
          yssPageInationBean.setsTableName("SysOperLog");
          rs =dbl.openResultSet(yssPageInationBean);
          while (rs.next()) {
              bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                  append(YssCons.YSS_LINESPLITMARK);
              //add by songjie 2012.08.16 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 FLogCode
              this.logCode = rs.getString("FLogCode");
              this.logDate = rs.getDate("FLogDate");
              this.logTime = rs.getString("FLogTime");
              this.operUserCode = rs.getString("FOperUser");
              this.moduleCode = rs.getString("FModuleCode");
              this.moduleName = rs.getString("FModuleName");
              this.funCode = rs.getString("FFunCode");
              this.funName = rs.getString("FFunName");
              this.operType = rs.getInt("FOperType");
              this.refInvokeCode = rs.getString("FRefInvokeCode");
              this.refInvokeParam = rs.getString("FInvokeParam");
//              this.logData1 = dbl.clobStrValue(rs.getClob("FLogData1")).
//                  replaceAll(
//                      "\t", "   ");
//              this.logData2 = dbl.clobStrValue(rs.getClob("FLogData2")).
//                  replaceAll(
//                      "\t", "   ");

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
          String sVocStr = getVoc();
          return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
          "\r\f" + sShowCols + "\r\f"+ yssPageInationBean.buildRowStr()+ "\r\f" + "voc" + sVocStr;
      } catch (Exception e) {
          throw new YssException(e.getMessage());
      } finally {
          dbl.closeResultSetFinal(rs);
          dbl.closeStatementFinal(dbl.getProcStmt());
      }

  }
    private String buildFilterSql() {
        String sResult = "where 1=1 ";
        if (this.operUserCode.length() != 0) {
            sResult = sResult + "and a.FOperUser=" + dbl.sqlString(this.operUserCode);

        }//add by yeshenghong  未明确定义列错误 20130124
        if (this.moduleCode.length() != 0) {
            sResult = sResult + "and a.FModuleCode=" + dbl.sqlString(this.moduleCode);

        }
        if (this.funCode.length() != 0) {
            sResult = sResult + "and a.FFunCode=" + dbl.sqlString(this.funCode);
        }
        
        //20130305 added by liubo.Story #2839
        //为日志类别添加筛选条件
        //==============================
        if (this.sLogType != null && this.sLogType.trim().length() != 0)
        {
        	sResult = sResult + " and a.ffuncode in (select ffuncode from tb_fun_logtype where ffuntype = " + dbl.sqlString(this.sLogType) + ")";
        }
        //=============end=================
        
        sResult = sResult + " and a.FLOGDATE between " +
            dbl.sqlDate(this.strartDate) +
            " and " + dbl.sqlDate(this.endDate);
        return sResult;
    }

    /**
     * getListViewData2
     * 第一次加载listView时不加载数据
     * @return String
     */
//    public String getListViewData2() throws YssException {
//        String sHeader = "";
//        String sShowDataStr = "";
//        String strSql = "";
//        ResultSet rs = null;
//        String sAllDataStr = "";
//        StringBuffer bufShow = new StringBuffer();
//        StringBuffer bufAll = new StringBuffer();
//        try {
//            sHeader = this.getListView1Headers();
//            strSql =
//                "select a.*,b.FBarName as FModuleName,c.FBarName as FFunName , d.FParams as FInvokeParam , " +
//                "e.FVocName as FOperTypeValue,f.FVocName as FOperResultValue" +
//                " from Tb_Sys_OperLog a " +
//                " left join (select FBarCode,FBarName from TB_FUN_MENUBAR) b on a.FModuleCode = b.FBarCode" +
//                " left join (select FBarCode,FBarName from TB_FUN_MENUBAR) c on a.FFunCode = c.FBarCode" +
//                " left join (select FRefInvokeCode,FParams from TB_FUN_REFINVOKE) d on a.FRefInvokeCode=d.FRefInvokeCode" +
//                " left join Tb_Fun_Vocabulary e on " +
//                dbl.sqlToChar("a.FOperType") +
//                " =e.FVocCode and e.FVocTypeCode = " +
//                dbl.sqlString(YssCons.YSS_OPERTYPE) +
//                " left join Tb_Fun_Vocabulary f on a.FOperResult = f.FVocCode and f.FVocTypeCode = " +
//                dbl.sqlString(YssCons.YSS_OPERRESULT) +
//                "where 1=2";
//            rs = dbl.openResultSet(strSql);
//            while (rs.next()) {
//                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
//                    append(YssCons.YSS_LINESPLITMARK);
//                this.logDate = rs.getDate("FLogDate");
//                this.logTime = rs.getString("FLogTime");
//                this.operUserCode = rs.getString("FOperUser");
//                this.moduleCode = rs.getString("FModuleCode");
//                this.moduleName = rs.getString("FModuleName");
//                this.funCode = rs.getString("FFunCode");
//                this.funName = rs.getString("FFunName");
//                this.operType = rs.getInt("FOperType");
//                this.refInvokeCode = rs.getString("FRefInvokeCode");
//                this.refInvokeParam = rs.getString("FInvokeParam");
//                this.logData1 = dbl.clobStrValue(rs.getClob("FLogData1")).
//                    replaceAll(
//                        "\t", "   ");
//                this.logData2 = dbl.clobStrValue(rs.getClob("FLogData2")).
//                    replaceAll(
//                        "\t", "   ");
//                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
//            }
//            if (bufShow.toString().length() > 2) {
//                sShowDataStr = bufShow.toString().substring(0,
//                    bufShow.toString().length() - 2);
//            }
//            if (bufAll.toString().length() > 2) {
//                sAllDataStr = bufAll.toString().substring(0,
//                    bufAll.toString().length() - 2);
//            }
//            String sVocStr = getVoc();
//            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
//                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
//        } catch (Exception e) {
//            throw new YssException(e.getMessage());
//        } finally {
//            dbl.closeResultSetFinal(rs);
//        }
//
//    }

    /**
     * getListViewData3
     * 查询,
     * @return String
     */
    public String getListViewData3() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            strSql =
                "select a.*,b.FBarName as FModuleName,c.FBarName as FFunName , d.FParams as FInvokeParam , " +
                "e.FVocName as FOperTypeValue,f.FVocName as FOperResultValue" +
                " from Tb_Sys_OperLog a " +
                " left join (select FBarCode,FBarName from TB_FUN_MENUBAR) b on a.FModuleCode = b.FBarCode" +
                " left join (select FBarCode,FBarName from TB_FUN_MENUBAR) c on a.FFunCode = c.FBarCode" +
                " left join (select FRefInvokeCode,FParams from TB_FUN_REFINVOKE) d on a.FRefInvokeCode=d.FRefInvokeCode" +
                " left join Tb_Fun_Vocabulary e on " +
                dbl.sqlToChar("a.FOperType") +
                " =e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_OPERTYPE) +
                " left join Tb_Fun_Vocabulary f on a.FOperResult = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_OPERRESULT) +
                buildFilterSql() + "order by FLogDate desc, FlogTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.logDate = rs.getDate("FLogDate");
                this.logTime = rs.getString("FLogTime");
                this.operUserCode = rs.getString("FOperUser");
                this.moduleCode = rs.getString("FModuleCode");

                this.moduleName = rs.getString("FModuleName");
                this.funCode = rs.getString("FFunCode");
                this.funName = rs.getString("FFunName");
                this.operType = rs.getInt("FOperType");
                this.refInvokeCode = rs.getString("FRefInvokeCode");
                this.refInvokeParam = rs.getString("FInvokeParam");
                this.logData1 = dbl.clobStrValue(rs.getClob("FLogData1")).
                    replaceAll(
                        "\t", "   ");
                this.logData2 = dbl.clobStrValue(rs.getClob("FLogData2")).
                    replaceAll(
                        "\t", "   ");

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
            String sVocStr = getVoc();
            String oo = this.getListView1ShowCols();
            String reStr = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 20130122 added by liubo.Story #2839
     * 以日志浏览界面选定的查询条件为条件，返回一个字符串
     * 这个字符串将包含修改内容。
     * 这个字符串最终将作为日志导出EXCEL的数据源
     *
     * @return String
     */
    public String getListViewData4() throws YssException
    {
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            strSql =
                "select a.*,Nvl(b.FBarName,' ') as FModuleName," +
                //20130317 added by liubo.Story #2839
                //用户权限设置的操作日志浏览，需要通过功能名称区分
                //===============================
                "(case when a.FFunCode = 'rightSet_Public' then '权限设置-公共级权限' " +
                " when a.FFunCode = 'rightSet_Port' then '权限设置-组合级权限' " +
                " when a.FFunCode = 'rightSet_Report' then '权限设置-报表权限' " +
                " when a.FFunCode = 'rightSet_Dao' then '权限设置-接口权限'" +
                " when a.FFunCode = 'rightSet_Role' then '权限设置-角色权限'" +
                " else c.FBarName end) as FFunName ," +
                //================end===============
                " Nvl(d.FParams,' ') as FInvokeParam , " +
                " Nvl(e.FVocName,' ') as FOperTypeValue,f.FVocName as FOperResultValue, 0 as fcheckstate," +
                " Nvl(g.Ffuntype, ' ') as FCategory,a.FLogData4 as FChangeContent " +
                " from Tb_Sys_OperLog a " +
                " left join (select FBarCode,FBarName from TB_FUN_MENUBAR) b on a.FModuleCode = b.FBarCode" +
                " left join (select FBarCode,FBarName from TB_FUN_MENUBAR) c on a.FFunCode = c.FBarCode" +
                " left join (select FRefInvokeCode,FParams from TB_FUN_REFINVOKE) d on a.FRefInvokeCode=d.FRefInvokeCode" +
                " left join Tb_Fun_Vocabulary e on " +
                dbl.sqlToChar("a.FOperType") +
                " =e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_OPERTYPE) +
                " left join Tb_Fun_Vocabulary f on a.FOperResult = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_OPERRESULT) +
                " left join Tb_Fun_LogType g on a.FFunCode = g.FFunCode " +
                buildFilterSql() + 
                //-----edit by maxin STORY #14624 需求上海-[YSS_SH]QDIIV4.0[高]20131227001（操作日志查询导出功能变更） 
//                " and a.Fopertype in (0,1) " +
                " order by FLogDate desc, FlogTime desc";
//            yssPageInationBean.setsQuerySQL(strSql);
//            yssPageInationBean.setsTableName("SysOperLog");
            rs =dbl.openResultSet(strSql);
            bufAll.append("日志编号\t日志日期\t日志时间\t用户名\t日志类别\t模块名称\t功能名称\t操作类型\t操作结果\t客户端IP\t客户端机器名\t客户端网卡地址\t本次修改内容\r\n");
            bufAll.append("").append(YssCons.YSS_LINESPLITMARK);
            while (rs.next()) {
            	bufAll.append(super.buildRowShowStr(rs, 
                		"FLogCode\tFLogDate\tFLogTime\tFOperUser\tFCategory\tFModuleName\tFFunName\tFOperTypeValue\tFOperResultValue\tFMacClientIP\tFMacClientName\tFMacNetCardAddr\tFChangeContent")).
                    append(YssCons.YSS_LINESPLITMARK);

            }

            bufAll.append("").append(YssCons.YSS_LINESPLITMARK).append("").append(YssCons.YSS_LINESPLITMARK).append("").append(YssCons.YSS_LINESPLITMARK).append("").append("\f\r\n");
            
            return bufAll.toString();
            
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() {
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkt(byte btOper) {
    }

    /**
     * checkSetting
     */
    public void checkSetting() {
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        String[] delStr = this.delList.split(YssCons.YSS_LINESPLITMARK);
        String log = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            for (int i = 0; i < delStr.length; i++) {
                log = delStr[i];
                String[] delLog = log.split("\t");
                strSql = " delete from Tb_Sys_OperLog where FLogDate=" +
                    dbl.sqlDate(delLog[0]) +
                    " and FLogTime=" + dbl.sqlString(delLog[1]) +
                    " and FOperUser=" + dbl.sqlString(delLog[2]) +
                    " and FModuleCode=" + dbl.sqlString(delLog[3]) +
                    " and FFunCode=" + dbl.sqlString(delLog[4]);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() {
        return "";
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.logDate).append("\t");
        buf.append(this.logTime).append("\t");
        buf.append(this.operUserCode).append("\t");
        buf.append(this.moduleCode).append("\t");
        buf.append(this.moduleName).append("\t");
        buf.append(this.funCode).append("\t");
        buf.append(this.funName).append("\t");
        buf.append(this.operTypeName).append("\t");
        buf.append(this.operResultName).append("\t");
        buf.append(this.refInvokeCode).append("\t");
        buf.append(this.refInvokeParam).append("\t");
//        buf.append(this.logData1.trim()).append("\t");
//        buf.append(this.logData2.trim()).append("\t");
        buf.append(this.operType).append("\t");
        //add by songjie 2012.08.16 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
        buf.append(this.logCode.trim()).append("\t");
        return buf.toString();
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {

        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\n\n\f") > 0) {
                this.delList = sRowStr.split("\n\n\f")[0]; //要删除的数据
                sTmpStr = sRowStr.split("\n\n\f")[1];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.moduleCode = reqAry[0];
            this.operUserCode = reqAry[1];
            this.funCode = reqAry[2];
            this.strartDate = reqAry[3];
            this.endDate = reqAry[4];
            this.onlyShowColumn=reqAry[5];//story 1840 by zhouwei 20111114
            
          //20130305 added by liubo.Story #2839.日志类别
            if (reqAry.length >= 7)
            {
            	this.sLogType = reqAry[6];		
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    /**
     * 获取所有的词汇数据
     * @throws YssException
     * @return String
     */
    public String getVoc() throws YssException {
        VocabularyBean vocabulary = new VocabularyBean();
        String sVocStr = "";
        ResultSet rs = null;
        String strSql = "";
        StringBuffer strBuf = new StringBuffer();
        String str = "";
        try {
            strSql = "select * from tb_fun_vocabularytype where FCheckState=1";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                strBuf.append(rs.getString("Fvoctypecode")).append(",");
            }
            if (strBuf.toString().length() > 1) {
                str = strBuf.toString().substring(0,
                                                  strBuf.toString().length() - 1);
            }
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(str);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

        return sVocStr;

    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";

    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
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
    
    /**
     *  add by jsc 20120706
     * @return
     * @throws YssException
     */
    private String getLogCode ()throws YssException{
    	
    	ResultSet rs = null;
    	String logCode ="";
    	try{
    		
    		rs = dbl.openResultSet(" select trim(to_char(SEQ_SYS_LOG.NextVal,'00000000000000000000')) as LogCode from dual ");
    		
    		if(rs.next()){
    			logCode = rs.getString("LogCode");
    		}
    		return logCode;
    	}catch(Exception e){
    		throw new YssException ("获取Sequence SEQ_SYS_LOG 值 失败 ");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    /**
     * add by songjie 2012.12.17 
     * STORY #2343 QDV4建行2012年3月2日04_A
     * 获取用户登陆信息
     * @return
     * @throws YssException
     */
    public String getUserLogInfo(boolean inputError) throws YssException{
    	ResultSet rs = null;
    	String logData = "";
    	try{
            //--- add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 start---//
    		if(!inputError){
    			MonitorExport.loginNumber += 1;
    		}else{//如果是录入密码错误
    			MonitorExport.errorInputNumber += 1;
    		}
    		//--- add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 end---//
            
            return logData;//logData = 1,2,001 (1-登录次数 2-密码输入错误次数 001-组合群代码)
    	}catch(Exception e){
    		throw new YssException("获取用户登录信息出错！");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    public String getModifiedContent(String sRequest) throws YssException
    {
    	String sReturn = "";
    	String strSql = "";
    	ResultSet rs = null;
    	
    	try
    	{
    		strSql = "select a.*,Nvl(b.ffuntype,' ') as FFunType," +
    				"(case when a.FFunCode = 'rightSet_Public' then '权限设置-公共级权限' " +
    				" when a.FFunCode = 'rightSet_Port' then '权限设置-组合级权限' " +
    				" when a.FFunCode = 'rightSet_Report' then '权限设置-报表权限' " +
    				" when a.FFunCode = 'rightSet_Dao' then '权限设置-接口权限' " + 
    				" when a.FFunCode = 'rightSet_Role' then '权限设置-角色权限' " +
    				" else c.fbarname end) as FBarName " +
    				" from tb_sys_operlog a " +
    				" left join tb_fun_logtype b on a.ffuncode = b.ffuncode " +
    				" left join tb_fun_menubar c on a.ffuncode = c.fbarCode " +
    				" where FLogCode = " + dbl.sqlString(sRequest);
    		
    		rs = dbl.queryByPreparedStatement(strSql);
    		
    		if(rs.next())
    		{
    			sReturn = rs.getString("FBarName") + "\t" + dbl.clobStrValue(rs.getClob("FLogData4"));
    		}
    	}
    	catch(Exception ye)
    	{
    		throw new YssException();
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    	
    	return sReturn;
    }
}
