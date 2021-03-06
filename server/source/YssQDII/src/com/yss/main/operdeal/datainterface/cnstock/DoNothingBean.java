package com.yss.main.operdeal.datainterface.cnstock;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;
import java.util.ArrayList;

/**
 * QDII国内：QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 国内接口处理
 * add by songjie
 * 2009-05-15
 * 用于处理国内接口数据从临时表到债券信息表的数据转换
 */
public class DoNothingBean extends DataBase {
    public static ArrayList alInterfaceCode = new ArrayList();//用于储存已导入的自定义接口代码
    /**
     * 构造函数
     */
    public DoNothingBean() {

    }

    public ArrayList getAlInterfaceCode() {
        return alInterfaceCode;
    }

    /**
     *用于处理国内接口的数据
     * 若自定义接口对应的数据已经导入到临时表，
     * 那么就将自定义接口代码储存到列表中
     */
    public void inertData() throws YssException {
        if (this.cusCfgCode.equals("SH_zqbd_imp")) { //上海证券变动库
            if (!alInterfaceCode.contains(this.cusCfgCode)) {
                alInterfaceCode.add(this.cusCfgCode);
            }
        }

        if (this.cusCfgCode.equals("SH_tmpgh_IMP")) { //上海过户库
            if (!alInterfaceCode.contains(this.cusCfgCode)) {
                alInterfaceCode.add(this.cusCfgCode);
            }
        }

        if (this.cusCfgCode.equals("SH_tmpdgh_IMP")) { //上海B股大宗交易过户库    panjunfang add 20100426
            if (!alInterfaceCode.contains(this.cusCfgCode)) {
                alInterfaceCode.add(this.cusCfgCode);
            }
        }
        
        if (this.cusCfgCode.equals("SZ_sjsfx_IMP")) { //深圳发行库
            if (!alInterfaceCode.contains(this.cusCfgCode)) {
                alInterfaceCode.add(this.cusCfgCode);
            }
        }

        if (this.cusCfgCode.equals("SZ_sjsgf_IMP")) { //深圳股份库
            if (!alInterfaceCode.contains(this.cusCfgCode)) {
                alInterfaceCode.add(this.cusCfgCode);
            }
        }

        if (this.cusCfgCode.equals("SZ_sjshb_IMP")) { //深圳回报库
            if (!alInterfaceCode.contains(this.cusCfgCode)) {
                alInterfaceCode.add(this.cusCfgCode);
            }
        }
    }
}
