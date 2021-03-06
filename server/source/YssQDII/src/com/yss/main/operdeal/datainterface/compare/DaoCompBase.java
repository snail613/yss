package com.yss.main.operdeal.datainterface.compare;

import java.util.*;

import com.yss.dsub.*;
import com.yss.main.datainterface.compare.*;
import com.yss.main.operdeal.datainterface.*;
import com.yss.util.*;

/**
 * 接口核对功能的基类
 * QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090430
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DaoCompBase
    extends BaseBean {
    public DaoCompBase() {
    }

    protected String sCompCode = "";
    protected String sCompType = "";
    protected String sTempTab = ""; //临时表名称
    protected String sCheckState=""; //QDV4赢时胜（上海）2009年7月22日01_A MS00574 by leeyu 20090729 增加审核状态
    protected java.util.Date dStartDate;
    protected java.util.Date dEndDate;
    protected Hashtable htDynamic;
    protected ImpCusInterface impCus;
    protected DaoCompQuery compQuery;
    protected DaoCompareExSet compareEx;

    public void init(String sCompCode, String sCompType, java.util.Date dStartDate, java.util.Date dEndDate, Hashtable htDynamic) throws YssException {
        this.sCompCode = sCompCode;
        this.sCompType = sCompType;
        this.dStartDate = dStartDate;
        this.dEndDate = dEndDate;
        this.htDynamic = htDynamic;
        setCompareExSet();
    }

    public void setDaoCompQuery(DaoCompQuery compQuery) {
        this.compQuery = compQuery;
    }

    /**
     * 保存前台选择的数据
     * 执行保存数据的预处理方法
     * @param alData ArrayList　要处理的数据
     * @return String 返回保存结果
     * @throws YssException
     */
    public String saveData(ArrayList alData) throws YssException {
        return "";
    }

    /**
     * 做核对预处理，用于保存数据
     * @throws YssException
     */
    public void doPrepared() throws YssException {

    }

    /**
     * 获取核对数据,方法一次性返回差异数据、与无差异数据到前台
     * 注：全部数据=差异数据+无差异数据
     * @return String
     * @throws YssException
     */
    public String getDataListView() throws YssException {
        return "";
    }

    /**
     * 初始化获取数据
     * 1:创建临时表
     * 2:加载初始化数据,包括：查询条件、列值
     * @return String
     * @throws YssException
     */
    public String initLoadListView() throws YssException {
        return "";
    }

    /**
     * 字段核算
     * 处理两数据是否在指定的核算范围内
     * 若两个数据在比值范围内
     * @param field DaoCompareField
     * @param dMarkValue double 基准源值
     * @param dValue double
     * @return String 当为true时为超过设定范围
     * @throws YssException
     */
    public String CalcCheckValue(DaoCompareField field, double dMarkValue, double dValue) throws YssException {
        double dResult = 0.0D;
        boolean bResult = false; //默认为没有超过范围
        String sResult = ""; //返回一个值：包括　布尔值\t计算值
        try {
            if (field.getIAccountType() == 1) { //相对值
                //ABS(值2-值1)/值1*100%
                dResult = YssD.div(Math.abs(YssD.sub(dValue, dMarkValue)),
                                   dMarkValue);
                sResult = YssFun.formatNumber(YssD.round(YssD.mul(dResult, 100), 2),
                                              "#,###.###") + "%";
                //dResult = YssD.round(YssD.mul(dResult, 100), 2);//这里先不乘上100，这里需将最大值放大100倍 by leeyu 20090525
            } else if (field.getIAccountType() == 2) { //绝对值
                dResult = YssD.sub(dValue, dMarkValue); //值1－基础源值
                sResult = YssFun.formatNumber(dResult, "#,###.###");
            } else { //空值计算时
                dResult = 0D;
                sResult = " ";
            }
            if (dResult >= field.getDRangeMax() || dResult <= field.getDRangeMin()) { //如果算的值不在范围内
                bResult = true;
            } else {
                bResult = false;
            }
            if (dResult == 0D) { //当结果值为０时，将其设置为无误
                bResult = false;
            }
            sResult = String.valueOf(bResult) + "\t" + sResult;
            return sResult;
        } catch (Exception ex) {
            throw new YssException("处理字段" + field.getSFieldCode() + "核对方式结果出错");
        }
    }

    public void setImpCusInterface(ImpCusInterface cus) {
        this.impCus = cus;
    }

    //QDV4赢时胜（上海）2009年7月22日01_A MS00574 by leeyu 20090729 增加审核状态
    //attribute
    public void setCheckState(String sCheck){
        this.sCheckState = sCheck;
    }

    public ImpCusInterface getImpCusInterface() {
        if (this.impCus == null) {
            impCus = new ImpCusInterface();
            impCus.setYssPub(pub);
        }
        return this.impCus;
    }

    private void setCompareExSet() throws YssException {
        if (compareEx == null) {
            compareEx = new DaoCompareExSet();
            compareEx.setSCompCode(sCompCode);
            compareEx.setYssPub(pub);
            compareEx.getSetting();
            this.sTempTab = compareEx.getSTabName(); //在这里将临时表的名称确定下来
            compQuery.setSTempTab(this.sTempTab);
        }
    }

}
