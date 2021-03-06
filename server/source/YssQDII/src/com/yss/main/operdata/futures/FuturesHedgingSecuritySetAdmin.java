package com.yss.main.operdata.futures;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.SecExchangeOutBean;
import com.yss.main.operdata.futures.pojo.FuturesHedgingSecuritySetBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 期货套期证券关联表实体bean操作类
 * @author xuqiji 20100513
 *
 */
public class FuturesHedgingSecuritySetAdmin extends BaseDataSettingBean implements IDataSetting{
	private FuturesHedgingSecuritySetBean HedgingSecuritySetBean = null;
	private FuturesHedgingSecuritySetAdmin filterType = null;
	public FuturesHedgingSecuritySetAdmin() {
		super();
		HedgingSecuritySetBean = new FuturesHedgingSecuritySetBean();
	}

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String addSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void delSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 保存套期保值证券信息
	 */
	public String saveMutliSetting(String sMutilRowStr,String sNum) throws YssException {
		String[] sMutilRowAry = null;
		PreparedStatement pstmt = null;
	    java.sql.Connection conn = dbl.loadConnection();
	    boolean bTrans = true;
	     String strSql = "";
		try{
			conn.setAutoCommit(false);
			sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
			
			strSql = "delete from " + pub.yssGetTableName("TB_Data_FutHedgSecurity") +
            		 " where FNum = " +dbl.sqlString(sNum);
        
			dbl.executeSql(strSql);
			strSql =
	            "insert into " + pub.yssGetTableName("TB_Data_FutHedgSecurity") +
	            "(FNum, FSecurityCode,FTradeAmount,FTradePrice,FTradeMoney,FBaseCuryRate,FPortCuryRate" +
	            ",FCheckState, FCreator, FCreateTime,FCheckUser,FCheckTime) " +
	            " values (?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(strSql);
			
			for (int i = 0; i < sMutilRowAry.length; i++) {
				this.parseRowStr(sMutilRowAry[i]);
				
				pstmt.setString(1, this.HedgingSecuritySetBean.getSNum());
				pstmt.setString(2, this.HedgingSecuritySetBean.getSSecurityCode());
				pstmt.setDouble(3, this.HedgingSecuritySetBean.getDTradeAmount());
				pstmt.setDouble(4, this.HedgingSecuritySetBean.getDTradePrice());
				pstmt.setDouble(5, this.HedgingSecuritySetBean.getDTradeMoney());
				pstmt.setDouble(6, this.HedgingSecuritySetBean.getDBaseCuryRate());
				pstmt.setDouble(7, this.HedgingSecuritySetBean.getDPortCuryRate());
				pstmt.setInt(8, 1);
				pstmt.setString(9, this.creatorCode);
				pstmt.setString(10, this.creatorTime);
				pstmt.setString(11, this.checkUserCode);
				pstmt.setString(12, this.checkTime);
				
				pstmt.executeUpdate();
				
			}
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
		}catch (Exception e) {
			throw new YssException("保存套期保值证券信息出错！",e);
		}finally{
			dbl.endTransFinal(conn, bTrans);
			dbl.closeStatementFinal(pstmt);
		}
		return null;
	}
	/**
	 * 保存套期保值证券信息
	 */
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 解析期货套期证券关联数据
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.HedgingSecuritySetBean.setSNum(reqAry[0]);
            this.HedgingSecuritySetBean.setSSecurityCode(reqAry[1]);
            this.HedgingSecuritySetBean.setSSecurityName(reqAry[2]);
            this.HedgingSecuritySetBean.setDTradeAmount(YssFun.toDouble(reqAry[3]));
            this.HedgingSecuritySetBean.setDTradePrice(YssFun.toDouble(reqAry[4]));
            this.HedgingSecuritySetBean.setDTradeMoney(YssFun.toDouble(reqAry[5]));
            this.HedgingSecuritySetBean.setDBaseCuryRate(YssFun.toDouble(reqAry[6]));
            
            this.HedgingSecuritySetBean.setDPortCuryRate(YssFun.toDouble(reqAry[7]));
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new FuturesHedgingSecuritySetAdmin();
                    filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

