import org.javabee.client.JavaBeeClient;
import org.javabee.client.config.JavaBeeConfigs;

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
		JavaBeeConfigs configs = new JavaBeeConfigs("C:\\Program Files (x86)\\javabee_engine\\", "xstream_1.4.1", true);
		JavaBeeClient javabeeClient = new JavaBeeClient(configs);
		javabeeClient.loadClasspath();
	}

}
