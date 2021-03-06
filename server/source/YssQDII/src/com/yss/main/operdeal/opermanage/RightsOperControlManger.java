package com.yss.main.operdeal.opermanage;

import java.sql.Connection;
import java.util.Date;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.main.operdata.OperDealBean;
import com.yss.main.operdeal.rightequity.REDivdend;
import java.util.ArrayList;

import com.yss.main.operdeal.rightequity.REBondToCash;
import com.yss.main.operdeal.rightequity.REBonusShare;
import com.yss.main.operdeal.rightequity.REDeflationBonus;
import com.yss.main.operdeal.rightequity.RERightIssue;
import com.yss.main.operdeal.rightequity.REMayApartBond;
import com.yss.main.operdeal.rightequity.RECashConsideration;
import com.yss.pojo.sys.YssStatus;

/**
 * <p>Title: xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理</p>
 *
 * <p>Description: 所有权益处理的控制类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class RightsOperControlManger extends BaseOperManage{
    private ArrayList rightData;//保存交易子表中数据
    private ArrayList tradeRealRightData;//保存交易关联表中数据
    
	public RightsOperControlManger() {
    }

    /**
     * 初始化信息
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     * @throws YssException
     */
    public void initOperManageInfo(java.util.Date dDate, String portCode) throws YssException {
        this.dDate = dDate;//操作日期
        this.sPortCode = portCode;//组合代码
    }

    /**
     * 执行业务处理
     * @throws YssException
     */
    public void doOpertion() throws YssException {
    	//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        Connection conn = null;
        boolean bTrans = true;
        //---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try {
        	//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
    		conn = dbl.loadConnection();// 获取连接
    		conn.setAutoCommit(false); // 设置为手动打开连接
    		dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_SubTrade")); // 给操作表加锁
    		dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Trade")); // 给操作表加锁
        	//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
    		
        	boolean state=true;
            /*股票分红权益处理 */
            REDivdend dividend = new REDivdend(); //实例化对象
            dividend.setYssPub(pub); //设置PUB
            /**shashijie 2012-6-7 BUG 4733  */
            //保存业务资料数据之前，先删除已经结算产生的资金调拨数据              
			dividend.delCashTransfer(YssOperCons.YSS_JYLX_PX,this.dDate,this.sPortCode);
			/**end*/
            rightData = dividend.getDayRightEquitys(this.dDate, this.sPortCode); //获取权益数据
            dividend.saveRightEquitys(rightData, this.dDate, this.sPortCode); //保存权益数据到交易子表
            //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
            if(rightData!=null && rightData.size()>0){
            	
            	state = false;
            }
            
            /*送股权益处理*/
            REBonusShare bonusShare = new REBonusShare(); //实例化对象
            bonusShare.setYssPub(pub); //设置PUB
            rightData = bonusShare.getDayRightEquitys(this.dDate, this.sPortCode); //获取权益数据
            bonusShare.saveRightEquitys(rightData, this.dDate, this.sPortCode); //保存权益数据到交易子表
            //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
            if(rightData!=null && rightData.size()>0){
            	
            	state = false;
            }
            /**缩股权益处理  shashijie 2011-08-09 STORY 1434 */
            REDeflationBonus bonus = new REDeflationBonus(); //实例化对象
            bonus.setYssPub(pub); //设置PUB
            rightData = bonus.getDayRightEquitys(this.dDate, this.sPortCode); //获取权益数据
            bonus.saveRightEquitys(rightData, this.dDate, this.sPortCode); //保存权益数据到交易子表
            /**------------------end------------------*/
            
			 //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
            if(rightData!=null && rightData.size()>0){
            	
            	state = false;
            }
            /*配股权益处理*/
            RERightIssue rightsIssue = new RERightIssue(); //实例化对象
            rightsIssue.setYssPub(pub); //设置PUB
            rightData = rightsIssue.getDayRightEquitys(this.dDate, this.sPortCode); //获取权益数据
            rightsIssue.saveRightEquitys(rightData, this.dDate, this.sPortCode); //保存权益数据到交易子表
            rightsIssue.rightIssuePaymentDeal(this.dDate, this.sPortCode);//处理配股缴款数据
            
            //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
            if(rightData!=null && rightData.size()>0){
            	
            	state = false;
            }
            
            /*可分离债送配权益处理*/
            REMayApartBond mayApartBond = new REMayApartBond(); //实例化对象
            mayApartBond.setYssPub(pub); //设置PUB
            rightData = mayApartBond.getDayRightEquitys(this.dDate, this.sPortCode); //获取交易子表中数据
            tradeRealRightData = mayApartBond.getTradeRealRightData(); //获取交易关联数据
            mayApartBond.saveRightEquitys(rightData, tradeRealRightData, this.dDate, this.sPortCode); //保存权益数据到交易子表和交易关联表中

            //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
            if(rightData!=null && rightData.size()>0){
            	
            	state = false;
            }
            
            /*现金对价权益处理*/
            RECashConsideration cashConsideration = new RECashConsideration(); //实例化对象
            cashConsideration.setYssPub(pub); //设置PUB
            rightData = cashConsideration.getDayRightEquitys(this.dDate, this.sPortCode); //获取交易子表中数据
            tradeRealRightData = cashConsideration.getTradeRealRightData(); //获取交易关联数据
            cashConsideration.saveRightEquitys(rightData, tradeRealRightData, this.dDate, this.sPortCode); //保存权益数据到交易子表和交易关联表中
           //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
            if(rightData!=null && rightData.size()>0){
            	
            	state = false;
            }
            
          //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    	  //当日产生数据，则认为有业务。
            if(state){
            	this.sMsg="        当日无业务";
            }
            
            //20120224 added by liubo.Story #1736
            //直接使用OperDealBean中【日终处理】>>【权益处理】现有的方法来计算债券兑付和债券派息
            //计算债券兑付
            //============================
            OperDealBean operDeal = null;
            YssStatus ysStatus = null;
            
            String sParam = YssFun.formatDate(this.dDate) + "\t"
			+ YssFun.formatDate(dDate) + "\t" + this.sPortCode + "\t"
			+ pub.getPrefixTB() + "\t";
            

			//20130508 deleted by liubo.Story #3528
			//债券兑付功能被继承到了债券派息模块中，需要删除独立的债券兑付功能
			//******************************************
//            operDeal = new OperDealBean();
//            	
//	        operDeal.setYssPub(pub);
//	        ysStatus = new YssStatus();
//	        operDeal.setYssRunStatus(ysStatus);
//			operDeal.parseRowStr(sParam + "FIInterest");
//			//---add by songjie 2012.09.06 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
//			if(this.comeFromBsnDeal){
//				operDeal.comeFromDD = true;
//			}
//			//---add by songjie 2012.09.06 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
//			operDeal.addSetting();
            
			//***************end***************************
				
			//计算债券派息
			operDeal = new OperDealBean();
	
	        operDeal.setYssPub(pub);
	        ysStatus = new YssStatus();
	        operDeal.setYssRunStatus(ysStatus);
			operDeal.parseRowStr(sParam + "BondDividend");
			//---add by songjie 2012.09.06 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
			if(this.comeFromBsnDeal){
				operDeal.comeFromDD = true;
			}
			//---add by songjie 2012.09.06 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			String sDataList = operDeal.addSetting();
            //============end================
			
			if(sDataList != null && sDataList.indexOf("true") > -1)
			{
				this.sMsg="";
			}
			else
			{
				this.sMsg="        当日无业务";
			}
        
			//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
			//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        } catch (Exception e) {
            throw new YssException(e.getMessage());
			//---add by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        } finally{
        	this.comeFromBsnDeal = false;
			//add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
			dbl.endTransFinal(conn, bTrans);
        }
		//---add by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
    }
}








