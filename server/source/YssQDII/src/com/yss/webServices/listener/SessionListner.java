package com.yss.webServices.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.yss.util.WarnPluginLoader;

/*
 * add by zhouwei 20120615 增加一个session的监听功能
 * */
public class SessionListner implements HttpSessionListener {

	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		//story 2188 add by  zhouwei 20120615 保存有效的session
		String key=arg0.getSession().getId();
		if(!WarnPluginLoader.sessionMap.containsKey(key)){
			WarnPluginLoader.sessionMap.put(key, arg0.getSession());	
		}
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		//根据sessionId在pubMap中做排除操作  add by zhouwei 20120615 story 2188
        WarnPluginLoader.sessionMap.remove(arg0.getSession().getId());
	}

}
