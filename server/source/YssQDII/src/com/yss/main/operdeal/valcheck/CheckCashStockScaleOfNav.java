package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * add by songjie 2014.05.10
 * STORY #15961 �����Ϻ�-[����ʩ�޵�]QDIIV4.0[����]20140401001
 * Ԥ��ָ�꣺�������ֽ�ռ�ܾ�ֵ������ ��Ӧ���߼�����
 * @author �ν�
 *
 */
public class CheckCashStockScaleOfNav extends BaseValCheck {
	public CheckCashStockScaleOfNav(){
	}
	
	public String doCheck(Date curDate, String portCode) throws Exception {
		try{
			checkCashStockScaleOfNav(curDate, portCode);
		}catch(Exception e){
			throw new YssException("�����ֽ�ռ�ܾ�ֵ����С�ڵ��ڷ�ֵ", e);
		}
		
		return "";
	}
	
	private void checkCashStockScaleOfNav(Date curDate, String portCode)throws YssException{
		String strSql = "";
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		java.util.Date suspendedDate = null;//ͣ����ʼ��
		String securityCode = "";//֤ȯ����
		String securityName = "";//֤ȯ����
		double navValue = 0;//�����ʲ���ֵ
		double scale = 0;//������ֵռ�����ʲ���ֵ��
		double cashStockM = 0;//��ֵ�ձ�λ���ֽ���ϼ�ֵ
		String showInfo = "";//��ϸԤ����ʾ��Ϣ
		try{
        	if (this.sPluginValue != null && 
        		!this.sPluginValue.equalsIgnoreCase("null") && 
        		!this.sPluginValue.trim().equals("")){
        		scale = Double.parseDouble(this.sPluginValue);
        	}
			
			//��ȡ��ֵ�ա������ϵ� �ʲ���ֵ
			sb.append(" select FPortMarketValue from ").append(pub.yssGetTableName("Tb_Data_NavData"))
			.append(" where FNavDate = ").append(dbl.sqlDate(curDate))
			.append(" and FPortCode = ").append(dbl.sqlString(portCode))
			.append(" and FReTypeCode = 'Total' and FKeyCode = 'TotalValue' ");
			
			rs = dbl.openResultSet(sb.toString());
			if(rs.next()){
				navValue = rs.getDouble("FPortMarketValue");
			}
			
			dbl.closeResultSetFinal(rs);
			sb.setLength(0);
			
			//��ȡ��ֵ�ա��������ֽ���ı�λ�ҳɱ����ϼ�
			sb.append(" select sum(FPortCuryBal) as FPortCuryBal from ")
			.append(pub.yssGetTableName("Tb_Stock_Cash"))
			.append(" where FStorageDate = ").append(dbl.sqlDate(curDate))
			.append(" and FPortCode = ").append(dbl.sqlString(portCode));
			rs = dbl.openResultSet(sb.toString());
			if(rs.next()){
				cashStockM = rs.getDouble("FPortCuryBal");
			}
			
			dbl.closeResultSetFinal(rs);
			sb.setLength(0);
			
			if(YssD.div(cashStockM, navValue) <= YssD.div(scale,100)){
				showInfo = YssFun.formatDate(curDate,"yyyy-MM-dd") + "��λ���˻����㾻ֵ" + scale + "%";
			}
			
			if(showInfo.trim().length() > 0){
				this.sIsError = "true";	
				this.checkInfos = showInfo;
			}
			else 
			{
				this.sIsError = "false";	
				this.checkInfos = "����";
			}
			
			dbl.closeResultSetFinal(rs);
			sb.setLength(0);

			printInfo(this.checkInfos);
		}catch(Exception e){
			throw new YssException("ִ��Ԥ��ָ�꣺�������ֽ�ռ�ܾ�ֵ������ ����", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
    //���������ʾ��Ϣ
    private void printInfo(String sInfo) throws Exception 
    {
        runStatus.appendValCheckRunDesc(sInfo);
        
        if (this.sNeedLog.equals("true"))
        {
        	this.writeLog(sInfo);
        }
    }
}
