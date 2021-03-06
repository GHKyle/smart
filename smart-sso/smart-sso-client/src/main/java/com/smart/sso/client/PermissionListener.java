package com.smart.sso.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.smart.mvc.config.ConfigUtils;
import com.smart.mvc.util.SpringUtils;
import com.smart.sso.rpc.AuthenticationRpcService;
import com.smart.sso.rpc.RpcPermission;

/**
 * 权限初始化
 * 
 * @author Joe
 */
public class PermissionListener implements ServletContextListener{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionListener.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = (ServletContext) event.getServletContext();
		initApplicationPermissions(servletContext);
	}

	/**
	 * 应用初始化，获取应用所有的菜单及权限
	 * @param servletContext
	 */
	public static void initApplicationPermissions(ServletContext servletContext) {
		AuthenticationRpcService authenticationRpcService = SpringUtils.getBean(AuthenticationRpcService.class);
		List<RpcPermission> dbList = null;
		try {
			dbList = authenticationRpcService.findPermissionList(null, ConfigUtils.getProperty("app.code"));
		}
		catch (Exception e) {
			dbList = new ArrayList<RpcPermission>(0);
			LOGGER.error("无法连接到单点登录鉴权系统,请检查service.properties中sso.local.url配置", e);
		}
		List<RpcPermission> menuList = new ArrayList<RpcPermission>();
		Set<String> operateSet = new HashSet<String>();
		for (RpcPermission menu : dbList) {
			if (menu.getIsMenu()) {
				menuList.add(menu);
			}
			if (!StringUtils.isEmpty(menu.getUrl())) {
				operateSet.add(menu.getUrl());
			}
		}
		ApplicationUtils.setApplicationMenu(servletContext, menuList);
		ApplicationUtils.setApplicationPermission(servletContext, operateSet);
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
	}
}
