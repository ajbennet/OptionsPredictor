package optionschain.predictor;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
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
	
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 16);
		today.set(Calendar.MINUTE, 30);
		today.set(Calendar.SECOND, 0);

		// every day at at 4:30 pm you run your task
		Timer timer = new Timer();
		timer.schedule(new OptionsTask(), today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)); // 60*60*24*100 = 8640000ms
		logger.info("Timer task started");
	}
	
}

class OptionsTask extends TimerTask{
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(OptionsTask.class);
	@Override
	public void run() {
		try{
//		TDController.getDataFromDB();
//		Utils.sendEmail("theoptionsprofit@gmail.com");
//		logger.info("Email sent");
		logger.info("Options Chain! Started now : " + Calendar.getInstance().getTime());
		TDController.clearData();
		
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
		}catch( JsonParseException e){
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}catch( IOException e){

			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
	}
	
}