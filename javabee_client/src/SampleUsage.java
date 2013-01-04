import org.javabee.client.JavaBeeClient;
import org.javabee.client.config.JavaBeeConfigs;
import org.javabee.client.config.JavaBeeConfigsEnvironmentVariable;

/**
 * 
 */

/**
 * @author Jean Villete
 *
 */
public class SampleUsage {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JavaBeeConfigs configs = new JavaBeeConfigsEnvironmentVariable("JAVABEE_HOME", "xstream_1.4.1");
		JavaBeeClient javabeeClient = new JavaBeeClient(configs);
		javabeeClient.loadClasspath();
	}

}
