package optionschain.predictor;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import optionschain.predictor.db.TDOptionsChainDao;

public class TDController {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(TDWorkerThread.class);
	private static ApplicationContext context;
	private static TDOptionsChainDao dao;

	static {

		context = new ClassPathXmlApplicationContext("Spring-Module.xml");

		dao = (TDOptionsChainDao) context.getBean("TDoptionschainDAO");
	}
	
	public static void getDataFromDB(){
		dao.filterData();
	}
	public static void clearData(){
		dao.clearData();
	}
}
