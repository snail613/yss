package com.yss.main.operdeal.platform.pfoper.pubpara;

import com.yss.util.YssException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;

import com.yss.dsub.BaseBean;
import java.sql.SQLException;

public class ParaWithPort
    extends BaseBean {
	private String paraGroupCode = "";
    private String pubParaCode = "";
    private String portCode = "";
    private String ctlGrpCode = "";
    private String ctlCode = "";
    private String paraID = "";//20110620 added by liubo.Story #1132
    
    public String getParaID() {
		return paraID;
	}

	public void setParaID(String paraID) {
		this.paraID = paraID;
	}
    public String getPortCode() {
        return portCode;
    }

    public String getParaGroupCode() {
        return paraGroupCode;
    }

    public void setPubParaCode(String pubParaCode) {
        this.pubParaCode = pubParaCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setParaGroupCode(String paraGroupCode) {
        this.paraGroupCode = paraGroupCode;
    }

    public String getPubParaCode() {
        return pubParaCode;
    }

    public String getFCtlGrpCode() {
        return ctlGrpCode;
    }

    public void setCtlGrpCode(String ctlGrpCode) {
        this.ctlGrpCode = ctlGrpCode;
    }

    public void setCtlCode(String ctlCode) {
        this.ctlCode = ctlCode;
    }

    public ParaWithPort() {
    }

    /**
     * 返回特定的控件值 sj add 20080528
     * @return Object
     * @throws YssException
     */
    public Object getSpeParaResult() throws YssException {
        String reStr = "";
        String sqlStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String resultValue = "";
        boolean isCheck = false;
        try {
            sqlStr = "select FParagroupCode,FPubParaCode,FParaId from " +
                pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                " and FParaId <> 0" +
                //delete by songjie 2013.04.27 报ORA-00600 内部错误代码 错误
                //" group by  FParagroupCode,FPubParaCode,FParaId " +
                " order by FParaID desc"; //,FParaId";  //modify huangqirong 2013-04-18 bug #7476 取参数ID最大的那个
            grpRs = dbl.openResultSet(sqlStr);
            while (grpRs.next() && reStr.length() == 0) { //若已经有返回值，则不再需要进入循环 sj edit 20080530
                sqlStr =
                    "select para.*,face.FCtlInd as FCtlInd,face.FCtlType as FCtlType from (select *" +
                    " from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                    " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                    " and FParaId = " +
                    grpRs.getInt("FParaId") +
                    ") para left join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face on " +
                    "  para.FCtlGrpCode = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode";
                rsTest = dbl.openResultSet(sqlStr);
                while (rsTest.next()) {
                    if (rsTest.getString("FCtlCode").equalsIgnoreCase(this.
                        ctlCode)) { //如果是需要返回的控件，则先将控件值保存
                        resultValue = rsTest.getString("FCtlValue");
                    }
                    //2008-6-12 单亮 从数据库取出的rsTest.getString("FCtlInd")的值有可能为空
                    if (rsTest.getString("FCtlInd") != null) {
                        if (rsTest.getString("FCtlInd").equalsIgnoreCase("<port>")
                            &&
                            rsTest.getString("FCtlValue").split("[|]")[0].
                            equalsIgnoreCase( //如果满足条件
                                portCode)) {
                            isCheck = true;
                        }
                    }                    
                    //delete by songjie 2012.04.19 BUG 4308 QDV4建行2012年4月17日01_B
                    //resultValue = isCheck ? resultValue : "";  //add by huangqirong 2012-02-29 story #2088
                    if (resultValue.length() > 0 && isCheck) { //如果需要返回的控件的控件值有值,而且满足条件
                        reStr = resultValue;
                        break;
                    }
                }
                //外层循环每循环一次将打开一个游标，此处进行关闭 sunkey 20090112
                dbl.closeResultSetFinal(rsTest);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException("获取公共参数出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsTest, grpRs);
        }

    }
    
    /**
     * 返回特定的控件值 
	 *add by huangqirong 2012-04-18 story #2088
     * @return Object
     * @throws YssException
     */
    public Object getSpeParaResult1() throws YssException {
        String reStr = "";
        String sqlStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String resultValue = "";
        boolean isCheck = false;
        try {
            sqlStr = "select FParagroupCode,FPubParaCode,FParaId from " +
                pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                " and FParaId <> 0" +
                " group by  FParagroupCode,FPubParaCode,FParaId order by FParaID desc"; //,FParaId";  //modify huangqirong 2013-04-18 bug #7476 取参数ID最大的那个
            grpRs = dbl.openResultSet(sqlStr);
            while (grpRs.next() && reStr.length() == 0) {
                sqlStr =
                    "select para.*,face.FCtlInd as FCtlInd,face.FCtlType as FCtlType from (select *" +
                    " from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                    " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                    " and FParaId = " +
                    grpRs.getInt("FParaId") +
                    ") para left join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face on " +
                    "  para.FCtlGrpCode = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode";
                rsTest = dbl.openResultSet(sqlStr);
                while (rsTest.next()) {
                    if (rsTest.getString("FCtlCode").equalsIgnoreCase(this.
                        ctlCode)) { //如果是需要返回的控件，则先将控件值保存
                        resultValue = rsTest.getString("FCtlValue");
                    }                    
                    if (rsTest.getString("FCtlInd") != null) {
                        if (rsTest.getString("FCtlInd").equalsIgnoreCase("<port>")
                            &&
                            rsTest.getString("FCtlValue").split("[|]")[0].
                            equalsIgnoreCase( //如果满足条件
                                portCode)) {
                            isCheck = true;
                        }
                    }
                    resultValue = isCheck ? resultValue : "";
                    if (resultValue.length() > 0 && isCheck) { //如果需要返回的控件的控件值有值,而且满足条件
                        reStr = resultValue;
                        break;
                    }
                }
                dbl.closeResultSetFinal(rsTest);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException("获取公共参数出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsTest, grpRs);
        }
    }

    public Object getParaResult() throws YssException {
        String reStr = "";
        String sqlStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String resultValue = "";
        try {
            sqlStr = "select FParagroupCode,FPubParaCode,FParaId from " +
                pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                //" where FParagroupCode = " + dbl.sqlString(this.paraGroupCode) +
                //" and FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                //" and FCtlGrpCode = " + dbl.sqlString(this.ctlGrpCode) +
                " and FParaId <> 0" +
                " group by  FParagroupCode,FPubParaCode,FParaId";
            grpRs = dbl.openResultSet(sqlStr);
            while (grpRs.next()) {
                sqlStr =
                    "select para.*,face.FCtlInd as FCtlInd,face.FCtlType as FCtlType from (select *" +
                    " from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                    //" where FParagroupCode = " + dbl.sqlString(this.paraGroupCode) +
                    //" and FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                    " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                    // " and FCtlGrpCode = " + dbl.sqlString(this.ctlGrpCode) +
                    " and FParaId = " +
                    grpRs.getInt("FParaId") +
                    ") para left join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face on " +
                    "  para.FCtlGrpCode = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode";
                rsTest = dbl.openResultSet(sqlStr);
                while (rsTest.next()) {
                    if (rsTest.getString("FCtlInd").equalsIgnoreCase("<Result>")) {
                        resultValue = rsTest.getString("FCtlValue");
                        resultValue += "\t";
                    }
                    if (rsTest.getString("FCtlInd").equalsIgnoreCase("<port>") 
                        &&
                        rsTest.getString("FCtlValue").split(",")[0].equalsIgnoreCase(
                            portCode)) {
                        reStr = resultValue;
                    }
                  //20110603 Added by liubo.Stroy #1132.
                  //取值的范围，增加<Color>，表示需要取出“财务估值表停牌信息颜色设置”参数的参数值
                    
                  //20110620 modified by liubo.Story #1132
                  //首先通过<portForPara>的参数类型值来确定设定的参数的编号ParaID，用以确定某投资组合是否设置了停牌颜色规则。
                  //然后取颜色时，需要增加ParaID的条件
                  //-------------------------------------
                    if (rsTest.getString("FCtlInd").equalsIgnoreCase("<portForPara>")
                            &&
                            rsTest.getString("FCtlValue").split("\\|")[0].equalsIgnoreCase(portCode))
                    {                    	
                    	reStr = rsTest.getString("FParaID");
                    	break;
                    }
                    else if (rsTest.getString("FCtlInd").equalsIgnoreCase("<Color>") && rsTest.getString("FCtlInd").equalsIgnoreCase(this.portCode) && rsTest.getString("FParaID").equalsIgnoreCase(paraID))
                    {
                    	reStr = rsTest.getString("FCtlValue");
                    	break;
                    	
                    }
                    else if (rsTest.getString("FCtlInd").equalsIgnoreCase("<ColorType>") && rsTest.getString("FCtlInd").equalsIgnoreCase(this.portCode) && rsTest.getString("FParaID").equalsIgnoreCase(paraID))
                    {
                    	reStr = rsTest.getString("FCtlValue");
                    	break;
                    	
                    }
                    
                    //---------------end----------------------
                }
                dbl.closeResultSetFinal(rsTest);
//            if (reStr.length() > 0) {
//               break;
//            }
            }
            if (reStr.length() > 0) {
                reStr = reStr.substring(reStr.length() - 1);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException("获取公共参数出错！", e);
        } finally {
            dbl.closeResultSetFinal(grpRs);
            dbl.closeResultSetFinal(rsTest);
        }
    }
    
    /**
     * 获取所有通用参数值，值以键值对的形式存放在哈希表中
     * @return	存放通用参数信息的哈希表 键：组合代码 ；值：实际设置的值
     * @throws YssException
     */
	public Object getParaResultAll() throws YssException {
		String sqlStr = "";
		ResultSet rs = null;
		
		Hashtable htParaResult = new Hashtable();
		try {
			sqlStr = "select para.FCtlValue,PARA.FPARAID,face.FCtlInd as FCtlInd from "
				+ "(select FCTLCODE,FCtlValue,FCTLGRPCODE,FPARAID from " + pub.yssGetTableName("Tb_Pfoper_Pubpara")
				+ " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) + " and FParaId <> 0) para"
				+ " left join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face"
				+ " on para.FCtlGrpCode = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode order by para.fparaid";
			rs = dbl.openResultSet(sqlStr);
			while (rs.next()) {
				
				//如果哈希表中存在FPARAID相同的值，表示已经获取到result，并且他在哈希表中的存放形势为 FPAEAID：value
				//此时需要将result取出来，并存放到已组合代码为主键的键值对中，同时删除取过的值
				//如果不存在的话代表第一次取到组合还没取到值，直接将FPARAID作为主键录入组合代码
				if (rs.getString("FCtlInd").equalsIgnoreCase("<port>")) {
					if(htParaResult.containsKey(rs.getString("FPARAID"))){
						htParaResult.put(rs.getString("FCTLVALUE").split("[|]")[0], htParaResult.get(rs.getString("FPARAID")));
						htParaResult.remove(rs.getString("FPARAID"));
					}else{
						htParaResult.put(rs.getString("FPARAID"), rs.getString("FCTLVALUE").split("[|]")[0]);
					}
				}
				
				//原理同上面
				if (rs.getString("FCtlInd").equalsIgnoreCase("<Result>")) {
					if(htParaResult.containsKey(rs.getString("FPARAID"))){
						htParaResult.put(htParaResult.get(rs.getString("FPARAID")), rs.getString("FCTLVALUE"));
						htParaResult.remove(rs.getString("FPARAID"));
					}else{
						htParaResult.put(rs.getString("FPARAID"), rs.getString("FCTLVALUE"));
					}
				}
			}
			return htParaResult;
		} catch (Exception e) {
			throw new YssException("获取公共参数出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

    public ArrayList parseValues() throws YssException {
        return null;
    }
    /**
	 * add by huangqirong 2012-12-07 story #3371
	 * 获取海富通持仓模式
	 * false :双边
	 * true ： 单边
	 * */    
    public boolean getFutursPositionType(String portCode) throws YssException {
    	boolean positionType = false ; //默认双边
    	CtlPubPara para = new CtlPubPara();
    	para.setYssPub(this.pub);
    	String tempResult = para.getFutursPositionType("PositionType", "futures", "FuturesPositionType", "PortCode", "ComboBox1", portCode);
    	
    	if(tempResult != null){
    		if(tempResult.indexOf("|") > -1){
    			tempResult = tempResult.split("\\|")[0];
    			if("BDB".equalsIgnoreCase(tempResult))
    				positionType = true; //单边
    			else if("ASB".equalsIgnoreCase(tempResult))
    				positionType = false; //双边 
    		}else if(tempResult.indexOf(",") > -1){
    			tempResult = tempResult.split("\\,")[0];
    			if("BDB".equalsIgnoreCase(tempResult))
    				positionType = true; //单边
    			else if("ASB".equalsIgnoreCase(tempResult))
    				positionType = false; //双边 
    		}
    	}    	
    	return positionType;
    }
    /**
     * add dongqingsong 2013-05-10 story #3871 联动清算 建行联动清算需求
     * @return 获取特定控件的值  ，返回清算账户来源    其中返回值：为01值是webservic来源 ，02则是现金账户来源
     * @throws YssException
     */
    public Object getSpeParaResultJH(String portCode) throws YssException {
        String reStr = "";
        String sqlStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String portCodeAndName="";
        String getQsSource="";
        try {
        	portCodeAndName=this.PortAndName(portCode);
        	getQsSource=this.getQsSource(portCodeAndName);
        	return getQsSource;
        } catch (Exception e) {
            throw new YssException("获取清算账户来源出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsTest, grpRs);
        }

    }
	
    /**
     * add dongqingsong 2013-05-10 story #3871 联动清算 建行联动清算需求
     * @param portCode
     * @return 组合和组合名称的连接值
     * @throws YssException
     */
    public String PortAndName(String portCode) throws YssException{
    	String portcode=portCode;
    	String PrefixTb = pub.getPrefixTB().toString();
    	ResultSet rs=null;
    	String portAndName="";
    	 if(portcode.contains("'")){
         	portcode=portCode.replace("'", "");
         }
         try {
         	String getNameSql="select a.fportcode,a.fportname, a.fportcode||'|'||a.fportname as PortName from Tb_"+PrefixTb+"_Para_Portfolio a " +
         			"where a.fportcode='"+portcode+"'";
         	rs = dbl.openResultSet(getNameSql);
         	while(rs.next()){
         		 portAndName=rs.getString("PortName");
         	}
         }catch (Exception e) {
             throw new YssException("获取公共参数出错！", e);
         } 
         return portAndName;
    }
	
    /**
     * add dongqingsong 2013-05-10 story #3871 联动清算 建行联动清算需求
     * @param str
     * @return
     * @throws YssException
     * 01代表清算账户来源是webservice 02代表清算账户来源是现金账户
     */
    public String getQsSource(String str) throws YssException{
    	ResultSet rs=null;
    	String fctlvalueArray=null;
    	String PrefixTb = pub.getPrefixTB().toString();
    	String source="";
    	try{
	    	String sqlStr = "select t.fctlvalue from Tb_"+PrefixTb+"_Pfoper_Pubpara t where FPubParaCode = 'qsAccountSource' " +
			"and t.fctlcode = 'ComboBox' and t.fparaid =" +
			" (select max(tt.fparaid) as fparaid from Tb_"+PrefixTb+"_Pfoper_Pubpara tt  where FPubParaCode = 'qsAccountSource'" +
			" and tt.fctlvalue ="+dbl.sqlString(str)+")";
	    	rs = dbl.openResultSet(sqlStr);
			while(rs.next()){
				fctlvalueArray=rs.getString("fctlvalue");
			}
			if(fctlvalueArray == null)
				return "";
			String[] strInfo=fctlvalueArray.split(",");
			source=strInfo[0];
			if(source==" "||source==null||source.equals("")){
				source="01";
			}
    	}catch (Exception e) {
            throw new YssException("获取清算账户来源出错！", e);
		    }
		return source;
    }
}
