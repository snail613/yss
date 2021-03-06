package com.yss.main.operdeal.report.repfix.Chinabank;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.ParaWithPubBean;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**shashijie 2012-04-12 STORY 2386 工银,估值系统，工银持仓核对报表，仅显示核对不上部分 */
public class NetValueEveryDay extends BaseBuildCommonRep {
    
    private CommonRepBean repBean;//报表对象
    
    private String FStartDate = "";//日期
    private String FPortCode = "";//组合代码(多选)
    
    //private FixPub fixPub = null;//获取基金成立日那天的金额
    private BigDecimal zcjz = new BigDecimal(0);//资产净值（元）
    private BigDecimal zjhj = new BigDecimal(0);//资产合计（元）
    private BigDecimal cjfe = new BigDecimal(0);//资产份额（份）
    public NetValueEveryDay() {
    }

	/**初始数据方法 shashijie 2012-4-23 STORY 2386*/
    public void initBuildReport(BaseBean bean) throws YssException {
        String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        
        FStartDate = reqAry[0].split("\r")[1];//日期
        FPortCode = reqAry[1].split("\r")[1];//组合

        /*fixPub = new FixPub();
        fixPub.setYssPub(pub);*/
    }
    
    /**程序入口 shashijie 2012-4-23 STORY 2386*/
    public String buildReport(String sType) throws YssException {
        String sResult = "";

    	//组合
    	String[] portCode = FPortCode.split(",");
    	for (int i = 0; i < portCode.length; i++) {
			String FAssetGroupCode = portCode[i].split("-")[0];//组合群
			String port = portCode[i].split("-")[1];//组合
			//获取报表内容
	        sResult += getInfo(port,FAssetGroupCode);
		}
    	//总计
    	sResult += operionStr("总计", 
				" ",
				" ", 
				" ",
				" ",
				" ", 
				zcjz.toString(),
				zjhj.toString(), 
				cjfe.toString(),
				" ",
				" ",
				" "," ", " ", " ", " ",	" ",
				" ");
        return sResult;
    }

	/**shashijie 2012-4-23 STORY 2386 */
	private String getInfo(String portCode,String FAssetGroupCode) throws YssException {
		String query = "";
		//是否取财务表--Ffund
		if (isFinancial(FAssetGroupCode,"Ffund")) {
			query = getSqlMoney(portCode,FAssetGroupCode);
		}//是否取净值表--plane
		else if (isFinancial(FAssetGroupCode,"plane")){
			query = getSqlSystem(portCode,FAssetGroupCode);
		} else {
			throw new YssException("当前组合群未设置\r\n\t["+FAssetGroupCode+"组合群---"+
					portCode+"组合]的通用业务参数!");
		}
		
		String str = "";//返回结果
		ResultSet rs = null;
		try {
			
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				//填充单元格
				str += operionStr(YssFun.formatDate(this.FStartDate), 
						rs.getString("Fassetcode"),
						rs.getString("Fassetgroupname"), 
						YssFun.formatDate(rs.getDate("Finceptiondate")),
						rs.getString("Fmanagername"),
						rs.getString("Ftrusteename"), 
						YssFun.formatNumber(rs.getDouble("Fnetvalue"),"0.##################"),
						YssFun.formatNumber(rs.getDouble("Fnetall"),"0.##################"), 
						YssFun.formatNumber(rs.getDouble("Fnetamount"),"0.##################"),
						YssFun.formatNumber(rs.getDouble("Fnetunit"),"0.##################"),
						YssFun.formatNumber(rs.getDouble("Fnetgrand"),"0.##################"),
						" "," ", " ", " ", " ",	" ",
						rs.getString("CFtrusteename"));
				//汇总,总计项
				zcjz = getFAmount(zcjz,rs.getBigDecimal("Fnetvalue"));//资产净值（元）
			    zjhj = getFAmount(zjhj,rs.getBigDecimal("Fnetall"));//资产合计（元）
			    cjfe = getFAmount(cjfe,rs.getBigDecimal("Fnetamount"));//资产份额（份）
			}
		} catch (Exception e) {
			throw new YssException("填充内容错误!");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return str;
	}

