/**
 * 
 */
package org.javabee.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.javabee.client.common.JavaBeeConstants;

/**
 * 
 * When Runtime.exec() won't.
 * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
 * 
 * @author Jean Villete
 *
 */
class JavaBeeStreamGobbler extends Thread {

	private InputStream is;
    private StringBuilder outputMessage = new StringBuilder();
    
	JavaBeeStreamGobbler(InputStream is) {
		super();
		this.is = is;
	}
	
	String getMessage() {
        return outputMessage.toString();
    }
	
	@Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(this.is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null)
                this.outputMessage.append(line).append(JavaBeeConstants.END_OF_LINE);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
	
}
