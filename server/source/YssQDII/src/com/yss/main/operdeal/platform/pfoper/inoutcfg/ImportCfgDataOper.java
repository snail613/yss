package com.yss.main.operdeal.platform.pfoper.inoutcfg;

import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.util.*;

import com.yss.main.operdeal.platform.pfoper.inoutcfg.pojo.*;
import com.yss.main.platform.pfsystem.inoutcfg.*;
import com.yss.main.syssetting.DataDictBean;
import com.yss.util.*;

import oracle.jdbc.*;
import oracle.sql.*;
import com.yss.vsub.YssDbFun;

public class ImportCfgDataOper
    extends BaseInOutCfgDeal {

    public ImportCfgDataOper() {
    }

    InOutCfgBean inOutBean = null; //2008-6-25 单亮
    //获取一些参数
    public String getOperValue(String sType) throws YssException {
        String sResult = "";
        InOutCfgBean inOutCfgBean = null;
        Object[] arrData = null;
        StringBuffer buf = new StringBuffer();
        Hashtable htTabField = null;
        try {
            if (sType.equalsIgnoreCase("getTabParam")) {
                inOutCfgBean = new InOutCfgBean();
                inOutCfgBean.setYssPub(pub);
                inOutCfgBean.parseRowStr(sAllData);

                htTabField = getTabField(inOutCfgBean);
                Set set = htTabField.keySet();
                arrData = set.toArray();
                for (int i = 0; i < arrData.length; i++) {
                    if (htTabField.get(arrData[i]) != null) {
                        buf.append(arrData[i]).append("\f\f\f");
                        buf.append(htTabField.get(arrData[i])).append("\f\f\r\f\f");
                    }
                }
                if (htTabField.get("tb_pfsys_inoutcfg") != null) {
                    buf.append("tb_pfsys_inoutcfg").append("\f\f\f");
                    buf.append(htTabField.get("tb_pfsys_inoutcfg")).append("\f\f\r\f\f");
                }
                sResult = buf.toString();
                if (sResult.endsWith("\f\f\r\f\f")) {
                    sResult = sResult.substring(0, sResult.length() - 5);
                }
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        return sResult;
    }

    //此方法可根据导出配置信息取出各表的字段信息
    private Hashtable getTabParam(InOutCfgBean inOutCfg) throws YssException {
        InOutCfgParamBean inOutCfgPOJO = new InOutCfgParamBean();
        inOutCfgPOJO.setYssPub(pub);
        String sKey = "";
        String sOutSql = "";
        String sTabFields = "";
        String sOutTmpTab = "";
        Hashtable htTabField = new Hashtable();
        InSourceParamBean inParamBean = null;
        try {
            inOutCfgPOJO.parseInRowStr(inOutCfg.getStrInCfgScript());
            for (int i = 0; i < inOutCfgPOJO.getHtInParam().size(); i++) {
                sKey = "[source" + (i + 1) + "]";
                if (inOutCfgPOJO.getHtInParam().get(sKey) != null) {
                    inParamBean = (InSourceParamBean) inOutCfgPOJO.getHtInParam().get(sKey);
                    sOutSql = " select * from " + inParamBean.getSSysTab() + " where 1=2 ";
                    sOutTmpTab = inParamBean.getSTmpTab();
                    sTabFields = buildTabParam(sOutSql);
                    if (htTabField.get(sOutTmpTab) == null) {
                        htTabField.put(sOutTmpTab, sTabFields);
                    }
                }
            }
            if (htTabField.get("tb_pfsys_inoutcfg") == null) {
                sOutSql = " select * from tb_pfsys_inoutcfg where 1=2 ";
                sTabFields = buildTabParam(sOutSql);
                htTabField.put("tb_pfsys_inoutcfg", sTabFields);
            }
            return htTabField;
        } catch (Exception ex) {
            throw new YssException("取表的字段配置信息出错", ex);
        }
    }

    //此方法可根据导出配置信息取出各表的字段信息
    private Hashtable getTabField(InOutCfgBean inOutCfg) throws YssException {
        InOutCfgParamBean inOutCfgPOJO = new InOutCfgParamBean();
        inOutCfgPOJO.setYssPub(pub);
        String sKey = "";
        String sOutSql = "";
        String sTabFields = "";
        String sOutTmpTab = "";
        Hashtable htTabField = new Hashtable();
        InSourceParamBean inParamBean = null;
        try {
            inOutCfgPOJO.parseInRowStr(inOutCfg.getStrInCfgScript());
            for (int i = 0; i < inOutCfgPOJO.getHtInParam().size(); i++) {
                sKey = "[source" + (i + 1) + "]";
                if (inOutCfgPOJO.getHtInParam().get(sKey) != null) {
                    inParamBean = (InSourceParamBean) inOutCfgPOJO.getHtInParam().get(sKey);
                    sOutSql = " select * from " + inParamBean.getSSysTab() + " where 1=2 ";
               		sOutTmpTab= inParamBean.getSTmpTab();
               		sTabFields = getTabFields(sOutSql);//by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
                    if (htTabField.get(sOutTmpTab) == null) {
                        htTabField.put(sOutTmpTab, sTabFields);
                    }
                }
            }
            if (htTabField.get("tb_pfsys_inoutcfg") == null) {
            	sOutSql=" select * from tb_pfsys_inoutcfg where 1=2 ";
            	sTabFields = getTabFields(sOutSql);//by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
                htTabField.put("tb_pfsys_inoutcfg", sTabFields);
            }
            return htTabField;
        } catch (Exception ex) {
            throw new YssException("取表的字段出错", ex);
        }
    }

    /**
     * 建表
     * @param inOutCfgPOJO InOutCfgParamBean
     * @param arrTab String[]
     * @throws YssException
     */
    private void doCreateTmpTab(InOutCfgParamBean inOutCfgPOJO, String[] arrTab) throws YssException {
        Hashtable htOutPara = new Hashtable(); //存放建表的SQL
        String sKey = "", sTmpKey = "", sTmpStr = "";
        String sSql = "", sRes = "";
        String[] arrTabPara = null;
        StringBuffer sCreateSql = new StringBuffer();
        InSourceParamBean inParamBean = null;
        ResultSet rs = null;
        try {
            for (int i = 0; i < inOutCfgPOJO.getHtInParam().size(); i++) { //根据导入的参数取出所有表的建表语句
                sKey = "[source" + (i + 1) + "]";
                if (inOutCfgPOJO.getHtInParam().get(sKey) != null) {
                    inParamBean = (InSourceParamBean) inOutCfgPOJO.getHtInParam().get(sKey);
                    sTmpKey = inParamBean.getSTmpTab();
                    if (htOutPara.get(sTmpKey) == null) {
                        sTmpStr = " select * from " + inParamBean.getSSysTab() + " where 1=2 ";
                        htOutPara.put(sTmpKey, sTmpStr);
                    }
                }
            }
            for (int i = 0; i < arrTab.length; i++) { //根据有数据的表来建表
                if (arrTab[i].equalsIgnoreCase("tb_pfsys_inoutcfg")) {
                    continue;
                }
                if (htOutPara.get(arrTab[i]) != null) {
                    sSql = htOutPara.get(arrTab[i]).toString();
                    //------xuqiji 20100326 MS00940 赢时胜(测试)2010年3月25日5_B 配置参数通用导入界面导入调拨类型的文件时报错 ----------//
                    //-----------------没有把原数据库中那张临时表删掉，所以创建不了新表--------------------//
                    if(dbl.yssTableExist(arrTab[i])){
                    	/**shashijie ,2011-10-12 , STORY 1698*/
                    	dbl.executeSql(dbl.doOperSqlDrop(" drop table " + arrTab[i]));
                    	/**end*/
                    }
                    //-------------------------end---------------------------//
                    //重新处理建临时表的过程，采用按系统表结构建临时表的方法 QDV4建行2009年2月23日02_B MS00266
                    // by leeyu 20090227
                    if (!dbl.yssTableExist(arrTab[i])) {
                        sRes = "create table " + arrTab[i] + " as (" + buildSql(sSql) + ")";
                        if (dbl.dbType == YssCons.DB_DB2) {
                            sRes += " definition only";
                        }
                        dbl.executeSql(sRes);
                    }
                    sCreateSql.delete(0, sCreateSql.length());
                }
            }
        } catch (Exception ex) {
            throw new YssException("创建表出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 向临时表中添加数据用的
     * @param sTabName String
     * @param htTabParam Hashtable
     * @param htTabIns Hashtable
     * @throws YssException
     */
    private void InsertDataToTmp(String sTabName,Hashtable htInParam, Hashtable htTabParam, Hashtable htTabIns) throws YssException {//添加新的参数 htTabParam QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
        //二次更改后的：把原来更新大对象的条件更改为，查询有没有空的大对象2008-6-27 单亮
        String sSql = "";
        String sField = "";
        String sFieldQus="";//保存问号 QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
        String sysTabName="";//系统表名 QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
        //String sQu = "";//这个就不要了 QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
        String sTabData = "";
        String[] arrData = null;
        String[] arrParam = null;
        Connection conn = dbl.loadConnection();;
        PreparedStatement pst = null;//采用批量更新的方式 QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
        int iIndexCol = -1; //2008-6-25 单亮 保存插入大对象时数据源的列的索引
        int iIndexRow = -1; //2008-6-25 单亮 保存插入大对象时数据源的行的索引
//        Hashtable htEmp = new Hashtable(); //2008-6-25 单亮 存储放大对象的位置
        ResultSet rs = null; //2008-6-25 单亮
        Reader reader1 = null;//add by yanghaiming 20100916 用于解决setString时字符串长度过长引起的错误
        try {
            conn.setAutoCommit(false);
			//循环的方式取出与临时表相对的系统表名 QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
            for (int i = 0; i < htInParam.size(); i++) {
                String sKey = "[source" + (i + 1) + "]";
                if (htInParam.get(sKey) != null) {
                	InSourceParamBean inParamBean = (InSourceParamBean) htInParam.get(sKey);
                    if(inParamBean.getSTmpTab().equalsIgnoreCase(sTabName)){
                		sysTabName=inParamBean.getSSysTab();
                		break;
                	}
                }
            }
			//QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
            if (htTabParam.get(sTabName) != null && htTabIns.get(sTabName) != null) {
                if (sTabName.equalsIgnoreCase("tb_pfsys_inoutcfg")) {
                    return;
                }
                arrParam = htTabParam.get(sTabName).toString().split(";");
                for (int iQ = 0; iQ < arrParam.length; iQ++) {
                    sField += arrParam[iQ].split(",")[0] + ",";
                    sFieldQus +=("?"+",");//QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
                }
                sSql = " delete from " + sTabName;
                if (sField.endsWith(",")) {
                    sField = sField.substring(0, sField.length() - 1);
                }
				//QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
                if(sFieldQus.endsWith(",")){
                	sFieldQus = sFieldQus.substring(0, sFieldQus.length()-1);
                }
				//QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
                dbl.executeSql(sSql);
                sSql = "insert into " + sTabName;
                sSql += "(" + sField + ")";
                sSql += " values("+sFieldQus+")";
                pst =conn.prepareStatement(sSql);//调整为通过批量插入数据的方法 QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
                sTabData = htTabIns.get(sTabName).toString();                
                arrData = sTabData.split("\r\t");
                HashMap hmPKField=buildPKField(sysTabName);//QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
                for (int iRow = 0; iRow < arrData.length; iRow++) {
                    //sQu = "";//不要了 QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
                	String clobField="";//大字段 QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
                	String condSql="";//查询条件 QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
                	String sPKFieldValue="";//主键条件 QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
                    if (arrData[iRow].length() == 0) {
                        continue;
                    }
                    HashMap hmValue=parse(arrData[iRow]);//将单行数据解析放到集合中 QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
                    for (int iCol = 0; iCol < arrParam.length; iCol++) { //改成这种有利于适应多种数据库的数据 by liyu 080526
                    	//重新处理数据的取值 QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
						Object objValue=null; //字段的值 QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
                    	String[] arrField=arrParam[iCol].split(",");//字段名,字段类型,字段长度 
                    	if(hmValue.get(arrField[0].toUpperCase())!=null){                    		
                    		//edit by licai 20101111 BUG #252 配置参数通用导入，制作人、审核人存在系统中没有的名字 
                    		String usercode=pub.getUserCode();//edit by licai 20101118 改为用户代码
                    		if("FCREATOR".equals(arrField[0].toUpperCase())){
                    			objValue=usercode;
                    		}
                    		else if("FCREATETIME".equals(arrField[0].toUpperCase())){
                    			objValue=YssFun.formatDatetime(new java.util.Date ());//"导入时间"
                    		}
                    		else if("FCHECKUSER".equals(arrField[0].toUpperCase())){
                    			objValue=usercode;
                    		}
                    		else if("FCHECKTIME".equals(arrField[0].toUpperCase())){
                    			objValue=YssFun.formatDatetime(new java.util.Date ());
                    		}else{
                    			objValue=hmValue.get(arrField[0].toUpperCase());
                    		}
                    		//edit by licai 20101111 BUG #252 配置参数通用导入，制作人、审核人存在系统中没有的名字==end=
                    		
                    		if(hmPKField.get(arrField[0].toUpperCase())!=null && String.valueOf(hmPKField.get(arrField[0].toUpperCase())).length()>0){
                    			sPKFieldValue+=(arrField[0]+"=");
                    		}
                    		if(arrField[1].toLowerCase().indexOf("char")>-1){//字符类型 
                    			//edit by yanghaiming 20100916处理字符串长度过长
                    			//modify  by zhangfa 20101018 MS01742    接口自定义配置中包含分隔符~，业务平台导出时会报错    QDV4华夏2010年09月14日01_B
                    			reader1 = new StringReader(replaceAll(String.valueOf(objValue),"in"));	
                    			pst.setCharacterStream(iCol+1,reader1,replaceAll(String.valueOf(objValue),"in").length());
                    			//pst.setString(iCol+1, replaceAll(String.valueOf(objValue)));
                    			
                    			if(hmPKField.get(arrField[0].toUpperCase())!=null && String.valueOf(hmPKField.get(arrField[0].toUpperCase())).length()>0)
                    				sPKFieldValue=(sPKFieldValue+dbl.sqlString(replaceAll(String.valueOf(objValue),"in"))+" and ");
                    			
                    		}else if(arrField[1].toLowerCase().indexOf("date")>-1){//日期类型
                    			pst.setDate(iCol+1, YssFun.toSqlDate(replaceAll(String.valueOf(objValue),"in")));
                    			if(hmPKField.get(arrField[0].toUpperCase())!=null && String.valueOf(hmPKField.get(arrField[0].toUpperCase())).length()>0)
                    				sPKFieldValue=(sPKFieldValue+dbl.sqlDate(replaceAll(String.valueOf(objValue),"in"))+" and ");
                    			
                    		}else if(arrField[1].toLowerCase().indexOf("number")>-1){//数值类型 
                    			pst.setDouble(iCol+1,YssFun.toDouble(replaceAll(String.valueOf(objValue),"in")));
                    			if(hmPKField.get(arrField[0].toUpperCase())!=null && String.valueOf(hmPKField.get(arrField[0].toUpperCase())).length()>0)
                    				sPKFieldValue=(sPKFieldValue+YssFun.toDouble(replaceAll(String.valueOf(objValue),"in"))+" and ");
                    		}else if(arrField[1].toLowerCase().indexOf("clob")>-1){//大字段类型
								if(dbl.getDBType() == YssCons.DB_ORA){
									//edit by songjie 2010.11.13 238 QDV4赢时胜（上海）2010年10月27日01_B
                    				pst.setString(iCol+1, " ");//应该只有oracle才有clob字段类型
                    				clobField=(clobField+arrField[0].toUpperCase()+",");
								}else{
									pst.setString(iCol+1, replaceAll(String.valueOf(objValue),"in"));//其他情况按字符串的处理方式来
								}
								if(hmPKField.get(arrField[0].toUpperCase())!=null && String.valueOf(hmPKField.get(arrField[0].toUpperCase())).length()>0)
                    				sPKFieldValue=(sPKFieldValue+dbl.sqlString(replaceAll(String.valueOf(objValue),"in"))+" and ");
                    		}else{//默认为字符类型
                    			pst.setString(iCol+1, replaceAll(String.valueOf(objValue),"in"));
                    			if(hmPKField.get(arrField[0].toUpperCase())!=null && String.valueOf(hmPKField.get(arrField[0].toUpperCase())).length()>0)
                    				sPKFieldValue=(sPKFieldValue+dbl.sqlString(replaceAll(String.valueOf(objValue),"in"))+" and ");
                    		}
                           //----------------------------MS01742---------------------------------------------------------------------------------
                    	}//QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324 以下代码不再用了

                    }
                    if(sPKFieldValue.endsWith(" and ")){
                    	sPKFieldValue =sPKFieldValue.substring(0,sPKFieldValue.length()-5);
                	}
                	if(clobField.endsWith(",")){
                		clobField =clobField.substring(0,clobField.length()-1);
                	}
                	pst.executeUpdate();
                	//下面代码用于更新大字段表处理 通过判断大字段来更新大字段的字段数据 QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
                	if(clobField.length()>0){
                		String sClobSql="";
                		PreparedStatement pstUpdate=null;
                		String[] arrClobField=clobField.split(",");
                		for(int i=0;i<arrClobField.length;i++){
                			sClobSql += (arrClobField[i]+"=?,");
                		}
						if(sPKFieldValue.length()==0){
							throw new YssException("系统表【" + sTabName + "】缺少主键，导入失败！");
						}
                		sSql="update "+sTabName+" set "+(sClobSql.endsWith(",")?sClobSql.substring(0, sClobSql.length()-1):sClobSql)+
                		" where "+sPKFieldValue;
                		pstUpdate=conn.prepareStatement(sSql);
                		if(dbl.dbType == YssCons.DB_ORA){
                			sSql ="select "+clobField+" from "+sTabName+" where "+
                			sPKFieldValue+" for update";//加行级锁
                			rs =dbl.openResultSet(sSql);
                			if(rs.next()){
	                			for(int i=0;i<arrClobField.length;i++){
	                				  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
	                				  // modify by jsc 20120809 连接池对大对象的特殊处理
	                				CLOB clob= dbl.CastToCLOB(rs.getClob(arrClobField[i]));
	                				//CLOB clob= ((oracle.jdbc.OracleResultSet)rs).getCLOB(arrClobField[i]);
	                				clob.putString(i+1, String.valueOf(hmValue.get(arrClobField[i])));
	                				pstUpdate.setClob(i+1, clob);
	                			}
	                			pstUpdate.executeUpdate();	                			
                			}
                		}else if(dbl.dbType == YssCons.DB_DB2){
                			//db2数据库
                		}else{
                			//
                		}
                		dbl.closeStatementFinal(pstUpdate);
                		dbl.closeResultSetFinal(rs);
                	}
                }
                dbl.closeStatementFinal(pst);
				//QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
                conn.commit();
                conn.setAutoCommit(true);
            }
        } catch (Exception ex) {
            try {
            	if(conn != null) 
            	{
            		conn.rollback();
            	}
            } catch (SQLException sql) {
                throw new YssException("向临时表中" + sTabName + "插入数据出错", sql);
            }
            throw new YssException("向临时表中添加数据出错", ex);
        } finally {
            dbl.endTransFinal(conn, false);
            dbl.closeResultSetFinal(rs);
        }
    }

    //此方法移到基类里  modify  by wangzuochun  2010.04.16 MS01081   系统增加通过通用导入导出来导词汇、菜单条、功能调用、权限等功能    QDV4赢时胜上海2010年03月12日01_AB
//    //此方法用于做特殊字符的替换 by leeyu
//    private String replaceAll(String sDS) throws YssException {
//        try {
//            if (sDS.indexOf("◆") > -1) {
//                sDS = sDS.replaceAll("◆", "\t");
//            }
//            if (sDS.indexOf("[tab]") > -1) { //根据通用导入前台传的前符号，将此转换成\t符 by leeyu 20090724 && this.tailPortCode.length() > 0) { //添加尾差帐户作为条件　QDV4赢时胜（上海）2009年4月28日04_B MS00422 by leeyu 200905012
//                sDS = sDS.replaceAll("\\[tab\\]", "\t");
//            }
//            return sDS;
//        } catch (Exception ex) {
//            throw new YssException("替换与处理特殊字符出错", ex);
//        }
//    }

    //创建根据临时表名为主键的删除条件语句
    private void insertToTable(Hashtable htInParam, Hashtable htField, Hashtable htTabIns) throws YssException {
        String delSql = "";
        String sKey = "", sTabName, sTmpTabName = "";
        InSourceParamBean inParamBean = null;
        Connection  conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
            conn.setAutoCommit(bTrans);
            bTrans = true;
            for (int i = 0; i < htInParam.size(); i++) {
                sKey = "[source" + (i + 1) + "]";
                if (htInParam.get(sKey) != null) {
                    inParamBean = (InSourceParamBean) htInParam.get(sKey);
                    sTabName = inParamBean.getSSysTab();
                    sTmpTabName = inParamBean.getSTmpTab();
                    if (htTabIns.get(sTmpTabName) == null) {
                        continue;
                    }
                    //modify by fangjiang 2010.11.09 BUG #280 调拨类型和调拨子类型名称导入时不应覆盖已有的类型
                    if(!(sTabName.equals("tb_base_transfertype") || sTabName.equals("tb_base_subtransfertype") || 
                    	 //--- add by songjie 2013.05.10 BUG 7684 QDV4建行2013年05月2日02_B start---//
                    	 //凭证字典 导入时不应覆盖已有的相同凭证字典代码的数据
                    	 sTabName.toUpperCase().indexOf("_VCH_DICT") != -1)){
                    	 //--- add by songjie 2013.05.10 BUG 7684 QDV4建行2013年05月2日02_B end---//
                    	delSql = " delete from " + sTabName + " " + sTabName + " where exists " +
                        " (select * from " + sTmpTabName + " " + sTmpTabName + " where " +
                        inParamBean.getSDelCond() + " )";
                    	delSql = buildSql(delSql);
                        dbl.executeSql(delSql);
                        //add by guolongchao 20120228 BUG3884 QDV4赢时胜(北京)2012年2月16日01_B.xls-------------start                     
                        if(sTabName.toLowerCase().indexOf("dao_pretreat")>=0) //若是接口预处理，将接口预处理对应的删除条件也级联删除掉     
                        {
                        	delSql = " delete from tb_" + pub.getAssetGroupCode() + "_Dao_TgtTabCond" +
                        			 " where fdpdscode in"+                    	
                                     " (select fdpdscode from "+sTmpTabName +" )";                        
                            dbl.executeSql(delSql);
                        }
                        //add by guolongchao 20120228 BUG3884 QDV4赢时胜(北京)2012年2月16日01_B.xls-------------end  
                    }  
                    insertTab(sTabName, sTmpTabName, htField, inParamBean);
                    //-------------------------
                }
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            try {
            	if (conn != null)
            	{
            		conn.rollback();
            	}
            } catch (Exception sqlE) {
                //防止删除了不必要的数据,必须如此 by liyu 080526
            }
            throw new YssException("删除数据库表错误", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    private void insertConfData(InOutCfgBean inOutCfgBean) throws YssException {
        String sSql = "";
        Connection conn = null;
        try {
            sSql = " delete from Tb_PfSys_InOutCfg where FInOutCode=" + dbl.sqlString(inOutCfgBean.getStrInOutCode());
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            dbl.executeSql(sSql);
            inOutCfgBean.addSetting();
            inOutCfgBean.checkStateId = 1; //审核
            inOutCfgBean.checkSetting();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException("插入配置文件信息出错", ex);
        } finally {
            dbl.endTransFinal(conn, false);
        }
    }

    private void insertTab(String sTabName, String sTmpTabName, Hashtable htField, InSourceParamBean inParamBean) throws YssException { //modify by fangjiang 2010.11.09 BUG #280 调拨类型和调拨子类型名称导入时不应覆盖已有的类型
        String sInsSql = "";
        String sInsFieldStr = "";
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;//add by songjie 2012.03.31 BUG 4122 QDV4农业银行2012年03月23日01_B
        try {
            if (htField.get(sTmpTabName) != null) {
                if (sTmpTabName.equalsIgnoreCase("tb_pfsys_inoutcfg")) {
                    return;
                }
                conn = dbl.loadConnection();
                conn.setAutoCommit(bTrans);
                sInsFieldStr = (String) htField.get(sTmpTabName);
            	if(!sInsFieldStr.endsWith(",")) sInsFieldStr+=",";//by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
            	sInsFieldStr=java.util.regex.Pattern.compile("[^;]*[$,]").matcher(sInsFieldStr).replaceAll("").replaceAll(";", ",");//去掉字段后面的字段类型 by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
                if (sInsFieldStr.endsWith(",")) {
                    sInsFieldStr = sInsFieldStr.substring(0,
                        sInsFieldStr.length() - 1);
                }
                sInsSql = " insert into " + sTabName + " ( " + sInsFieldStr +
                " ) select " +
                sInsFieldStr + "  from " + sTmpTabName;
                //modify by fangjiang 2010.11.09 BUG #280 调拨类型和调拨子类型名称导入时不应覆盖已有的类型
                if(sTabName.equals("tb_base_transfertype") || sTabName.equals("tb_base_subtransfertype") ||
                   //--- add by songjie 2013.05.10 BUG 7684 QDV4建行2013年05月2日02_B start---//
                   //凭证字典导入时不应覆盖已有的相同凭证字典代码的数据
                   sTabName.toUpperCase().indexOf("_VCH_DICT") != -1) {
                   //--- add by songjie 2013.05.10 BUG 7684 QDV4建行2013年05月2日02_B end---//
                	sInsSql += " where not exists (select * from " + sTabName + " where " + inParamBean.getSDelCond() + " )";
                }
                //------------------------------
                //---add by songjie 2012.03.31 BUG 4122 QDV4农业银行2012年03月23日01_B start---//
                if(sTabName.equalsIgnoreCase("tb_<group>_dao_pretreat")){ 
                	//若导入接口预处理数据 则需更新 组合群共享字段 若为 false  则 更新为'' 若为 true  则 更新为 所用组合群
					if (existsTabColumn_Ora(sTmpTabName, "FMGroupShare")) {
						String strSql = " update " + sTmpTabName + " set FMGroupShare = '' where FMGroupShare = 'false' ";
						dbl.executeSql(strSql);

						StringBuffer tBuffer = new StringBuffer();
						String tStr = "";

						strSql = "select fAssetGroupCode from tb_sys_assetgroup";
						rs = dbl.openResultSet(strSql);
						while (rs.next()) {
							tBuffer.append(rs.getString("fAssetGroupCode")).append(",");
						}
						tStr = tBuffer.substring(0, tBuffer.length() - 1);

						strSql = " update " + sTmpTabName + " set FMGroupShare = " + dbl.sqlString(tStr)
								+ "where FMGroupShare = 'true'";

						dbl.executeSql(strSql);
					}
                }
                //---add by songjie 2012.03.31 BUG 4122 QDV4农业银行2012年03月23日01_B end---//
                
                sInsSql = buildSql(sInsSql);
                dbl.executeSql(sInsSql);
                
                //---add by songjie 2011.07.07 BUG 2182 QDV4长信2011年6月28日01_B---//
                if(sTmpTabName.equalsIgnoreCase("tmp_fun_dict") && 
                   sTabName.equalsIgnoreCase("tb_fun_datadict")){
                	updateTmpTable(sTmpTabName);//更新临时表结构
                }
                //---add by songjie 2011.07.07 BUG 2182 QDV4长信2011年6月28日01_B---//
                
                conn.commit();
                bTrans = true;
                conn.setAutoCommit(bTrans);
                bTrans = false;
            }
        } catch (Exception ex) {
            throw new YssException("往" + sTabName + "插入数据错误", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            //---add by songjie 2012.03.31 BUG 4122 QDV4农业银行2012年03月23日01_B start---//
			if(rs != null){
				dbl.closeResultSetFinal(rs);
			}
			//---add by songjie 2012.03.31 BUG 4122 QDV4农业银行2012年03月23日01_B end---//
        }
    }
    
    /***
     * 根据表来判断表的字段是否存在
     * 不存在  : false 
     * 存在  : true
     * sTabName :表名
     * cloumsn : 要查询的表字段 
     * add by songjie 2012.03.31
     * BUG 4122 QDV4农业银行2012年03月23日01_B
     */
    public boolean existsTabColumn_Ora(String sTabName, String columns) throws YssException {
        boolean existCol = false;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select * from user_col_comments where upper(table_name)=upper(" + dbl.sqlString(sTabName) + ")" +
                " and upper(Column_Name) = " + dbl.sqlString(columns.toUpperCase());
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                existCol = true;
            }
            return existCol;
        } catch (Exception e) {
            throw new YssException("查询Oracle表" + sTabName + "的字段" + columns + "时出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //导入数据
    public void importData(String sRequestStr) throws YssException {
        InOutCfgBean inOutCfgBean = null;
        InOutCfgParamBean inOutCfgPOJO = null;
        String[] arrTab = null;
        String sData = "";
        Hashtable htTabIns = new Hashtable();
        try {
            inOutCfgBean = new InOutCfgBean();
            inOutCfgPOJO = new InOutCfgParamBean();
            inOutCfgBean.setYssPub(pub);
            inOutCfgPOJO.setYssPub(pub);
            arrTab = sRequestStr.split("\f\f\r\f\f");
            for (int i = 0; i < arrTab.length; i++) {
                if (arrTab[i].split("\f\t\f").length == 1) {
                    continue;
                }
                sData = arrTab[i].split("\f\t\f")[1];
                if (arrTab[i].split("\f\t\f")[0].trim().equalsIgnoreCase("tb_pfsys_inoutcfg")) {
                    inOutCfgBean.parseRowStr(arrTab[i].split("\f\t\f")[1]);
                    inOutCfgPOJO.parseInRowStr(inOutCfgBean.getStrInCfgScript());
                    inOutCfgPOJO.parseOutRowStr(inOutCfgBean.getStrOutCfgScript());
                }
                arrTab[i] = arrTab[i].split("\f\t\f")[0].trim();
                //1 取值
                if (htTabIns.get(arrTab[i]) == null) {
                    htTabIns.put(arrTab[i], sData);
                }
            }
            //2 建临时表的过程
            doCreateTmpTab(inOutCfgPOJO, arrTab);
            this.inOutBean = inOutCfgBean; //2008-6-25 单亮 获取这个bean以便在其他地方用
            Hashtable htParam = getTabParam(inOutCfgBean);
            Hashtable htField = getTabField(inOutCfgBean);
            for (int i = 0; i < arrTab.length; i++) {
                InsertDataToTmp(arrTab[i],inOutCfgPOJO.getHtInParam(), htParam, htTabIns);//QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
            }
            //3 向系统表中插入值
            insertToTable(inOutCfgPOJO.getHtInParam(), htField, htTabIns);
            // 4 更新配置文件
            insertConfData(inOutCfgBean);   
            //5 删除 临时表
            dropTmpTable(arrTab);
            //-----add by guolongchao 20111219 STORY1903 QDV4赢时胜（上海）2011年11月18日01_A.xls 更新导入存储表的表结构并且能够保存历史记录-----start
            updateImportStoreTableStructure();
			//-----add by guolongchao 20111219 STORY1903 QDV4赢时胜（上海）2011年11月18日01_A.xls 更新导入存储表的表结构并且能够保存历史记录-----end
        } catch (Exception ex) {
            throw new YssException("导入数据出错", ex);
        }
    }

    /**
     * add by songjie 2011.07.07
     * BUG 2182 QDV4长信2011年6月28日01_B
     * 若导入数据接口，自动更新相关数据接口对应的数据字典中临时表表结构
     * @throws YssException
     */
    private void updateTmpTable(String sTmpTabName) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        DataDictBean dataDict = null;//声明数据字典实例
        DataDictBean subDict = null;//声明数据字典实例
    	try{
			strSql = " select distinct tmpdict.FTabName from tmp_fun_dict " + 
				 	 " tmpdict where tmpdict.Ftabletype = 1";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				dataDict = new DataDictBean();// 新建实例
				dataDict.setYssPub(pub);
				dataDict.getTableInfo(rs.getString("FTabName").trim());// 从数据字典表中取表名为SHGH的数据

				subDict = new DataDictBean();// 新建实例
				subDict.setYssPub(pub);
				String[] lastInfo = dataDict.getSsubData().split("\f\f");// 拆分获取的表结构数据
				subDict.protocolParse(lastInfo[lastInfo.length - 1]);
				dataDict.createTab(rs.getString("FTabName").trim());
			}
    	}catch(Exception e){
    		throw new YssException("更新临时表结构出错", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    private void dropTmpTable(String[] arrTmpTab) throws YssException {
        try {
            for (int iTab = 0; iTab < arrTmpTab.length; iTab++) {
                if (arrTmpTab[iTab].trim().length() == 0 || arrTmpTab[iTab].equalsIgnoreCase("tb_pfsys_inoutcfg")) {
                    continue;
                }
                if (dbl.yssTableExist(arrTmpTab[iTab])) {
                	/**shashijie ,2011-10-12 , STORY 1698*/
                    dbl.executeSql(dbl.doOperSqlDrop(" drop table " + arrTmpTab[iTab]));
                    /**end*/
                }
            }
        } catch (Exception ex) {
            throw new YssException("删除临时表出错", ex);
        }
    }
	
	/**
	*将前台传过来一行的数据解析放到集合中，key=字段代码 value=值
	*QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
	*/
    private HashMap parse(String sDatas) throws YssException{
    	HashMap hmData=new HashMap();
    	try{
    		if(sDatas==null||sDatas.length()==0) return hmData;
    		String[] arrData=sDatas.split("\t");
    		for(int i=0;i<arrData.length;i++){
    			String field="";
    			String value="";
    			field=arrData[i].split("<=>")[0];
    			//modify  by zhangfa 20101018 MS01742    接口自定义配置中包含分隔符~，业务平台导出时会报错    QDV4华夏2010年09月14日01_B
    			if(arrData[i].split("<=>")[1]!=null){
    				value=replaceAll(arrData[i].split("<=>")[1],"in");
    			}else{
    				value=arrData[i].split("<=>")[1];
    			}
    			
    			//-------------------------------------------------------------------------------------------------------------
    			hmData.put(field, value);
    		}
    	}catch(Exception ex){
    		throw new YssException("解析数据出错",ex);
    	}
    	return hmData;
    }
	
	/**
	*取出表的主键并放入到集合中
	*QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
	*/
    private HashMap buildPKField(String tableName) throws YssException{
    	HashMap hmPKField=new HashMap();
    	String sPKField="";
    	try{
    		sPKField=buildTabPKFiled(tableName);
    		String[] arrField=sPKField.split(",");
    		for(int i=0;i<arrField.length;i++){
    			if(arrField[i].length()==0)continue;
    			hmPKField.put(arrField[i], ""+i);
    		}
    	}catch(Exception ex){
    		throw new YssException(ex.getMessage());
    	}
    	return hmPKField;
    }
    /**
     * add by guolongchao 20111219 STORY1903 QDV4赢时胜（上海）2011年11月18日01_A.xls
     * 更新导入存储表的表结构
     * @throws YssException 
     */
    private void updateImportStoreTableStructure() throws YssException
    {
    	String strSql = "";
        ResultSet rs = null;
        DataDictBean dataDict = null;
     	try{
 			strSql = " select distinct(a.ftabname)  from Tb_Fun_DataDict a  where a.ftabletype=2 "; 				 	
 			rs = dbl.openResultSet(strSql);
 			while (rs.next()) {
 				dataDict = new DataDictBean();
 				dataDict.setYssPub(pub);
 				dataDict.updateTableStructure(rs.getString("ftabname"));
 			}
     	}catch(Exception e){
     		throw new YssException("更新导入存储表的表结构发生出错", e);
     	}finally{
     		dbl.closeResultSetFinal(rs);
     	}
    }
}