	/**shashijie 2012-5-23 STORY 2386 */
	private String getSqlSystem(String portCode, String FAssetGroupCode) throws YssException {
		String assetGroupCode = pub.getAssetGroupCode();//备份系统组合群代码
		String Sql = "";
		try {
			pub.setPrefixTB(FAssetGroupCode);//表前缀
			Sql = 
				" Select a.Fassetcode," +
				" b.Fassetgroupname," +
				" a.Finceptiondate," +
				" NVL(d.Fmanagername,' ') Fmanagername," +
				" NVL(f.Ftrusteename,' ') Ftrusteename," +
				" NVL(F1.Ftrusteename,' ') Cftrusteename," +
				" NVL(g.Fnetvalue,0) Fnetvalue," +
				" NVL(h.Fnetall,0) Fnetall," +
				" NVL(i.Fnetamount,0) Fnetamount," +
				" NVL(j.Fnetunit,0) Fnetunit," +
				" NVL(k.Fnetgrand,0) Fnetgrand " +
				" From "+pub.yssGetTableName("Tb_Para_Portfolio")+" a" +
				" Left Join Tb_Sys_Assetgroup b On a.Fassetgroupcode = b.Fassetgroupcode"+
				//管理人
				" Left Join (Select Wm_Concat(F2.Fmanagername) Fmanagername, E2.Fportcode"+
				" From (Select E1.Fsubcode, E1.Fportcode"+
				" From "+pub.yssGetTableName("Tb_Para_Portfolio_Relaship")+" E1"+
				" Where E1.Frelatype = 'Manager'" +
				" And E1.Fportcode = "+dbl.sqlString(portCode)+
				" ) E2  Left Join (Select F1.Fmanagercode, F1.Fmanagername" +
				" From "+pub.yssGetTableName("Tb_Para_Manager")+" F1) F2 On E2.Fsubcode = F2.Fmanagercode" +
				" Group By E2.Fportcode ) d On a.Fportcode = d.Fportcode"+
				//主托管人
				" Left Join (Select Wm_Concat(F2.Ftrusteename) Ftrusteename, E2.Fportcode"+
				" From (Select E1.Fsubcode, E1.Fportcode"+
				" From "+pub.yssGetTableName("Tb_Para_Portfolio_Relaship")+" E1"+
				" Where E1.Frelatype = 'Trustee'"+
				" And E1.Frelagrade = 'primary'"+
				" And E1.Fportcode = "+dbl.sqlString(portCode)+
				" ) E2 Left Join (Select F1.Ftrusteecode, F1.Ftrusteename"+
				" From "+pub.yssGetTableName("Tb_Para_Trustee")+" F1) F2 On E2.Fsubcode = F2.Ftrusteecode"+
				" Group By E2.Fportcode) f On a.Fportcode = f.Fportcode"+
				//次托管人
				" Left Join (Select Wm_Concat(F2.Ftrusteename) Ftrusteename, E2.Fportcode"+
				" From (Select E1.Fsubcode, E1.Fportcode"+
				" From "+pub.yssGetTableName("Tb_Para_Portfolio_Relaship")+" E1"+
				" Where E1.Frelatype = 'Trustee'"+
				" And E1.Frelagrade = 'secondary'"+
				" And E1.Fportcode = "+dbl.sqlString(portCode)+
				" ) E2 Left Join (Select F1.Ftrusteecode, F1.Ftrusteename"+
				" From "+pub.yssGetTableName("Tb_Para_Trustee")+" F1) F2 On E2.Fsubcode = F2.Ftrusteecode"+
				" Group By E2.Fportcode ) F1 On a.Fportcode = F1.Fportcode" +
				//资产净值
				" Left Join (Select G1.Fportmarketvalue Fnetvalue, G1.Fportcode" +
				" From "+pub.yssGetTableName("Tb_Data_Navdata")+" G1" +
				" Where G1.FNavDate = "+dbl.sqlDate(FStartDate)+
				" And G1.FPortCode = "+dbl.sqlString(portCode)+
				" And G1.FOrderCode = 'Total1') g On a.Fportcode = g.Fportcode" +
				//资产类合计
				" Left Join ("+
					//证券
				" Select A1.Fportcode," +
				" Nvl(A1.Fportmarketvalue, 0) + Nvl(B1.Fportmarketvalue, 0) +" +
				" Nvl(C1.Fportmarketvalue, 0) + Nvl(D1.Fportmarketvalue, 0) As FNetall" +
				" From (Select G1.Fnavdate," +
				" G1.Fportcode," +
				/*G1.Fordercode,G1.Fkeycode,*/
				" G1.Fretypecode," +
				" G1.Finvmgrcode," +
				" G1.Finvesttype," +
				" Sum(G1.Fportmarketvalue) Fportmarketvalue" +
				" From "+pub.yssGetTableName("Tb_Data_Navdata")+" G1" +
				" Where G1.Fnavdate = "+dbl.sqlDate(FStartDate)+
				" And G1.Fportcode = "+dbl.sqlString(portCode)+
				" And G1.Fretypecode = 'Security'" +
				" And G1.Fordercode Not Like '%#%'" +
				" Group By G1.Fnavdate, G1.Fportcode, G1.Fretypecode, G1.Finvmgrcode, G1.Finvesttype) A1"+
					//现金
				" Join (Select G1.Fnavdate," +
				" G1.Fportcode," +
				/*G1.Fordercode,G1.Fkeycode,*/
				" G1.Fretypecode," +
				" G1.Finvmgrcode," +
				" G1.Finvesttype," +
				" Sum(G1.Fportmarketvalue) Fportmarketvalue" +
				" From "+pub.yssGetTableName("Tb_Data_Navdata")+" G1" +
				" Where G1.Fnavdate = "+dbl.sqlDate(FStartDate)+
				" And G1.Fportcode = "+dbl.sqlString(portCode)+
				" And G1.Fretypecode = 'Cash'" +
				" And G1.Fordercode Not Like '%#%'" +
				" Group By G1.Fnavdate, G1.Fportcode, G1.Fretypecode, G1.Finvmgrcode, G1.Finvesttype" +
				" ) B1 On A1.Fnavdate = B1.Fnavdate And A1.Fportcode = B1.Fportcode " +
				"And A1.Finvmgrcode = B1.Finvmgrcode And A1.Finvesttype = B1.Finvesttype" +
					//证券应收
				" Left Join (Select G1.Fnavdate," +
				" G1.Fportcode," +
				" G1.Fordercode," +
				" G1.Fkeycode," +
				" G1.Fretypecode," +
				" G1.Finvmgrcode," +
				" G1.Finvesttype," +
				" Sum(G1.Fportmarketvalue) Fportmarketvalue" +
				" From "+pub.yssGetTableName("Tb_Data_Navdata")+" G1" +
				" Where G1.Fnavdate ="+dbl.sqlDate(FStartDate)+
				" And G1.Fportcode = "+dbl.sqlString(portCode)+
				" And G1.Fretypecode = 'Security'" +
				" And G1.Fgradetype6 Like '06%'" +
				" Group By G1.Fnavdate, G1.Fportcode, G1.Fordercode, G1.Fkeycode, G1.Fretypecode, G1.Finvmgrcode," +
				" G1.Finvesttype) C1 On A1.Fnavdate = C1.Fnavdate And A1.Fportcode = C1.Fportcode" +
				" And A1.Finvmgrcode = C1.Finvmgrcode And A1.Finvesttype = C1.Finvesttype" +
					//现金应收
				" Left Join (Select G1.Fnavdate," +
				" G1.Fportcode," +
				" G1.Fretypecode," +
				" G1.Finvmgrcode," +
				" G1.Finvesttype," +
				" Sum(G1.Fportmarketvalue) Fportmarketvalue" +
				" From "+pub.yssGetTableName("Tb_Data_Navdata")+" G1" +
				" Where G1.Fnavdate = "+dbl.sqlDate(FStartDate)+
				" And G1.Fportcode = "+dbl.sqlString(portCode)+
				" And G1.Fretypecode = 'Cash'" +
				" And G1.Fgradetype6 Like '06%'" +
				" Group By G1.Fnavdate, G1.Fportcode, G1.Fretypecode, G1.Finvmgrcode, G1.Finvesttype" +
				" ) D1 On B1.Fnavdate = D1.Fnavdate And B1.Fportcode = D1.Fportcode And B1.Finvmgrcode =" +
				" D1.Finvmgrcode And B1.Finvesttype = D1.Finvesttype) h On a.Fportcode = h.Fportcode" +
				//实收资本
				" Left Join (Select G1.Fportmarketvalue Fnetamount, G1.Fportcode" +
				" From "+pub.yssGetTableName("Tb_Data_Navdata")+" G1" +
				" Where G1.Fnavdate = "+dbl.sqlDate(FStartDate)+
				" And G1.Fportcode = "+dbl.sqlString(portCode)+
				" And G1.Fordercode = 'Total2') i On a.Fportcode = i.Fportcode" +
				//单位净值
				" Left Join (Select G1.Fprice Fnetunit, G1.Fportcode" +
				" From "+pub.yssGetTableName("Tb_Data_Navdata")+" G1" +
				" Where G1.Fnavdate = "+dbl.sqlDate(FStartDate)+
				" And G1.Fportcode = "+dbl.sqlString(portCode)+
				" And G1.Fordercode = 'Total3'" +
				" And G1.Fkeycode = 'Unit') j On a.Fportcode = j.Fportcode" +
				//累计单位净值
				" Left Join (Select G1.Fprice Fnetgrand, G1.Fportcode" +
				" From "+pub.yssGetTableName("Tb_Data_Navdata")+" G1" +
				" Where G1.Fnavdate = "+dbl.sqlDate(FStartDate)+
                " And G1.Fportcode = "+dbl.sqlString(portCode)+
                " And G1.Fordercode = 'Total7') k On a.Fportcode = k.Fportcode"+
				" Where a.Fportcode = "+dbl.sqlString(portCode)+
				"";
			
		} catch (Exception e) {
			throw new YssException("获取财务估值表SQL错误!",e);
		} finally {
			pub.setPrefixTB(assetGroupCode);//恢复系统表前缀
		}
		return Sql;
	}

