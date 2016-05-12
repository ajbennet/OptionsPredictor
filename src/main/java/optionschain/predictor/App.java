package optionschain.predictor;

import java.io.IOException;
import java.text.ParseException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;


/**
 * Hello world!
 *
 */
public class App {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Utils.class);

	public static void main(String[] args) throws JsonParseException, IOException, ParseException {
		logger.info("Options Chain!");
		

		ExecutorService executor = Executors.newFixedThreadPool(20);
		for (int i = 0; i < SecuritiesConstants.list.length; i++) {
			Runnable worker = new WorkerThread(SecuritiesConstants.list[i]);
			executor.execute(worker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		System.out.println("Finished all threads");
	}

	
}
