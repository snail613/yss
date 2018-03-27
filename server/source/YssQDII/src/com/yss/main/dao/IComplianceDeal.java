package com.yss.main.dao;

import java.util.*;

import com.yss.dsub.*;
import com.yss.util.*;

public interface IComplianceDeal {
    public void setYssPub(YssPub pub);

    public void init(BaseBean bean) throws YssException;

//   public ArrayList getCompTemplate

    //获取一组监控指标
//   public ArrayList getCompIndexs() throws YssException;

    //执行监控指标,返回所有违规的指标对象
    public HashMap doCompliance() throws YssException;

}
