package optionschain.predictor;


import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;


public class Utils {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Utils.class);
	
	public static double convertToDouble (String value){
		if(value==null || value.equals("-")|| value.isEmpty()){
			return 0.0d;
		}else{
			return Double.parseDouble(value);
		}
	}
	
	public static Date convertToDate(String dateStr) throws ParseException{
		DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy"); 
		Date startDate = (Date)new java.sql.Date(formatter.parse(dateStr).getTime()); 
		return startDate;
	}
	public static java.util.Date convertToUtilDate(String dateStr) throws ParseException{
		DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy"); 
		java.util.Date  startDate = (java.util.Date)(formatter.parse(dateStr)); 
		return startDate;
	}
}
