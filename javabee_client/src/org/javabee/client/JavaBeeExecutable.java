/**
 * 
 */
package org.javabee.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.javabee.client.config.JavaBeeConfigs;

/**
 * @author Jean Villete
 *
 */
class JavaBeeExecutable {
	
	private JavaBeeConfigs 				configs;

	JavaBeeExecutable(JavaBeeConfigs configs) {
		super();
		this.configs = configs;
	}
	
	String execute() {
		try {
			List<String> cmd = new ArrayList<String>();
	        cmd.add(this.configs.getJavabeeTargetScript());
	        cmd.add("-mount"); 
	        cmd.add("-lib"); 
	        cmd.add("\"" + this.configs.getDesiredLibraries() + "\"");
	        cmd.add("-md");
	        cmd.add(this.configs.getManageDependencies().toString());
	        cmd.add("-fc");
	
	        ProcessBuilder pb = new ProcessBuilder();
	        pb.directory(new File(System.getProperty("user.home")));
	        pb.redirectErrorStream(true);
	        pb.command(cmd);
			Process process = pb.start();
			JavaBeeStreamGobbler outputGobbler = new JavaBeeStreamGobbler(process.getInputStream());
			outputGobbler.start();
			process.waitFor();
			return outputGobbler.getMessage();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
