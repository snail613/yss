package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * add by songjie 2013.05.02
 * STORY #3895 需求深圳-(南方基金)QDII估值系统V4.0(紧急)20130425001
 * 添加 预警指标：停牌证券当日市值 / 前一估值日的资产净值 >= 0.25%
 * @author 宋洁
 *
 */
public class CheckYesNavScale extends BaseValCheck {
	public CheckYesNavScale(){
		
	}
	
	public String doCheck(Date curDate, String portCode) throws Exception {
		try{
			checkYesNavScale(curDate, portCode);
		}catch(Exception e){
			throw new YssException("停牌证券当日市值 / 前一估值日的资产净值 >= 0.25%预警执行 出错", e);
		}
		
		return "";
	}
	
	private void checkYesNavScale(Date curDate, String portCode)throws YssException{
		String strSql = "";
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		boolean isUseAmount = false;//是否启用 财务估值成交数量为0则为停牌 的通参
		String group = "";//组合群代码
		String[] groupPorts = null;
		java.util.Date suspendedDate = null;//停牌起始日
		String securityCode = "";//证券代码
		String securityName = "";//证券名称
		double yesNavValue = 0;//昨日资产净值
		double tadayNavValue = 0;//当日证券市值
		double scale = 0;//今日市值占昨日资产净值比
		String titleInfo = "停牌证券当日市值 / 前一估值日资产净值 >= 0.25% 的证券信息如下：\r\n";
		String showInfo = "";//明细预警提示信息
		try{
			CtlPubPara ctlpubpara = new CtlPubPara();
			ctlpubpara.setYssPub(pub);
			
			//判断是否启用 财务估值成交数量为0则为停牌 的通用参数
			isUseAmount = ctlpubpara.getIsUseAmount(portCode);
			
			BaseOperDeal baseOperDeal = new BaseOperDeal(); // 新建BaseOperDeal
			baseOperDeal.setYssPub(pub);
			
			//获取昨日 资产净值
			sb.append(" select FPortMarketValue from ").append(pub.yssGetTableName("Tb_Data_NavData"))
			.append(" where FNavDate = ").append(dbl.sqlDate(YssFun.addDay(curDate, -1)))
			.append(" and FPortCode = ").append(dbl.sqlString(portCode))
			.append(" and FReTypeCode = 'Total' and FKeyCode = 'TotalValue' ");
			
			rs = dbl.openResultSet(sb.toString());
			while(rs.next()){
				yesNavValue = rs.getDouble("FPortMarketValue");
			}
			
			dbl.closeResultSetFinal(rs);
			sb.setLength(0);
			
			//查询当天没有行情的证券数据  关联出 节假日群代码、 最近一个有行情的行情日期、估值日市值
			sb.append(" select st.FSecurityCode, vmp.FSecurityCode as FValSecurityCode, ")
			.append(" sec.FSecurityName, sec.FHolidaysCode, mk.FMktValueDate + 1 as FMktValueDate, navdt.FPortMarketValue from ")
			.append(pub.yssGetTableName("Tb_Stock_Security"))
			.append(" st left join (select FPortCode, fSubCode from ")
			.append(pub.yssGetTableName("Tb_Para_Portfolio_Relaship"))
			.append(" where FRelaType = 'MTV' and FCheckState = 1) pfrl on st.FPortCode = pfrl.FPortCode ")
			.append(" join (select FMtvCode, FMktSrcCode from ")
			.append(pub.yssGetTableName("Tb_Para_MtvMethod"))
			.append(" where FCheckState = 1) mtv on mtv.FMtvCode = pfrl.FSubCode ")
			.append(" join (select FMtvCode, FLinkcode from ")
			.append(pub.yssGetTableName("Tb_Para_Mtvmethodlink"))
			.append(" where FCheckState = 1) mtvlink on mtvlink.FMtvCode = pfrl.FSubCode ")
			.append(" and mtvlink.FLinkcode = st.FSecurityCode ")
			.append(" left join (select FSecurityCode, FValDate from ")
			.append(pub.yssGetTableName("Tb_Data_ValMktPrice"))
			.append(" where FCheckState = 1) vmp on vmp.FSecurityCode = st.FSecurityCode ")
			.append(" and vmp.FValDate = st.FStorageDate ")
			.append(" join (select FSecurityCode, FSecurityName, FHolidaysCode from ")
			.append(pub.yssGetTableName("Tb_Para_Security"))
			.append(" where FcheckState = 1) sec on sec.FSecurityCode = st.FSecurityCode ")
			.append(" left join (select max(FMktValueDate) as FMktValueDate, FSecurityCode,FMktSrcCode from ")
			.append(pub.yssGetTableName("Tb_Data_MarketValue"))
			.append(" where FCheckState = 1 and FMktValueDate <= ").append(dbl.sqlDate(curDate))
			.append(" group by FSecurityCode, FMktSrcCode) mk on mk.FSecurityCode = st.FSecurityCode ")
			.append(" join (select FGradeType5,FNavDate, FPortMarketValue, FPortCode from ")
			.append(pub.yssGetTableName("Tb_Data_NavData"))
			.append(" ) navdt on navdt.FGradeType5 = st.FSecurityCode ")
			.append(" and navdt.FNavDate = st.FStorageDate and navdt.FPortCode = st.FPortCode ")
			.append(" and mk.FMktSrcCode = mtv.FMktsrcCode ")
			.append(" where st.FStorageAmount <> 0 and st.FStorageDate = ").append(dbl.sqlDate(curDate))
			.append(" and st.FPortCode = ").append(dbl.sqlString(portCode))
			.append(" and st.FCheckState = 1 and vmp.FSecurityCode is null and mk.FMktValueDate is not null ");
			
			rs = dbl.openResultSet(sb.toString());
			while(rs.next()){
				securityCode = rs.getString("FSecurityCode");
				securityName = rs.getString("FSecurityName");
				tadayNavValue = rs.getDouble("FPortMarketValue");
				//停牌起始日 = 最近的一个有行情的行情日期 + 1个自然日
				suspendedDate = rs.getDate("FMktValueDate");
				
				//如果昨日资产净值 不为 0 
				if(yesNavValue != 0){
					scale = YssD.round(YssD.div(tadayNavValue, yesNavValue), 4);
					//若  估值日停牌证券市值 / 昨日资产净值 > 0.25%
					if(scale >= 0.0025){
						showInfo += "证券代码：" + securityCode + " 证券名称：" + securityName + 
						" 市值占前日净值比  = " + YssD.mul(scale, 100) + "% 停牌起始日：" + 
						YssFun.formatDate(suspendedDate) + "\r\n";
					}
				}
			}
			
			dbl.closeResultSetFinal(rs);
			sb.setLength(0);
			
			if(isUseAmount){
				sb.append(" select st.FSecurityCode,sec.FSecurityName,sec.FHolidaysCode,")
				.append("(case when mk.FMktValueDate is not null then mk.fmktvaluedate else vmp.FMktValueDate + 1 end) as FMktValueDate,")
				.append("navdt.FPortMarketValue from ")
				.append(pub.yssGetTableName("Tb_Stock_Security"))
				.append(" st left join (select FPortCode, fSubCode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio_Relaship"))
				.append(" where FRelaType = 'MTV' and FCheckState = 1) pfrl on pfrl.FPortCode = st.FPortCode ")
				.append(" join (select FMtvCode, FMktSrcCode from ")
				.append(pub.yssGetTableName("Tb_Para_MtvMethod"))
				.append(" where FCheckState = 1) mtv on mtv.FMtvCode = pfrl.FSubCode ")
				.append(" join (select FMtvCode, FLinkcode from ")
				.append(pub.yssGetTableName("Tb_Para_Mtvmethodlink"))
				.append(" where FCheckState = 1) mtvlink on mtvlink.FMtvCode = pfrl.FSubCode ")
				.append(" and mtvlink.FLinkcode = st.FSecurityCode ")
				.append(" join (select FSecurityCode, FSecurityName, FHolidaysCode from ")
				.append(pub.yssGetTableName("Tb_Para_Security"))
				.append(" where FCheckState = 1) sec on sec.FSecurityCode = st.FSecurityCode ")
				.append(" join (select FSecurityCode, FMktSrcCode, FMktValueDate from ")
				.append(pub.yssGetTableName("Tb_Data_MarketValue"))
				.append(" where FCheckState = 1 and FBargainAmount = 0) mk on mk.FSecurityCode = st.fSecurityCode ")
				.append(" and mk.FMktSrcCode = mtv.FMktsrcCode and mk.FMktValueDate = st.FStorageDate ")
				.append(" left join (select max(FMktValueDate) as FMktValueDate, FSecurityCode, FMktSrcCode from ")
				.append(pub.yssGetTableName("Tb_Data_MarketValue"))
				.append(" where FCheckState = 1 and FBargainAmount > 0 and FMktValueDate <= ")
				.append(dbl.sqlDate(curDate))
				.append(" group by FSecurityCode, FMktSrcCode) vmp ")
				.append(" on vmp.FSecurityCode = st.FSecurityCode and vmp.FMktSrcCode = mtv.FMktSrcCode ")
				.append(" join (select FGradeType5, FNavDate, FPortMarketValue,FPortCode from ")
				.append(pub.yssGetTableName("Tb_Data_NavData"))
				.append(" ) navdt on navdt.FGradeType5 = st.FSecurityCode ")
				.append(" and navdt.FNavDate = st.FStorageDate and navdt.FPortCode = st.FPortCode ")
				.append(" where st.FStorageAmount <> 0 and st.FStorageDate = ").append(dbl.sqlDate(curDate))
				.append(" and st.FPortCode = ").append(dbl.sqlString(portCode))
				.append(" and st.FCheckState = 1 ")
				.append(" and vmp.FMktValueDate is not null ");
				
				rs = dbl.openResultSet(sb.toString());
				while(rs.next()){
					securityCode = rs.getString("FSecurityCode");
					securityName = rs.getString("FSecurityName");
					tadayNavValue = rs.getDouble("FPortMarketValue");
					//停牌起始日 = 最近的一个有行情的行情日期 + 1个自然日
					suspendedDate = rs.getDate("FMktValueDate");
					
					//如果昨日资产净值 不为 0 
					if(yesNavValue != 0){
						scale = YssD.round(YssD.div(tadayNavValue, yesNavValue),4);
						//若  估值日停牌证券市值 / 昨日资产净值 > 0.25%
						if(scale >= 0.0025){
							showInfo += "证券代码：" + securityCode + " 证券名称：" + securityName + 
							" 市值占前日净值比  = " + YssD.mul(scale, 100) + "% 停牌起始日：" + 
							YssFun.formatDate(suspendedDate) + "\r\n";
						}
					}
				}
				
				dbl.closeResultSetFinal(rs);
				sb.setLength(0);
			}

			/**Start 20131213 modified by liubo.Bug #85463 QDV4南方2013年12月12日01_B
			 * 检测出违规数据时，应将sIsError变量赋值"true"，表示此预警指标检测到违规数据*/
			if(showInfo.trim().length() > 0){
				this.sIsError = "true";	
				this.checkInfos = titleInfo + showInfo;
			}
			else 
			{
				this.sIsError = "false";	
				this.checkInfos = "正常";
			}
			/**End 20131213 modified by liubo.Bug #85463 QDV4南方2013年12月12日01_B*/
			
			dbl.closeResultSetFinal(rs);
			sb.setLength(0);

			printInfo(this.checkInfos);
		}catch(Exception e){
			throw new YssException("停牌证券当日市值 / 前一估值日的资产净值 >= 0.25% 预警执行失败");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
    //界面输出提示信息
    private void printInfo(String sInfo) throws Exception 
    {
        runStatus.appendValCheckRunDesc(sInfo);
        
        if (this.sNeedLog.equals("true"))
        {
        	this.writeLog(sInfo);
        }
    }
}
