package com.yss.main.taoperation;

import java.sql.Connection;
import java.sql.ResultSet;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class TaPortClsBean
    extends BaseDataSettingBean implements IDataSetting {
    private String portClsCode = "";
    private String portClsName = "";
    private String desc = "";
    private String portCode = "";
    private String portName = "";

    private String oldPortClsCode = "";
    private TaPortClsBean filterType;
    private String sRecycled = null; //保存未解析前的字符串
    private String portClsRank="";//组合分级级别
    private String showItem="";//显示项 story 2727 add by zhouwei 20120619

    /**Start  20130512 added by liubo.Story #3759.需求北京-[银华基金]QDIIV4[中]20130326001*/
    
    private String sShareCategory = "";		//份额类别
    private String sConventionCode = "";		//约定收益率计算公式代码
    private String sConventionName = "";		//约定收益率计算公式名称
    private String sPeriodCode = "";		//年天数期间代码
    private String sPeriodName = "";		//年天数期间名称
    private String sDailyNavCode = "";		//每日单位净值计算公式代码
    private String sDailyNavName = "";		//每日单位净值计算公式名称
    private String sAfterDiscountNavCode = "";		//折算后单位净值计算公式代码
    private String sAfterDiscountNavName = "";		//折算后单位净值计算公式名称
    private String sAfterDiscountAmountCode = "";		//折算新增数量计算公式代码
    private String sAfterDiscountAmountName = "";		//折算新增数量计算公式名称
    
    /**End  20130512 added by liubo.Story #3759.需求北京-[银华基金]QDIIV4[中]20130326001*/
    
    
    /*add by  yeshenghong story 4151 20130724 ---start*/
    private String sPortClsSchema = "abLimited";//组合分级模式 story 4151 2013050724 add by  yeshenghong
    private String sPortClsCash = "";//现钞现汇 story 4151 2013050724 add by  yeshenghong
    private String sPortClsCurrency = "";//销售币种 story 4151 2013050724 add by  yeshenghong
    private String sPortClsNav = "";//分级资产净值 story 4151 2013050724 add by  yeshenghong
    private String sPortClsCurrencyName = "";//销售币种 story 4151 2013050724 add by  yeshenghong
    private String sPortClsNavName = "";
    public String getsPortClsSchema() {
		return sPortClsSchema;
	}

	public void setsPortClsSchema(String sPortClsSchema) {
		this.sPortClsSchema = sPortClsSchema;
	}

	public String getsPortClsCash() {
		return sPortClsCash;
	}

	public void setsPortClsCash(String sPortClsCash) {
		this.sPortClsCash = sPortClsCash;
	}

	public String getsPortClsCurrency() {
		return sPortClsCurrency;
	}

	public void setsPortClsCurrency(String sPortClsCurrency) {
		this.sPortClsCurrency = sPortClsCurrency;
	}

	public String getsPortClsNav() {
		return sPortClsNav;
	}

	public void setsPortClsNav(String sPortClsNav) {
		this.sPortClsNav = sPortClsNav;
	}

	public String getsPortClsCurrencyName() {
		return sPortClsCurrencyName;
	}

	public void setsPortClsCurrencyName(String sPortClsCurrencyName) {
		this.sPortClsCurrencyName = sPortClsCurrencyName;
	}

	public String getsPortClsNavName() {
		return sPortClsNavName;
	}

	public void setsPortClsNavName(String sPortClsNavName) {
		this.sPortClsNavName = sPortClsNavName;
	}

    /*add by  yeshenghong story 4151 20130724 ---end*/
    
    
    public String getShareCategory() {
		return sShareCategory;
	}

	public void setShareCategory(String sShareCategory) {
		this.sShareCategory = sShareCategory;
	}

	public String getConventionCode() {
		return sConventionCode;
	}

	public void setConventionCode(String sConventionCode) {
		this.sConventionCode = sConventionCode;
	}

	public String getConventionName() {
		return sConventionName;
	}

	public void setConventionName(String sConventionName) {
		this.sConventionName = sConventionName;
	}

	public String getPeriodCode() {
		return sPeriodCode;
	}

	public void setPeriodCode(String sPeriodCode) {
		this.sPeriodCode = sPeriodCode;
	}

	public String getPeriodName() {
		return sPeriodName;
	}

	public void setPeriodName(String sPeriodName) {
		this.sPeriodName = sPeriodName;
	}

	public String getDailyNavCode() {
		return sDailyNavCode;
	}

	public void setDailyNavCode(String sDailyNavCode) {
		this.sDailyNavCode = sDailyNavCode;
	}

	public String getDailyNavName() {
		return sDailyNavName;
	}

	public void setDailyNavName(String sDailyNavName) {
		this.sDailyNavName = sDailyNavName;
	}

	public String getAfterDiscountNavCode() {
		return sAfterDiscountNavCode;
	}

	public void setAfterDiscountNavCode(String sAfterDiscountNavCode) {
		this.sAfterDiscountNavCode = sAfterDiscountNavCode;
	}

	public String getAfterDiscountNavName() {
		return sAfterDiscountNavName;
	}

	public void setAfterDiscountNavName(String sAfterDiscountNavName) {
		this.sAfterDiscountNavName = sAfterDiscountNavName;
	}

	public String getAfterDiscountAmountCode() {
		return sAfterDiscountAmountCode;
	}

	public void setAfterDiscountAmountCode(String sAfterDiscountAmountCode) {
		this.sAfterDiscountAmountCode = sAfterDiscountAmountCode;
	}

	public String getAfterDiscountAmountName() {
		return sAfterDiscountAmountName;
	}

	public void setAfterDiscountAmountName(String sAfterDiscountAmountName) {
		this.sAfterDiscountAmountName = sAfterDiscountAmountName;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	private String parentCode = "";
    private String parentName  = "";
    
    private String sOffset = "";  //20120709 added by liubo.Story #2719.轧差计算份额总净值
    
    public String getOffset() {
		return sOffset;
	}

	public void setOffset(String sOffset) {
		this.sOffset = sOffset;
	}

	public String getShowItem() {
		return showItem;
	}

	public void setShowItem(String showItem) {
		this.showItem = showItem;
	}

	public String getPortClsName() {
        return portClsName;
    }

    public String getDesc() {
        return desc;
    }

    public String getPortClsCode() {
        return portClsCode;
    }

    public TaPortClsBean getFilterType() {
        return filterType;
    }

    public void setOldPortClsCode(String oldPortClsCode) {
        this.oldPortClsCode = oldPortClsCode;
    }

    public void setPortClsName(String portClsName) {
        this.portClsName = portClsName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPortClsCode(String portClsCode) {
        this.portClsCode = portClsCode;
    }

    public void setFilterType(TaPortClsBean filterType) {
        this.filterType = filterType;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public String getOldPortClsCode() {
        return oldPortClsCode;
    }

    public String getPortName() {
        return portName;
    }

    public String getPortCode() {
        return portCode;
    }

    public TaPortClsBean() {
    }

    public String getListViewData1() throws YssException {
        String strSql =
            "select  y.*," +
            //20130512 added by liubo.Story #3759.
            //从算法公式设置表中获取每日单位净值算法名称，折算后单位净值算法名称，折算后份额数量算法名称，约定收益率公式名称，年天数期间名称
            //=====================================
            " o.fdailynavname,p.fafterdiscountnavname,q.fafterdiscountamountname,r.fconventionname,s.fperiodname,cu.fcuryname, n.fclsnavname " +	
            //==============end=======================
            " from " +
            "(select * from " +
            pub.yssGetTableName("Tb_TA_PortCls") + " " +
            //以下修改为了前台回收站能够显示
            //" where FCheckState <> 2) x join" +
            ") x join" +
            " (select a.*,b.fportclsname as FParentName,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FPortName as FPortName  " +
            "  from " +
            pub.yssGetTableName("Tb_TA_PortCls") + " a" + //add by yeshenghong story 3264
            " left join " + pub.yssGetTableName("Tb_TA_PortCls") + " b on a.fparrentcode = b.fportclscode " + 
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +

            
            //------ modify by wangzuochun  2010.07.16  MS01449    组合代码相同而启用日期不同的组合时，新建买入证券据，进行库存统计后，现金库存会增倍 QDV4赢时胜(测试)2010年7月15日01_B 
            //----------------------------------------------------------------------------------------------------
            // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
            
 
            " left join (select FPortCode, FPortName, FPortCury from " +
            pub.yssGetTableName("Tb_Para_Portfolio") +
            " where  FCheckState = 1) d on a.FPortCode = d.FPortCode " +
            //20130512 added by liubo.Story #3759.
            //从算法公式设置表中获取每日单位净值算法名称，折算后单位净值算法名称，折算后份额数量算法名称，约定收益率公式名称，年天数期间名称
            //=====================================
            ")y on y.FPortClsCode=x.FPortClsCode " +
            " left join (select fcurycode, fcuryname from tb_base_currency where fcheckstate = 1) cu on y.fportclscurrency = cu.fcurycode " + //add by yeshenghong 20130724 story4151
            " left join (select FCIMCode, FCIMName as FClsNavName from tb_base_calcinsmetic " + 
            " where FCheckState = 1) n on y.fportclsNav = n.fcimcode " + //add by yeshenghong 20130724 story4151
            " left join (select FCIMCode,FCIMName as FDailyNavName from tb_base_calcinsmetic where FCheckState = 1) o " + 
            " on y.fdailynav = o.fcimcode " + 
            " left join (select FCIMCode,FCIMName as FAfterDiscountNavName from tb_base_calcinsmetic " +
            " where FCheckState = 1) p  " + 
            " on y.FAfterDiscountNav = p.fcimcode " + 
            " left join (select FCIMCode,FCIMName as FAfterDiscountAmountName from tb_base_calcinsmetic " +
            " where FCheckState = 1) q  " + 
            " on y.FAfterDiscountAmount = q.fcimcode" +
            " left join (select FFormulaCode,FFormulaName as FConventionName from " + pub.yssGetTableName("Tb_Para_Performula") +
            " where FCheckState = 1) r " +
            " on y.FConvention = r.fformulacode" +
            " left join (select FPeriodCode,FPeriodName from " + pub.yssGetTableName("Tb_Para_Period") + 
            " where FCheckState = 1) s " +
            " on y.FPeriod = s.fperiodname " +
            //================end=====================
          
            // end by lidaolong
            //-------------------------------------------- MS01449 -------------------------------------------//
            
            buildFilterSql() +

            " order by y.FPortClsCode";
        return builderListViewData(strSql);

    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.setPortClsAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_TA_PORTCLS_RANK+","+YssCons.YSS_TA_PORTCLS_SCHEMA+","//add by yeshenghong 20130724 story4151
            		+YssCons.YSS_TA_PORTCLS_CASH+","+YssCons.YSS_TA_PORTCLS_SHOWITEM);//ETF add by zhouwei 20120620 组合分级显示项

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+"\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取文件头设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void setPortClsAttr(ResultSet rs) throws YssException {
        try {
            //   this.cusCfgCode = rs.getString("FcusCfgCode");
            this.portClsCode = rs.getString("FPortClsCode");
            this.portClsName = rs.getString("FPortClsName");
            this.portCode = rs.getString("FPortCode");
            this.portName = rs.getString("FPortName");
            this.desc = rs.getString("Fdesc");
            this.portClsRank=rs.getString("fportClsRank");
			//---add by zhouwei STORY #2727 2012.06.20 start---//
            this.showItem=rs.getString("FShowItem");
            this.parentCode = rs.getString("FParrentcode");
            this.parentName = rs.getString("FParentName");
            this.sOffset = rs.getString("FOffset");
			//---add by zhouwei STORY #2727 2012.06.20 end---//

            /**Start  20130512 added by liubo.Story #3759.需求北京-[银华基金]QDIIV4[中]20130326001*/
            
            this.sShareCategory = rs.getString("FShareCategory");						//份额类别
            this.sConventionCode = rs.getString("FConvention");							//约定收益率计算公式代码		
            this.sConventionName = rs.getString("FConventionName");						//约定收益率计算公式名称
            this.sPeriodCode = rs.getString("FPeriod");									//年天数期间代码
            this.sPeriodName = rs.getString("FPeriodName");								//年天数期间名称
            this.sDailyNavCode = rs.getString("FDailyNav");								//每日单位净值计算公式代码
            this.sDailyNavName = rs.getString("FDailyNavName");							//每日单位净值计算公式名称
            this.sAfterDiscountNavCode = rs.getString("FAfterDiscountNav");				//折算后单位净值计算公式代码
            this.sAfterDiscountNavName = rs.getString("FAfterDiscountNavName");			//折算后单位净值计算公式名称
            this.sAfterDiscountAmountCode = rs.getString("FAfterDiscountAmount");		//折算新增数量计算公式代码
            this.sAfterDiscountAmountName = rs.getString("FAfterDiscountAmountName");	//折算新增数量计算公式名称

            /**End  20130512 added by liubo.Story #3759.需求北京-[银华基金]QDIIV4[中]20130326001*/
            
            /*add by yeshenghong 20130724  story4151  ---start*/
            this.sPortClsSchema = rs.getString("FPORTCLSSCHEMA");
            this.sPortClsCash = rs.getString("FPORTCLSCASH");
            this.sPortClsCurrency = rs.getString("FPORTCLSCURRENCY");
            this.sPortClsCurrencyName = rs.getString("FCuryName");
            this.sPortClsNav = rs.getString("FPORTCLSNAV");
            this.sPortClsNavName = rs.getString("FClsNavName");
            /*add by yeshenghong 20130724  story4151  ---end*/
            super.setRecLog(rs);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.portClsCode.length() != 0) {
                sResult = sResult + " and y.FPortClsCode like '" +
                    filterType.portClsCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.portClsName.length() != 0) {
                sResult = sResult + " and y.FPortClsName like '" +
                    filterType.portClsName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and y.Fdesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.portCode != null &&
                this.filterType.portCode.length() != 0) {
                sResult += " and y.FPortCode like'" +
                    filterType.portCode.replaceAll("'", "''") + "%'";
            }

            /**Start  20130512 added by liubo.Story #3759.需求北京-[银华基金]QDIIV4[中]20130326001*/
            
            //约定收益率计算公式代码
            if(this.filterType.sConventionCode != null && this.filterType.sConventionCode.trim().length() != 0)
            {
                sResult += " and y.FConvention like'" +
                filterType.sConventionCode.replaceAll("'", "''") + "%'";
            	
            }
            //份额类别
            if(this.filterType.sShareCategory != null && this.filterType.sShareCategory.trim().length() != 0 
            		&& !this.filterType.sShareCategory.equals("4"))
            {
            	sResult += " and y.FShareCategory like'" +
                filterType.sShareCategory.replaceAll("'", "''") + "%'";
            }
            //年天数期间代码
            if(this.filterType.sPeriodCode != null && this.filterType.sPeriodCode.trim().length() != 0)
            {
            	sResult += " and y.FPeriod like'" +
                filterType.sPeriodCode.replaceAll("'", "''") + "%'";
            }
            //每日单位净值计算公式代码
            if(this.filterType.sDailyNavCode != null && this.filterType.sDailyNavCode.trim().length() != 0)
            {
            	sResult += " and y.FDailyNav like'" +
                filterType.sDailyNavCode.replaceAll("'", "''") + "%'";
            }
            //折算后单位净值计算公式代码
            if(this.filterType.sAfterDiscountNavCode != null && this.filterType.sAfterDiscountNavCode.trim().length() != 0)
            {
            	sResult += " and y.FAfterDiscountNav like'" +
                filterType.sAfterDiscountNavCode.replaceAll("'", "''") + "%'";
            }
            //折算新增数量计算公式代码
            if(this.filterType.sAfterDiscountAmountCode != null && this.filterType.sAfterDiscountAmountCode.trim().length() != 0)
            {
            	sResult += " and y.FAfterDiscountAmount like'" +
                filterType.sAfterDiscountAmountCode.replaceAll("'", "''") + "%'";
            }

            /**End  20130512 added by liubo.Story #3759.需求北京-[银华基金]QDIIV4[中]20130326001*/
            
        }
        return sResult;
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = "组合分级代码\t组合分级名称\t组合分级描述";
            //conn =dbl.loadConnection();
            sqlStr = "select a.*,' ' as FParentName,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FPortName as FPortName " +
            //20130512 added by liubo.Story #3759.
            //从算法公式设置表中获取每日单位净值算法名称，折算后单位净值算法名称，折算后份额数量算法名称，约定收益率公式名称，年天数期间名称
            //=====================================
            ", o.fdailynavname,p.fafterdiscountnavname,q.fafterdiscountamountname,r.fconventionname,s.fperiodname,cu.FCURYNAME,n.FClsNavName " +	
            //==============end=======================
            	" from " +
                pub.yssGetTableName("Tb_TA_PortCls") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join (select FPortCode,FPortName from " +
                pub.yssGetTableName("tb_para_portfolio") +
                ") d on d.FPortCode=a.FPortCode " +
                //20130512 added by liubo.Story #3759.
                //从算法公式设置表中获取每日单位净值算法名称，折算后单位净值算法名称，折算后份额数量算法名称，约定收益率公式名称，年天数期间名称
                //=====================================
                " left join (select FCIMCode, FCIMName as FClsNavName from tb_base_calcinsmetic " + 
                " where FCheckState = 1) n on a.fportclsNav = n.fcimcode " + //add by yeshenghong 20130724 story4151
                " left join (select FCIMCode,FCIMName as FDailyNavName from tb_base_calcinsmetic where FCheckState = 1) o " + 
                " on a.fdailynav = o.fcimcode " + 
                " left join (select FCIMCode,FCIMName as FAfterDiscountNavName from tb_base_calcinsmetic " +
                " where FCheckState = 1) p  " + 
                " on a.FAfterDiscountNav = p.fcimcode " + 
                " left join (select FCIMCode,FCIMName as FAfterDiscountAmountName from tb_base_calcinsmetic " +
                " where FCheckState = 1) q  " + 
                " on a.FAfterDiscountAmount = q.fcimcode" +
                " left join (select FFormulaCode,FFormulaName as FConventionName from " + pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState = 1) r " +
                " on a.FConvention = r.fformulacode" +
                " left join (select FPeriodCode,FPeriodName from " + pub.yssGetTableName("Tb_Para_Period") + 
                " where FCheckState = 1) s " +
                " on a.FPeriod = s.fperiodname " +
                //================end=====================
                " left join (select fcurycode, FCURYNAME from tb_base_currency where fcheckstate =1) cu " +
                " on a.fportclscurrency = cu.fcurycode " +
                " where a.FCheckState =1 order by a.FPortClsCode";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FPortClsCode")).append("\t");
                bufShow.append(rs.getString("FPortClsName")).append("\t");
                bufShow.append(rs.getString("FDesc")).append(YssCons.
                    YSS_LINESPLITMARK);
                this.setPortClsAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取TA组合分级设置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            //dbl.endTransFinal(conn,bTrans);
        }
    }

    /**
     * getListViewData3
     * add by yeshenghong story3264
     * @return String
     */
    public String getListViewData3() throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = "组合分级代码\t组合分级名称\t组合分级描述";
            //conn =dbl.loadConnection();
            sqlStr = "select a.*,' ' as FParentName, b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FPortName as FPortName " +
	            //20130512 added by liubo.Story #3759.
	            //从算法公式设置表中获取每日单位净值算法名称，折算后单位净值算法名称，折算后份额数量算法名称，约定收益率公式名称，年天数期间名称
	            //=====================================
	            ", o.fdailynavname,p.fafterdiscountnavname,q.fafterdiscountamountname,r.fconventionname,s.fperiodname,cu.fcuryname, n.fclsnavname " +	
	            //==============end=======================
            	" from " +
                pub.yssGetTableName("Tb_TA_PortCls") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join (select FPortCode,FPortName from " +
                pub.yssGetTableName("tb_para_portfolio") +
                ") d on d.FPortCode=a.FPortCode " +
                //20130512 added by liubo.Story #3759.
                //从算法公式设置表中获取每日单位净值算法名称，折算后单位净值算法名称，折算后份额数量算法名称，约定收益率公式名称，年天数期间名称
                //=====================================
                " left join (select fcurycode, fcuryname from tb_base_currency where fcheckstate = 1) cu on a.fportclscurrency = cu.fcurycode " + //add by yeshenghong 20130724 story4151
                " left join (select FCIMCode, FCIMName as FClsNavName from tb_base_calcinsmetic " + 
                " where FCheckState = 1) n on a.fportclsNav = n.fcimcode " + //add by yeshenghong 20130724 story4151
                " left join (select FCIMCode,FCIMName as FDailyNavName from tb_base_calcinsmetic where FCheckState = 1) o " + 
                " on a.fdailynav = o.fcimcode " + 
                " left join (select FCIMCode,FCIMName as FAfterDiscountNavName from tb_base_calcinsmetic " +
                " where FCheckState = 1) p  " + 
                " on a.FAfterDiscountNav = p.fcimcode " + 
                " left join (select FCIMCode,FCIMName as FAfterDiscountAmountName from tb_base_calcinsmetic " +
                " where FCheckState = 1) q  " + 
                " on a.FAfterDiscountAmount = q.fcimcode" +
                " left join (select FFormulaCode,FFormulaName as FConventionName from " + pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState = 1) r " +
                " on a.FConvention = r.fformulacode" +
                " left join (select FPeriodCode,FPeriodName from " + pub.yssGetTableName("Tb_Para_Period") + 
                " where FCheckState = 1) s " +
                " on a.FPeriod = s.fperiodname " +
                //================end=====================
                " where a.FCheckState =1 order by a.FPortClsCode";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FPortClsCode")).append("\t");
                bufShow.append(rs.getString("FPortClsName")).append("\t");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                this.setPortClsAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取TA组合分级设置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            //dbl.endTransFinal(conn,bTrans);
        }
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        /* String strSql=
            "select y.* from " +
                "(select FVchTplCode,FEntityCode,FCheckState from " +
                pub.yssGetTableName("Tb_Vch_EntityResume") + " " +
         " where FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join" +
         " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName ," +
                "  e.FDesc as FResumeFieldValue,d.FDictName as FResumeDictValue, f.FVocName as FValueTypeValue from " +
                pub.yssGetTableName("Tb_Vch_EntityResume") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join(select distinct FDictCode,FDictName from "+pub.yssGetTableName("Tb_Vch_Dict")+
                " ) d on d.FDictCode=a.FResumeDict"+
                " left join(select FAliasName,FDesc from "+pub.yssGetTableName("Tb_Vch_DsTabField")+
                " where FVchDsCode="+dbl.sqlString(this.dataSource)+") e on e.FAliasName=a.FResumeField "+
                " left join Tb_Fun_Vocabulary f on a.FValueType = f.FVocCode and f.FVocTypeCode = " +
                 dbl.sqlString(YssCons.YSS_VALUETYPE) +
                buildFilterSql() +
         ")y on y.FVchTplCode=x.FVchTplCode and y.FEntityCode=x.FEntityCode" +
                " order by y.FVchTplCode,y.FEntityCode";
          return builderListViewData(strSql);*/
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_TA_PortCls"),
                               "FPortClsCode", this.portClsCode,
                               this.oldPortClsCode);
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection con = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        try {
        	if(!getPortCLsCodeByCon("").equals("")){
        		throw new YssException("组合"+this.portCode+" 已存在级别为"+this.portClsRank+"的分级");
        	}
        	/**start add by huangqirong 2013-7-16 Bug #8597 判断组合轧差个数  */
        	
        	/**Start 20130809 modified by liubo.Bug #9027.QDV4赢时胜(上海开发)2013年8月9日01_B
        	 * 在这个BUG之前，没有判断sOffset的条件，这种情况下会造成：当系统中存在轧差的分级组合，然后新建一个非轧差的分级
        	 * 系统仍然提示只能有一个分级为轧差的问题。因此这里需要加上sOffset的判断
        	 * 另外getTATrack方法增加一个参数，用于区分是在新建时调用还是在修改时调用*/
        	if(this.sOffset.equals("1")&&this.getTATrack(this.portCode,0)){
        		throw new YssException("组合"+this.portCode+"下已经有一个分级组合轧差且只能有一个为轧差，请重新设置！");
        	}
        	/**End 20130809 modified by liubo.Bug #9027.QDV4赢时胜(上海开发)2013年8月9日01_B*/
        	
			/**end add by huangqirong 2013-7-16 Bug #8597 判断组合轧差个数*/
            strSql = "insert into " + pub.yssGetTableName("Tb_TA_PortCls") +
                "(FPortClsCode,FPortClsName," +
                " FDesc,FPortCode,fparrentcode,fportclsRank,FShowItem," +//edit by zhouwei STORY #2727 2012.06.20
                " FOffset," +	//20120709 added by liubo.Story #2719.轧差计算份额总净值
                " FShareCategory,FConvention,FPeriod,FDailyNav,FAfterDiscountNav,FAfterDiscountAmount," +
                " FPORTCLSSCHEMA, FPORTCLSCASH, FPORTCLSCURRENCY, FPORTCLSNAV, " +//add by yeshenhgong 20130724 story4151
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" +
                dbl.sqlString(this.portClsCode) + "," +
                dbl.sqlString(this.portClsName) + "," +
                dbl.sqlString(this.desc) + "," +
                dbl.sqlString(this.portCode) + "," +
                dbl.sqlString(this.parentCode) + "," +
                dbl.sqlString(this.portClsRank)+","+
                dbl.sqlString(this.showItem)+","+//story 2727 add by zhouwei 20120619 显示项
                
                /**Start  20130512 added by liubo.Story #3759.需求北京-[银华基金]QDIIV4[中]20130326001*/
                
                dbl.sqlString(this.sOffset) + "," + 					//轧差计算份额总净值
                dbl.sqlString(this.sShareCategory) + "," + 				//份额类别
                dbl.sqlString(this.sConventionCode) + "," + 			//约定收益率计算公式代码
                dbl.sqlString(this.sPeriodCode) + "," + 				//年天数期间代码
                dbl.sqlString(this.sDailyNavCode) + "," + 				//每日单位净值计算公式代码
                dbl.sqlString(this.sAfterDiscountNavCode) + "," + 		//折算后单位净值计算公式代码
                dbl.sqlString(this.sAfterDiscountAmountCode) + "," + 	//折算新增数量计算公式代码

                /**End  20130512 added by liubo.Story #3759.需求北京-[银华基金]QDIIV4[中]20130326001*/
                //add by yeshenghong 20130724 story4151 ---start
                dbl.sqlString(this.sPortClsSchema) + "," +
                dbl.sqlString(this.sPortClsCash) + "," +
                dbl.sqlString(this.sPortClsCurrency) + "," +
                dbl.sqlString(this.sPortClsNav) + "," +
                //add by yeshenghong 20130724 story4151 ---end
                (pub.getSysCheckState() ? 0 : 1) + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                dbl.sqlString( (pub.getSysCheckState() ? " " : this.creatorCode)) +
                ")";
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增TA组合分级设置信息出错!",e);
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";
    }
    
    /**
     * 判断组合下某分级是否可以为轧差
	 * add by huangqirong 2013-7-16 Bug #8597 判断组合轧差个数
	 * 20130809 modified by liubo.Bug #9027.
	 * 增加一个iFlag的参数，为0时表示是在新增时被调用，为1时表示是在修改时调用
	 * 修改时调用，增加一个逻辑，查询轧差的分级组合时，排除当前修改的数据本身。
	 * 避免造成修改一个轧差的分级时，始终提示只能有一个轧差分级的问题
     * */
    private boolean getTATrack(String portCode, int iFlag){
    	ResultSet rs = null;
    	int count = 0 ;
    	boolean result = false;
    	String sql = " select count(*) as FCount from " + pub.yssGetTableName("tb_ta_portcls") + 
    					" tpc where tpc.fportcode  = " + dbl.sqlString(portCode) +
    					" and Foffset = 1  and fportclsschema = " + dbl.sqlString(sPortClsSchema);
    					//add by yeshenghong story4151  20130816
    	if (iFlag == 1)
    	{
    		sql += " and tpc.FPortClsCode <> " + dbl.sqlString(this.oldPortClsCode);
    	}
    	try {
    		rs = dbl.openResultSet(sql);
    		if(rs.next()){
    			count = rs.getInt("FCount");
    		}
    		if(count > 0)
    			result = true;
    		else
    			result = false;
		} catch (Exception e) {
			System.out.println("获取组合下某分级是否可以为轧差报错：" + e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}    	
    	return result;
    }
    
    /**story 2254 add by zhouwei 20120226
     * 获取已存在组合分级级别的分级编号
     * @return
     * @throws YssException
     */
    private String getPortCLsCodeByCon(String conSql) throws YssException{
    	ResultSet rs=null;
    	String str="";
    	String reportClsCode="";
    	try{
    		str="select * from "+ pub.yssGetTableName("Tb_TA_PortCls")+
            		" where FPortCode="+dbl.sqlString(this.portCode)+" and fportClsRank="+dbl.sqlString(this.portClsRank)
            		+conSql;       	
    		rs=dbl.openResultSet(str);
    		if(rs.next()){
    			reportClsCode=rs.getString("FPortClsCode");
    		}
    	}catch (Exception ex) {
			throw new YssException("根据组合分级级别信息出错！", ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return reportClsCode;
    }
    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	String conSql=" and FPortClsCode <>"+dbl.sqlString(this.oldPortClsCode);
        	if(!getPortCLsCodeByCon(conSql).equals("")){
        		throw new YssException("组合"+this.portCode+" 已存在级别为"+this.portClsRank+"的分级");
        	}
        	/**start add by huangqirong 2013-7-16 Bug #8597 判断组合轧差个数  */
        	if(this.sOffset.equals("1")&&this.getTATrack(this.portCode,1)){
        		throw new YssException("组合"+this.portCode+"下已经有一个分级组合轧差且只能有一个为轧差，请重新设置！");
        	}
			/**end add by huangqirong 2013-7-16 Bug #8597 判断组合轧差个数*/
            strSql = "update " + pub.yssGetTableName("Tb_TA_PortCls") +
                " set FPortClsCode = " +
                dbl.sqlString(this.portClsCode) + ", FPortClsName = " +
                dbl.sqlString(this.portClsName) +", fportclsrank="+
				//edit by zhouwei 2012.06.20 STORY 2727
                dbl.sqlString(this.portClsRank)+",FShowItem="+dbl.sqlString(this.showItem)
                + ", FOffset = " + dbl.sqlString(this.sOffset) 	//20120709 added by liubo.Story #2719.轧差计算份额总净值
                
                //20120709 added by liubo.Story #2719.轧差计算份额总净值
                + ", FShareCategory = " + dbl.sqlString(this.sShareCategory) 	
                //20120709 added by liubo.Story #2719.轧差计算份额总净值
                + ", FConvention = " + dbl.sqlString(this.sConventionCode) 	
                //20120709 added by liubo.Story #2719.轧差计算份额总净值
                + ", FPeriod = " + dbl.sqlString(this.sPeriodCode) 	
                //20120709 added by liubo.Story #2719.轧差计算份额总净值
                + ", FDailyNav = " + dbl.sqlString(this.sDailyNavCode) 	
                //20120709 added by liubo.Story #2719.轧差计算份额总净值
                + ", FAfterDiscountNav = " + dbl.sqlString(this.sAfterDiscountNavCode) 
                //20120709 added by liubo.Story #2719.轧差计算份额总净值
                + ", FAfterDiscountAmount = " + dbl.sqlString(this.sAfterDiscountAmountCode) 
                //add by yeshenghong 20130724 story4151 ---start
                + ", FPORTCLSSCHEMA = " + dbl.sqlString(this.sPortClsSchema) 
                + ", FPORTCLSCASH = " + dbl.sqlString(this.sPortClsCash) 
                + ", FPORTCLSCURRENCY = " +  dbl.sqlString(this.sPortClsCurrency) 
                + ", FPORTCLSNAV = " + dbl.sqlString(this.sPortClsNav) 
                //add by yeshenghong 20130724 story4151 ---end
                + ", FDesc = " +
                dbl.sqlString(this.desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ",FPortCode=" + dbl.sqlString(this.portCode) +
                ",FParrentcode=" + dbl.sqlString(this.parentCode) +
                " where FPortClsCode = " +
                dbl.sqlString(this.oldPortClsCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新TA组合分级设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            strSql = "update " + pub.yssGetTableName("Tb_TA_PortCls") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FPortClsCode = " + dbl.sqlString(this.portClsCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除TA组合分级设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        String[] arrData = null;
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
			/**shashijie 2012-7-2 STORY 2475 */
            if (this.sRecycled == null || !this.sRecycled.equalsIgnoreCase("")) {
			/**end*/
                arrData = this.sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_TA_PortCls") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FPortClsCode = " + dbl.sqlString(this.portClsCode);
                    dbl.executeSql(strSql);
                    conn.commit();
                    bTrans = false;
                    conn.setAutoCommit(true);

                }
            }
        } catch (Exception e) {
            throw new YssException("TA组合分级信息还原出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {

        return "";
    }

    /**
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() throws YssException {
        String sqlStr = "";
        ResultSet rs = null;
        try {
        	//--- edit by songjie 2013.06.27 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 start---//
            sqlStr = " select FPORTCODE,FPortClsRank from " +//添加 FPortClsRank
            //--- edit by songjie 2013.06.27 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 end---//
                pub.yssGetTableName("TB_TA_PORTCLS") +
                " where FPORTCLSCODE=" + dbl.sqlString(this.portClsCode);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                this.portCode = rs.getString("FPORTCODE");
                //--- add by songjie 2013.06.27 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 start---//
                this.portClsRank = rs.getString("FPortClsRank");//TA分级级别
                //--- add by songjie 2013.06.27 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 end---//
            }
        } catch (Exception e) {
            throw new YssException("获取组合代码信息出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }

    //--- add by songjie 2013.06.27 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 start---//
    //添加参数 portClsRank 的get、set方法
    public String getPortClsRank() {
		return portClsRank;
	}

	public void setPortClsRank(String portClsRank) {
		this.portClsRank = portClsRank;
	}
	//--- add by songjie 2013.06.27 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 end---//

	/**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            //this.cusCfgCode=reqAry[0];
            this.portClsCode = reqAry[0];
            this.portClsName = reqAry[1];

            this.desc = reqAry[2];
            this.checkStateId = YssFun.toInt(reqAry[3]);
            this.oldPortClsCode = reqAry[4];
            this.portCode = reqAry[5];
            this.portClsRank=reqAry[6];
            this.showItem=reqAry[7];//add by zhouwei 2012.06.20 STORY #2727
            this.sOffset=reqAry[8];	//20120709 added by liubo.Story #2719.轧差计算份额总净值
            this.parentCode = reqAry[9];

            /**Start  20130512 added by liubo.Story #3759.需求北京-[银华基金]QDIIV4[中]20130326001*/
            
            this.sShareCategory = reqAry[10];				//份额类别
            this.sConventionCode = reqAry[11];				//约定收益率计算公式代码
            this.sPeriodCode = reqAry[12];					//年天数期间代码
            this.sDailyNavCode = reqAry[13];				//每日单位净值计算公式代码
            this.sAfterDiscountNavCode = reqAry[14];		//折算后单位净值计算公式代码
            this.sAfterDiscountAmountCode = reqAry[15];		//折算新增数量计算公式代码

            /**End  20130512 added by liubo.Story #3759.需求北京-[银华基金]QDIIV4[中]20130326001*/
            
            //add by yeshenghong 20130724 story4151 ---start
            this.sPortClsSchema = reqAry[16];	
            this.sPortClsCash = reqAry[17];	
            this.sPortClsCurrency = reqAry[18];	
            this.sPortClsNav = reqAry[19];	
            //add by yeshenghong 20130724 story4151	---end
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TaPortClsBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析TA组合分级设置出错");
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.portClsCode).append("\t");
        buf.append(this.portClsName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.portClsRank).append("\t");
        buf.append(this.showItem).append("\t");//story 2727 add by zhouwei 20120619 显示项
        buf.append(this.sOffset).append("\t");	//20120709 added by liubo.Story #2719.轧差计算份额总净值
        buf.append(this.parentCode).append("\t");
        buf.append(this.parentName).append("\t");

        /**Start  20130512 added by liubo.Story #3759.需求北京-[银华基金]QDIIV4[中]20130326001*/
        
        buf.append(this.sShareCategory).append("\t");    			//份额类别
        buf.append(this.sConventionCode).append("\t");    			//约定收益率计算公式代码
        buf.append(this.sConventionName).append("\t");    			//约定收益率计算公式名称
        buf.append(this.sPeriodCode).append("\t");    				//年天数期间代码
        buf.append(this.sPeriodName).append("\t");    				//年天数期间名称
        buf.append(this.sDailyNavCode).append("\t");    			//每日单位净值计算公式代码
        buf.append(this.sDailyNavName).append("\t");    			//每日单位净值计算公式名称
        buf.append(this.sAfterDiscountNavCode).append("\t");    	//折算后单位净值计算公式代码
        buf.append(this.sAfterDiscountNavName).append("\t");    	//折算后单位净值计算公式名称
        buf.append(this.sAfterDiscountAmountCode).append("\t");    	//折算新增数量计算公式代码
        buf.append(this.sAfterDiscountAmountName).append("\t");    	//折算新增数量计算公式名称

        /**End  20130512 added by liubo.Story #3759.需求北京-[银华基金]QDIIV4[中]20130326001*/
        //add by yeshenghong 20130724  story4151 ---start
        buf.append(this.sPortClsSchema).append("\t"); 	
        buf.append(this.sPortClsCash).append("\t"); 	
        buf.append(this.sPortClsCurrency).append("\t"); 	
        buf.append(this.sPortClsNav).append("\t"); 
        buf.append(this.sPortClsCurrencyName).append("\t"); 
        buf.append(this.sPortClsNavName).append("\t"); 
        //add by yeshenghong 20130724  story4151 ---end
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        String[] arrData = null;
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (sRecycled != null || ! ("").equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData.length == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_TA_PortCls") +
                        " where FPortClsCode = " + dbl.sqlString(this.portClsCode);
                    dbl.executeSql(strSql);
                    conn.commit();
                    bTrans = false;
                    conn.setAutoCommit(true);

                }
            }
        } catch (NullPointerException ex) {
            throw new YssException(ex.getMessage()); 
        } catch (Exception e) {
            throw new YssException("审核TA销售网点设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData1() throws YssException {
        return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }

}
