/**
 * 
 */
package org.javabee.client;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.javabee.client.common.JavaBeeConstants;
import org.javabee.client.config.JavaBeeConfigs;
import org.javabee.client.config.JavaBeeConfigsEnvironmentVariable;
import org.javabee.client.config.JavaBeeConfigsFullPath;

/**
 * @author villjea
 *
 */
public class JavaBeeWebListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();
		
		// manage dependencies param
		String manageDependenciesParam = servletContext.getInitParameter(JavaBeeConstants.WEB_PARAM_MANAGE_DEPENDENCIES);
		boolean manageDependencies = false;
		if (manageDependenciesParam != null && !manageDependenciesParam.isEmpty()) {
			manageDependencies = manageDependenciesParam.equals("true") || manageDependenciesParam.equals("1") ||
					manageDependenciesParam.equals("yes") || manageDependenciesParam.equals("y");
		}
		
		// libraries param
		String librariesParam = servletContext.getInitParameter(JavaBeeConstants.WEB_PARAM_LIBRARIES);
		if (librariesParam == null || librariesParam.isEmpty()) {
			throw new IllegalArgumentException("The parameter " + JavaBeeConstants.WEB_PARAM_LIBRARIES + 
					" is missing or is null, and this is mandatory!");
		}
		
		String javabeeHome = null;
		JavaBeeConfigs configs = null;
		if ((javabeeHome = servletContext.getInitParameter(JavaBeeConstants.WEB_PARAM_FULL_PATH)) != null
				&& !javabeeHome.isEmpty()) {
			configs = new JavaBeeConfigsFullPath(javabeeHome, librariesParam, manageDependencies);
		} else if ((javabeeHome = servletContext.getInitParameter(JavaBeeConstants.WEB_PARAM_ENVIRONMENT_VARIABLE)) != null
				&& !javabeeHome.isEmpty()) {
			configs = new JavaBeeConfigsEnvironmentVariable(javabeeHome, librariesParam, manageDependencies);
		} else {
			throw new IllegalArgumentException("No valid value was found neither for context param: " + JavaBeeConstants.WEB_PARAM_FULL_PATH
					+ " nor for context param: " + JavaBeeConstants.WEB_PARAM_ENVIRONMENT_VARIABLE 
					+ " and at least one of the two is mandatory");
		}
		
		// invoking the dynamically classpath load
		new JavaBeeClient(configs).loadClasspath();
	}

}
