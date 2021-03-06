package com.yss.dbupdate.autoupdatetables.updatemain;

import java.util.*;

import com.yss.dbupdate.autoupdatetables.standardframebuild.*;
import com.yss.dbupdate.autoupdatetables.tableframecompare.*;
import com.yss.dbupdate.autoupdatetables.updateexecute.*;
import com.yss.dsub.*;
import com.yss.util.*;

/**
 *
 * <p>Title: 自动更新的操作控制类，也是入口类</p>
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
public class CtlAutoUpdate
    extends BaseBean {
    private VersionControl versionCtl;

    public CtlAutoUpdate() {
    }

    /**
     * 执行类的初始化
     */
    private void initAttributes() {
        versionCtl = new VersionControl();
        versionCtl.setYssPub(pub);
    }

    /**
     * 执行更新，入口方法
     * @param hmInfo HashMap：提供返回值的哈希表，需要传入实例
     * @param sAssetGroupCode: 执行更新的组合群代码
     * @param sUserCode：启动更新的用户代码
     * @return String：现在什么都不返回，该返回的都通过哈希表返回了，但是这个参数先留着
     * @throws YssException
     */
    public String executeUpdate(HashMap hmInfo, String sAssetGroupCode, String sUserCode) throws YssException {
        String resStr = "";
        ArrayList alNeedUpdateVerNum = null;
        //记录所有涉及结构调整和数据调整的 SQL 语句
        StringBuffer sqlBuf = new StringBuffer(500000);
        //记录更新过程中出现的异常
        StringBuffer errBuf = new StringBuffer(10000);
        //记录完成更新的表名
        StringBuffer updTables = new StringBuffer(2000);
        try {
            //不要传入空对象
            if (hmInfo == null) {
                throw new YssException("调用执行更新出错，请传入 HashMap 实体！");
            } else {
                hmInfo.put("sqlinfo", sqlBuf);
                hmInfo.put("errinfo", errBuf);
                hmInfo.put("updatetables", updTables);
            }

            //初始化属性
            initAttributes();
            //获取需要更新的版本号
            alNeedUpdateVerNum = versionCtl.getNeedUpdateVerNum(sAssetGroupCode);

            for (int i = 0; i < alNeedUpdateVerNum.size(); i++) {
                String[] sCurrtUpdateVerNum = (String[]) alNeedUpdateVerNum.get(i);
                //自动更新前的手工调整
                resStr = beforeUpdate(hmInfo, sCurrtUpdateVerNum[0]);
                if (resStr.equals(YssCons.YSS_DBUPDATE_FAIL)) {
                    versionCtl.updateVersionInfo(sAssetGroupCode, sUserCode, sCurrtUpdateVerNum[0], resStr, hmInfo);
                    //如果出错了，抛出异常告诉上层方法
                    throw new YssException();
                }

                //判断是否有更新文件
                if (sCurrtUpdateVerNum[1].equals("1")) {
                    //自动更新
                    resStr = autoUpdate(hmInfo, sCurrtUpdateVerNum[0]);
                    if (resStr.equals(YssCons.YSS_DBUPDATE_FAIL)) {
                        versionCtl.updateVersionInfo(sAssetGroupCode, sUserCode,
                            sCurrtUpdateVerNum[0], resStr, hmInfo);
                        throw new YssException();
                    }
                }
                //自动更新后的手工调整
                resStr = afterUpdate(hmInfo, sCurrtUpdateVerNum[0]);
                if (resStr.equals(YssCons.YSS_DBUPDATE_FAIL)) {
                    versionCtl.updateVersionInfo(sAssetGroupCode, sUserCode, sCurrtUpdateVerNum[0], resStr, hmInfo);
                    throw new YssException();
                }

                versionCtl.updateVersionInfo(sAssetGroupCode, sUserCode, sCurrtUpdateVerNum[0], resStr, hmInfo);
                runStatus.appendRunDesc("updatedb", "updating" + "\r\f" + 5 + "\r\f" + sAssetGroupCode + "\r\f" + sCurrtUpdateVerNum[0]);//add by yeshenghong 20120324 story2164
            }
        } catch (Exception e) {
            throw new YssException("数据库更新出错！\r\n", e);
        }
        return "";
    }

    /**
     * 自动更新后的数据调整，执行手写代码
     * @param hmInfo HashMap：返回更新信息
     * @param sCurrtVerNum String：当前更新版本
     * @return String：更新结果，成功还是失败
     * @throws YssException
     */
    private String beforeUpdate(HashMap hmInfo, String sCurrtVerNum) throws YssException {
        String sResultTag = "";
        BeforeUpdate update = new BeforeUpdate();
        update.setYssPub(pub);  //设置pub 实现西东更新前的手动更新 sunkey@Modify 20090810
        sResultTag = update.beforeExecute(hmInfo, sCurrtVerNum);
        return sResultTag;
    }

    /**
     * 自动更新前的数据调整，执行手写代码
     * @param hmInfo HashMap：返回更新信息
     * @param sCurrtVerNum String：当前更新版本
     * @return String：更新结果，成功还是失败
     * @throws YssException
     */
    private String afterUpdate(HashMap hmInfo, String sCurrtVerNum) throws YssException{
        String sResultTag = "";
        AfterUpdate update = new AfterUpdate();
        update.setYssPub(pub); //MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-11 用于设置数据库连接
        sResultTag = update.afterExecute(hmInfo, sCurrtVerNum);
        return sResultTag;
    }

    /**
     * 自动更新的入口，不对外开放，通过主方法调用
     * @param hmInfo HashMap：返回更新信息
     * @return String：当前更新版本
     * @throws YssException
     */
    private String autoUpdate(HashMap hmInfo, String sCurrtVerNum) throws YssException {
        String sResultTag = YssCons.YSS_DBUPDATE_SUCCESS;
        ArrayList alTabName = null;
        StandardBuilder stdbild = null;
        TabCompare compare = null;
        try {
            if (hmInfo == null) {
                throw new YssException("调用自动更新表结构方法出错，请传入 HashMap 实例！");
            }
            //创建自定义字典表
            //所有的对象现在都是 new 出来的，以后可以考虑使用一个专门的对象来创建对象，封装好对象的创建更有利于对代码进行控制
            stdbild = new StandardBuilder();
            stdbild.setYssPub(pub);
            stdbild.build(sCurrtVerNum);

            //比较自定义字典表和系统字典表查询出表结构不同的表名
            compare = new TabCompare();
            compare.setYssPub(pub);
            alTabName = compare.getDiffFrameTablesName();
            //delete by songjie 2011.12.13 BUG 3302 QDV4赢时胜(测试)2011年12月02日01_B 
            //系统根据文件自动更新时，未更新非组合群的表结构，原因是把非组合群的表排除了
            //xuqiji 20100526
            //当有多个组合群更新时，第一个组合群更新成功，并且此时一些系统表被更新为最高版本，此时更新其它组合群时，要排除重新更新这些系统表
            //edit by songjie 2012.01.31 BUG 3723 QDV4赢时胜(上海)2012年01月20日03_B
            compare.removeTableByOtherAssetGroupUpdated(sCurrtVerNum,alTabName);
            try {
                BaseUpdateExecute update = new chkExecute();
                update.setYssPub(pub);
                //给个分割符，免得看不清
                ( (StringBuffer) hmInfo.get("sqlinfo")).append("--==================AutoUpdateCheck==================--\n");
                update.updExecute(alTabName, hmInfo, 1);

                update = new updExecute();
                update.setYssPub(pub);
                ( (StringBuffer) hmInfo.get("sqlinfo")).append("--==================AutoUpdateExecute==================--\n");
                update.updExecute(alTabName, hmInfo, 0);
            } catch (Exception e) {
                //如果出现异常，修改返回标志
                sResultTag = YssCons.YSS_DBUPDATE_FAIL;
            }
        } catch (Exception e) {
            throw new YssException("自动更新表结构出错\r\n", e);
        }
        return sResultTag;
    }
}
