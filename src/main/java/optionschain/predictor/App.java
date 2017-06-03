package optionschain.predictor;

import java.io.IOException;
import java.text.ParseException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;

import optionschain.predictor.utils.Utils;


/**
 * Hello world!
 *
 */
public class App {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws JsonParseException, IOException, ParseException {
		TDController.getDataFromDB();
		Utils.sendEmail("theoptionsprofit@gmail.com");
		logger.info("Email sent");
		TDController.clearData();
		logger.info("Options Chain!");
		TDWorkerThread.login();

		ExecutorService executor = Executors.newFixedThreadPool(20);
		
		Runnable worker ;
//		worker= new WorkerThread("APC");
//		executor.execute(worker);
	
		//for (int i = 0; i <1; i++) {
			
		for (int i = 0; i <SecuritiesConstants.list.length; i++) {
			worker = new TDWorkerThread(SecuritiesConstants.list[i]);
			executor.execute(worker);
		}
		
		executor.shutdown();
//		while (!executor.isTerminated()) {
//			
//		}
		
		logger.info("Finished all threads");
		
		try {
			executor.awaitTermination(75, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TDController.getDataFromDB();
		Utils.sendEmail("theoptionsprofit@gmail.com");
		logger.info("Email sent");
	}
}
