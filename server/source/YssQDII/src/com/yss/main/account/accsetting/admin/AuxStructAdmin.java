package com.yss.main.account.accsetting.admin;

import com.yss.main.dao.IDataSetting;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import com.yss.main.account.accsetting.pojo.AuxStructBean;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import java.sql.ResultSet;
import com.yss.main.account.accsetting.pojo.AuxStructNodeBean;
import java.sql.Connection;
import com.yss.util.YssFun;

/**
 * <p>Title: 核算结构设置</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author CTQ
 * @version 1.0
 */
public class AuxStructAdmin extends BaseDataSettingBean implements IDataSetting {
    private AuxStructBean m_Data;
    private AuxStructBean m_OldData;
    private AuxStructBean m_Filter;

    private String m_Request;

    private String m_Command;

    public AuxStructAdmin() {
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Acc_AuxStruct"), "FSetCode,FStructCode", m_Data.getSetCode() + "," + m_Data.getStructCode(),
                               m_OldData.getSetCode() + "," + m_OldData.getStructCode());
    }

    /**
      * 新增设置信息
      * @return String
      * @throws YssException
      */
     public String addSetting() throws YssException {
         String strSql = "";
         boolean bTrans = false;
         Connection conn = dbl.loadConnection();
         try {
             conn.setAutoCommit(false);
             bTrans = true;

             //插入新纪录到核算结构表
             Object[] nodeList = m_Data.getNodeList().toArray();
             for (int i = 0; i < nodeList.length; i++) {
                 AuxStructNodeBean node = (AuxStructNodeBean) nodeList[i];

                 strSql = "insert into " + pub.yssGetTableName("Tb_Acc_AuxStruct") +
                     "(FSetCode,FStructCode,FStructName,FNodeCode,FNodeName,FParent,FAuxiliary,FAuxDetail," +
                     "FLevel,FDetail,FAmount,FCheckState,FCreator,FCreateTime)" +
                     "values(" +
                     dbl.sqlString(m_Data.getSetCode()) + "," +
                     dbl.sqlString(m_Data.getStructCode()) + "," +
                     dbl.sqlString(m_Data.getStructName()) + "," +
                     dbl.sqlString(node.getNodeCode()) + "," +
                     dbl.sqlString(node.getNodeName()) + "," +
                     dbl.sqlString(node.getParentCode()) + "," +
                     dbl.sqlString(node.getAuxCode()) + "," +
                     dbl.sqlString(node.getAuxDetailCode()) + "," +
                     node.getLevel() + "," +
                     (node.isDetail()? 1:0) + "," +
                     (node.isAmount()? 1:0) + "," +

                     (pub.getSysCheckState() ? "0" : "1") + "," +
                     dbl.sqlString(m_Data.creatorCode) + "," +
                     dbl.sqlString(m_Data.creatorTime) + ")";

                 dbl.executeSql(strSql);
             }

             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
         } catch (Exception e) {
             throw new YssException("新增核算结构设置信息出错", e);
         } finally {
             dbl.endTransFinal(conn, bTrans);
         }
         return m_Data.buildRowStr();
    }

    /**
     * 修改设置信息
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;

            strSql="delete " + pub.yssGetTableName("Tb_Acc_AuxStruct") +
                " where FSetCode =" +  dbl.sqlString(m_OldData.getSetCode()) +
                " and FStructCode =" +  dbl.sqlString(m_OldData.getStructCode());

            dbl.executeSql(strSql);

            //插入新纪录到核算结构表
             Object[] nodeList = m_Data.getNodeList().toArray();
             for (int i = 0; i < nodeList.length; i++) {
                 AuxStructNodeBean node = (AuxStructNodeBean) nodeList[i];

                 strSql = "insert into " + pub.yssGetTableName("Tb_Acc_AuxStruct") +
                     "(FSetCode,FStructCode,FStructName,FNodeCode,FNodeName,FParent,fauxiliary,FAuxDetail," +
                     "FLevel,FDetail,FAmount,FCheckState,FCreator,FCreateTime)" +
                     "values(" +
                     dbl.sqlString(m_Data.getSetCode()) + "," +
                     dbl.sqlString(m_Data.getStructCode()) + "," +
                     dbl.sqlString(m_Data.getStructName()) + "," +
                     dbl.sqlString(node.getNodeCode()) + "," +
                     dbl.sqlString(node.getNodeName()) + "," +
                     dbl.sqlString(node.getParentCode()) + "," +
                     dbl.sqlString(node.getAuxCode()) + "," +
                     dbl.sqlString(node.getAuxDetailCode()) + "," +
                     node.getLevel() + "," +
                     (node.isDetail()? 1:0) + "," +
                     (node.isAmount()? 1:0) + "," +

                     (pub.getSysCheckState() ? "0" : "1") + "," +
                     dbl.sqlString(m_Data.creatorCode) + "," +
                     dbl.sqlString(m_Data.creatorTime) + ")";

                 dbl.executeSql(strSql);
             }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增套账设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return m_Data.buildRowStr();
    }


    /**
      * 删除设置信息
      * @throws YssException
      */
     public void delSetting() throws YssException {
         String strSql = "";
         boolean bTrans = false;
         Connection conn = dbl.loadConnection();
         try {
             conn.setAutoCommit(false);
             bTrans = true;

             //更新记录的审核状态为2
             strSql = "update " + pub.yssGetTableName("Tb_Acc_AuxStruct") +
                 " set FCheckState = 2" +
                 ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                 ", FCheckTime = " +
                 dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " where FSetCode =" +  dbl.sqlString(m_Data.getSetCode()) +
                " and FStructCode =" +  dbl.sqlString(m_Data.getStructCode());

             dbl.executeSql(strSql);

             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
         } catch (Exception e) {
             throw new YssException("删除套账设置信息出错", e);
         } finally {
             dbl.endTransFinal(conn, bTrans);
         }
    }

    /**
      * 审核设置信息
      * @throws YssException
      */
     public void checkSetting() throws YssException {
         String strSql = "";
         String data = "";
         String[] arrData = null;
         String[] arrItem = null;
         boolean bTrans = false;
         Connection conn = null;
         try {
             conn = dbl.loadConnection();
             conn.setAutoCommit(false);
             bTrans = true;

             //由于回收站还原操作也使用此方法，所以必须支持批量操作
             //注意：回收站还原操作的请求数据的格式与审核操作的请求数据格式不同
             arrData = this.m_Request.split("\r\t")[0].split(YssCons.YSS_PASSAGESPLITMARK);
             for (int i = 0; i < arrData.length; i++) {
                 if (arrData[i].length() == 0) {
                     continue;
                 }
				/**shashijie 2012-7-2 STORY 2475 */
                 if (arrData[i].toLowerCase().startsWith("filter:") || 
                		 arrData[i].toLowerCase().startsWith("olddate:")) {
				/**end*/
                     continue;
                 } else if (arrData[i].toLowerCase().startsWith("data:")) {
                     data = arrData[i].substring(5);
                 } else {
                     data = arrData[i];
                 }

                 arrItem = data.split("\r\n");

                 //循环更新记录的审核状态
                 for (int j = 0; j < arrItem.length; j++) {
                     if (arrItem[j].length() == 0) {
                         continue;
                     }

                     this.m_Data.parseRowStr(arrItem[j]);

                     strSql = "update " + pub.yssGetTableName("Tb_Acc_AuxStruct") +
                         " set FCheckState = case fcheckstate when 0 then 1 else 0 end" +
                         ", FCheckUser = case fcheckstate when 0 then " +
                         dbl.sqlString(pub.getUserCode()) + " else null end" +
                         ", FCheckTime = case fcheckstate when 0 then " +
                         dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                         " else null end " +
                         " where FSetCode =" +  dbl.sqlString(m_Data.getSetCode()) +
                         " and FStructCode =" +  dbl.sqlString(m_Data.getStructCode());

                     dbl.executeSql(strSql);
                 }
             }

             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
         } catch (Exception e) {
             throw new YssException("审核核算结构设置信息出错", e);
         } finally {
             dbl.endTransFinal(conn, bTrans);
         }
    }

    /**
      * 清空回收站记录
      * @throws YssException
      */
     public void deleteRecycleData() throws YssException {
         String strSql = "";
         Connection conn = null;
         boolean bTrans = false;
         String[] arrData = null;
         try {
             conn = dbl.loadConnection();
             conn.setAutoCommit(false);
             bTrans = true;

             arrData = this.m_Request.split("\r\t")[0].split("\r\n");
             for (int i = 0; i < arrData.length; i++) {
                 if (arrData[i].length() == 0) {
                     continue;
                 }

                 this.m_Data.parseRowStr(arrData[i]);

                 //删除核算结构信息
                 strSql="delete " + pub.yssGetTableName("Tb_Acc_AuxStruct") +
                        " where FSetCode =" +  dbl.sqlString(m_Data.getSetCode()) +
                        " and FStructCode =" +  dbl.sqlString(m_Data.getStructCode());

                 dbl.executeSql(strSql);

             }
             conn.commit();
             conn.setAutoCommit(true);
             bTrans = false;
         } catch (Exception e) {
             throw new YssException("清除数据出错", e);
         } finally {
             dbl.endTransFinal(conn, bTrans);
         }
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
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

    /**
     *获取设置内容列表信息
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
       String sHeader = "";
       String sShowDataStr = "";
       String sAllDataStr = "";
       String sVocStr ="";
       String strSql = "";
       ResultSet rs = null;
       ResultSet rsNode = null;

       StringBuffer bufShow = new StringBuffer();
       StringBuffer bufAll = new StringBuffer();

       try {
          if (m_Data == null) {
             m_Data = new AuxStructBean();
             m_Data.setYssPub(pub);
          }

          sHeader = this.getListView1Headers();

          strSql="select t1.*,t2.fusername as FCreatorName,t3.fusername as FCheckUserName from (" +
              "select distinct fsetcode,fstructcode,fstructname,fcheckstate,fcreator,fcreatetime,fcheckuser,fchecktime "+
              " from "+ pub.yssGetTableName("tb_acc_auxstruct") +
              this.buildFilterSql() +
              ") t1 "+
              " left join tb_sys_userlist t2 on t1.fcreator=t2.fusercode " +
              " left join tb_sys_userlist t3 on t1.fcheckuser=t3.fusercode ";

          rs = dbl.openResultSet(strSql);

          while (rs.next()) {
             bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                 append(YssCons.YSS_LINESPLITMARK);

             m_Data.setValues(rs);

             m_Data.getNodeList().clear();

             strSql= "select t1.fnodecode,t1.fnodename,t1.fparent as fparentcode,t2.fnodename as fparentname,"+
                 "t1.fauxiliary as fauxcode,t3.fauxname as fauxname,t1.fauxdetail as fauxdetailcode,"+
                 "t1.flevel,t1.fdetail,t1.famount  from ("+

                 "select * from "+ pub.yssGetTableName("tb_acc_auxstruct") +
                 " where FSetCode = " + dbl.sqlString(rs.getString("FSetCode")) +
                 " and FStructCode = " + dbl.sqlString(rs.getString("FStructCode")) +

                 ") t1 "+
                 " left join "+ pub.yssGetTableName("tb_acc_auxstruct") +
                 " t2 on t1.fparent=t2.fnodecode and t1.fsetcode=t2.fsetcode and t1.fstructcode=t2.fstructcode "+
                 " left join tb_base_auxiliary t3 on t1.fauxiliary=t3.fauxcode ";

             rsNode = dbl.openResultSet(strSql);

             AuxDetailAdmin detailBean = new AuxDetailAdmin();
             detailBean.setYssPub(pub);

             while(rsNode.next())
             {
                 AuxStructNodeBean bean=new AuxStructNodeBean();
                 bean.setValues(rsNode);
                 if(bean.getAuxDetailCode()!=null && bean.getAuxDetailCode().trim().length()!=0)
                 {
                     bean.setAuxDetailName(detailBean.getAuxDetailName(bean.getAuxCode(),bean.getAuxDetailCode()));
                 }

                 m_Data.getNodeList().add(bean);
             }

             bufAll.append(m_Data.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
          }
          if (bufShow.toString().length() > 2) {
             sShowDataStr = bufShow.toString().substring(0,
                 bufShow.toString().length() - 2);
          }

          if (bufAll.toString().length() > 2) {
             sAllDataStr = bufAll.toString().substring(0,
                 bufAll.toString().length() - 2);
          }

          return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
              this.getListView1ShowCols();

       }
       catch (Exception e) {
          throw new YssException("获取核算项目设置信息出错！", e);
       }
       finally {
          dbl.closeResultSetFinal(rs);
          dbl.closeResultSetFinal(rsNode);
       }
   }

   /**
     * 生成过滤条件子句
     * @return String
     * @throws YssException
     */
    private String buildFilterSql() throws YssException {
       String sResult = "";

       sResult = " where 1=1";

       if (m_Command.trim().equalsIgnoreCase("GetHeader")) {
           sResult += " and 1=2 ";
       } else {
           if (m_Data != null && m_Data.getSetCode().trim().length() != 0) {
               sResult += " and FSetCode = " + dbl.sqlString(m_Data.getSetCode());
           } else {
               sResult += " and FSetCode = " + dbl.sqlString(m_Command.trim());
           }

           if (m_Filter!=null && m_Filter.getStructCode().length() != 0) {
               sResult = sResult + " and FStructCode like " +
                   dbl.sqlString(this.m_Filter.getStructCode().replaceAll("'", "''"));
           }

           if (m_Filter!=null && m_Filter.getSetName().length() != 0) {
               sResult = sResult + " and FStructName like " +
                   dbl.sqlString(this.m_Filter.getStructName().replaceAll("'", "''"));
           }

       }
       return sResult;
   }

   /**
    *获取结构列表
    * @return String
    * @throws YssException
    */
   public String getListViewData2() throws YssException {
      String sHeader = "";
      String sShowDataStr = "";
      String sAllDataStr = "";
      String strSql = "";
      ResultSet rs = null;

      StringBuffer bufShow = new StringBuffer();
      StringBuffer bufAll = new StringBuffer();

      try {
         if (m_Data == null) {
            m_Data = new AuxStructBean();
            m_Data.setYssPub(pub);
         }

         sHeader = "结构代码\t结构名称";

         strSql="select distinct fstructcode,fstructname "+
             " from "+ pub.yssGetTableName("tb_acc_auxstruct") +
             " where fcheckstate=1 and fsetcode=" +dbl.sqlString(m_Filter.getSetCode());

         rs = dbl.openResultSet(strSql);

         while (rs.next()) {
              bufShow.append(rs.getString("FStructCode")).append(YssCons.YSS_ITEMSPLITMARK1);
              bufShow.append(rs.getString("FStructName")).append(YssCons.YSS_LINESPLITMARK);

              m_Data.setStructCode(rs.getString("FStructCode"));
              m_Data.setStructName(rs.getString("FStructName"));

              bufAll.append(m_Data.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
          }
          if (bufShow.toString().length() > 2) {
              sShowDataStr = bufShow.toString().substring(0,
                  bufShow.toString().length() - 2);
          }

          if (bufAll.toString().length() > 2) {
              sAllDataStr = bufAll.toString().substring(0,
                  bufAll.toString().length() - 2);
          }

          return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr ;
      }
      catch (Exception e) {
         throw new YssException("获取核算核算结构列表出错！", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
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

    public String getBeforeEditData() throws YssException {
        return "";
    }

    /**
     * 将请求数据给全局变量赋值
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }

            //保存Request数据
            m_Request = sRowStr;

            m_Command = "";

            if (this.m_Filter == null) {
                this.m_Filter = new AuxStructBean();
                this.m_Filter.setYssPub(pub);
            }

            if (this.m_Data == null) {
                this.m_Data = new AuxStructBean();
                this.m_Data.setYssPub(pub);
            }

            if (this.m_OldData == null) {
                this.m_OldData = new AuxStructBean();
                this.m_OldData.setYssPub(pub);
            }

            reqAry = sRowStr.split(YssCons.YSS_PASSAGESPLITMARK);
            for (int i = 0; i < reqAry.length; i++) {
                if (reqAry[i].startsWith("filter:")) {
                    this.m_Filter.parseRowStr(reqAry[i].substring(7));
                } else if (reqAry[i].startsWith("data:")) {
                    this.m_Data.parseRowStr(reqAry[i].substring(5));
                } else if (reqAry[i].startsWith("olddata:")) {
                    this.m_OldData.parseRowStr(reqAry[i].substring(8));
                } else if (reqAry[i].startsWith("command:")) {
                    this.m_Command = reqAry[i].substring(8);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析核算结构信息出错", e);
        }
    }

    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }
}
