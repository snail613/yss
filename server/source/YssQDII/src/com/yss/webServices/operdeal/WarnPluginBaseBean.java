package com.yss.webServices.operdeal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.yss.dsub.BaseBean;
import com.yss.dsub.DbBase;
import com.yss.dsub.YssPub;
import com.yss.projects.para.set.pojo.BEN_PLUGIN;
import com.yss.projects.para.set.pojo.BEN_PLUGIN_PRODUCE;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

//by zhouwei 20120113 预警父类
public class WarnPluginBaseBean extends BaseBean
   implements com.yss.projects.act.PluginExecuteBase{
	protected BEN_PLUGIN_PRODUCE plugPro=null;//插件类，作为传输数据的载体
	protected BEN_PLUGIN plug=null;
	protected ArrayList list=null;
	protected static Logger log=Logger.getLogger("E");//log4j
	protected Map mapPlugPro=new HashMap();
	protected String curDate=null;//当前处理日期
	protected String[] groupPorts=null;//组合群-组合数组
	protected String sessionId="";//sessionID
	protected String prefixTag="";//原有的前缀
	
	public String getPrefixTag() {
		return prefixTag;
	}

	public void setPrefixTag(String prefixTag) {
		this.prefixTag = prefixTag;
	}

	//初始化参数
	public void initBean(BEN_PLUGIN plug,ArrayList list,String sessionId){
		this.list=list;
		this.plug=plug;
		this.sessionId=sessionId;
		//成员变量赋值
		this.plugPro=(BEN_PLUGIN_PRODUCE) list.get(0);
		this.curDate=YssFun.formatDate(plugPro.getOperDate_Begin(),"yyyy-MM-dd");
		if(plugPro.getC_PORT_CODE().equalsIgnoreCase("public")){//公共指标
			groupPorts=getAllPorts().split("\t");
		}else{
			groupPorts=plugPro.getC_PORT_CODE().split("\t");//组合-组合群
		}		
	}
		
	//业务处理
	public void doOperation(){
		
	}
	//阀值作为判断条件
	public String buildFilterSql(String sql,BEN_PLUGIN_PRODUCE pp){
		String thresholdValue=pp.getC_PLUGIN_VALUE();
		String[] thresholdValues=null;
		String reStr="";
		if(pp.getC_PLUGIN_CONDITION().equalsIgnoreCase("between")){//阀值区间
			thresholdValues=thresholdValue.split(",");
			reStr=" ( ";
			for(int i=1;i<=thresholdValues.length;i++){
				if(i==thresholdValues.length){
					reStr+=" ( " +sql+" between "+thresholdValues[i-1].split("-")[0]+" and "+thresholdValues[i-1].split("-")[1]+" ) )";
				}else{
					reStr+=" ( " +sql+" between "+thresholdValues[i-1].split("-")[0]+" and "+thresholdValues[i-1].split("-")[1]+" ) or";
				}
			}		
		}else if(pp.getC_PLUGIN_CONDITION().equalsIgnoreCase("out")){//待补充
			
		}else{
			reStr="("+sql+pp.getC_PLUGIN_CONDITION()+pp.getC_PLUGIN_VALUE()+")";
		}
		return reStr;
	}
	//返回处理结果
	public BEN_PLUGIN_PRODUCE getPluginProduce(){
		return this.plugPro;
	}
	//转换代码，例如 001,002转换成'001','002'
	public String sqlCodes(String sCodes) {
        String strReturn = "";
        String[] sPortAry;
        if (sCodes.trim().length() > 0) {
            if (sCodes.substring(sCodes.length() - 1, sCodes.length()).equalsIgnoreCase(",")) {
                sCodes = sCodes.substring(0, sCodes.length() - 1);
            }
            sPortAry = sCodes.split(",");
            for (int i = 0; i < sPortAry.length; i++) {
                if (YssFun.right(sPortAry[i], 1).equalsIgnoreCase("'") &&
                    YssFun.left(sPortAry[i], 1).equalsIgnoreCase("'")) { //判断如果字符前后都有"'"，那么就没必要在前后都加"'"  胡昆 20070912
                    strReturn = strReturn + sPortAry[i] + ",";
                } else {
                    strReturn = strReturn + "'" + sPortAry[i].replaceAll("'", "''") +
                        "',";
                }
            }
            if (strReturn.length() > 0) {
                strReturn = YssFun.left(strReturn, strReturn.length() - 1);
            }
        } else if (sCodes.length() == 1 && sCodes.equalsIgnoreCase(" ")) { //当传入的代码为空格时
            strReturn = "' '";
        } else if (sCodes.trim().length() == 0 && sCodes.equalsIgnoreCase("")) {
            strReturn = "''";
        }
        return strReturn;
    }
	/**
	 * 获取带前缀的表名,不判断表是否存在 临时表(Tb_Temp_XXXXBak)不加前缀 对
	 * tb_sys,tb_pfsys,tb_base,tb_fun,及A开头的财务表 的表不加前缀
	 * 
	 * @param 无前缀的表名
	 * @param 组合群代码
	 * @return String
	 */
	public String yssGetTableName(String sTableName, String sAssetGroupCode)
			throws YssException {
		String PrefixTB = ""; // 默认表的前缀
		String sqlStr = "";
		try {
			PrefixTB=sAssetGroupCode;
			if (sTableName.toLowerCase().indexOf("tb_temp") < 0
					&& !sTableName.toUpperCase().startsWith("A")
					&& PrefixTB.length() != 0
					&& !sTableName.toLowerCase().startsWith("tb_sys")
					&& !sTableName.toLowerCase().startsWith("tb_pfsys")
					&& !sTableName.toLowerCase().startsWith("tb_base")
					&& !sTableName.toLowerCase().startsWith("tb_fun")) {
				if (YssFun.left(sTableName, 3).equalsIgnoreCase("Tb_")) {
					return YssFun.left(sTableName, 3) + PrefixTB
							+ YssFun.right(sTableName, sTableName.length() - 2);
				} else if (YssFun.left(sTableName, 6)
						.equalsIgnoreCase("PK_Tb_")) {
					return YssFun.left(sTableName, 6) + PrefixTB
							+ YssFun.right(sTableName, sTableName.length() - 5);
				} else {
					return PrefixTB + sTableName;
				}
			} else {
				return sTableName;
			}
		} catch (Exception ex) {
			throw new YssException("通过组合群代码【" + sAssetGroupCode + "】取系统表名失败");
		} 
	}
	public void setPlugProInfoToClient(String proState,String resultState,String info){
		this.plugPro.setC_PRODUCE_STATE(proState);
		this.plugPro.setC_RESULT_STATE(resultState);
		this.plugPro.setC_RESULT_INFO(info);
	}
	//获取当前系统可用的组合与组合群信息
	public String getAllPorts(){
    	Connection conn=null;
    	ResultSet rs=null;
    	ResultSet rsPort=null;
    	String reStr="";
    	try{
    		String sql="select * from TB_SYS_ASSETGROUP";
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){
    			String groupCode=rs.getString("FAssetGroupCode");
    			sql="select * from tb_"+groupCode.trim()+"_Para_Portfolio where fcheckstate=1";
    			rsPort=dbl.openResultSet(sql);
    			while(rsPort.next()){
    				reStr+=groupCode+"-"+rsPort.getString("FPortCode")+"\t";
    			}
    			dbl.closeResultSetFinal(rsPort);
    		}
    		if(reStr.length()>1){
    			reStr=reStr.substring(0, reStr.length()-1);
    		}
    	}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return reStr;
    }

	public BEN_PLUGIN_PRODUCE getExecuteResult() {		
		return this.plugPro;
	}

	public void initBean(BEN_PLUGIN arg0, ArrayList arg1, HttpSession arg2) {
		this.list=arg1;
		this.plug=arg0;
		YssPub obj=(YssPub) arg2.getAttribute(YssCons.SS_PUBLIC);
		this.setYssPub(obj);
		this.prefixTag=obj.getPrefixTB();
		this.sessionId=arg2.getId();
		//成员变量赋值
		this.plugPro=(BEN_PLUGIN_PRODUCE) list.get(0);
		this.curDate=YssFun.formatDate(plugPro.getOperDate_Begin(),"yyyy-MM-dd");
		if(plugPro.getC_PORT_CODE().equalsIgnoreCase("public")){//公共指标
			groupPorts=getAllPorts().split("\t");
		}else{
			groupPorts=plugPro.getC_PORT_CODE().split("\t");//组合-组合群
		}				
	}

	public String getGroupPorts(HttpSession arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*转换为资产类型名称
	01 -证券投资基金
	02 -资产管理计划
	03 -信托计划产品 
	04 -企业年金产品
  */
	protected String getAssetTypeName(String assetType){
		String assetTypeName="";
		if("01".equals(assetType)){
			assetTypeName="证券投资基金";
		}else if("02".equals(assetType)){
			assetTypeName="资产管理计划";
		}else if("03".equals(assetType)){
			assetTypeName="信托计划产品 ";
		}else if("04".equals(assetType)){
			assetTypeName="企业年金产品";
		}else{
			assetTypeName="";
		}
		return assetTypeName;
	}
	//获取组合的相关信息，如 资产类型，套账号等
	/**
	 * modify by huangqirong 2013-04-24 bug #7486 调整组合套帐链接相关代码
	 * */
	protected void getPortRelaInfo(String group,String port){
		//ResultSet rs=null;
		//String sql="";
		String oldGroup=pub.getPrefixTB();
		YssFinance finace = new YssFinance();
		try{
			pub.setPrefixTB(group);
			
			finace.setYssPub(this.pub);
			//edit by songjie 2013.04.28 传组合群代码有误 STORY #3895 需求深圳-(南方基金)QDII估值系统V4.0(紧急)20130425001
			String tmpSetId = finace.getBookSetId(pub.getPrefixTB() , port);
			if(tmpSetId != null && tmpSetId.trim().length() > 0 ){
				plugPro.setBookSetCode(tmpSetId);
				plugPro.setAssetType(this.getAssetTypeName(finace.getPortCodeAbout(port , "FAssetType")));
				plugPro.setBookSetCode(tmpSetId);
				plugPro.setCreatorCode(pub.getUserCode());
				plugPro.setCreatorName(pub.getUserName());
				plugPro.setBussinessModule("预警系统");
				plugPro.setBussinessSubModule("预警系统");
			}
			
			/*sql=" select a.FAssetType,b.FBookSetCode  from "+pub.yssGetTableName("Tb_Para_Portfolio")+" a"
		       +" left join "+pub.yssGetTableName("Tb_Vch_PortSetLink")+" b on a.FPortCode=b.FPortCode"
		       +" where a.FPORTCODE="+dbl.sqlString(port);
			rs=dbl.openResultSet(sql);
			if(rs.next()){
				plugPro.setAssetType(this.getAssetTypeName(rs.getString("FAssetType")));
				plugPro.setBookSetCode(rs.getString("FBookSetCode"));
				plugPro.setCreatorCode(pub.getUserCode());
				plugPro.setCreatorName(pub.getUserName());
				plugPro.setBussinessModule("预警系统");
				plugPro.setBussinessSubModule("预警系统");
			}
			*/
			plugPro.setMacClientIP(pub.getClientPCAddr());
			plugPro.setMacClientName(pub.getClientPCName());
			plugPro.setMacClientAddr(pub.getClientMacAddr());
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			//dbl.closeResultSetFinal(rs);
			pub.setPrefixTB(oldGroup);
		}
	}
}
