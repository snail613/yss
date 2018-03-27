package com.yss.main.operdeal.datainterface.etf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;

/**
 * add by songjie 2014.08.08
 * STORY #18037 �����Ϻ�-[����]QDIIV4.0[��]20140724001
 * ����ETF�ǵ������ջ���ָ����ӿ�  
 * �ӿڵ�����ȡ�������� ��ʱETF�ǵ������ջ���ָ���ȡ���߼�
 * @author songjie
 *
 */
public class ExpETFNonGrtStl extends DataBase {
	public ExpETFNonGrtStl(){
		
	}
//  add by songjie 2014.08.08
//  STORY #18037 �����Ϻ�-[����]QDIIV4.0[��]20140724001 
//  �ܵĵ����߼����	
	public void inertData() throws YssException {
        Connection con = dbl.loadConnection(); // �½�����
        boolean bTrans = false;//������Ʊ�ʶ
        try{
        	con.setAutoCommit(false);
        	bTrans = true;
        	
        	createTMPTable();//���û����ʱ��  �򴴽���ʱ��
        	delTMPData();//ɾ����ʱ���е���������
			dealDataIntoTMP();//ͨ��sql�߼�ȡ���ݲ��뵽��ʱ����
		
			con.commit(); //�ύ����
			bTrans = false;
			con.setAutoCommit(true); //���ÿ����Զ��ύ
        }catch(Exception e){
        	throw new YssException("����ETF�ǵ�������ָ�����ݳ���",e);
        }finally{
        	dbl.endTransFinal(con,bTrans);
        }
	}

//  add by songjie 2014.08.08
//  STORY #18037 �����Ϻ�-[����]QDIIV4.0[��]20140724001 
//  ���û����ʱ��  �򴴽���ʱ��
	private void createTMPTable()throws YssException{
		try{
			if (dbl.yssTableExist("TMP_QS_ETF_HA")) {
				return;
			} else {
				StringBuffer createTabSql = new StringBuffer();
				createTabSql
				.append("create table TMP_QS_ETF_HA")
				.append("(")
				.append(" SBBH VARCHAR2(10), ")
				.append(" HFRQ VARCHAR2(8), ")
				.append(" HFLB VARCHAR2(3), ")
				.append(" HFBZ VARCHAR2(1), ")
				.append(" GDDM VARCHAR2(10), ")
				.append(" HBLB VARCHAR2(3), ")
				.append(" HKJE VARCHAR2(10), ")
				.append(" RQ1  VARCHAR2(8), ")
				.append(" RQ2  VARCHAR2(10), ")
				.append(" JJDM VARCHAR2(20), ")
				.append(" YWDM VARCHAR2(20), ")
				.append(" BZ   VARCHAR2(40) ")
				.append(")");
			
				dbl.executeSql(createTabSql.toString());
			}
		}catch(Exception e){
			throw new YssException("������ʱ�� TMP_QS_ETF_HA ����", e);
		}
	}

//  add by songjie 2014.08.08
//  STORY #18037 �����Ϻ�-[����]QDIIV4.0[��]20140724001 
//  ɾ����ʱ���е���������	
    private void delTMPData() throws YssException{
    	try {
    		dbl.executeSql(" delete from TMP_QS_ETF_HA ");
        }catch(Exception e){
        	throw new YssException("ɾ����ʱ�� TMP_QS_ETF_HA ���ݳ���");
        }
	}

//  add by songjie 2014.08.08
//  STORY #18037 �����Ϻ�-[����]QDIIV4.0[��]20140724001 
//  ͨ��sql�߼�ȡ���ݲ��뵽��ʱ����    
    private void dealDataIntoTMP() throws YssException{
        ResultSet rs = null;//���������
        PreparedStatement pst = null;// ����PreparedStatement
        StringBuffer insertSql = new StringBuffer();
        int count = 0;
    	try {
            insertSql
            .append(" insert into TMP_QS_ETF_HA(SBBH,HFRQ,HFLB,HFBZ,GDDM,HBLB,HKJE,RQ1,RQ2,JJDM,YWDM,BZ)")
            .append(" values(?,?,?,?,?,?,?,?,?,?,?,?) ");
            pst = dbl.openPreparedStatement(insertSql.toString());
            
            String[] portCodes = this.sPort.split(",");
            for(int i = 0; i < portCodes.length; i++){
            	rs = dbl.openResultSet(buildQuerysql(portCodes[i]));
            	while(rs.next()){
            		count++;
            		pst.setString(1, rs.getString("SBBH"));
            		pst.setString(2, rs.getString("HFRQ"));
            		pst.setString(3, rs.getString("HFLB"));
            		pst.setString(4, rs.getString("HFBZ"));
            		pst.setString(5, rs.getString("GDDM"));
            		pst.setString(6, rs.getString("HBLB"));
            		pst.setString(7, rs.getString("HKJE"));
            		pst.setString(8, rs.getString("RQ1"));
            		pst.setString(9, rs.getString("RQ2"));
            		pst.setString(10, rs.getString("JJDM"));
            		pst.setString(11, rs.getString("YWDM"));
            		pst.setString(12, rs.getString("BZ"));
            		
            		pst.addBatch();
            		
            		if(count == 100){
            			pst.executeBatch();
            			count = 0;
            		}
            	}
            	dbl.closeResultSetFinal(rs);
            }
            
            if(count > 0){
            	pst.executeBatch();
            }
    	}catch(Exception e){
    		throw new YssException("������ʱ��  TMP_QS_ETF_HA ����",e);
    	}finally{
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
        }
	}
   
//    add by songjie 2014.08.08
//    STORY #18037 �����Ϻ�-[����]QDIIV4.0[��]20140724001 
    
//    ��ȡ������DBF�ļ��е����ݣ����ֶ�ȡ���߼����£�    
//    HKJE��������ֶ�ȡ���߼����£�      
//    ��ȡ���� 1 ���ݣ�
//    YWDM��ҵ����룩= 205
//    BZ����ʶ�� = ETF�깺����ֽ����
//    һ����ȡ��Ӧ�������������������������
//    1���깺��������ȡ������
//    ͨ����Ʒ����ҵ�������ֽ����ת����=�������ڣ��������������ڣ�
//    �ٸ��ݡ�ETF������ϸ�⡱��Tb_014_ETF_JSMXInterface���е��깺���ڣ�FBargainDate��=�������ڡ�
//    ҵ�����ͣ�FTradeTypeCode��=102���깺���������ʶ��FClearMark��=276����¼���ͣ�FRecordType��=003������
//    ȡ���깺�ݶFTradeAmount�������չɶ����루fstockholdercode���ֶλ����깺�ݶFTradeAmount����
//    �����깺�ݶFTradeAmount���ֶγ��ԡ�ETF�������á�����ġ���׼�����������õ�ÿ���ɶ��깺����������
//
//    2�������������ȡ������
//    ͨ����Ʒ����ҵ�������ֽ����ת����=�������ڣ��������������ڣ�
//    �ٸ��ݡ�ETF������ϸ�⡱��Tb_014_ETF_JSMXInterface���е��깺���ڣ�FBargainDate��=�������ڡ�
//    ҵ�����ͣ�FTradeTypeCode��=103����أ��������ʶ��FClearMark��=�ա���¼���ͣ�FRecordType��=003������
//    ȡ����طݶFTradeAmount�������չɶ����루fstockholdercode��������طݶFTradeAmount����
//    ������طݶFTradeAmount���ֶγ��ԡ�ETF�������á�����ġ���׼�����������õ�ÿ���ɶ���ص���������
//
//    ��ͬʱ���˴��Ĺɶ����루fstockholdercode���ֶμ����ɶ����롱��
//
//    3��ÿ���ɶ�������������=�깺������-�����������
//
//    ������ȡ��Ӧ������������ĵ�λ�ֽ���
//    1��ͨ����Ʒ����ҵ�������ֽ����ת����=�������ڣ��������������ڣ�
//    �ٸ��ݲ����ֵ���е�����=�������ڣ�ȡ����λ�ֽ����ֶΣ�
//
//    ������ȡ���յ��ֽ����
//    ���=����ÿ���ɶ�������������*��λ�ֽ��
//    ���ý�����0ʱ��HFLB�����������ʾΪF��
//    ���ý��С��0ʱ��HFLB�����������ʾΪS��
//
//    -----------------------------------------------------------------------------------------------------------------
    
//    ��ȡ���� 2 ���ݣ�
//    YWDM��ҵ����룩= 203
//    BZ����ʶ�� = ETF�깺����ֽ�����˲����깺���˲��

//    1����ȡ�깺��������
//    ͨ��ETF̨�˱�(Tb_XXX_ETF_StandingBook)�У���������Ϊ�깺�����˿����ڣ�FRefundDate��=��������ʱ��
//    ֱ�ӻ�ȡ�깺���ڣ�FBuyDate����
//    �ٸ��ݡ�ETF������ϸ�⡱��Tb_014_ETF_JSMXInterface���е��깺���ڣ�FBargainDate��=ETF̨�˱��е��깺���ڣ�FBuyDate����
//    ҵ�����ͣ�FTradeTypeCode��=102���깺���������ʶ��FClearMark��=276��
//    ��¼���ͣ�FRecordType��=003������ȡ���깺�ݶFTradeAmount����
//    ���չɶ����루fstockholdercode���ֶλ����깺�ݶFTradeAmount����
//    �����깺�ݶFTradeAmount���ֶγ��ԡ�ETF�������á�����ġ���׼�����������õ�ÿ���ɶ��깺����������
//    ��ͬʱ���˴��Ĺɶ����루fstockholdercode���ֶμ����ɶ����롱��
//
//    2����ȡ��λ���ӵ��깺�˲���
//    ȡ��ETF̨�˱�(Tb_XXX_ETF_StandingBook)�У���������Ϊ�깺�����˿����ڣ�FRefundDate��=�������ڣ�
//    ������ڣ�FDate��Ϊ�������ڣ��ٸ��ݹɶ����루fstockholdercode���ֶλ��ܡ�Ӧ�˺ϼơ��ֶΣ�FSumReturn����
//
//    3����ȡ���յ��깺�˲������1�����Ӧ�ɶ����깺������*2�����Ӧ�ɶ��ĵ�λ�����깺�˲��
//    ���ý�����0ʱ��HFLB�����������ʾΪS��
//    ���ý��С��0ʱ��HFLB�����������ʾΪF��
//
//    -----------------------------------------------------------------------------------------------------------------
    
//    ��ȡ���� 3 ���ݣ�
//    YWDM��ҵ����룩= 202
//    BZ����ʶ�� = ETF����ֽ����������˲��
//
//    1����ȡ�����������
//    ͨ��ETF̨�˱�(Tb_XXX_ETF_StandingBook)�У���������Ϊ��أ����˿����ڣ�FRefundDate��=��������ʱ��
//    ֱ�ӻ�ȡ������ڣ�FBuyDate����
//    �ٸ��ݡ�ETF������ϸ�⡱��Tb_014_ETF_JSMXInterface���е��깺���ڣ�FBargainDate��=ETF̨�˱��е�������ڣ�FBuyDate����
//    ҵ�����ͣ�FTradeTypeCode��=103����أ��������ʶ��FClearMark��=�ա�
//    ��¼���ͣ�FRecordType��=003������ȡ����طݶFTradeAmount����
//    ���չɶ����루fstockholdercode���ֶλ�����طݶFTradeAmount����
//    ������طݶFTradeAmount���ֶγ��ԡ�ETF�������á�����ġ���׼�����������õ�ÿ���ɶ���ص���������
//    ��ͬʱ���˴��Ĺɶ����루fstockholdercode���ֶμ����ɶ����롱��
//
//    2����ȡ��صĵ�λ���ӱ����ֽ����
//    ͨ����Ʒ����ҵ�������������Ϊ��أ����ֽ������ת����=�������ڣ��������������ڣ�
//    ��ͨ��ETF��Ʊ����Tb_XXX_ETF_StockList���е�������=�������ڣ������־Ϊ6�ġ�������ֶ�
//
//    3����ȡ��صĵ�λ���ӿ����ֽ����
//    ȡ��ETF̨�˱�(Tb_XXX_ETF_StandingBook)����������Ϊ��أ����˿����ڣ�FRefundDate��=�������ڣ�
//    ������ڣ�Fdate��Ϊ�������ڣ��ٸ��ݹɶ����루fstockholdercode���ֶλ��ܡ�Ӧ�˺ϼơ��ֶΣ�FSumReturn����
//
//    4����ȡ���յ�����˲���
//    ��1����ÿ���ɶ���������*2����ĵ�λ���ӱ����ֽ�������+1����ÿ���ɶ���������*3����ÿ���ɶ��ĵ�λ���ӿ����ֽ������
//    ������������жϽ��������HFLB���������Ĭ����ʾΪS��
    
//    JJDM��������룩�ֶ� = ��Ӧ�ӿڴ�����湴ѡ����Ӧ��ϵ��ʲ�����
//    RQ2��Ӧ�����գ��ֶ� = ��HFRQ���������ڣ�
//    RQ1���������ڣ��ֶ� = ��Ҫ���ա�ҵ����롱���ֶλ�ȡ���Ľ������ڣ���ϸ������ա�������ֶΡ�
//    HBLB�����֣��ֶ� = RMB
//    GDDM���ɶ����룩�ֶ� = ��ϸ������ա�������ֶε�ȡ����
//    HFBZ���ո���־���ֶ�  = Ĭ��ΪF��S����Ҫ���ա�������͡�ҵ����롱�������ֶ�ָ��̬����ΪF��S����ϸ������ա�������ֶε�ȡ����
//    HFLB����������ֶ� = 002 
    private String buildQuerysql(String portCode) throws YssException{
    	StringBuffer strBf = new StringBuffer();
    	strBf
    	.append(" select REPLACE(TO_CHAR(FSettleDate, 'MMdd') || to_char(rownum, '000000'), ' ', '') as SBBH, ")
        .append(" to_char(FSettleDate,'yyyyMMdd') as HFRQ, ")
        .append(" '002' as HFLB, ")
        .append(" case ")
        .append(" when FCapitalType = '205' and FCommandMoney > 0 then ")
        .append(" 'F' ")
        .append(" when FCapitalType = '205' and FCommandMoney < 0 then ")
        .append(" 'S' ")
        .append(" when FCapitalType = '203' and FCommandMoney > 0 then ")
        .append(" 'S' ")
        .append(" when FCapitalType = '203' and FCommandMoney < 0 then ")
        .append(" 'F' ")
        .append(" when FCapitalType = '202' then ")
        .append(" 'S' ")
        .append(" end as HFBZ, ")
        .append(" fstockholdercode as GDDM, ")
        .append(" 'RMB' as HBLB, ")
        .append(" case ")
        .append(" when FCapitalType = '205' then ")
        .append(" abs(FCommandMoney) ")
        .append(" when FCapitalType = '203' then ")
        .append(" abs(FCommandMoney) ")
        .append(" else ")
        .append(" FCommandMoney ")
        .append(" end as HKJE, ")
        .append(" to_char(FBargainDate,'yyyyMMdd') as RQ1, ")
        .append(" to_char(FSettleDate,'yyyyMMdd') as RQ2, ")
        .append(" FFundCode as JJDM, ")
        .append(" FCapitalType as YWDM, ")
        .append(" FDesc as BZ ")
        .append(" from (select ").append(dbl.sqlDate(this.sDate)).append(" as FSettleDate, ")
        .append(" nvl((detail.FTradeAmount / para.FNormScale * ")
        .append(" cashbal.FStandardMoneyMarketValue), ")
        .append(" 0) as FCommandMoney, ")
        .append(" detail.FBuyDate as FBargainDate, ")
        .append(" port.FAssetCode as FFundCode, ")
        .append(" '205' as FCapitalType, ")
        .append(" 'ETF�깺����ֽ����' as FDesc, ")
        .append(" detail.FStockHolderCode ")
        .append(" from (select FPortCode, ")
        .append(" FClearCode as FBrokerCode, ")
        .append(" FBargainDate as FBuyDate, ")
        .append(" FStockHolderCode, ")
        .append(" sum(case ")
        .append(" when FTradeTypeCode = '103' and jsmx.FClearMark = ' ' then ")
        .append(" -FTradeAmount ")
        .append(" when FTradeTypeCode = '102' and ")
        .append(" jsmx.FClearMark = '276' then ")
        .append(" FTradeAmount ")
        .append(" end) as FTradeAmount ")
        .append(" from ").append(pub.yssGetTableName("Tb_ETF_JSMXInterface")).append(" jsmx ")
        .append(" where FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and exists ")
        .append(" (select FTradeDate, FTradeTypeCode ")
        .append(" from (select FTradeDate, ")
        .append(" case ")
        .append(" when FSellType = '01' then ")
        .append(" '102' ")
        .append(" else ")
        .append(" '103' ")
        .append(" end as FTradeTypeCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_Ta_Trade"))
        .append(" where FCheckState = 1 ")
        .append(" and FSellType in ('01', '02') ")
        .append(" and FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FCashBalanceDate = ")
        .append(dbl.sqlDate(this.sDate)).append(") ta ")
        .append(" where ta.FTradeDate = jsmx.FBargainDate ")
        .append(" and ta.FTradeTypeCode = jsmx.FTradeTypeCode) ")
        .append(" and jsmx.FRecordType = '003' ")
        .append(" group by FClearCode, ")
        .append(" FPortCode, ")
        .append(" FBargainDate, ")
        .append(" FStockHolderCode) detail ")
        .append(" left join (select mm.FStandardMoneyMarketValue, ")
        .append(" mm.FSetCode, ")
        .append(" mm.FDate, ")
        .append(" nn.FSetID, ")
        .append(" kk.FPortCode ")
        .append(" from (select gv.FStandardMoneyMarketValue, ")
        .append(" lpad(gv.FPortCode, 3, '0') as FSetCode, ")
        .append(" FDate ")
        .append(" from ").append(pub.yssGetTableName("Tb_Rep_GuessValue")).append(" gv ")
        .append(" where gv.FDate = (select distinct FTradeDate ")
        .append(" from ").append(pub.yssGetTableName("Tb_Ta_Trade")).append(" ta ")
        .append(" where FCheckState = 1 ")
        .append(" and FCashBalanceDate = ").append(dbl.sqlDate(this.sDate))
        .append(" and ta.FSellType in ('01', '02')) ")
        .append(" and gv.FAcctCode = '9802') mm ")
        .append(" left join (select FSetID, FSetCode, FYear ")
        .append(" from lSetList) nn ")
        .append(" on mm.FSetCode = nn.FSetCode ")
        .append(" and nn.FYear = to_number(to_char(mm.FDate, 'yyyy')) ")
        .append(" left join (select FPortCode, FAssetCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
        .append(" where FCheckState = 1) kk ")
        .append(" on kk.FAssetCode = nn.FSetID) cashbal ")
        .append(" on cashbal.FPortCode = detail.FPortCode ")
        .append(" left join (select FAssetCode, FPortCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
        .append(" where FCheckState = 1) port ")
        .append(" on port.FPortCode = detail.FPortCode ")
        .append(" left join (select etfp.FNormScale, etfp.FPortCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_ETF_Param")).append(" etfp ")
        .append(" where FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FCheckState = 1) para ")
        .append(" on para.FPortCode = detail.FPortCode ")
        .append(" union all ")
        .append(" select ").append(dbl.sqlDate(this.sDate)).append(" as FSettleDate, ")
        .append(" nvl(a.FTradeAmount / para.FNormScale * m.FSumReturn, 0) as FCommandMoney, ")
        .append(" a.FBuydate as FBargainDate, ")
        .append(" port.FAssetCode as FFundCode, ")
        .append(" '203' as FCapitalType, ")
        .append(" 'ETF�깺����ֽ�����˲�' as FDesc, ")
        .append(" a.FStockHolderCode ")
        .append(" from (select FClearCode as FBrokerCode, ")
        .append(" FPortCode, ")
        .append(" FBargainDate as FBuyDate, ")
        .append(" FTradeTypeCode, ")
        .append(" FStockHolderCode, ")
        .append(" sum(jsmx.FTradeAmount) as FTradeAmount ")
        .append(" from ").append(pub.yssGetTableName("Tb_ETF_JSMXInterface")).append(" jsmx ")
        .append(" where jsmx.FBargainDate = ")
        .append(" (select distinct FBuyDate ")
        .append(" from ").append(pub.yssGetTableName("TB_ETF_StandingBook"))
        .append(" where FRefundDate = ").append(dbl.sqlDate(this.sDate))
        .append(" and FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FBS = 'B') ")
        .append(" and jsmx.FTradeTypeCode = '102' ")
        .append(" and jsmx.FClearMark = '276' ")
        .append(" and jsmx.FRecordType = '003' ")
        .append(" group by FSettleDate, ")
        .append(" FPortCode, ")
        .append(" FClearCode, ")
        .append(" FBargainDate, ")
        .append(" FTradeTypeCode, ")
        .append(" FStockHolderCode) a ")
        .append(" left join (select FAssetCode, FPortCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
        .append(" where FcheckState = 1) port ")
        .append(" on port.FPortCode = a.FPortCode ")
        .append(" left join (select etfp.FNormScale, etfp.FPortCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_ETF_Param")).append(" etfp ")
        .append(" where FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FCheckState = 1) para ")
        .append(" on para.FPortCode = a.FPortCode ")
        .append(" left join (select a1.FSumReturn, ")
        .append(" a1.FDate, ")
        .append(" a1.FPortCode, ")
        .append(" a1.FStockHolderCode ")
        .append(" from (select sum(FSumReturn) as FSumReturn, ")
        .append(" fDate, ")
        .append(" FPortCode, ")
        .append(" FStockHolderCode ")
        .append(" from ").append(pub.yssGetTableName("TB_ETF_StandingBook"))
        .append(" where FReFundDate = ").append(dbl.sqlDate(this.sDate))
        .append(" and FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FBs = 'B' ")
        .append(" and FStockHolderCode <> ' ' ")
        .append(" group by FDate, ")
        .append(" FPortCode, ")
        .append(" FStockHolderCode, ")
        .append(" FReFundDate) a1 ")
        .append(" join (select max(FDate) as FDate, ")
        .append(" FPortCode, ")
        .append(" FStockHolderCode ")
        .append(" from ").append(pub.yssGetTableName("TB_ETF_StandingBook"))
        .append(" where FReFundDate = ").append(dbl.sqlDate(this.sDate))
        .append(" and FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FBs = 'B' ")
        .append(" and FStockHolderCode <> ' ' ")
        .append(" group by FPortCode, ")
        .append(" FStockHolderCode, ")
        .append(" FReFundDate) a2 ")
        .append(" on a1.fDate = a2.fDate ")
        .append(" and a1.FPortCode = a2.FPortCode ")
        .append(" and a1.FStockHolderCode = a2.FStockHolderCode) m ")
        .append(" on m.FPortCode = a.FPortCode ")
        .append(" and m.FStockHolderCode = a.FStockHolderCode ")
        .append(" where a.FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and a.FTradeTypeCode = '102' ")
        .append(" and a.FBrokerCode <> ' ' ")
        .append(" union all ")
        .append(" select ").append(dbl.sqlDate(this.sDate)).append(" as FSettleDate, ")
        .append(" nvl(a.FTradeAmount / para.FNormScale * m.FSumReturn, 0) + ")
        .append(" nvl(a.FTradeAmount / para.FNormScale * n.FReplaceMoney, 0) as FCommandMoney, ")
        .append(" a.FBuyDate as FBargainDate, ")
        .append(" port.FAssetCode as FFundCode, ")
        .append(" '202' as FCapitalType, ")
        .append(" 'ETF����ֽ����' as FDesc, ")
        .append(" a.FStockHolderCode ")
        .append(" from (select ").append(dbl.sqlDate(this.sDate)).append(" as FRefundDate, ")
        .append(" FClearCode as FBrokerCode, ")
        .append(" FPortCode, ")
        .append(" FBargainDate as FBuydate, ")
        .append(" FTradeTypeCode, ")
        .append(" FStockHolderCode, ")
        .append(" sum(jsmx.FTradeAmount) as FTradeAmount ")
        .append(" from ").append(pub.yssGetTableName("Tb_ETF_JSMXInterface")).append(" jsmx ")
        .append(" where jsmx.FBargainDate = ")
        .append(" (select distinct FBuyDate ")
        .append(" from ").append(pub.yssGetTableName("TB_ETF_StandingBook"))
        .append(" where FReFundDate = ").append(dbl.sqlDate(this.sDate))
        .append(" and FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FBS = 'S') ")
        .append(" and jsmx.ftradetypecode = '103' ")
        .append(" and jsmx.fclearmark = ' ' ")
        .append(" and jsmx.frecordtype = '003' ")
        .append(" group by FSettleDate, ")
        .append(" FPortCode, ")
        .append(" FClearCode, ")
        .append(" FBargainDate, ")
        .append(" FTradeTypeCode, ")
        .append(" FStockHolderCode) a ")
        .append(" left join (select FAssetCode, FPortCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
        .append(" where FCheckState = 1) port ")
        .append(" on port.FPortCode = a.FPortCode ")
        .append(" left join (select etfp.FNormScale, etfp.FPortCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_ETF_Param")).append(" etfp ")
        .append(" where FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FCheckState = 1) para ")
        .append(" on para.FPortCode = a.FPortCode ")
        .append(" left join (select a1.FSumReturn, ")
        .append(" a1.fDate, ")
        .append(" a1.FPortCode, ")
        .append(" a1.FStockHolderCode ")
        .append(" from (select sum(FSumReturn * -1) as FSumReturn, ")
        .append(" fDate, ")
        .append(" FPortCode, ")
        .append(" FStockHolderCode ")
        .append(" from ").append(pub.yssGetTableName("TB_ETF_StandingBook"))
        .append(" where FReFundDate = ").append(dbl.sqlDate(this.sDate))
        .append(" and FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FBS = 'S' ")
        .append(" and FStockHolderCode <> ' ' ")
        .append(" group by fDate, FPortCode, FStockHolderCode) a1 ")
        .append(" join (select max(FDate) as FDate, ")
        .append(" FPortCode, ")
        .append(" FStockHolderCode ")
        .append(" from ").append(pub.yssGetTableName("TB_ETF_StandingBook"))
        .append(" where FReFundDate = ").append(dbl.sqlDate(this.sDate))
        .append(" and FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FBS = 'S' ")
        .append(" and FStockHolderCode <> ' ' ")
        .append(" group by FPortCode, FStockHolderCode) a2 ")
        .append(" on a1.fDate = a2.fDate ")
        .append(" and a1.FPortCode = a2.FPortCode ")
        .append(" and a1.FStockHolderCode = a2.FStockHolderCode) m ")
        .append(" on m.FPortCode = a.FPortCode ")
        .append(" and m.FStockHolderCode = a.FStockHolderCode ")
        .append(" left join (select FReplaceMoney, FPortCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_ETF_StockList"))
        .append(" where FDate = ")
        .append(" (select FTradeDate ")
        .append(" from ").append(pub.yssGetTableName("Tb_Ta_Trade"))
        .append(" where FSellType = '02' ")
        .append(" and FCashReplaceDate = ").append(dbl.sqlDate(this.sDate)).append(") ")
        .append(" and FReplaceMark = '6') n ")
        .append(" on n.FPortCode = a.FPortCode) ");
    	
    	return strBf.toString();
    }
}
