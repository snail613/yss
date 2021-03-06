package com.yss.main.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepCustomBean;
import com.yss.main.cusreport.RepFormatBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.dao.IBuildReport;
import com.yss.main.dao.IClientReportView;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.report.compliance.pojo.ReportCompPropVO;
import com.yss.util.YssException;
import com.yss.vsub.YssFinance;

public class CommonRepBean extends BaseBean implements IClientReportView {

    private String repCode = ""; //报表代码
    private String repCtlParam = ""; //报表参数
    private String repData = ""; //报表数据
    private boolean dataIsTrue = true; //报表数据是否正确
    private String ctlGrpCode = "";//报表控件组代码
    private String param;//条件参数
    
    //--- add by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 start---//
    private boolean crossGroup = false;
    public void setCrossGroup(boolean crossGroup){
    	this.crossGroup = crossGroup;
    }
    
    public boolean getCrossGroup(){
    	return this.crossGroup;
    }
    
    private String assetGroupCode = "";
    public void setAssetGroupCode(String assetGroupCode){
    	this.assetGroupCode = assetGroupCode;
    }
    
    public String getAssetGroupCode(){
    	return this.assetGroupCode;
    }
    //--- add by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 end---//
    
	public void setRepCode(String repCode) {
        this.repCode = repCode;
    }

    public void setRepCtlParam(String repCtlParam) {
        this.repCtlParam = repCtlParam;
    }

    public void setDataIsTrue(boolean dataIsTrue) {
        this.dataIsTrue = dataIsTrue;
    }

    public String getRepCode() {
        return repCode;
    }

    public String getRepCtlParam() {
        return repCtlParam;
    }

    public boolean isDataIsTrue() {
        return dataIsTrue;
    }

