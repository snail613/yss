package com.yss.commeach;

import java.util.*;
import com.yss.util.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;

public class EachPubDataOper
    extends BaseCommEach {
    public EachPubDataOper() {
    }

    private Hashtable htPubParams = null; //公共数据跨组合群参数QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
    }

    /**
     * 创建公共表数据的视图的方法
     * 创建 证券信息表、行情表、汇率表三张表的视图
     * @throws YssException
     */
    public void createPubDataView() throws YssException {
        String sqlStr = "";
        try {
            if (dbl.dbType == YssCons.DB_ORA) {
                //先创建证券信息表的视图V_Base_Security视图
                if (dbl.yssViewExist("V_Base_Security")) {
                    dbl.executeSql("drop view V_Base_Security");
                }
                sqlStr = "create view V_Base_Security as (select y.FSECURITYCODE,y.FSTARTDATE,y.FSECURITYNAME,y.FSECURITYSHORTNAME,y.FSECURITYCORPNAME,y.FCATCODE,y.FSUBCATCODE,y.FCUSCATCODE,y.FEXCHANGECODE,y.FMARKETCODE,y.FEXTERNALCODE," +
                    "y.FISINCODE,y.FTRADECURY,y.FHOLIDAYSCODE,y.FSETTLEDAYTYPE,y.FSETTLEDAYS,y.FSECTORCODE,y.FTOTALSHARE,y.FCURRENTSHARE,y.FHANDAMOUNT,y.FFACTOR,y.FISSUECORPCODE,y.FDESC,y.FCHECKSTATE," +
                    "y.FCREATOR,y.FCREATETIME,y.FCHECKUSER,y.FCHECKTIME from tb_base_security y where not exists(" +
                    "select * from (select y1.* from tb_base_security y1 join " +
                    "(select * from tb_base_security where FASSETGROUPCODE<>' ' )y2 on " +
                    "y1.FSecurityCode=y2.FSecurityCode and y1.FStartDate=y2.FStartDate )x where x.FSecurityCode=y.FSecurityCode " +
                    " and x.FStartDate=y.FStartDate and (y.FASSETGROUPCODE=' ' or y.FASSETGROUPCODE<>'" + pub.getAssetGroupCode() + "')))"; //将组合群带进来
                dbl.executeSql(sqlStr);
                //再创建行情数据表的视图V_Base_MarketValue视图
                if (dbl.yssViewExist("V_Base_MarketValue")) {
                    dbl.executeSql("drop view V_Base_MarketValue");
                }
                sqlStr = "create view V_Base_MarketValue as (select y.FMKTSRCCODE,y.FSECURITYCODE,y.FMKTVALUEDATE,y.FMKTVALUETIME,y.FPORTCODE,y.FBARGAINAMOUNT,y.FBARGAINMONEY," +
                    "y.FYCLOSEPRICE,y.FOPENPRICE,y.FTOPPRICE,y.FLOWPRICE,y.FCLOSINGPRICE,y.FAVERAGEPRICE," +
                    "y.FNEWPRICE,y.FMKTPRICE1,y.FMKTPRICE2,y.FMARKETSTATUS,y.FDESC,y.FDATASOURCE,y.FCHECKSTATE,y.FCREATOR," +
                    "y.FCREATETIME,y.FCHECKUSER,y.FCHECKTIME from TB_Base_MARKETVALUE y where not exists(select * from ( " +
                    "select y1.* from TB_Base_MARKETVALUE y1 join (select * from TB_Base_MARKETVALUE where FASSETGROUPCODE<>' ' )y2 on " +
                    "y1.FMKTSRCCODE=y2.FMKTSRCCODE and y1.FSECURITYCODE=y2.FSECURITYCODE and y1.FMKTVALUEDATE=y2.FMKTVALUEDATE and y1.FMKTVALUETIME= y2.FMKTVALUETIME and y1.FPORTCODE=y2.FPORTCODE " +
                    ")x where x.FMKTSRCCODE=y.FMKTSRCCODE and x.FSECURITYCODE=y.FSECURITYCODE and x.FMKTVALUEDATE=y.FMKTVALUEDATE " +
                    "and x.FMKTVALUETIME= y.FMKTVALUETIME and x.FPORTCODE=y.FPORTCODE and (y.FASSETGROUPCODE=' ' or y.FASSETGROUPCODE<>'" + pub.getAssetGroupCode() + "')))"; //将组合群带进来
                dbl.executeSql(sqlStr);
                //再创建汇率数据表的视图V_Base_ExchangeRate视图
                if (dbl.yssViewExist("V_Base_ExchangeRate")) {
                    dbl.executeSql("drop view V_Base_ExchangeRate");
                }
                sqlStr = "create view V_Base_ExchangeRate as (select y.FEXRATESRCCODE,y.FCURYCODE,y.FMARKCURY,y.FEXRATEDATE,y.FEXRATETIME,y.FPORTCODE,y.FEXRATE1,y.FEXRATE2,y.FEXRATE3,y.FEXRATE4,y.FEXRATE5,y.FEXRATE6,y.FEXRATE7," +
                    "y.FEXRATE8,y.FDESC,y.FDATASOURCE,y.FCHECKSTATE,y.FCREATOR,y.FCREATETIME,y.FCHECKUSER,y.FCHECKTIME from tb_base_exchangeRate y where not exists(select * from (" +
                    "select y1.* from tb_base_exchangeRate y1 join (select * from tb_base_ExchangeRate where FASSETGROUPCODE<>' ' )y2 on " +
                    "y1.FExRateSrcCode=y2.FExRateSrcCode and y1.FCuryCode=y2.FCuryCode and y1.FMarkCury=y2.FMarkCury and y1.FExRateDate=y2.FExRateDate and y1.FExRateTime=y2.FExRateTime and y1.FPortCode=y2.FPortCode)x where " +
                    " x.FExRateSrcCode=y.FExRateSrcCode and x.FCuryCode=y.FCuryCode and x.FMarkCury=y.FMarkCury and x.FExRateDate=y.FExRateDate and x.FExRateTime=y.FExRateTime and x.FPortCode=y.FPortCode " +
                    " and (y.FASSETGROUPCODE=' ' or y.FASSETGROUPCODE<>'" + pub.getAssetGroupCode() + "')))"; //将组合群带进来
                dbl.executeSql(sqlStr);
            } else if (dbl.dbType == YssCons.DB_DB2) {

            } else if (dbl.dbType == YssCons.DB_SQL) {

            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
    }

    /**
     * QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
     * @return Hashtable
     */
    public Hashtable getHtPubParams() {
        if (htPubParams == null || htPubParams.size() == 0) { //当变量中的参数为空或无内容时就取一次
            setHtPubParams();
        }
        return htPubParams;
    }

    /**
     * 设置htPubParams参数的方法
     * QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
     */
    private void setHtPubParams() {
        try {
            if (htPubParams == null) {
                htPubParams = new Hashtable();
            }
            CtlPubPara ctlPara = new CtlPubPara();
            ctlPara.setYssPub(pub);
            htPubParams = ctlPara.getPubParamType();
        } catch (Exception ex) {
        }
    }

}
