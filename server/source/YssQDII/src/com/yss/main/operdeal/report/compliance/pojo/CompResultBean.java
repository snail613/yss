package com.yss.main.operdeal.report.compliance.pojo;

import java.sql.Connection;
import java.sql.ResultSet;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.operdata.DividendBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class CompResultBean
    extends BaseBean implements IYssConvert {

    private java.util.Date compDate; //监控日期
    //创建日期 2009-02-10 蒋锦 添加 MS00195 QDV4建行2009年1月15日01_B
    private java.util.Date createDate; //
    private String portCode = ""; //组合代码
    private String portName = ""; //组合名称
    private String indexCfgCode = ""; //指标配置代码
    private String indexCfgName = ""; //指标配置名称
    private String compResult = ""; //监控结果
    private String Desc = ""; //描述
    

    //BugId MS00040 20081127   王晓光   添加numerator、denominator、factRatio
    private double numerator; //分子
    private double denominator; //分母
    private double factRatio; //实际比值
    private double dCompStandard;//阀值
    //private String sCompType;// 
private String remindResult;//监控结果（提示函中）add by zhaoxianlin 20130301
    
    
    
    
    /**add by zhaoxianlin 20130301 start*/
    public String getRemindResult() {
		return remindResult;
	}

	public void setRemindResult(String remindResult) {
		this.remindResult = remindResult;
	}
	/**add by zhaoxianlin 20130301 end*/
    private String sState;    
    
    public String getsState() {
		return sState;
	}

	public void setsState(String sState) {
		this.sState = sState;
	}


	//20120209 added by liubo.Bug #3526.每次监控结果生成操作的所产生的序列号，用来做为主键的一部分，无实际意义。
    //=======================================
    private String sSerialNo = "";		

    
    public String getSerialNo() {
		return sSerialNo;
	}
    //===============end========================

	public void setSerialNo(String sSerialNo) {
		this.sSerialNo = sSerialNo;
	}

	public double getdCompStandard() {
		return dCompStandard;
	}

	public void setdCompStandard(double dCompStandard) {
		this.dCompStandard = dCompStandard;
	}

//	public String getsCompType() {
//		return sCompType;
//	}
//
//	public void setsCompType(String sCompType) {
//		this.sCompType = sCompType;
//	}

	public java.util.Date getCompDate() {
        return compDate;
    }

    public String getCompResult() {
        return compResult;
    }

    public String getDesc() {
        return Desc;
    }

    public String getIndexCfgCode() {
        return indexCfgCode;
    }

    public String getIndexCfgName() {
        return indexCfgName;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getPortName() {
        return portName;
    }

    public double getNumerator() {
        return numerator;
    }

    public double getDenominator() {
        return denominator;
    }

    public double getFactRatio() {
        return factRatio;
    }

    public java.util.Date getCreateDate() {
        return createDate;
    }

    public void setCompDate(java.util.Date compDate) {
        this.compDate = compDate;
    }

    public void setCompResult(String compResult) {
        this.compResult = compResult;
    }

    public void setDesc(String Desc) {
        this.Desc = Desc;
    }

    public void setIndexCfgCode(String indexCfgCode) {
        this.indexCfgCode = indexCfgCode;
    }

    public void setIndexCfgName(String indexCfgName) {
        this.indexCfgName = indexCfgName;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setNumerator(double numerator) {
        this.numerator = numerator;
    }

    public void setDenominator(double denominator) {
        this.denominator = denominator;
    }

    public void setFactRatio(double factRatio) {
        this.factRatio = factRatio;
    }

    public void setCreateDate(java.util.Date createDate) {
        this.createDate = createDate;
    }

    public CompResultBean() {
    }

    /**
     * parseRowStr
     * 解析分红权益数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
    	 String[] reqAry = null;
         String sTmpStr = "";
         String sMutiAudit = ""; 
         try {
             if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) {
                 sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1];
                // multAuditString = sMutiAudit;
                 sRowStr = sRowStr.split("\f\n\f\n\f\n")[0];
             }
             if (sRowStr.trim().length() == 0) {
                 return;
             }
             if (sRowStr.indexOf("\r\t") >= 0) {
                 sTmpStr = sRowStr.split("\r\t")[0];
             } else {
                 sTmpStr = sRowStr;
             }
             //sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
             reqAry = sTmpStr.split("\t");
             this.compDate = YssFun.parseDate(reqAry[0]);
             this.portCode = reqAry[1];
             this.indexCfgCode = reqAry[2];
             this.compResult = reqAry[3];
             //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
             this.numerator = YssFun.toDouble(reqAry[4]);
             this.denominator=YssFun.toDouble(reqAry[5]);
             this.factRatio = YssFun.toDouble(reqAry[6]);

             this.dCompStandard=YssFun.toDouble(reqAry[7]);
            
//             if (sRowStr.indexOf("\r\t") >= 0) {
////                 if (this.filterType == null) {
////                     this.filterType = new DividendBean();
////                     this.filterType.setYssPub(pub);
////                 }
////                 this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
//             }
         } catch (Exception e) {
             throw new YssException("解析分红权益数据信息出错", e);
         }

    }
    
    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
    	StringBuffer buff = new StringBuffer();
    	buff.append(this.compDate).append("\t");
    	buff.append(this.portCode).append("\t");
    	buff.append(this.portName).append("\t");
    	buff.append(this.indexCfgCode).append("\t");
    	buff.append(this.indexCfgName).append("\t");
    	buff.append(this.compResult).append("\t");
    	buff.append(this.numerator).append("\t");
    	buff.append(this.denominator).append("\t");
    	buff.append(this.factRatio).append("\t");
    	buff.append(this.dCompStandard).append("\t");
    	buff.append(this.sState).append("\t");
        return buff.toString();
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
    	
//    	if(sType.equalsIgnoreCase("find")){
//    		
//    	}else if(sType.equalsIgnoreCase("add")){
//    		
//    	}else if(sType.equalsIgnoreCase("edit")){
//    		
//    	}else if(sType.equalsIgnoreCase("del")){
//    		
//    	}else if(sType.equalsIgnoreCase("audit")){
//    		
//    	}
        return "";
    }


    /**
     * getListViewData1
     * 获取分红权益数据
     * @return String
     */
    public String getListViewData1() throws YssException {
       return null;
    }
    
    private String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
        	String creatorTime = YssFun.formatDatetime(new java.util.Date());
            strSql =
                "insert into " + pub.yssGetTableName("TB_COMP_RESULTDATA") +
                "(FCOMPDATE,FPORTCODE,FINDEXCFGCODE,FCREATEDATE,FCOMPRESULT,FDESC,FCHECKSTATE,"+
                "FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,FNUMERATOR,FDENOMINATOR,FFACTRATIO,FSTATE,FCOMPSTANDARD)" +
                " values(" + dbl.sqlDate(this.compDate) + "," +
                dbl.sqlString(this.portCode) + "," +
                dbl.sqlString(this.indexCfgCode) + "," +
                dbl.sqlDate(this.createDate) + "," +
                dbl.sqlString(this.compResult) + ",' ',0," +
                dbl.sqlString(pub.getUserCode())+","+
                dbl.sqlString(creatorTime) + "," +
                dbl.sqlString(pub.getUserCode()) +","+
                //---------------------------------end--------------------------------------//
                dbl.sqlString(creatorTime) + "," +
                this.numerator + "," +
                this.denominator + "," +
                this.factRatio +
                ",1" +
                this.dCompStandard + ")"; 
            
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增分红权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
    
    
    private void delSetting() throws YssException {
//        String strSql = "";
//        String[] arrData = null;
//        boolean bTrans = false; //代表是否开始了事务
//        Connection conn = dbl.loadConnection();
//        try {
//            conn.setAutoCommit(false);
//            bTrans = true;
//            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
//            if ( (!sRecycled.equalsIgnoreCase("")) && sRecycled != null) {
//                arrData = sRecycled.split("\r\n");
//                for (int i = 0; i < arrData.length; i++) {
//                    if (arrData[i].length() == 0) {
//                        continue;
//                    }
//                    this.parseRowStr(arrData[i]);
//                    strSql = "update " + pub.yssGetTableName("Tb_Data_Dividend") +
//                        " set FCheckState = " + this.checkStateId +
//                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//                        ", FCheckTime = '" +
//                        YssFun.formatDatetime(new java.util.Date()) +
//                        "' where FSecurityCode = " +
//                        dbl.sqlString(this.oldSecurityCode) +
//                        " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
//                        " and FDivdendType = " + this.DividentType + //将原来的OldDividentType修改为了DividentType（此处不用OldDividentType）
//                        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
//                        " and FCuryCode =" + dbl.sqlString(this.oldCuryCode)+
//                        " and FPortCode=" +dbl.sqlString(this.PortCode.length() == 0 ? " " :this.PortCode)+
//                        " and FASSETGROUPCODE=" + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ")
//                        //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
//                        +"and FDISTRIBUTEDATE="+dbl.sqlDate(this.oldDistributeDate)
//                        //------------------------end----------------//
//                        ;
//                        //------------------------end----------------//
//                    dbl.executeSql(strSql);
//                }
//            }
//            //如果sRecycled为空，而oldSecurityCode不为空，则按照oldSecurityCode来执行sql语句
//            else if ( (!oldSecurityCode.equalsIgnoreCase("")) && oldSecurityCode != null) {
//                strSql = "update " + pub.yssGetTableName("Tb_Data_Dividend") +
//                    " set FCheckState = " + this.checkStateId +
//                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//                    ", FCheckTime = '" +
//                    YssFun.formatDatetime(new java.util.Date()) +
//                    "' where FSecurityCode = " +
//                    dbl.sqlString(this.oldSecurityCode) +
//                    " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
//                    " and FDivdendType = " + this.DividentType + //将原来的OldDividentType修改为了DividentType（此处不用OldDividentType）
//                    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
//                    " and FCuryCode =" + dbl.sqlString(this.oldCuryCode) +
//                    " and FPortCode=" + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
//                    " and FASSETGROUPCODE=" + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ")
//                    //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
//                    +"and FDISTRIBUTEDATE="+dbl.sqlDate(this.oldDistributeDate)
//                    //------------------------end----------------//
//                    ;
//                    //------------------------end----------------//
//                dbl.executeSql(strSql);
//            }
//            conn.commit();
//            bTrans = false;
//            conn.setAutoCommit(true);
//        } catch (Exception e) {
//            throw new YssException("删除分红权益业务数据出错", e);
//        } finally {
//            dbl.endTransFinal(conn, bTrans);
//        }
    }
    

    private String editData() throws YssException {
    	 Connection conn = dbl.loadConnection();
         boolean bTrans = false;
         String strSql = "";
         try {
             strSql =
                 "update " + (pub.yssGetTableName("TB_COMP_RESULTDATA")) +
                 " set FNUMERATOR = " + this.numerator +
                 ",FDENOMINATOR = " + this.denominator +
                 ",FFACTRATIO = " + this.factRatio +
                 ",FCOMPSTANDARD = " + this.dCompStandard +
                 ",FCOMPRESULT = " + dbl.sqlString(this.compResult) +
                 ",FCreator = " + dbl.sqlString(pub.getUserCode()) 
                 //",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                 +" where FCOMPDATE = " + dbl.sqlDate(this.compDate) +
                 " and FPORTCODE = " + dbl.sqlString(this.portCode) +
                 " and FINDEXCFGCODE = " + dbl.sqlString(this.indexCfgCode) + 
                 " and FCheckState='0'";
             // System.out.println("SQL="+strSql);
             conn.setAutoCommit(false);
             bTrans = true;
             dbl.executeSql(strSql);
             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
             return buildRowStr();
         } catch (Exception e) {
             throw new YssException("修改监控管理结果出错", e);
         } finally {
             dbl.endTransFinal(conn, bTrans);
         }
	}
    
    
    private void doCheck() throws YssException{
    	
    	StringBuffer buff = new StringBuffer();
    	ResultSet rs = null;
    	try{
    		
    		buff.append(" select 1 from ").append(pub.yssGetTableName("tb_comp_resultdata"));
    		buff.append(" where FCOMPDATE=").append(dbl.sqlDate(this.compDate)).append(" and fcheckstate=1");
    		buff.append(" fportcode=").append(dbl.sqlString(this.portCode)).append(" and FINDEXCFGCODE=").append(dbl.sqlString(this.indexCfgCode));
    		
    		rs = dbl.openResultSet(buff.toString());
    		while(rs.next()){
    			throw new YssException(" 当日监控结果已生成,请进行反确认以后,再重新操作!!!");
    		}
    	}catch(Exception e){
    		  e.getMessage();
    	}
    }
    
    
}
