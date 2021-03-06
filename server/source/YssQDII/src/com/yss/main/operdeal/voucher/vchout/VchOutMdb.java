package com.yss.main.operdeal.voucher.vchout;

import java.sql.*;

import com.yss.main.voucher.*;
import com.yss.util.*;

public class VchOutMdb
    extends BaseVchOut {
    private String[] vchTplCode;

    public VchOutMdb() {
    }

    public void getTplCode() {
        vchTplCode = vchTplCodes.split(",");
    }

    public String doInsert() throws YssException {
        String reStr = "";
        try {
            runStatus.appendRunDesc("VchRun", "开始导出凭证的配置信息... ...\r\n");
            runStatus.appendRunDesc("VchRun", "  开始导出凭证模版的配置信息... ...    导出成功！\r\n");
            this.getTplCode();
            reStr = this.getOtherMdbData(vchTplCodes);
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
        return reStr;
    }

    private String setSuperRel() {
        StringBuffer buf = new StringBuffer();
        buf.append("1").append("\t");
        buf.append("").append("\t");
        buf.append(" ").append("\t");
        buf.append(" ").append("\t");
        buf.append(" ").append("\t");
        buf.append(" ").append("\t");
        buf.append(" ").append("\t");
        buf.append(" ");
        return buf.toString();
    }

    public String getOtherMdbData(String vchTplCodes) throws YssException {
        VchTplBean tpl = null;
        VchEntityBean entity = null;
        VchEntityResumeBean resume = null;
        VchEntitySubjectBean subject = null;
        VchDataSourceBean source = null;
        VchDsTabFieldBean field = null;
        VchAttrBean attr = null;
        VchPortSetLinkBean link = null;
        VchEntityMABean ma = null;
        VchAssistantBean assistant = null;
        StringBuffer tmpBuf = new StringBuffer();
        StringBuffer tmpBuf1 = new StringBuffer();
        ResultSet rs = null;
        String strSql = "", strSql1 = "";
        String dsCode = "'all',", attrCode = "'all',", linkCode = "'all',",
            entityCode = "'all',";
        StringBuffer buf = new StringBuffer();
        try {
            runStatus.appendRunDesc("VchRun", "    开始导出凭证模板设置... ...");
            //导出凭证模版Tb_XXX_Vch_VchTpl
            strSql = " select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                " where FVchTplCode in (" +
                (isInData ? operSql.sqlCodes(getVchTpl()) :
                 operSql.sqlCodes(this.vchTypes)) + ")" +
                " and FCheckState=1";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                tpl = new VchTplBean();
                tpl.setYssPub(pub);
                tpl.setVchTplCode(rs.getString("FVchTplCode"));
                tpl.setVchTplName(rs.getString("FVchTplName"));
                tpl.setSrcCuryCode(rs.getString("FSrcCury"));
                tpl.setCuryCode(rs.getString("FCuryCode"));
                tpl.setMode(rs.getString("FMode"));
                tpl.setDateFieldCode(rs.getString("FDateField"));
                tpl.setDsCode(rs.getString("FDsCode"));
                tpl.setLinkCode(rs.getString("FLinkCode"));
                tpl.setFields(rs.getString("FFields"));
                tpl.setVchTWay(rs.getString("FVchTWay"));
                tpl.setAttrCode(rs.getString("FAttrCode"));
                tpl.setDesc(rs.getString("FDesc"));
                buf.append(tpl.buildRowStr());
                dsCode += rs.getString("FDsCode") + ",";
                linkCode += rs.getString("FLinkCode") + ",";
                attrCode += rs.getString("FAttrCode") + ",";
                buf.append("\r\f"); //注："\r\r"表与表；\r\f记录与记录
            }
            if (dsCode.length() > 0) {
                dsCode = dsCode.substring(0,
                                          dsCode.length() - 1);
            }
            if (linkCode.length() > 0) {
                linkCode = linkCode.substring(0,
                                              linkCode.length() - 1);
            }
            if (attrCode.length() > 0) {
                attrCode = attrCode.substring(0,
                                              attrCode.length() - 1);

            }
            runStatus.appendRunDesc("VchRun", "    导出成功！\r\n");
            buf.append("\r\r");
            runStatus.appendRunDesc("VchRun", "    开始导出凭证分录设置... ...");
            //导出凭证模版Tb_XXX_Vch_Entity
            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_Entity") +
                " where FVchTplCode in(" + operSql.sqlCodes(this.vchTplCodes) +
                ")" +
                " and FCheckState=1";
            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                entity = new VchEntityBean();
                entity.setYssPub(pub);
                entity.setVchTplCode(rs.getString("FVchTplCode"));
                entity.setEntityCode(rs.getString("FEntityCode"));
                entity.setEntityName(rs.getString("FEntityName"));
                entity.setDCalcWay(rs.getString("FCalcWay"));
                entity.setSetMoneyDesc(rs.getString("FSetMoneyDesc"));
                entity.setEnCuryCode(rs.getString("FEncuryCode"));
                entity.setDCWay(rs.getString("FDCWay"));
                entity.setPriceFieldCode(rs.getString("FPriceField"));
                entity.setResumeDesc(rs.getString("FResumeDesc"));
                entity.setSubjectDesc(rs.getString("FSubjectCode"));
                entity.setMoneyDesc(rs.getString("FMoneyDesc"));
                entity.setAmountDesc(rs.getString("FAmountDesc"));
                entity.setCondDesc(rs.getString("FCondDesc"));
                entity.setAssDesc(rs.getString("FAssistantDesc"));
                entity.setEntityInd(rs.getString("FEntityInd"));
                entity.setDesc(rs.getString("FDesc"));
                buf.append(entity.buildRowStr());
                entityCode += rs.getString("FEntityCode") + ",";
                buf.append("\r\f");
            }
            if (entityCode.length() > 0) {
                entityCode = entityCode.substring(0,
                                                  entityCode.length() - 1);

            }
            runStatus.appendRunDesc("VchRun", "    导出成功！\r\n");
            buf.append("\r\r");
            runStatus.appendRunDesc("VchRun", "    开始导出凭证分录摘要设置... ...");
            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_EntityResume") +
                " where FVchTplCode in(" + operSql.sqlCodes(this.vchTplCodes) +
                ") and " +
                " FEntityCode in (" + operSql.sqlCodes(entityCode) + ")" +
                " and FCheckState<>2";
            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                resume = new VchEntityResumeBean();
                resume.setYssPub(pub);
                buf.append("").append("\t");
                buf.append("").append("\t");
                buf.append(rs.getString("FResumeDict")).append("\t");
                buf.append(rs.getString("FResumeField")).append("\t");
                buf.append(rs.getString("FResumeConent")).append("\t");
                buf.append(rs.getString("FDesc")).append("\t");
                buf.append("").append("\t");
                buf.append(rs.getString("FValueType")).append("\t");
                buf.append(setSuperRel());
                buf.append("\r\f");
                tmpBuf.append(rs.getString("FVchTplCode")).append("\t");
                tmpBuf.append(rs.getString("FEntityCode")).append("\r\n");
            }
            tmpBuf.append("\r\r");
            runStatus.appendRunDesc("VchRun", "    导出成功！\r\n");
            buf.append("\r\r");
            runStatus.appendRunDesc("VchRun", "    开始导出凭证分录科目设置... ...");
            strSql1 = "select * from " +
                pub.yssGetTableName("Tb_Vch_EntitySubject") +
                " where FVchTplCode in(" + operSql.sqlCodes(this.vchTplCodes) +
                ") and " +
                " FEntityCode in (" + operSql.sqlCodes(entityCode) + ")" +
                " and FCheckState<>2";
            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                subject = new VchEntitySubjectBean();
                subject.setYssPub(pub);
                subject.setVchTplCode(rs.getString("FVchTplCode"));
                subject.setEntityCode(rs.getString("FEntityCode"));
                subject.setOrderIndex(rs.getString("FOrderNum"));
                subject.setValueType(rs.getString("FValueType"));
                subject.setSubjectConnet(rs.getString("FSubjectConent"));
                subject.setSubjectFieldCode(rs.getString("FSubjectField"));
                subject.setSubjectDictCode(rs.getString("FSubjectDict"));
                subject.setDesc(rs.getString("FDesc"));
                buf.append(subject.buildRowStr());
                buf.append("\r\f");
                tmpBuf.append(subject.getVchTplCode()).append("\t");
                tmpBuf.append(subject.getEntityCode()).append("\r\n");
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            tmpBuf.append("\r\r");
            runStatus.appendRunDesc("VchRun", "    导出成功！\r\n");
            buf.append("\r\r");
            runStatus.appendRunDesc("VchRun", "    开始导出凭证数据源设置... ...");
            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_DataSource") +
                " where FVchDsCode in(" + operSql.sqlCodes(dsCode) + ")";
            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                source = new VchDataSourceBean();
                source.setYssPub(pub);
                source.setVchDsCode(rs.getString("FVchDsCode"));
                source.setVchDsName(rs.getString("FVchDsName"));
                source.setVchAttrCode(rs.getString("FAttrCode"));
                source.setDataSource(dbl.clobStrValue(rs.getClob("FDataSource")));
                source.setDesc(rs.getString("FDesc"));
                buf.append(source.buildRowStr());
                buf.append("\r\f");
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            runStatus.appendRunDesc("VchRun", "    导出成功！\r\n");
            buf.append("\r\r");
            runStatus.appendRunDesc("VchRun", "    开始导出凭证资源表字段... ...");
            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_DsTabField") +
                " where FVchDsCode in(" + operSql.sqlCodes(dsCode) + ")" +
                " and FCheckState=1";
            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                field = new VchDsTabFieldBean();
                field.setYssPub(pub);
                field.setDsVchDsCode(rs.getString("FVchDsCode"));
                field.setFieldName(rs.getString("FFieldName"));
                field.setAliasName(rs.getString("FAliasName"));
                field.setFunction(rs.getString("FFunction"));
                field.setDesc(rs.getString("FDesc"));
                buf.append(field.buildRowStr());
                buf.append("\r\f");
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            runStatus.appendRunDesc("VchRun", "    导出成功！\r\n");
            buf.append("\r\r");
            runStatus.appendRunDesc("VchRun", "    开始导出凭证设置... ...");
            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_Attr") +
                " where FAttrCode in(" + operSql.sqlCodes(attrCode) + ")" +
                " and FCheckState=1";
            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                attr = new VchAttrBean();
                attr.setYssPub(pub);
                attr.setAttrCode(rs.getString("FAttrCode"));
                attr.setAttrName(rs.getString("FAttrName"));
                attr.setDesc(rs.getString("FDesc"));
                attr.setVchInd(rs.getString("FVchInd"));
                attr.setHandCheck(rs.getInt("FHandCheck")); // add liyu 1015
                buf.append(attr.buildRowStr());
                buf.append("\r\f");
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            runStatus.appendRunDesc("VchRun", "    导出成功！\r\n");
            buf.append("\r\r");
           //modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
//            runStatus.appendRunDesc("VchRun", "    开始导出组合套帐链接设置... ...");
//            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_PortSetLink") +
//                " where FLinkCode in(" + operSql.sqlCodes(linkCode) + ")" +
//                " and FCheckState=1";
//            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
//            while (rs.next()) {
//                link = new VchPortSetLinkBean();
//                link.setYssPub(pub);
//                link.setLinkCode(rs.getString("FLinkCode"));
//                link.setLinkName(rs.getString("FLinkName"));
//                link.setPortCode(rs.getString("FPortCode"));
//                link.setBookSetCode(rs.getString("FBookSetCode"));
//                link.setDesc(rs.getString("FDesc"));
//                buf.append(link.buildRowStr());
//                tmpBuf1.append(link.getPortCode()).append("\t");
//                tmpBuf1.append(link.getBookSetCode()).append("\r\f");
//                buf.append("\r\f");
//            }
//            
//            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
//            dbl.closeResultSetFinal(rs);
//            
//            runStatus.appendRunDesc("VchRun", "    导出成功！\r\n");
//            buf.append("\r\r");
            runStatus.appendRunDesc("VchRun", "    开始导出分录金额数量设置... ...");
            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_EntityMA") +
                " where FVchTplCode in(" + operSql.sqlCodes(vchTplCodes) +
                ") and " +
                " FEntityCode in (" + operSql.sqlCodes(entityCode) + ")" +
                " and FCheckState<>2";
            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                ma = new VchEntityMABean();
                ma.setYssPub(pub);
                ma.setVchTplCode(rs.getString("FVchTplCode"));
                ma.setEntityCode(rs.getString("FEntityCode"));
                ma.setOrderIndex(rs.getString("FOrderNum"));
                ma.setType(rs.getString("FType"));
                ma.setValueType(rs.getString("FValueType"));
                ma.setMaConent(rs.getDouble("FMAConent"));
                ma.setMaField(rs.getString("FMAField"));
                ma.setMaDictCode(rs.getString("FMADict"));
                ma.setOperSign(rs.getString("FOperSign"));
                ma.setDesc(rs.getString("FDesc"));
                buf.append(ma.buildRowStr());
                buf.append("\r\f");
                tmpBuf.append(ma.getVchTplCode()).append("\t");
                tmpBuf.append(ma.getEntityCode()).append("\r\n");
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            tmpBuf.append("\r\r");
            runStatus.appendRunDesc("VchRun", "    导出成功！\r\n");
            buf.append("\r\r");
            runStatus.appendRunDesc("VchRun", "    开始导出辅助核算设置... ...");
            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_Assistant") +
                " where FVchTplCode in(" + operSql.sqlCodes(vchTplCodes) +
                ") and " +
                " FEntityCode in (" + operSql.sqlCodes(entityCode) + ")" +
                " and FCheckState<>2";
            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                assistant = new VchAssistantBean();
                assistant.setYssPub(pub);
                assistant.setVchTplCode(rs.getString("FVchTplCode"));
                assistant.setEntityCode(rs.getString("FEntityCode"));
                assistant.setOrderIndex(rs.getString("FOrderNum"));
                assistant.setValueType(rs.getString("FValueType"));
                assistant.setAssistantConent(rs.getString("FAssistantConent"));
                assistant.setAssistantFieldCode(rs.getString("FAssistantField"));
                assistant.setAssistantDictCode(rs.getString("FAssistantDict"));
                assistant.setDesc(rs.getString("FDesc"));
                buf.append(assistant.buildRowStr());
                buf.append("\r\f");
                tmpBuf.append(assistant.getVchTplCode()).append("\t");
                tmpBuf.append(assistant.getEntityCode()).append("\r\n");
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            tmpBuf.append("\r\r");
            runStatus.appendRunDesc("VchRun", "    导出成功！\r\n");
            buf.append("\r\r");
            rs.close();
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
        //---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        //---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
        runStatus.appendRunDesc("VchRun", "导出凭证的配置信息成功！\r\n");
        return buf.toString() + tmpBuf.toString() + "\r\r" + tmpBuf1.toString();
    }
}
