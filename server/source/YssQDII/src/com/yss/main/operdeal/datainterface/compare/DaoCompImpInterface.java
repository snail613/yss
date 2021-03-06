package com.yss.main.operdeal.datainterface.compare;

import java.sql.*;
import java.util.*;

import com.yss.main.dao.*;
import com.yss.main.datainterface.*;
import com.yss.main.datainterface.compare.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdeal.datainterface.pojo.*;
import com.yss.util.*;

/***
 *@ 此类用于实现 数据接口核对保存功能，
 *@ 包括核对、查询、保存的具体方法
 *@ QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090430
 */
public class DaoCompImpInterface
    extends DaoCompBase {
    public DaoCompImpInterface() {
    }

    private ArrayList alCompField = null; //存放指定的字段配置信息,这里是按顺序存

    /**
     * 保存前台选择的数据
     * @param alData ArrayList　已选择的要处理的数据.
     * @return String 返回保存结果
     * @throws YssException
     */
    public String saveData(ArrayList alData) throws YssException {
        String[] arrDpDdCode = null; //预处理代码组
        DaoPretreatBean pret = null;
        try {
            this.setCompField();
            if (this.compareEx.getSDpDdCode() == null || this.compareEx.getSDpDdCode().length() == 0) {
                throw new YssException("核对数据源配置预处理代码没有设置，保存数据不成功！");
            }
            arrDpDdCode = this.compareEx.getSDpDdCode().split(","); //分解预处理，然后遍历之
            for (int i = 0; i < arrDpDdCode.length; i++) {
                if (arrDpDdCode[i].trim().length() == 0) {
                    continue;
                }
                pret = new DaoPretreatBean();
                pret.setYssPub(pub);
                pret.setDPDsCode(arrDpDdCode[i]);
                pret.getSetting();
                //通过buildSaveSql方法将前台传过来的值全部插入到数据源中，实现批量插入的功能
                pret.setDataSource(buildSaveSql(pret.getDataSource(), alData, pret.getRelaCompareCode())); //此步处理预处理数据源中的特定字符
                //1:调用接口导入的通用方法进行处理保存,
                //注：这里取不到组合，在插入数据时若采用spring方法调用，对于部分要用通用组合计算数据的方法会报错的
                if(pret.getDataSource().trim().length()>0){ //QDV4深圳2010年01月11日01_B MS00919 by leeyu 20100110 修改核对时取null报错的问题
                	this.getImpCusInterface().init(this.dStartDate, this.dEndDate, "", "",sCheckState, "");//添加审核状态,默认为已审核 by leeyu 20090722 //QDV4赢时胜（上海）2009年7月22日01_A MS00574 by leeyu 20090729 增加审核状态
                	this.getImpCusInterface().doOnePretreat(pret); //调用接口中的预处理方法来执行导入操作
                }
            }
        } catch (Exception ex) {
            throw new YssException("保存数据出错", ex);
        }
        return "true";
    }

    /**
     * 前台点击查询用
     * 获取核对数据,方法一次性返回差异数据、与无差异数据到前台
     * 注：全部数据=差异数据+无差异数据
     * @return String
     * @throws YssException
     */

    public String getDataListView() throws YssException {
        //加载临时表的数据
        String sGroup = "";
        ArrayList arrGroupField = null; //存放所有的分组字段
        StringBuffer bufResult = new StringBuffer(); //查询结果数据
        StringBuffer bufGroup = new StringBuffer(); //查询出的所有的组
        String[] arrDpDC = null; //所有的数据配置预处理代码
        ArrayList arrGroup = null; //存放所有的分组
        Hashtable htDatas = null; //所有的数据
        Hashtable htMarkData = null; //基准行
        Hashtable htData = null; //普通行
        Hashtable htDiff = null; //差异行
        DaoCompareField Field = null;
        try {
            arrGroupField = new ArrayList();
            //1:获取所有字段的属性
            setCompField();
            //2.1:获取核对相关的全部分组字段
            for (int i = 0; i < alCompField.size(); i++) {
                Field = (DaoCompareField) alCompField.get(i);
                if (Field.getIGroupField() == 1) { //当为分组字段时
                    sGroup += Field.getSFieldCode();
                    sGroup += ",";
                    arrGroupField.add(Field);
                }
            }
            if (sGroup.endsWith(",")) {
                sGroup = sGroup.substring(0, sGroup.length() - 1);
            }
            //2.2:获取核对相关的全部分组
            if(sGroup==null || sGroup.length()==0) return "";//by guyichuan BUG2206点＂核对＂时出现问题
            arrGroup = getAllGroup(sGroup, arrGroupField); //这里获取所有的分组数据

            //4:循环所有数据源(基准数据源除外)
            arrDpDC = this.compareEx.getSDpDcCode().split(",");
            htDatas = new Hashtable(); //保存所有的数据到集合中
            //先取出所有的数据到集合中
            for (int iDs = 0; iDs < arrDpDC.length; iDs++) {
                htDatas.put(arrDpDC[iDs], getSourceData(arrDpDC[iDs], arrGroupField));
            }
            //3:先取出基准数据出来。
            htMarkData = (Hashtable) htDatas.get(this.compareEx.getSMarkSource());
            boolean bCheck = false;
            //循环所有的分组，进行循环判断，并将核对的结果放到相应的代码中去
            //当前代码还不能处理有两个以个的核对源的情况(这种情况较复杂)，仅支持一个基准源与一个普通源的情况
            for (int iDs = 0; iDs < arrDpDC.length; iDs++) {
                if (arrDpDC[iDs].equalsIgnoreCase(this.compareEx.getSMarkSource())) {
                    continue; //过滤掉基准源数据
                }
                htData = (Hashtable) htDatas.get(arrDpDC[iDs]); //取出非基准数据源数据
                for (int row = 0; row < arrGroup.size(); row++) {
                    Hashtable htMarkSource = (Hashtable) htMarkData.get(arrGroup.get(row).toString().split("\n\f")[0]); //取出主键组数据
                    Hashtable htDataSource = (Hashtable) htData.get(arrGroup.get(row).toString().split("\n\f")[0]); //取出非主键组数据
                    htDiff = new Hashtable();
                    bCheck = false; //判断本次组值取出的差异行数据的结果是否有差异，以便在前台显示不同的颜色
                    bCheck = checkRowDiff(htMarkSource, htDataSource, htDiff);
                    if (htMarkSource != null) {
                        bufResult.append(buildOneRowStr(htMarkSource, this.compareEx.getSMarkSource(), "", false)).append("\f\f");
                    } else {
                        bCheck = true; //如果基准源为空，则差异行为真
                    }
                    if (htDataSource != null) {
                        bufResult.append(buildOneRowStr(htDataSource, arrDpDC[iDs], "", false)).append("\f\f");
                    } else {
                        bCheck = true; //如果非基准源为空，则差异行为真
                    }
                    bufResult.append(buildDiffRow(htMarkSource == null ? htDataSource : htMarkSource, htDiff, arrDpDC[iDs], bCheck)).append("\f\f");
                }
            }
            for (int i = 0; i < arrGroup.size(); i++) {
                bufGroup.append(arrGroup.get(i)).append("\b\b");
            }
            if (bufGroup.length() > 2) {
                bufGroup.setLength(bufGroup.length() - 2);
            }
            if (bufResult.length() > 2) {
                bufResult.setLength(bufResult.length() - 2);
            }
        } catch (Exception ex) {
            throw new YssException("加载数据出错", ex);
        }
        return bufGroup.toString() + "\r\f" + bufResult.toString();
    }

    /**前台点击核对按钮时用
     * 做核对预处理，用于保存数据
     * @param sPreCode String　预处理代码
     * @throws YssException
     */
    public void doPrepared() throws YssException {
        String[] arrPrepCode = null;
        DaoPretreatBean pret = null;
        try {
            //1:删除临时表数据
            this.compQuery.setSTempTab(this.sTempTab);
            this.compQuery.setSCompCode(this.sCompCode);
            this.compQuery.deleteTempTab(); //快速删除临时表数据
            //2:循环数据源配置，执行预处理，并将值插入到临时表里
            if (this.compareEx.getSDpDcCode() != null && this.compareEx.getSDpDcCode().length() > 0) {
                arrPrepCode = this.compareEx.getSDpDcCode().split(",");
                for (int i = 0; i < arrPrepCode.length; i++) {
                    pret = new DaoPretreatBean();
                    pret.setYssPub(pub);
                    pret.setDPDsCode(arrPrepCode[i]);
                    pret.getSetting();
                    if (pret.getDsType() == 5) { //当为核对数据数据源时才开始执行
                        insertTgtTabData(pret); //执行预处理，插入数据到指定表中
                    }
                }
            }
            //3:查询数据
            //getDataListView();
        } catch (Exception ex) {
            throw new YssException("核对预处理出错", ex);
        }
    }

    /**
     * 前台系统打开界面时用
     * 初始化获取数据
     * 1:创建临时表
     * 2:加载初始化数据,包括：查询条件、列名称
     * @return String 返回
     * 返回1　查询条件:DaoCompareField
     * 返回2　列值：DaoCompareField
     * @throws YssException
     */
    public String initLoadListView() throws YssException {
        DaoCompareField compField = null;
        StringBuffer bufTitle = new StringBuffer();
        StringBuffer bufQue = new StringBuffer();
        try {
            //1:删除临时表
            compQuery.dropTempTab();
            //2:创建临时表
            compQuery.createTempTab();
            //3:加载初始化数据
            //01:查询核对字段表，取出名称，代码，查询条件
            setCompField();
            for (int i = 0; i < this.alCompField.size(); i++) {
                compField = (DaoCompareField) alCompField.get(i);
                bufTitle.append(compField.getSFieldName()).append("\f")
                    .append(compField.buildRowStr()).append("\b\b");
                if (compField.getIRogatoryField() == 1) {
                    bufQue.append(compField.buildRowStr()).append("\b\b");
                }
            }
            if (bufTitle.length() > 2) {
                bufTitle.setLength(bufTitle.length() - 2);
            }
            if (bufQue.length() > 2) {
                bufQue.setLength(bufQue.length() - 2);
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        }
        return bufTitle.toString() + "\r\f" + bufQue.toString() + "\r\f" + compareEx.getSMarkSource();
    }

    /**
     * 插入数据到临时表,处理核对数据源时将数据统一到核对临时表
     * @param pret DaoPretreatBean
     * @throws YssException
     */
    public void insertTgtTabData(DaoPretreatBean pret) throws
        YssException {
        CommonPrepFunBean prepFunBean = null;
        Connection conn = dbl.loadConnection();
        PreparedStatement pst = null;
        String strSql = "";
        ResultSet rsDs = null; //数据源的记录集
        ResultSet rs = null; //该记录集用来打开接口预处理字段设置表
        String strPretSql = "";
        IOperValue operValue = null;
        HashMap hmFieldType = null; //字段的字段名:字段的类型
        HashMap hmFieldSql = null; //根据ＳＱＬ查出的字段的类型
        int iPstOrder = 1; //pst的编号
        Object oData = ""; //通过函数获取的数据处理
        int tmpNum = 1; //暂时为了调试用的
        String sDsFields = ""; //通过spring 的方式，去调用某个涵数时
        String[] arrDsFields = null;
        ArrayList alDsFieldValue = null; //通过spring调用的方式，把要传入的参数放入ArrayList中
        boolean bTrans = false;
        try {
            setCompField();
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = buildInsertSql(pret.getDPDsCode()); //生成插入到目标表的sql语句
            pst = conn.prepareStatement(strSql);
            hmFieldType = dbFun.getFieldsType(this.sTempTab); //获取临时表的字段类型
            strPretSql =
                " select * " +
                " from " + pub.yssGetTableName("Tb_Dao_PretreatField") +
                " where FDPDsCode=" + dbl.sqlString(pret.getDPDsCode()) +
                " and FCheckState=1" +
                " order by FOrderIndex ";
            rs = dbl.openResultSet_antReadonly(strPretSql);
            //执行预处理中的数据源
            strSql = this.compQuery.buildSql(pret.getDataSource()); //生成数据源的sql语句
            rsDs = dbl.openResultSet(strSql); //数据源的记录集
            hmFieldSql = dbFun.getFieldsType(rsDs); //获取数据源的字段类型
            while (rsDs.next()) { //循环数据源的记录集
                tmpNum = tmpNum + 1; //为了调试用
                rs.beforeFirst();
                iPstOrder = 1;
                while (rs.next()) {
                    if (rs.getInt("FPretType") == 0) { //数据源获取
                        //直接通过rsDs记录集和数据源字段取数,并做插入操作
                        this.compQuery.init(rs.getString("FDsField"));
                        this.compQuery.setHmFieldType(hmFieldSql);
                        this.compQuery.setRsValue(rsDs);
                        oData = this.compQuery.replaceFormulaStr();
                        if ( ( (String) hmFieldType.get(rs.getString("FTargetField").toUpperCase())).indexOf("NUMBER") > -1) { //根据目标表字段判断字段类型,若为数值型就执行一次
                            oData = this.compQuery.getResultSet(oData); //获取相应的值
                        }
                        this.getImpCusInterface().setPretPstValue(pst, iPstOrder,
                            hmFieldType,
                            oData,
                            rs.getString("FTargetField"));
                    } else if (rs.getInt("FPretType") == 1) { //函数获取(通过spring的方式)
                        prepFunBean = new CommonPrepFunBean();
                        prepFunBean.setBeginDate(dStartDate);
                        prepFunBean.setEndDate(dEndDate);
                        alDsFieldValue = new ArrayList();
                        SpringInvokeBean springInvoke = new SpringInvokeBean();
                        springInvoke.setYssPub(pub);
                        springInvoke.setSICode(rs.getString("FSICode")); //设置Spring的调用代码
                        springInvoke.getSetting();
                        operValue = (IOperValue) pub.getDataInterfaceCtx().
                            getBean(springInvoke.getBeanID()); //通过beanId得到对象
                        sDsFields = rs.getString("FDsField"); //得到做为参数用的字段,用","分割

                        arrDsFields = sDsFields.split(",");
                        for (int i = 0; i < arrDsFields.length; i++) {
                            alDsFieldValue.add(rsDs.getString(arrDsFields[i]));
                        }
                        if (rs.getString("FTargetField").equalsIgnoreCase(
                            "FNum")) {
                            alDsFieldValue.add(this.getImpCusInterface()); // 处理交易编号的问题
                        }
                        alDsFieldValue.add(dStartDate); //将起始日期添加到list中
                        alDsFieldValue.add(dEndDate); //将终止日期添加到list中
                        prepFunBean.setObj(alDsFieldValue);
                        operValue.init(prepFunBean);
                        operValue.setYssPub(pub);
                        oData = operValue.getTypeValue(springInvoke.getParams());
                        iPstOrder = this.getImpCusInterface().setFunPstValue(pst,
                            iPstOrder,
                            hmFieldType,
                            oData,
                            rs.getString("FTargetField"));
                    }
                    iPstOrder++;
                }
                pst.setString(iPstOrder, pret.getDPDsCode()); //添加最后一项，将FID值也保存进去
                pst.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rsDs);
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 生成插入到临时表的SQL语句
     * @return String
     * @throws YssException
     */
    private String buildInsertSql(String sDpDsCode) throws
        YssException {
        String sResult = "";
        StringBuffer bufFields = new StringBuffer(); //所有字段的buf
        StringBuffer finalFieldsBuf = new StringBuffer(); //最终所有字段的buf
        String finalFields = ""; //最终所有字段
        String finalParam = ""; //所有字段对应的问号
        ResultSet rs = null;
        String sqlStr = "";
        try {
            sqlStr =
                " select FTarGetField " +
                " from " + pub.yssGetTableName("Tb_Dao_PretreatField") +
                " where FDPDsCode=" + dbl.sqlString(sDpDsCode) +
                " and FCheckState=1" +
                " order by FOrderIndex ";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufFields.append(rs.getString("FTarGetField")).append(",");
                finalFieldsBuf.append("?").append(",");
            }
            //这里还要加上FID
            bufFields.append("FID");
            finalFieldsBuf.append("?");
            finalFields = bufFields.toString();
            finalParam = finalFieldsBuf.toString();
        } catch (Exception ex) {
            throw new YssException("获取核对临时表字段出错!", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        sResult = "insert into " + this.sTempTab +
            " ( " + finalFields + " ) values (" + finalParam + ")";
        return sResult;
    }

    /**
     * 将保存的查询语句的条件添加到执行的ＳＱＬ中,批量插入，采用in方法插入值
     * 将每行的值编到一个串中进行处理
     * @param sDsStr String
     * @param sPrepSource 预处理来源,
     * @return String
     * @throws YssException
     */
    private String buildSaveSql(String sDsStr, ArrayList alSaveData, String sPrepSource) throws YssException {
        String[] arrData = null; //保存单行的各列的值用的。
        HashMap hmSaveData = null; //将各值按来源FID的方式存入到集合中。
        HashMap hmData = null; //存放一个来源的数据值的
        String sResult = ""; //返回结果

        sResult = sDsStr;
        if (alSaveData.size() == 0) {
            return sResult;
        }
        try {
            //1:取出前台传过来的参数，并进行编辑处理
            hmData = new HashMap();
            hmSaveData = new HashMap();
            for (int i = 0; i < alSaveData.size(); i++) {
                arrData = String.valueOf(alSaveData.get(i)).split("\t");
                if(arrData[arrData.length - 1].split("[=]").length<2) return "";//by guyichuan 2011.07.27 BUG2206
                String sFidValue = arrData[arrData.length - 1].split("[=]")[1];
                if (hmSaveData.get(sFidValue) != null) {
                    hmData = (HashMap) hmSaveData.get(sFidValue);
                    hmData = build(arrData, hmData);
                    hmSaveData.put(hmData.get("FID"), hmData);
                    hmData = new HashMap();
                } else {
                    hmData = new HashMap();
                    hmData = build(arrData, hmData);
                    hmSaveData.put(hmData.get("FID"), hmData);
                }
            }
            //2:根据预处理里的核对预处理代码取哪个值的FID
            if (hmSaveData.size() > 0) {
                if (sPrepSource != null && sPrepSource.length() > 0) {
                    hmData = (HashMap) hmSaveData.get(sPrepSource);
                    //3:通过通用方法替换掉特定的字符
                    this.compQuery.init(sDsStr);
                    this.compQuery.setAlField(this.alCompField);
                    this.compQuery.setHmSpecialValue(hmData);
                    sResult = String.valueOf(this.compQuery.replaceFormulaStr());
                    if(hmData==null){//QDV4深圳2010年01月11日01_B MS00919 by leeyu 20100110 修改核对时取null报错的问题
                    	sResult ="";
                    }
                } else {
                    Set s = hmSaveData.keySet();
                    Object[] obj = s.toArray();
                    for (int i = 0; i < obj.length; i++) {
                        hmData = (HashMap) hmSaveData.get(obj[i]);
                        //3:通过通用方法替换掉特定的字符
                        this.compQuery.init(sDsStr);
                        this.compQuery.setAlField(this.alCompField);
                        this.compQuery.setHmSpecialValue(hmData);
                        sResult = String.valueOf(this.compQuery.replaceFormulaStr());
                        if(hmData ==null){//QDV4深圳2010年01月11日01_B MS00919 by leeyu 20100110 修改核对时取null报错的问题
                        	sResult="";
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new YssException("编辑保存的SQL语句时出错", ex);
        }
        return sResult;
    }

    /**
     * 将数据存放到集合中
     * 多个参数值中间用逗号分隔
     * 根据数据来源将不同的来源的的值分开，将相同来源的同一个字段的值串联起来，
     * @param arrData String[]
     * @return HashMap
     * @throws YssException
     */
    private HashMap build(String[] arrData, HashMap htData) throws YssException {
        String[] arrDataValue = null;
        String sValue = "";
        try {
            for (int i = 0; i < arrData.length; i++) {
                arrDataValue = arrData[i].split("[=]");
                if (arrDataValue[0].equalsIgnoreCase("FID")) {
                    htData.put("FID", arrDataValue[1]);
                    continue;
                }
                if (htData.get(arrDataValue[0]) != null) {
                    sValue = String.valueOf(htData.get(arrDataValue[0]));
                    if (arrDataValue.length > 1) {
                        if (!sValue.equalsIgnoreCase(arrDataValue[1])) { //这里过滤掉重复的数据
                            sValue = sValue + "," + arrDataValue[1];
                        }
                    }
                    htData.put(arrDataValue[0], sValue);
                } else {
                    if (arrDataValue.length > 1) {
                        sValue = arrDataValue[1];
                    } else {
                        sValue = "";
                    }
                    htData.put(arrDataValue[0], sValue);
                }
            }
        } catch (Exception ex) {
            throw new YssException("处理保存的SQL语句条件时出错", ex);
        }
        return htData;
    }

    /**
     * 编写分组合并计算的SQL
     * @return String
      select sum(FTradeFee1) as FTradeFee1,FSecurityCode,FBrokerCode,FPortCode,FTradeTypeCode,FID
      from Temp_Test
      group by FSecurityCode,FID,FBrokerCode,FPortCode,FTradeTypeCode
      order by FSecurityCode,FBrokerCode,FPortCode,FTradeTypeCode,FID
     * @param sID String 数据来源
     * @throws YssException
     */
    private String buildGroupSql(String sID) throws YssException {
        StringBuffer Fieldbuf = new StringBuffer(); //查询字段
        StringBuffer bufGroup = new StringBuffer(); //分组语句 group by 与排序字段 order by
        String sOrder = "", sGroup = "", sField = "";
        String sqlStr = "";
        DaoCompareField compField = null;
        try {
            for (int i = 0; i < this.alCompField.size(); i++) {
                compField = (DaoCompareField) alCompField.get(i);
                if (compField.getSFieldType().equalsIgnoreCase("number")) {
                    if (compField.getIGroupField() == 1) { //当为分组字段时才将其合计
                        Fieldbuf.append("sum(").append(compField.getSFieldCode()).
                            append(")").append(" as ").append(compField.
                            getSFieldCode()).append(","); //求合运算，并别名为本字段
                    } else {
                        Fieldbuf.append(compField.getSFieldCode()).append(",");
                        bufGroup.append(compField.getSFieldCode()).append(",");
                    }
                } else {
                    Fieldbuf.append(compField.getSFieldCode()).append(",");
                    bufGroup.append(compField.getSFieldCode()).append(",");
                }
            }
            Fieldbuf.append("FID"); //添加上来源字段
            bufGroup.append("FID"); //添加上来源字段
            sOrder = bufGroup.toString();
            sGroup = bufGroup.toString();
            sField = Fieldbuf.toString();
            sqlStr = "select " + sField + " from " + this.sTempTab + " where FID=" + dbl.sqlString(sID) + buildDynamic() + " group by " +
                sGroup + " order by " + sOrder;
        } catch (Exception ex) {
            throw new YssException("编写分组合并计算的SQL出现异常！", ex);
        }
        return sqlStr;
    }

    /**
     * 获取核对配置信息下的全部核对字段的配置信息,并按序放在ArrayList中
     * @throws YssException
     */
    private void setCompField() throws YssException {
        String sqlStr = "";
        DaoCompareField compField = null;
        ResultSet rs = null;
        try {
            if (this.alCompField == null) {
                this.alCompField = new ArrayList();
            } else {
                return;
            }
            sqlStr = "select * from " + pub.yssGetTableName("Tb_Dao_CompField") +
                " where FCompCode =" + dbl.sqlString(this.sCompCode) +
                " order by FOrderIndex ";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                compField = new DaoCompareField();
                compField.setSFieldCode(rs.getString("FFIELDCODE"));
                compField.setSFieldName(rs.getString("FFIELDNAME"));
                compField.setIAccountType(rs.getInt("FACCOUNTTYPE"));
                compField.setIGroupField(rs.getInt("FGROUPFIELD"));
                compField.setIGroupTitleField(rs.getInt("FGROUPTITLEFIELD"));
                compField.setIPKField(rs.getInt("FPKFIELD"));
                compField.setIRogatoryField(rs.getInt("FROGATORYFIELD"));
                compField.setDRangeMax(rs.getDouble("FRANGEMAX"));
                compField.setDRangeMin(rs.getDouble("FRANGEMIN"));
                compField.setSFieldType(rs.getString("FFieldType"));
                compField.setSFieldPre(rs.getString("FFieldPre"));
                this.alCompField.add(compField);
            }
        } catch (Exception ex) {
            throw new YssException("获取字段信息数据有误", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取所有的主键数据
     * 多个组，中间用"-"分隔
     * @return String
     * @throws YssException
     */
    private ArrayList getAllGroup(String sGroup, ArrayList arrGroup) throws YssException {
        //查出临时表中所有的组
        ResultSet rs = null;
        String sqlStr = "";
        ArrayList alKeyGroup = new ArrayList();
        String sGroupField = "";
        try {
            sqlStr = "select " + sGroup + " from " + this.sTempTab + " where 1=1 " + buildDynamic() + " group by " + sGroup;
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                sGroupField = buildGroupField(arrGroup, rs); //将数据取出来
                alKeyGroup.add(sGroupField);
            }
        } catch (Exception ex) {
            throw new YssException("获取全部分组出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return alKeyGroup;
    }

    /**
     * 判断一行是否为差异行，若是差异返回true ，否则返回false
     * @param htMarkValue 基准字段值
     * @param htData 查出的一行的字段值
     * @param htDiff 差异行
     * @throws YssException
     */
    private boolean checkRowDiff(Hashtable htMarkValue, Hashtable htData, Hashtable htDiff) throws YssException {
        DaoCompareField Field = null;
        String[] arrCheck = null;
        double dMarkValue = 0D;
        double dDataValue = 0D;
        boolean bCheck = false; //默认为无差异
        try {
            if (htMarkValue == null) {
                dMarkValue = 0D;
            }
            if (htData == null) {
                dDataValue = 0D;
            }
            for (int i = 0; i < this.alCompField.size(); i++) {
                dMarkValue = 0D;
                dDataValue = 0D;
                Field = (DaoCompareField)this.alCompField.get(i);
                if (Field.getIAccountType() != 0) {
                    if (htMarkValue != null && htMarkValue.get(Field.getSFieldCode()) != null) {
                        dMarkValue = Double.parseDouble(htMarkValue.get(Field.getSFieldCode()).toString());
                    }
                    if (htData != null && htData.get(Field.getSFieldCode()) != null) {
                        dDataValue = Double.parseDouble(htData.get(Field.getSFieldCode()).toString());
                    }
                    arrCheck = this.CalcCheckValue(Field, dMarkValue, dDataValue).
                        split("\t");
                    if (arrCheck.length == 2) {
                        if (!bCheck) {
                            bCheck = Boolean.valueOf(arrCheck[0]).booleanValue();
                        }
                        htDiff.put(Field.getSFieldCode(), arrCheck[1]);
                    } else {
                        bCheck = true;
                        htDiff.put(Field.getSFieldCode(), " ");
                    }

                } else {
                    htDiff.put(Field.getSFieldCode(), "");
                }
            }
            return bCheck;
        } catch (Exception ex) {
            throw new YssException("核对差异行出现异常！", ex);
        }
    }

    /**
     * 获取数据源内所有的数据并放入一张哈希表
     * @param sSourceCode 数据源代码
     * @return Hashtable
     */
    private Hashtable getSourceData(String sSourceCode, ArrayList arrGroupField) throws YssException {
        Hashtable htSource = null;
        ResultSet rs = null;
        String sqlStr = "";
        StringBuffer buf = new StringBuffer();
        try {
            htSource = new Hashtable();
            sqlStr = buildGroupSql(sSourceCode); //获取分组的SQL
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                buf.append(buildGroupField(arrGroupField, rs));
                htSource.put(buf.toString().split("\n\f")[0], buildRowValue(rs)); //按分组信息将数据装入到Hash表中
                buf.setLength(0);
            }
            if (htSource.size() == 0) {
                throw new YssException("当前临时表无数据，请确定核对操作是否正确处理");
            }
        } catch (Exception ex) {
            throw new YssException("获取数据源内数据出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        //条件：采用固定条件部分，可通过 DaoCompQuery类处理
        //key = 组数据，
        //value=  buildRowValue(rs);
        return htSource;
    }

    /**
     * build字段的组，多个中间用"-"分隔
     * @param arrGroupField ArrayList
     * @param rs ResultSet
     * @return String
     * @throws YssException
     */
    private String buildGroupField(ArrayList arrGroupField, ResultSet rs) throws YssException {
        DaoCompareField Field = null;
        String fieldValue = "";
        StringBuffer bufTitle = new StringBuffer(); //存放列标题信息
        StringBuffer buf = new StringBuffer(); //存放分组字段的信息
        try {
            for (int i = 0; i < arrGroupField.size(); i++) { //处理键的问题
                Field = (DaoCompareField) arrGroupField.get(i);
                if (Field.getSFieldType().equalsIgnoreCase("varchar")) {
                    fieldValue = rs.getString(Field.getSFieldCode());
                    if (fieldValue.length() == 0 &&
                        fieldValue.equalsIgnoreCase("null")) {
                        fieldValue = " ";
                    }
                } else if (Field.getSFieldType().equalsIgnoreCase("number")) {
                    fieldValue = rs.getDouble(Field.getSFieldCode()) + "";
                } else {
                    fieldValue = YssFun.formatDate(rs.getDate(Field.getSFieldCode()));
                }
                buf.append(fieldValue).append("-");
                if (Field.getIGroupTitleField() == 1) { //如果此列是标题列的话就取出来
                    bufTitle.append(fieldValue).append("-");
                }
            }
            if (buf.length() > 1) {
                buf.setLength(buf.length() - 1); //去掉最后一个小横线
            }
            if (bufTitle.length() > 1) {
                bufTitle.setLength(bufTitle.length() - 1); //去掉最后一个小横线
            }
        } catch (Exception ex) {
            throw new YssException("获取分组数据出错！", ex);
        }
        return buf.toString() + "\n\f" + bufTitle.toString(); //这里将分组信息与分组标题信息都传过去
    }

    /**
     * 取出一条记录的值，并放入到Hashtable中
     * key =FieldCode, value =值
     * @param rs ResultSet
     * @return ArrayList
     * @throws YssException
     */
    private Hashtable buildRowValue(ResultSet rs) throws YssException {
        Hashtable htValue = null;
        DaoCompareField field = null;
        Object objValue = null;
        try {
            htValue = new Hashtable();
            for (int i = 0; i < alCompField.size(); i++) {
                field = (DaoCompareField) alCompField.get(i);
                if (field.getSFieldType().equalsIgnoreCase("varchar")) {
                    objValue = rs.getString(field.getSFieldCode());
                } else if (field.getSFieldType().equalsIgnoreCase("number")) {
                    objValue = new Double(rs.getDouble(field.getSFieldCode()));
                } else {
                    objValue = rs.getDate(field.getSFieldCode());
                }
                htValue.put(field.getSFieldCode(), objValue);
            }
        } catch (Exception ex) {
            throw new YssException("获取一条记录的值出错", ex);
        }
        return htValue;
    }

    /**
     * 将数据连接成一行
     * @param htData 一行值
     * sMarkeSource 基准源
     * sDiffRow 差异的行
     * bDiff 差异标识
     * @return String
     * @throws YssException
     */
    private String buildOneRowStr(Hashtable htData, String sMarkeSource, String sDiffRow, boolean bDiff) throws YssException {
        try {
            return buildRowStr(htData, sMarkeSource) + "\t\tOne\t" + bDiff //注添加此标识为前台打勾时用,这个标识可以为"One",表示仅能选择一个,
                + (sDiffRow.length() > 0 ? "\f\f" + sDiffRow : "");
        } catch (Exception ex) {
            throw new YssException("连接数据出错!", ex);
        }
    }

    /**
     * 将差值比较出来，然后按字段顺序编写成字符串
     * htMarkDate 基准数据
     * htData 核对数据
     * sMarkSource 基准代码
     * bDiff 是否有差异
     * @return String
     */
    private String buildDiffRow(Hashtable htMarkData, Hashtable htData,
                                String sMarkeSource, boolean bDiff) throws
        YssException {
        try {
            if (htMarkData == null) {
                htMarkData = new Hashtable();
            }
            return buildRowStr(htMarkData, sMarkeSource).split("\b\b")[0] + "\b\b" +
                "核对结果:" + buildRowStr(htData, sMarkeSource).split("\b\b")[1] +
                "\tNo\tNo\t" +
                bDiff; //注添加此标识为前台打勾时用,这个标识可以为"No":表示不能选择
        } catch (Exception ex) {
            throw new YssException("组装核对字符串出错!", ex);
        }
    }

    /**
     * 根据字段顺序将数据编写成字符串,包括一行数据与一行组
     * 所有的到前台的一行数据都通过此方法处理
     * @param htData 为一行数据
     * @return String
     * @throws YssException
     */
    private String buildRowStr(Hashtable htData, String sMarkeSource) throws YssException {
        //遍历this.alCompField ,然后取出htData的字段写成一行数据
        StringBuffer bufValue = new StringBuffer();
        StringBuffer bufGroup = new StringBuffer();
        DaoCompareField field = null;
        Object objValue = null;
        try {
            for (int i = 0; i < alCompField.size(); i++) {
                objValue = null;
                field = (DaoCompareField) alCompField.get(i);
                objValue = htData.get(field.getSFieldCode());
                if (field.getSFieldType().equalsIgnoreCase("varchar")) {
                    bufValue.append(objValue);
                    if (field.getIGroupField() == 1) {
                        bufGroup.append(objValue).append("-");
                    }
                } else if (field.getSFieldType().equalsIgnoreCase("number")) {
                    bufValue.append(objValue);
                    if (field.getIGroupField() == 1) {
                        bufGroup.append(objValue).append("-");
                    }
                } else {
                    if (objValue != null && objValue.toString().length() > 0) {
                        bufValue.append(YssFun.formatDate(YssFun.toDate(objValue.
                            toString())));
                        if (field.getIGroupField() == 1) {
                            bufGroup.append(YssFun.formatDate(YssFun.toDate(objValue.
                                toString()))).append("-");
                        }
                    } else {
                        bufValue.append("");
                        if (field.getIGroupField() == 1) {
                            bufGroup.append("").append("-");
                        }
                    }
                }
                bufValue.append("\t");
            }
            if (bufGroup.length() > 1) {
                bufGroup.setLength(bufGroup.length() - 1); //去掉最后一个小横线
            }
            bufValue.append(sMarkeSource); //先将源放进去
            return bufGroup.toString() + "\b\b" + bufValue.toString();
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
    }

    /**
     * 获取动态条件及字段的值
     * @return String
     */
    private String buildDynamic() {
        StringBuffer bufDyn = new StringBuffer();
        String[] DynValue = null;
        DaoCompareField field = null;
        for (int i = 0; i < alCompField.size(); i++) {
            field = (DaoCompareField) alCompField.get(i);
            if (field.getIRogatoryField() == 1) {
                if (this.compQuery.getHtDynamic() != null &&
                    this.compQuery.getHtDynamic().get(field.getSFieldCode()) != null &&
                    ( (String) compQuery.getHtDynamic().get(field.getSFieldCode())).
                    length() > 0) {
                    DynValue = ( (String) compQuery.getHtDynamic().get(field.
                        getSFieldCode())).split(","); //前台的数据用逗号分隔
                    bufDyn.append(" and ");
                    bufDyn.append(DynamicStr(field.getSFieldCode(), field.getSFieldType(),
                                             DynValue));
                }
            }
        }
        return bufDyn.toString();
    }

    /**
     * 根据类型，值来处理动态ＳＱＬ
     * @param fieldCode String
     * @param sType String
     * @param arrValue String[]
     * @return String
     */
    private String DynamicStr(String fieldCode, String sType, String[] arrValue) {
        StringBuffer buf = new StringBuffer();
        String pec = ""; //百分号
        buf.append(" (");
        for (int i = 0; i < arrValue.length; i++) {
            if (!arrValue[i].endsWith("%")) { //去掉[]符号 by leeyu 20090525 号修改
                pec = "%";
            }
            if (sType.equalsIgnoreCase("varchar")) {
                //此处添加'*'号处理 以及','号处理 by leeyu 20090525 号 修改QDV4深圳2009年01月13日01_RA MS00192 by leeyu
                if (arrValue[i].split(",").length > 1) { //判断值是否有逗号
                    String sTmpValue = "";
                    for (int j = 0; j < arrValue[i].split(",").length; j++) {
                        sTmpValue = arrValue[i].split(",")[j];
                        if (!sTmpValue.endsWith("%")) {
                            pec = "%";
                        }
                        buf.append(fieldCode).append(" like '").append(sTmpValue).append(
                            pec).append("'");
                        buf.append(" or ");
                    }
                    sTmpValue = buf.toString();
                    if (sTmpValue.endsWith(" or ")) {
                        buf.setLength(buf.length() - 4);
                    }
                } else if (arrValue[i].equals("*")) { //如果值为星号
                    buf.append(fieldCode).append(" like '").append("%").append("'");
                } else {
                    buf.append(fieldCode).append(" like '").append(arrValue[i]).append(
                        pec).append("'");
                }
            } else if (sType.equalsIgnoreCase("date")) {
                buf.append(fieldCode).append(" = ").append(dbl.sqlDate(arrValue[i]));
            } else {
                buf.append(fieldCode).append(" = ").append(arrValue[i]);
            }
            buf.append(" or ");
        }
        if (buf.length() > 4) {
            buf.setLength(buf.length() - 4);
        }
        buf.append(") ");
        return buf.toString();
    }

}
