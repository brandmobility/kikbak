package com.kikbak.servletlisteners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.kikbak.config.ContextUtil;

public class KikbakLoadListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent servletCtxEvt) {
		ServletContext servletCtx = servletCtxEvt.getServletContext();
		ContextUtil.setRealPath(servletCtx.getRealPath("/"));
	}

}