    public CommonRepBean() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(repData).append("\f\r\f\r");
        buf.append(dataIsTrue);
        return buf.toString();
    }

    /**
     * getOperValue
     *modify huangqirong 2012-11-15 story #3272
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType)  throws YssException {
    	if("getportCodeBySetId".equalsIgnoreCase(sType)){
    		YssFinance finace = new YssFinance();
    		finace.setYssPub(this.pub);
    		if( this.param.indexOf("\f\t") > -1){
    			String[] reqAry = null;
    			reqAry = this.param.split("\f\t");
    			if(reqAry.length > 3)
    				return finace.getPortCode(reqAry[3]);
    		}else
    			return "";
    	}
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        try {
            if (sRowStr.equals("")) {
                return;
            }
            reqAry = sRowStr.split("\f\t");
            this.repCode = reqAry[0];
            this.repCtlParam = reqAry[1];
            this.ctlGrpCode = reqAry[2];
            //记录条件信息
            this.param=sRowStr;
            //--- add by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 start---//
            if(reqAry.length >= 5){
            	this.crossGroup = Boolean.valueOf(reqAry[3]);
            	this.assetGroupCode = reqAry[4];
            }else{
            	this.assetGroupCode = pub.getAssetGroupCode();
            }
            //--- add by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 end---//
        } catch (Exception e) {
            throw new YssException("解析自定义报表请求出错", e);
        }

    }

    /**
     * getReportData
     *
     * @param sReportType String
     * @return String
     */
    public String getReportData(String sReportType) throws YssException {
//      StringBuffer buf = new StringBuffer();
    	//对导出操作作判断
    	if(sReportType.equalsIgnoreCase("getExport")){
    		return getComplExportData();
    	}
        IBuildReport br = (IBuildReport) pub.getOperDealCtx().
            getBean("commonrep");
		//add by songjie 2013.02.25 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001
        String currentAsset = pub.getPrefixTB();
        try {
            br.setYssPub(pub);
//         buf.append(getReportHeaders(sReportType)).append(YssCons.
//               YSS_LINESPLITMARK);
            br.initBuildReport(this);
          //add by huangqirong 2011-10-19 Story #1747
        	if(sReportType.equalsIgnoreCase("getdata")){
        		BaseBuildCommonRep CommonRep=(BaseBuildCommonRep)br;
        		CommonRep.setRepBean(this);
        		CommonRep.setYssPub(pub);
        		return CommonRep.getReportData(repCode);
        	}
            //---end---
            //this.repData = br.buildReport("");
            //===============增加封账状态的判断，如已封账，返回封账信息 edit by qiuxufeng 20101108 QDV4太平2010年09月16日03_A
          //modify by huangqirong 2011-07-20 story #1101
            String tmpInfo ="";
            if(sReportType.equalsIgnoreCase("getsearch"))
            	sReportType="getsearch";
            else
            	sReportType="";
            //--- add by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 start---//
            if(this.crossGroup && assetGroupCode.trim().length() > 0 && !this.assetGroupCode.equals(pub.getPrefixTB())){
            	pub.setPrefixTB(assetGroupCode);
            	pub.setAssetGroupCode(assetGroupCode);
            }
            //--- add by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 end---//
            tmpInfo = br.buildReport(sReportType);
            //---end---
            if(tmpInfo.startsWith("<OFFACCT>")) {
            	return tmpInfo;
            } else {
            	this.repData = tmpInfo;
            }
            //================end=========
            String reStr = this.buildRowStr();
            return reStr;
//         buf.append(br.buildReport(""));
//         valuationRepOper.saveReport("");
//         return buf.toString();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } 
        //--- add by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 start---//
        finally{
        	pub.setPrefixTB(currentAsset);
        	pub.setAssetGroupCode(currentAsset);
        }
        //--- add by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 end---//
    }
    /**
     * @author zhouwei
     * @throws YssException 
     * 获取违规监控结果的导出数据
     */
    private String getComplExportData() throws YssException{
    	String[] arrStr=param.split("\f\t");
    	String beginDate=arrStr[0];//监控起始时间
    	String endDate =arrStr[1];//监控结束时间
    	String[] sportCodes=arrStr[2].split(",");//组合号码数组
    	ResultSet rs=null;
    	Map compresultMap=new HashMap();
    	Map handMap=new HashMap();;//手动生成的监控结果
    	Map fportCodeMap=new HashMap();//保存有提示函配置信息的组合号
    	Map promMap=new HashMap();;//存放提示函基础信息,键值是组合号码
    	ReportCompPropVO vo=null;//存放提示函基础信息的值对象
    	String returnStr=null;
		StringBuffer sb=new StringBuffer();
		String assertGroupCode=pub.getAssetGroupCode();
		String messageStr="";//提示信息
		int flag=0;//标志位数值大于0，代表查询条件下没有违规记录
		//---add by songjie 2013.02.21 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 start---//
		String[] groupPort = null;
		String fportCode= "";
		String originalGroup = pub.getAssetGroupCode();
		//---add by songjie 2013.02.21 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 end---//
	    String tmeplatePath ="";//提示函导出模版路径 add by zhaoxianlin 20130305 story #3688
    	try{
    		//不考虑跨组合群   	
    		for(int i=0;i<sportCodes.length;i++){
    			fportCode=sportCodes[i];  //组合号
    			//--- add by songjie 2013.02.21 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 start---//
    			groupPort = fportCode.split("-");
    			assertGroupCode = groupPort[0];
    			fportCode = groupPort[1];
    			pub.setPrefixTB(assertGroupCode);
    			//--- add by songjie 2013.02.21 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 end---//
    			
    			int flag1=0;//标志位数值大于0，代表该组合具有违规记录
    			sb.setLength(0);
    			//导出自动生成的违规监控结果
    			//查询违规的监控结果与它的监控配置信息
        	sb.append("select a.frecheckstate,a.FCompDate,a.FIndexCfgCode,a.FFactRatio,b.FCompParam,b.FViolateAnalysis,b.FIndexCfgName,a.fremindresult from ")//modified by zhaoxianlin 20130304 story #3688 增加fremindresult字段
        		  .append(pub.yssGetTableName("tb_comp_resultdata",assertGroupCode)).append(" a,")
        		  .append(pub.yssGetTableName("tb_Comp_IndexCfg",assertGroupCode)).append(" b")
        		  .append(" where a.FCompDate>=").append(dbl.sqlDate(beginDate))
        		  .append(" and a.FCompDate<=").append(dbl.sqlDate(endDate))
        		  .append(" and a.FCompResult='Violate' and a.FIndexCfgCode=b.FIndexCfgCode")
        		  .append(" and a.fportcode='").append(fportCode).append("'  and a.Fstate<>2");
        	    rs = dbl.openResultSet(sb.toString());
        	    while(rs.next()){
        	    	String fcomdate=rs.getString("FCompDate");//监控日期
        	    	String findexcfgcode=rs.getString("FIndexCfgCode").toLowerCase();//指标配置代码
        	    	String findexcfgname=rs.getString("FIndexCfgName");//指标配置名称
        	    	String fcompparam=rs.getString("FCompParam");//监控参数
        	    	String fviolateanalysis=rs.getString("FViolateAnalysis");//违规分析脚本
        	    	String ffactratio=rs.getString("FFactRatio");//实际比值
                    String remindResult = rs.getString("fremindresult");//add by zhaoxianlin 20130304 story #3688
        	    	//edit by songjie 2013.02.21 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 
        	    	//sportCodes[i] 改为 assertGroupCode + "-" + fportCode
                    String keyValue= assertGroupCode + "-" + fportCode+"\f\t"+fcomdate+"\f\t"+findexcfgcode+"\f\t"+remindResult;//组合号，日期，指标配置,监控结果 构成键值
        	    	//modified by zhaoxianlin 20130304 story #3688 key 增加提示结果
        	    	String value=ffactratio+"\f\t"+fcompparam+"\f\t"+findexcfgname+"\f\t"+parseFViolateAnalysis(fviolateanalysis)+"\f\t"+remindResult;//实际比值  监控参数  指标配置名称  违规分析脚本参数构成值
        	    	//modified by zhaoxianlin 20130304 story #3688 value 增加提示结果
        	    	int recheckstate=rs.getInt("frecheckstate");
        	    	if(recheckstate==1){//确认过的违规的监控结果
            	    	if(!compresultMap.containsKey(keyValue)){
                	    	compresultMap.put(keyValue, value);
            	    	}
            	    	flag1++;
        	    	}else{//未确认的违规记录
        	    		//edit by songjie 2013.02.21 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 
        	    		//sportCodes[i] 改为 fportCode 添加 组合群
        	    		messageStr+="[组合群" + assertGroupCode + ",组合"+fportCode+",监控事项"+findexcfgcode+"，监控日期"+fcomdate+"],";
        	    	}
        	    	flag++;
        	    }
        	    //手动生成违规的监控结果
        	    sb.setLength(0);
    			//查询违规的监控结果与它的监控配置信息包含阀值等信息
        		sb.append("select a.frecheckstate,a.FCompDate,a.FIndexCfgCode,b.FIndexCfgName,a.FNUMERATOR,a.FDENOMINATOR,a.FFACTRATIO,a.FCOMPStandard from ")
        		  .append(pub.yssGetTableName("tb_comp_resultdata",assertGroupCode)).append(" a,")
        		  .append(pub.yssGetTableName("tb_Comp_IndexCfg",assertGroupCode)).append(" b")
        		  .append(" where a.FCompDate>=").append(dbl.sqlDate(beginDate))
        		  .append(" and a.FCompDate<=").append(dbl.sqlDate(endDate))
        		  .append(" and a.FCompResult='Violate' and a.FIndexCfgCode=b.FIndexCfgCode")
        		  .append(" and a.fportcode='").append(fportCode).append("'  and a.Fstate=2");
        	    rs = dbl.openResultSet(sb.toString());
        	    while(rs.next()){
        	    	String fcomdate=rs.getString("FCompDate");//监控日期
        	    	String findexcfgcode=rs.getString("FIndexCfgCode").toLowerCase();//指标配置代码
        	    	String findexcfgname=rs.getString("FIndexCfgName");//指标配置名称
        	    	String fnumerator=rs.getString("FNUMERATOR");//分子
        	    	String fdenominator=rs.getString("FDENOMINATOR");//分母
        	    	String fcompstandard=rs.getString("FCOMPStandard");//阀值
        	    	String ffactratio=rs.getString("FFactRatio");//实际比值
        	    	int recheckstate=rs.getInt("frecheckstate");
        	    	if(recheckstate==1){//确认的违规
        	    		String fcomprule=composeFindexcfg2(new BigDecimal(fcompstandard).setScale(4, BigDecimal.ROUND_HALF_UP),
            	    			new BigDecimal(ffactratio).setScale(4, BigDecimal.ROUND_HALF_UP));//监控事项规则
        	    		//edit by songjie 2013.02.21 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 
        	    		//sportCodes[i] 改为 assertGroupCode + "-" + fportCode
            	    	String key=assertGroupCode + "-" + fportCode+"\f\t"+fcomdate;////组合号， 监控日期 构成键值
            	    	String value=findexcfgname+"\f\t"+fcomprule;
            	    	if(!handMap.containsKey(key)){
            	    		List list=new ArrayList();
            	    		list.add(value);
            	    		handMap.put(key,list);
            	    	}else{
            	    		List list=(List)handMap.get(key);
            	    		list.add(value);
            	    		handMap.put(key,list);
            	    	}
            	    	
            	    	flag1++;
        	    	}else{//未确认的违规
        	    		//edit by songjie 2013.02.21 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 
        	    		//sportCodes[i] 改为 fportCode 添加 组合群
        	    		messageStr+="[组合群" + assertGroupCode + ",组合"+fportCode+",监控事项"+findexcfgcode+"，监控日期"+fcomdate+"],";
        	    	} 	    	
        	    	flag++;
        	    }
        	    dbl.closeResultSetFinal(rs);
        	    if(flag1>0){//组合具有确认的违规记录，查询提示函的配置信息
        	    	sb.setLength(0);
        	    	sb.append("select f.FTrusteeName,c.FLinkManName,f.fusername,c.FPhoneCode,c.FFaxCode,f.FPortCode,f.FDateFormat,f.FTransferPath,f.FAssetCode,f.FPortName from ( select ")
        	    	.append("a.ftrusteecode,b.FTrusteeName,d.fusername,a.FPortCode,a.FDateFormat,a.FTransferPath,e.FAssetCode,e.FPortName,a.FLinkManCode from ")
        	    	  .append(pub.yssGetTableName("Tb_comp_prompting",assertGroupCode)).append(" a,").append(pub.yssGetTableName("Tb_Para_Trustee",assertGroupCode)).append(" b,").append(pub.yssGetTableName("tb_sys_userlist")).append(" d,")
        	    	  .append(pub.yssGetTableName("tb_Para_Portfolio",assertGroupCode)).append(" e")
        	    	.append(" where a.FSupervisionUserCode=d.fusercode and a.FTrusteeCode=b.FTrusteeCode  and e.FPortCode=a.FPortCode").append(" and a.FPortCode='" ).append(fportCode).append("'")
        	    	.append(" and a.fcheckstate=1 and b.fcheckstate=1 and e.fcheckstate=1 ) f left join ( select * from ")	    	  
        	    	.append(pub.yssGetTableName("tb_Para_Linkman",assertGroupCode)).append(" where frelatype='Trustee' and fcheckstate=1 )").append(" c on")	    	
        	    	.append(" f.ftrusteecode=c.FRelaCode  and f.FLinkManCode=c.FLinkManCode");
        	    	rs=dbl.openResultSet(sb.toString());
        	    	while(rs.next()){
        	    		vo=new ReportCompPropVO();
        	    		vo.setfTrusteeName(rs.getString("FTrusteeName"));
        	    		vo.setfLinkManName(rs.getString("FLinkManName"));
        	    		vo.setfUserName(rs.getString("fusername"));
        	    		vo.setfPhoneCode(rs.getString("FPhoneCode"));
        	    		vo.setfFaxCode(rs.getString("FFaxCode"));
        	    		vo.setfPortCode(rs.getString("FPortCode"));
        	    		vo.setfPortName(rs.getString("FPortName"));
        	    		vo.setfDateFormat(rs.getString("FDateFormat"));
        	    		vo.setfTransferPath(rs.getString("FTransferPath"));
        	    		vo.setfAssetCode(rs.getString("FAssetCode"));
        	    		fportCodeMap.put(rs.getString("FPortCode"), rs.getString("FPortCode"));
						//--- edit by songjie 2013.02.25 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 start---//
        	    		if(!promMap.containsKey(assertGroupCode + "-" + rs.getString("FPortCode"))){
        	    			promMap.put(assertGroupCode + "-" + rs.getString("FPortCode"), vo);
        	    		}
						//--- edit by songjie 2013.02.25 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 end---//
        	    	}
        	    	 dbl.closeResultSetFinal(rs);
        	    }
    		}

    		//根据前面结果，获取监控事项信息
    		if(flag==0){//返回信息提示，没有违规记录可导出
    			return "没有违规记录";
    		}
    		if(handMap.size()==0 && compresultMap.size()==0){//违规记录都未确认
    			//edit by songjie 2013.02.21 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001
    			return "监控日期区间【"+beginDate+"-"+endDate+"】内的所选 组合群-组合【"+arrStr[2]+"】的违规记录未确认，不能导出";
    		}
    		if(promMap.size()==0){//查询的组合提示函配置不满足条件
    			//edit by songjie 2013.02.21 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001
				return "无法导出违规记录，请检查 组合群-组合【"+arrStr[2]+"】的提示函配置";
			} 
    		String headerStr="";//返回的头信息
    		Map notPrompMap=new HashMap();//筛选没有提示函配置或者未审核
    		Map fportDateMap=new HashMap();//提示函是以组合号与日期作为键值区分的
            String conSymbol[] =null;//条件符号add by zhaoxianlin 20130304 story #3688
    		//自动生成
    		Iterator it=compresultMap.entrySet().iterator();
    		while(it.hasNext()){
    			java.util.Map.Entry entry=(java.util.Map.Entry) it.next();
    			String[] key=((String) entry.getKey()).split("\f\t");//组合号，日期，指标配置构成键值
    			String[] value=((String) entry.getValue()).split("\f\t");//监控结果实际比值  监控参数  指标配置名称  违规分析脚本参数构成值
    			String portDate=key[0]+"\f\t"+key[1];
    			List ctlvalueList=new ArrayList();//动态存放阀值
				BigDecimal ffactratio=new BigDecimal(value[0]).setScale(4,BigDecimal.ROUND_HALF_UP);//比值   			
    			if(promMap.containsKey(key[0])){//该组合号具有提示函配置，查询监控配置对应的的阀值集
    				//---add by songjie 2013.02.21 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 start---//
    				groupPort = key[0].split("-");
    				assertGroupCode = groupPort[0];
    				fportCode = groupPort[1];
    				//---add by songjie 2013.02.21 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 end---//
    				sb.setLength(0);
					sb.append("select b.fctlvalue from Tb_PFSys_FaceCfgInfo a,")
					//edit by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001
		    		.append(pub.yssGetTableName("Tb_Comp_PortIndexLink",assertGroupCode)).append(" b")
		    		.append(" where a.fctlgrpcode=b.fctlgrpcode and a.fctlcode=b.fctlcode")
		    		.append(" and lower(a.fctlgrpcode)='").append(value[1].toLowerCase()).append("'")
		    		.append(" and a.fctlind in ").append(value[3])
					//edit by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001
		    		.append(" and b.fportcode='").append(fportCode).append("' and lower(b.findexcfgcode)='").append(key[2].toLowerCase()).append("'")
					.append(" order by a.fctlind asc");
					rs=dbl.openResultSet(sb.toString());
					java.sql.Clob clob = null;
					while(rs.next()){
							clob=rs.getClob("fctlvalue");
							String rtn=clob.getSubString((long)1,(int)clob.length());
							BigDecimal ctlvalue=new BigDecimal(rtn).setScale(4, BigDecimal.ROUND_HALF_UP);
							ctlvalueList.add(ctlvalue);
					}	
					 dbl.closeResultSetFinal(rs);
    			}else{
    				//选出没有提示函配置信息或者未审核的组合号码
    				if(!notPrompMap.containsKey(key[0])){  
    					//edit by songjie 2013.02.25 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001
    					headerStr=headerStr+key[0]+",";
        				notPrompMap.put(key[0], key[0]);
    				}
    				continue;
    			}
                /**add by zhaoxianlin 20130304 story #3688 start 只导出违规记录*/
                if(ctlvalueList.size()==0){
    				//return "请检查指标关联设置中是否设置监控指标"+key[2].toLowerCase()+"的监控值!";
    				continue;// 未添加到组合指标关联设置中的违规记录不做导出 modified by zhaoxianlin 20130410 BUG7502
    			}
    			conSymbol = getConSymbol(key);//获取指标违规条件
    			double factRatio = Double.parseDouble(value[0]);//实际比例
    	    	double ctlValueH  = Double.parseDouble(ctlvalueList.get(0).toString());//控件阀值（或区间指标中控件阀值最高值）
    	    	double ctlValueL=0;//控件阀值最低值
    	        if(ctlvalueList.size()>1){//存在多个阀值
    	    			ctlValueL  = Double.parseDouble(ctlvalueList.get(1).toString());
    	    		}
    	    		if(ctlvalueList!=null&&conSymbol!=null){//比较阀值和实际比例，不违规，则不输出
    					if(conSymbol.length>3){//通过截取到的符号数量判断是否是区间指标
    						if(conSymbol[1].toString().equals("<")&&conSymbol[3].toString().equals(">")){//违规方向
    							if(factRatio>=ctlValueL&&factRatio<=ctlValueH){//实际比例在区间内
    								continue;
    							}
    						}else if(conSymbol[1].toString().equals("<=")&&conSymbol[3].toString().equals(">=")){//违规方向
    							if(factRatio>ctlValueL&&factRatio<ctlValueH){//实际比例在区间内
    								continue;
    							}
    	    				}
    					}else{
    						if(conSymbol[1].toString().equals(">=")&&factRatio<ctlValueH){
    	    					//违规脚本中条件符号位>=且实际比例小于等于阀值时continue
    	    					continue;
    	    				}else if(conSymbol[1].toString().equals("<=")&&factRatio>ctlValueH){
    	    					//违规脚本中条件符号位<=且实际比例大于阀值时continue
    	    					continue;
    	    				}else if(conSymbol[1].toString().equals("<")&&factRatio>=ctlValueH){
    	    					//违规脚本中条件符号位<且实际比例大于等于阀值时continue
    	    					continue;
    	    				}else if(conSymbol[1].toString().equals(">")&&factRatio<=ctlValueH){
    	    					//违规脚本中条件符号位>且实际比例小于等于阀值时continue
    	    					continue;
    	    				}
    					}
    	    		}
    			/**add by zhaoxianlin 20130304 story #3688 end*/
    			//筛选提示函信息
    			if(fportDateMap.containsKey(portDate)){//多个监控指标的提示函的拼接  				
                    //String s=(String)fportDateMap.get(portDate)+"\f\f\f"+value[2]+"\f\n\t"+composeFindexcfg(ctlvalueList,ffactratio);
    				//modified by zhaoxianlin 20130304 story #3688 这里不再判断记录是否超过阀值，直接取库中remindResult字段的值
    				String s=(String)fportDateMap.get(portDate)+"\f\f\f"+value[2]+"\f\n\t"+value[4];
        			fportDateMap.put(portDate, s); 
    			}else{//每封提示函作为map的一条记录
    				vo=(ReportCompPropVO) promMap.get(key[0]);
    				String s=vo.getfTrusteeName()+"\f\t"+vo.getfDateFormat()+"\f\t"+vo.getfAssetCode()
    					 	+"\f\t"+vo.getfPortName()+"\f\t"+vo.getfUserName()+"\f\t"+vo.getfTransferPath()+"\f\t";
    				if(vo.getfPhoneCode()==null){
    					s+=" \f\t";
    				}else{
    					s+=vo.getfPhoneCode()+"\f\t";
    				}
    				if(vo.getfFaxCode()==null){
    					s+=" ";
    				}else{
    					s+=vo.getfFaxCode();
    				}
                    //s+="\f\t"+value[2]+"\f\n\t"+composeFindexcfg(ctlvalueList,ffactratio);
    				//modified by zhaoxianlin 20130304 story #3688 这里不再判断记录是否超过阀值，直接取库中remindResult字段的值
    				s+="\f\t"+value[2]+"\f\n\t"+value[4];
    				if(handMap.containsKey(portDate)){
    					List list=(List) handMap.get(portDate);
    					for(int i=0;i<list.size();i++){
    						String[] values=((String)list.get(i)).split("\f\t");
    						 s=s+"\f\f\f"+values[0]+"\f\n\t"+values[1];
    					}
    				}       			
    				fportDateMap.put(portDate, s);
    			}
    		}
    		//整合手动生成的监控结果
    		it=handMap.entrySet().iterator();
    		while(it.hasNext()){
    			java.util.Map.Entry e=(java.util.Map.Entry) it.next();
    			if(!fportDateMap.containsKey(e.getKey())){
    				String[] key=((String)e.getKey()).split("\f\t");
    				if(!promMap.containsKey(key[0])){
    					//选出提示函配置信息无或者未审核的组合号码
        				if(!notPrompMap.containsKey(key[0])){ 
        					//edit by songjie 2013.02.25 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001
    						headerStr=headerStr+key[0]+",";
            				notPrompMap.put(key[0], key[0]);
        				}
        				continue;
    				}
    				//每封提示函作为map的一条记录
    				vo=(ReportCompPropVO) promMap.get(key[0]);
    				String s=vo.getfTrusteeName()+"\f\t"+vo.getfDateFormat()+"\f\t"+vo.getfAssetCode()
    					 	+"\f\t"+vo.getfPortName()+"\f\t"+vo.getfUserName()+"\f\t"+vo.getfTransferPath()+"\f\t";
    				if(vo.getfPhoneCode()==null){
    					s+=" \f\t";
    				}else{
    					s+=vo.getfPhoneCode()+"\f\t";
    				}
    				if(vo.getfFaxCode()==null){
    					s+=" ";
    				}else{
    					s+=vo.getfFaxCode();
    				}
    				List list=(List) e.getValue();
					for(int i=0;i<list.size();i++){
						String[] values=((String)list.get(i)).split("\f\t");
						if(i==0){
							 s=s+"\f\t"+values[0]+"\f\n\t"+values[1];
						}else{
							 s=s+"\f\f\f"+values[0]+"\f\n\t"+values[1];
						}
					}
    				fportDateMap.put(e.getKey(), s);
    			}
    		}
    		//拼接提示函信息
    		it=fportDateMap.entrySet().iterator();
    		while(it.hasNext()){
    			java.util.Map.Entry e=(java.util.Map.Entry) it.next();
    			if(returnStr==null){
    				returnStr=(String)e.getKey()+"\f\t"+(String)e.getValue();
    			}else{//多个提示函
    				returnStr+="\f\t\f\t"+(String)e.getKey()+"\f\t"+(String)e.getValue();
    			}
    		}   		
    		if(!headerStr.equals("")){
    			if(!messageStr.equals("")){
    				//edit by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001
    				headerStr="请检查组合群-组合【"+headerStr+"】提示函配置。"+messageStr+"违规监控结果没有被确认";
    			} 
    			//--- add by songjie 2013.02.25 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 start---//
    			if(headerStr.indexOf(",") != -1){
    				headerStr = headerStr.substring(0, headerStr.length() - 1);
    			}
    			//--- add by songjie 2013.02.25 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 end---//
    			return headerStr+"\f\f\f\f"+returnStr;
    		}else{
    			if(!messageStr.equals("")){
    				return messageStr+"违规监控结果没有被确认"+"\f\f\f\f"+returnStr;
    			} else{
    				return "none"+"\f\f\f\f"+returnStr;
    			}	
    		}
    	}catch(Exception e){
    		throw new YssException(e.getMessage());
    	}finally{
    		 dbl.closeResultSetFinal(rs);
    		 //add by songjie 2013.02.21 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001
    		 pub.setPrefixTB(originalGroup);
    	}
}
/**
     * add by zhaoxianlin 20130304 story #3688
     * @param key
     * @return
     * @throws YssException
     */
    public String[] getConSymbol(String[] key) throws YssException{
    	String sql = "";
    	ResultSet rs = null;
    	String conSymbol[] =null;//条件符号
    	String violateAnalysis = "";//违规脚本
    	String indexCode ="";//指标代码
    	try{
    		indexCode = key[2].replaceFirst(key[2].substring(0,1), key[2].substring(0,1).toUpperCase());
    		indexCode = indexCode.replaceFirst(indexCode.substring(2,3), indexCode.substring(2,3).toUpperCase());
    		sql = " select * from "+ pub.yssGetTableName("Tb_Comp_IndexCfg")+" a where a.findexcfgcode = "+dbl.sqlString(indexCode);
    		rs = dbl.openResultSet(sql);
    		if(rs.next()){
    			violateAnalysis = rs.getString("FViolateAnalysis");
                    conSymbol = violateAnalysis.split(";");
    		}else{
    			throw new YssException("请判断是否设置指标"+key[2]+"违规脚本");
    		}
    	}catch(Exception e){
    		throw new YssException("获取违规记录条件符号出错");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    	return conSymbol;
    }
    //监督事项的内容构成
    private String composeFindexcfg(List list,BigDecimal ffactratio){
    	if(list.size()==0){//没有阀值
    		return "没有设定阀值";
    	}else if(list.size()==1){//只有一个阀值
    		BigDecimal ctlvalue=(BigDecimal)list.get(0);	
    		if(ffactratio.compareTo(ctlvalue)==1){
    			return "比例"+ffactratio+"超过阀值"+ctlvalue;
    		}else if(ffactratio.compareTo(ctlvalue)==-1){
    			return "比例"+ffactratio+"低于阀值"+ctlvalue;
    		}else{
    			return "比例"+ffactratio+"等于阀值"+ctlvalue;
    		}
    	}else if(list.size()>1){//具有多个阀值
    		Collections.sort(list);//把阀值从小到大排序
    		for(int i=0;i<list.size();i++){
    			BigDecimal ctlvalue=(BigDecimal)list.get(i);
    			if(ffactratio.compareTo(ctlvalue)==1){//比例大于阀值
    				//判断当前阀值是否是最后一个
    				if(i==list.size()-1){
    					return "比例"+ffactratio+"超过阀值"+ctlvalue;
    				}else{
    					continue;
    				}  			
        		}else if(ffactratio.compareTo(ctlvalue)==-1){//比例小于阀值
        			//判断当前阀值是否是list中第一个
        			if(i==0){
            			return "比例"+ffactratio+"低于阀值"+ctlvalue;
        			}else{
        				return "比例"+ffactratio+"位于阀值区间["+list.get(i-1)+"-"+ctlvalue+"]";
        			}
        		}else{
        			return "比例"+ffactratio+"等于阀值"+ctlvalue;
        		}
    		}
    	}
    	
    	return null;
    }
    //手动生成的监控结果监督事项的内容构成
    private String composeFindexcfg2(BigDecimal ctlvalue,BigDecimal ffactratio){
    	//只有一个阀值
		if(ffactratio.compareTo(ctlvalue)==1){
			return "比例"+ffactratio+"超过阀值"+ctlvalue;
		}else if(ffactratio.compareTo(ctlvalue)==-1){
			return "比例"+ffactratio+"低于阀值"+ctlvalue;
		}else{
			return "比例"+ffactratio+"等于阀值"+ctlvalue;
		}
    }
    //解析违规分析脚本中用到的参数
    private String parseFViolateAnalysis(String s){
    	String s1=s.replaceAll("FSjBl;", "");
    	String s2=null;
    	int index=s1.indexOf(";");
    	while(index!=-1){
    		s1=s1.substring(index+1);
        	index=s1.indexOf(">");
        	if(s2!=null){
            	s2=s2+","+"'"+s1.substring(0, index+1)+"'";//多个阀值参数以 ，分隔
        	}else{
            	s2="'"+s1.substring(0, index+1)+"'";//阀值参数
        	}
            s1=s1.substring(index+1);
            index=s1.indexOf(";");
    	}
    	if(s2!=null){//去除尾部 ，
    		s2=s2.substring(0, s2.length());
    	}
//    	System.out.println("("+s2+")");
    	return "("+s2+")";//作为sql的参数条件
    }
    public static void main(String[] args) {
    	CommonRepBean cb=new CommonRepBean();
    	List list=new ArrayList();
    	list.add(new BigDecimal("112.0").setScale(4, BigDecimal.ROUND_HALF_UP));
    	list.add(new BigDecimal("13").setScale(4, BigDecimal.ROUND_HALF_UP));
    	list.add(new BigDecimal("114").setScale(4, BigDecimal.ROUND_HALF_UP));
    	list.add(new BigDecimal("5.0").setScale(4, BigDecimal.ROUND_HALF_UP));
    	//Collections.sort(list);
    	System.out.println(list.toString());
    	System.out.println(cb.composeFindexcfg(list,new BigDecimal("111").setScale(4, BigDecimal.ROUND_HALF_UP)));
//    	cb.parseFViolateAnalysis("RsCount[FSjBl;<=;S<FIInvtRate1> || FSjBl;>=;S<FIInvtRate2>] >= 1");
	}
    /**
     * getReportHeaders
     *
     * @param sReportType String
     * @return String
     */
    public String getReportHeaders(String sReportType) throws YssException {
        String reStr = "", sFmtCode = "";
        RepTabCellBean rtc = null;
        RepFormatBean rf = null;
        StringBuffer buf = new StringBuffer();
        RepCustomBean rc = new RepCustomBean(this.repCode);
        rc.setYssPub(pub);
        rc.getSetting();
        if (rc.getRepType().equalsIgnoreCase("0")) { //明细报表
//         sFmtCode = rc.getOperValue("getFormatCode");
            if (rc.getRepFormatCode().length() == 0) {
                throw new YssException("自定义报表【" + this.repCode + "】尚未进行格式设计，请确认");
            }
            rf = new RepFormatBean(rc.getRepFormatCode());
            rf.setYssPub(pub);
            //edit by licai 20110212 STORY #441 需优化现在的报表自定义模板
            if(sReportType.equals("dynRep")){
            	reStr=rf.getDynRepListViewData3()+ "\f\f" + rc.getCusRepName();
            }else{
            	reStr = rf.getListViewData3() + "\f\f" + rc.getCusRepName();
            }
            //edit by licai 20110212 STORY #441 =====================end
        } else if (rc.getRepType().equalsIgnoreCase("1")) { //汇总报表
            rf = new RepFormatBean(rc.getRepFormatCode());
            rf.setYssPub(pub);
            rf.getSetting();
            //modify huangqirong 2013-03-08 story #3651 
            //rf.setRepCols(rc.getMaxCols());
            rf.setRepCols(rf.getRepCols()); 
            //---end---
            rf.setRepRows(1);
            buf.append("\f\f").append(rf.buildRowStr()).append("\f\f");
            rtc = new RepTabCellBean();
            buf.append(rtc.buildRowStr()).append("\f\f");
            buf.append(rc.getCusRepName());
            reStr = buf.toString();
        }
        this.repData = reStr;
        return this.buildRowStr();
    }
    
    
    //add by fangjiang 2010.12.22 STORY #301 需在进行现金头寸预测表查询之前，先对以下数据进行检查
    public String checkReportBeforeSearch(String para) throws YssException {
    	ResultSet rs = null;
        ResultSet rs1 = null;
        String strSql = "";
        String sResult = "";
        try {
            strSql = " select * from " + pub.yssGetTableName("Tb_Rep_Custom") +
                     " where FCusRepCode = " + dbl.sqlString(repCode) + " and FCheckState = 1 ";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                if (rs.getString("FParamSource") != null && rs.getString("FParamSource").trim().length() > 0) { //有参数来源的报表
                    
                } else {
                    if (rs.getString("FRepType").equalsIgnoreCase("0")) { //明细组合
                        strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DataSource") +
                        		 " where FRepDsCode = " + dbl.sqlString(rs.getString("FSubDsCodes"));
                        rs1 = dbl.openResultSet(strSql);
                        if (rs1.next()) {
                            if (rs1.getInt("FDsType") == 1) { //动态数据源
                                
                            } else if (rs1.getInt("FDsType") == 2) { //固定数据源
                                IBuildReport bRep = (IBuildReport) pub.getOperDealCtx().getBean(rs1.getString("FBeanId"));
                                bRep.setYssPub(this.pub);
                                sResult = bRep.checkReportBeforeSearch(para);
                            } else { //静态数据源
                                
                            }
                        }
                    } 
                    //---delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
//                    else if (rs.getString("FRepType").equalsIgnoreCase("1")) { //汇总组合
//                        
//                    }
                    //---delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
                }
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e); 
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rs1);
        }
    }
    //----------------------

    /**shashijie 2011.04.07 STORY #805 头寸表应该预测T日到T+N-1日共N个工作日的头寸 */
	public String getSaveDefuntDay(String sRepotyType) throws YssException {
		String sResult = "保存失败:";
		ResultSet rs = null;
        String strSql = "";
        try {
        	//预估天数
        	String dayValue = getDayValue(sRepotyType);
        	if (dayValue.equals("")) {
				sResult = "默认预估天数为空";
			} else {
				strSql = " SELECT * FROM Tb_PFSys_FaceCfgInfo " +
		    			" WHERE FCtlGrpCode = " + dbl.sqlString(ctlGrpCode) + " AND FCtlCode = 'TextBox6' ";
		    	rs = dbl.openResultSet(strSql);
		    	if (rs.next()) {
					updateDayParam(rs,dayValue);
					sResult = "保存成功";
				}
			}
		} catch (Exception e) {
			throw new YssException(e); 
		}finally {
			dbl.closeResultSetFinal(rs);
		}
		return sResult;
	}
    /**end*/

	/**shashijie 2011.04.07 修改预估天数*/
	private void updateDayParam(ResultSet rs,String dayValue) throws YssException {
		String sql = "";
		try {
			String param = rs.getString("FParam");
			String[] paramValue = param.split("\n");
			paramValue[8] = dayValue;
			String paramDeft = "";
			for (int i = 0; i < paramValue.length; i++) {
				paramDeft += paramValue[i] + "\n";
			}
			sql = "UPDATE tb_pfSys_facecfginfo set FParam = " +dbl.sqlString(paramDeft)+ 
					" WHERE FCtlGrpCode = " + dbl.sqlString(ctlGrpCode) + " AND FCtlCode = 'TextBox6' ";
			dbl.executeSql(sql);
		} catch (Exception e) {
			throw new YssException("解析预估天数出错", e);
		}
	}

	/**shashijie 获取预估天数*/
	private String getDayValue(String sRepotyType) throws YssException {
		String[] reqAry = null;
		String dayValue = ""; //预估天数
        try {
            if (sRepotyType.equals("")) {
                return dayValue ;
            }
            reqAry = sRepotyType.split("\t");
            String dayString = reqAry[4];//节假日...头寸预估天数
            dayValue = dayString.split(" 头寸预估天数：")[1].trim();//预估天数值
        } catch (Exception e) {
            throw new YssException("解析预估天数出错", e);
        }
		return dayValue;
	}
	
    public String GetBookSetName(String sPortCode) throws YssException
    {
    	return "";
    	
    }
	
}