            super.parseRecLog();

        } catch (Exception e) {
            throw new YssException("解析期货套期证券关联数据出错", e);
        }
	}
	/**
	 * 拼接期货套期证券关联数据
	 */
	public String buildRowStr() throws YssException {
		StringBuffer buff = new StringBuffer();
		try{
			buff.append(this.HedgingSecuritySetBean.getSNum()).append("\t");
			buff.append(this.HedgingSecuritySetBean.getSSecurityCode()).append("\t");
			buff.append(this.HedgingSecuritySetBean.getSSecurityName()).append("\t");
			buff.append(this.HedgingSecuritySetBean.getDTradeAmount()).append("\t");
			buff.append(this.HedgingSecuritySetBean.getDTradePrice()).append("\t");
			buff.append(this.HedgingSecuritySetBean.getDTradeMoney()).append("\t");
			buff.append(this.HedgingSecuritySetBean.getDBaseCuryRate()).append("\t");
			buff.append(this.HedgingSecuritySetBean.getDPortCuryRate()).append("\t");
			
			buff.append(super.buildRecLog());
			
		}catch (Exception e) {
			throw new YssException("拼接期货套期证券关联数据出错", e);
		}
		return buff.toString();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 查询数据
	 */
	public String getListViewData1() throws YssException {
		String strSql = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShowDataStr = new StringBuffer();
        StringBuffer bufAllDataStr = new StringBuffer();
		try{
			sHeader = this.getListView1Headers();
			
			strSql = " select a.*,b.FSecurityName,c.fusername as fcreatorname,d.fusername as fcheckusername from " + pub.yssGetTableName("TB_Data_FutHedgSecurity") +
					 " a join(select * from " + pub.yssGetTableName("tb_para_security") +" where FCheckState =1) b on a.FSecurityCode =b.FSecurityCode" +
					 " left join (select fusercode, fusername from tb_sys_userlist) c on a.fcreator =c.fusercode" +
					 " left join (select fusercode, fusername from tb_sys_userlist) d on a.fcheckuser = d.fusercode" +
					 " where a.FNum =" + dbl.sqlString(this.HedgingSecuritySetBean.getSNum()) +
					 " and a.FCheckState = 1";
					
			
			rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	//------ modify by wangzuochun 2010.08.06  MS01516    保值证券信息分页中listview中显示的数据格式与双击浏览时显示的格式不一致    QDV4赢时胜(测试)2010年07月29日05_B  
            	bufShowDataStr.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).append(YssCons.YSS_LINESPLITMARK);
                setResultSetAttr(rs);
                //----------------------MS01516---------------------//
                bufAllDataStr.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }

            if (bufShowDataStr.toString().length() > 2) {
                sShowDataStr = bufShowDataStr.toString().substring(0,
                    bufShowDataStr.toString().length() - 2);
            }
            if (bufAllDataStr.toString().length() > 2) {
                sAllDataStr = bufAllDataStr.toString().substring(0,
                    bufAllDataStr.toString().length() - 2);
            }
            if (rs != null) { //关闭记录集
                dbl.closeResultSetFinal(rs);
            }
		}catch (Exception e) {
			throw new YssException("获取期货套期证券关联数据出错", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +"\r\f" +this.getListView1ShowCols();
	}
	/**
	 * 赋值
	 * @param rs
	 * @throws YssException 
	 */
	private void setResultSetAttr(ResultSet rs) throws YssException {
		try{
			this.HedgingSecuritySetBean.setSNum(rs.getString("FNum"));
			this.HedgingSecuritySetBean.setSSecurityCode(rs.getString("FSecurityCode"));
			this.HedgingSecuritySetBean.setSSecurityName(rs.getString("FSecurityName"));
			this.HedgingSecuritySetBean.setDTradeAmount(rs.getDouble("FTradeAmount"));
			this.HedgingSecuritySetBean.setDTradePrice(rs.getDouble("FTradePrice"));
			this.HedgingSecuritySetBean.setDTradeMoney(rs.getDouble("FTradeMoney"));
			this.HedgingSecuritySetBean.setDBaseCuryRate(rs.getDouble("FBaseCuryRate"));
			this.HedgingSecuritySetBean.setDPortCuryRate(rs.getDouble("FPortCuryRate"));
			
			super.setRecLog(rs);
			
		}catch (Exception e) {
			throw new YssException("为期货套期证券关联数据赋值出错！", e);
		}
	}

	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

}
