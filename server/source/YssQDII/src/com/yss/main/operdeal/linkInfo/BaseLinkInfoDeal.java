package com.yss.main.operdeal.linkInfo;

import com.yss.dsub.BaseBean;
import java.util.ArrayList;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;

public class BaseLinkInfoDeal
    extends BaseBean {
    public BaseLinkInfoDeal() {
    }

    public void setLinkAttr(BaseDataSettingBean LinkInfoBean) throws
        YssException {

    }

    protected String buildLinkCondition() {
        return "";
    }

    public ArrayList getLinkInfoBeans() throws
        YssException {
        String[] sFeeCondAry = null;
        ArrayList list = null;
        String tempName = "";
        Object obj = null;
        try {
        	//delete by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B
//            tempName = createTempData(); //获取数据至临时存储处
            sFeeCondAry = buildLinkCondition().split("\t");
            for (int i = 0; i <= sFeeCondAry.length; i++) {
                if (i == sFeeCondAry.length) {
                	//edit by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B
                    obj = getBeans("");
                } else {
                	//edit by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B
                    obj = getBeans(sFeeCondAry[i]);
                }
                if (obj != null) {
                    list = (ArrayList) obj;
                    break;
                }
            }
        } catch (Exception e) {
            throw new YssException("获取符合链接条件的信息出错",e);
        } finally {
            return list;
        }
    }

    /**
     * 返回临时存储处的名称
     * @throws YssException
     * @return String
     */
    protected String createTempData() throws YssException {
        return "";
    }

    /**
     *
     * @param sFeeCondAry String[]
     * @param tempName String 临时存储处的名称
     * @return ArrayList
     */
    //edit by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B
    protected Object getBeans(String sFeeCond) throws YssException {
        return null;
    }
}