	/**shashijie 2012-4-23 STORY 2386 财务估值表
	* @param portCode
	* @param fAssetGroupCode
	* @return*/
	private String getSqlMoney(String portCode, String FAssetGroupCode) throws YssException {
		String assetGroupCode = pub.getAssetGroupCode();//备份系统组合群代码
		String Sql = "";
		try {
			pub.setPrefixTB(FAssetGroupCode);//表前缀
			Sql = 
				" Select a.Fassetcode," +
				" b.Fassetgroupname," +
				" a.Finceptiondate," +
				" NVL(d.Fmanagername,' ') Fmanagername," +
				" NVL(f.Ftrusteename,' ') Ftrusteename," +
				" NVL(F1.Ftrusteename,' ') Cftrusteename," +
				" NVL(g.Fnetvalue,0) Fnetvalue," +
				" NVL(h.Fnetall,0) Fnetall," +
				" NVL(i.Fnetamount,0) Fnetamount," +
				" NVL(j.Fnetunit,0) Fnetunit," +
				" NVL(k.Fnetgrand,0) Fnetgrand " +
				" From "+pub.yssGetTableName("Tb_Para_Portfolio")+" a" +
				" Left Join Tb_Sys_Assetgroup b On a.Fassetgroupcode = b.Fassetgroupcode"+
				//管理人
				" Left Join (Select Wm_Concat(F2.Fmanagername) Fmanagername, E2.Fportcode"+
				" From (Select E1.Fsubcode, E1.Fportcode"+
				" From "+pub.yssGetTableName("Tb_Para_Portfolio_Relaship")+" E1"+
				" Where E1.Frelatype = 'Manager'" +
				" And E1.Fportcode = "+dbl.sqlString(portCode)+
				" ) E2  Left Join (Select F1.Fmanagercode, F1.Fmanagername" +
				" From "+pub.yssGetTableName("Tb_Para_Manager")+" F1) F2 On E2.Fsubcode = F2.Fmanagercode" +
				" Group By E2.Fportcode ) d On a.Fportcode = d.Fportcode"+
				//主托管人
				" Left Join (Select Wm_Concat(F2.Ftrusteename) Ftrusteename, E2.Fportcode"+
				" From (Select E1.Fsubcode, E1.Fportcode"+
				" From "+pub.yssGetTableName("Tb_Para_Portfolio_Relaship")+" E1"+
				" Where E1.Frelatype = 'Trustee'"+
				" And E1.Frelagrade = 'primary'"+
				" And E1.Fportcode = "+dbl.sqlString(portCode)+
				" ) E2 Left Join (Select F1.Ftrusteecode, F1.Ftrusteename"+
				" From "+pub.yssGetTableName("Tb_Para_Trustee")+" F1) F2 On E2.Fsubcode = F2.Ftrusteecode"+
				" Group By E2.Fportcode) f On a.Fportcode = f.Fportcode"+
				//次托管人
				" Left Join (Select Wm_Concat(F2.Ftrusteename) Ftrusteename, E2.Fportcode"+
				" From (Select E1.Fsubcode, E1.Fportcode"+
				" From "+pub.yssGetTableName("Tb_Para_Portfolio_Relaship")+" E1"+
				" Where E1.Frelatype = 'Trustee'"+
				" And E1.Frelagrade = 'secondary'"+
				" And E1.Fportcode = "+dbl.sqlString(portCode)+
				" ) E2 Left Join (Select F1.Ftrusteecode, F1.Ftrusteename"+
				" From "+pub.yssGetTableName("Tb_Para_Trustee")+" F1) F2 On E2.Fsubcode = F2.Ftrusteecode"+
				" Group By E2.Fportcode ) F1 On a.Fportcode = F1.Fportcode" +
				//财务估值表
				" Join Lsetlist l On a.Fassetcode = l.Fsetid"+
				//资产净值
				" Left Join (Select G1.Fstandardmoneymarketvalue As Fnetvalue, Fportcode"+
				" From "+pub.yssGetTableName("Tb_Rep_Guessvalue")+" G1" +
				" Where G1.Fdate = "+dbl.sqlDate(FStartDate)+
				" And G1.Facctcode = '9000'" +
				" And G1.Fcurcode = ' ') g On l.Fsetcode = g.Fportcode" +
				//资产类合计
				" Left Join (Select G1.Fstandardmoneymarketvalue As Fnetall, Fportcode" +
				" From "+pub.yssGetTableName("Tb_Rep_Guessvalue")+" G1" +
				" Where G1.Fdate = "+dbl.sqlDate(FStartDate)+
				" And G1.Facctcode = '8800' " +
				" And G1.Fcurcode = ' ') h On l.Fsetcode = h.Fportcode" +
				//实收资本
				" Left Join (Select G1.FAmount As Fnetamount, Fportcode" +
				" From "+pub.yssGetTableName("Tb_Rep_Guessvalue")+" G1" +
				" Where G1.Fdate = "+dbl.sqlDate(FStartDate)+
				" And G1.Facctcode = '8700' " +
				" And G1.Fcurcode = ' ') i On l.Fsetcode = i.Fportcode" +
				//单位净值
				" Left Join (Select G1.Fstandardmoneymarketvalue As Fnetunit, Fportcode" +
				" From " +pub.yssGetTableName("Tb_Rep_Guessvalue")+" G1" +
				" Where G1.Fdate = "+dbl.sqlDate(FStartDate)+
				" And G1.Facctcode = '9600' " +
				" And G1.Fcurcode = ' ') j On l.Fsetcode = j.Fportcode" +
				//累计单位净值
				" Left Join (Select G1.Fstandardmoneymarketvalue As Fnetgrand, Fportcode" +
				" From "+pub.yssGetTableName("Tb_Rep_Guessvalue")+" G1" +
				" Where G1.Fdate = "+dbl.sqlDate(FStartDate)+
				" And G1.Facctcode = '9612'" +
				" And G1.Fcurcode = ' ') k On l.Fsetcode = k.Fportcode" +
				" Where a.Fportcode = "+dbl.sqlString(portCode)+
				" And l.Fyear = "+dbl.sqlString(FStartDate.substring(0,4))+//套帐年份
				"";
			
		} catch (Exception e) {
			throw new YssException("获取财务估值表SQL错误!",e);
		} finally {
			pub.setPrefixTB(assetGroupCode);//恢复系统表前缀
		}
		return Sql;
	}

