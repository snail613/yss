package com.yss.main.dao;

import com.yss.util.YssException;
import com.yss.dsub.YssPub;

/**
 * <p>Title: </p>
 *
 * <p>Description: 用于在资产估值中的业务处理</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech </p>
 *
 * @author 沈杰
 * @version 1.0
 * MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
 */
public interface IOperManage {
   public void setYssPub(YssPub pub);

   public String doOperManage(String sType) throws YssException;
}
