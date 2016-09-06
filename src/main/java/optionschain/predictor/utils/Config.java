package optionschain.predictor.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Config {
	private static Properties prop = new Properties();
	private static String propFileName = "config.properties";
	private static final Logger logger = LoggerFactory
			.getLogger(Config.class);
	static {
		InputStream inputStream = Config.class.getClassLoader()
				.getResourceAsStream(propFileName);
		try {
			prop.load(inputStream);
			logger.info("Properties :" +prop);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key) {
		return prop.getProperty(key);
	}

}