	/**shashijie 2012-4-23 STORY 2386 检查是否设置通参 */
	private boolean isFinancial(String FAssetGroupCode,String ctlCode) throws YssException {
		boolean flage = false;
		ResultSet rs = null;
		try {
	        //获取通用业务参数公共类
	        ParaWithPubBean para = new ParaWithPubBean();
	    	para.setYssPub(pub);
	    	rs = para.getResultSetByLike("CtlFPortOperion", "1");
	    	while (rs.next()) {
	    		if (ctlCode.equals(rs.getString("FCtlCode"))) {//控件代码相同
	    			String ctlValue = rs.getString("FCtlValue").split("[|]")[0];//组合群代码
					if (ctlValue.indexOf(FAssetGroupCode) > -1) {
						flage = true;
					}
				}
			}
		} catch (Exception e) {
			throw new YssException("检查是否设置通参错误!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return flage;
	}

	/**shashijie 2012-4-23 STORY 2386  */
	private BigDecimal getFAmount(BigDecimal v1, BigDecimal v2, BigDecimal v3) {
		BigDecimal value = YssD.addD(v1, v2, v3);
		return value;
	}

	/**shashijie 2012-4-23 STORY 2386  */
	private BigDecimal getFAmount(BigDecimal v1, BigDecimal v2) {
		BigDecimal value = getFAmount(v1, v2, new BigDecimal(0));
		return value;
	}

	public String getFStartDate() {
		return FStartDate;
	}

	public void setFStartDate(String fStartDate) {
		FStartDate = fStartDate;
	}

	public String getFPortCode() {
		return FPortCode;
	}

	public void setFPortCode(String fPortCode) {
		FPortCode = fPortCode;
	}

	/**shashijie 2012-4-23 STORY 2386 把内容拼接上格式 */
	private String buildRowCompResult(String str) throws YssException {
        String strReturn = "";
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";//报表格式HashMap的key
        RepTabCellBean rtc = null;//报表格式--单元格设置
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            hmCellStyle = getCellStyles("JiJinMeiRi");
            for (int i = 0; i < sArry.length; i++) {
                sKey = "JiJinMeiRi" + "\tDSF\t-1\t" + i;
                if (hmCellStyle.containsKey(sKey)) {
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(sArry[i]).append("\t");
            }
            if (buf.toString().trim().length() > 1) {
            	strReturn = YssFun.getSubString(buf.toString());
            }
            
            return strReturn + "\t\t";
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            //dbl.closeResultSetFinal(rs);
        }
	}
	
	/**shashijie 2012-4-23 STORY 2386 拼接18列数据
	* @param row1 第一列
	* @param row2 第二列...
	* @return*/
	private String operionStr(String row1, String row2, String row3,String row4,String row5,String row6,
			String row7,String row8,String row9,String row10,String row11,String row12,
			String row13,String row14,String row15,String row16,String row17,String row18) {
		String str = "";
		
		str += row1 + "\t";//第一列
		str += row2 + "\t";//第二列...
		str += row3 + "\t";
		str += row4 + "\t";
		str += row5 + "\t";
		str += row6 + "\t";
		str += row7 + "\t";
		str += row8 + "\t";
		str += row9 + "\t";
		str += row10+ "\t";
		str += row11+ "\t";
		str += row12+ "\t";
		str += row13+ "\t";
		str += row14+ "\t";
		str += row15+ "\t";
		str += row16+ "\t";
		str += row17+ "\t";
		str += row18+ "\t";
		
		try {
			str = buildRowCompResult(str)+"\r\n";
		} catch (Exception e) {
			str = "";
		}
		
		return str;
	}
}
