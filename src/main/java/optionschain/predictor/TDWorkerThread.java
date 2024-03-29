package optionschain.predictor;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import optionschain.predictor.db.OptionsChainDao;
import optionschain.predictor.db.TDOptionsChainDao;
import optionschain.predictor.model.Amtd;
import optionschain.predictor.model.Amtd.OptionChainResults.OptionDate;
import optionschain.predictor.model.Amtd.OptionChainResults.OptionDate.OptionStrike;
import optionschain.predictor.model.Expirations;
import optionschain.predictor.model.Expiry;
import optionschain.predictor.model.OptionsChain;
import optionschain.predictor.model.Puts;
import optionschain.predictor.utils.Config;
import optionschain.predictor.utils.OrderedHashMap;
import optionschain.predictor.utils.Utils;
import optionschain.predictor.utils.XMLNode;
import optionschain.predictor.utils.XMLNodeBuilder;

public class TDWorkerThread implements Runnable {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(TDWorkerThread.class);
	private String command;
	private static OptionsChain chain;
	private static ApplicationContext context;
	private static TDOptionsChainDao dao;
	private static int counter = 1;

	static {

		context = new ClassPathXmlApplicationContext("Spring-Module.xml");

		dao = (TDOptionsChainDao) context.getBean("TDoptionschainDAO");
	}

	public TDWorkerThread(String s) throws JsonParseException, IOException {
		this.command = s;

	}

	public void run() {
		try {
			processCommand();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			logger.error(e.getLocalizedMessage(), e);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			logger.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			logger.error(e.getLocalizedMessage(), e);
		}
	}

	private void processCommand() throws JsonParseException, IOException, ParseException, Exception {

		Amtd amtd = getOptions(this.command);

		if (amtd != null && amtd.getOptionChainResults() != null
				&& amtd.getOptionChainResults().getOptionDate() != null) {
			Iterator<OptionDate> optionDateIterator = amtd.getOptionChainResults().getOptionDate().iterator();
			while (optionDateIterator.hasNext()) {
				OptionDate optionDate = (OptionDate) optionDateIterator.next();
				Iterator<OptionStrike> strikeIterator = optionDate.getOptionStrike().iterator();
				while (strikeIterator.hasNext()) {
					OptionStrike optionStrike = (OptionStrike) strikeIterator.next();
					double bid = optionStrike.getPut().getBid();
					double strike = optionStrike.getStrikePrice();
					double roc = bid / (strike - bid);
					int dte = optionDate.getDaysToExpiration();
					double aroc = ((double) Math.pow((double) (1 + roc), (double) (365 / dte))) - 1;
					double last = amtd.getOptionChainResults().getLast();
					double percentBelow = strike/last;
					//regTMargin = MAX(
					//   (SUM(
//							((0.2*Last)*100),
//							((Strike-Last)*100),
//							(Bid*100))
//						  ),
//						  (SUM(
//								  ((0.1*Strike)*100),
//								  (Bid*100))
//						  ),
//						  (SUM(
//								  50,(Bid*100)
//						  )
//						 ))
					double regT1 = (last*0.2*100)+((strike-last) * 100) + (bid*100);
					double regT2 = ((0.1* strike)*100)+(bid*100);
					double regT3 = 50+(bid*100);
					double regTMargin = Math.max(regT1, regT2);
					regTMargin = Math.max(regT3,  regTMargin);
					double rom = bid*100/regTMargin;
					//arom= =((1+rom)^(365/dte))-1
					double arom = Math.pow((1+rom),(365/dte))-1;
					String good = "N/A";
					String earningsDateStr = Config.getEarnings(optionStrike.getPut().getUnderlyingSymbol());
					if (earningsDateStr != null && !earningsDateStr.isEmpty()) {
						Date earningsDate = new SimpleDateFormat("MM/dd/yyyy").parse(earningsDateStr);
						Date today = Calendar.getInstance().getTime();
						Date expirationDate = new SimpleDateFormat("yyyyMMdd").parse(optionDate.getDate() + "");
						if (earningsDate.after(expirationDate) || earningsDate.before(today)) {
							good = "Good";
						} else {
							good = "NotGood";
						}
					}
					dao.insert(optionStrike.getPut(), optionDate.getDate(), optionStrike.getStrikePrice(),
							amtd.getOptionChainResults().getTime(), amtd.getOptionChainResults().getLast(),
							amtd.getOptionChainResults().getOpen(), amtd.getOptionChainResults().getClose(),
							amtd.getOptionChainResults().getHigh(), amtd.getOptionChainResults().getLow(), roc, aroc, dte, percentBelow, rom, arom , new Double(regTMargin).intValue(), earningsDateStr, good);

				}
			}
		}
		counter++;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			logger.error(e.getLocalizedMessage(), e);
		}

		try {
			if (counter % 3000 == 0) {
				Thread.sleep(2 * 60 );
			}
			;
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			logger.error(e.getLocalizedMessage(), e);
		}

	}

	@Override
	public String toString() {
		return this.command;
	}

	public Amtd getOptions(String symbols) throws IOException {

		String str = "https://apis.tdameritrade.com/apps/100/OptionChain;jsessionid=" + SessionControl.getSessionid()
				+ "?source=" + Config.getProperty("AMTDsourceID") + "&symbol=" + symbols + "&type=P&quotes=true";
		Amtd amtd = null;

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(Amtd.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			amtd = (Amtd) jaxbUnmarshaller.unmarshal(Utils.getStreamfromURL(str));
			logger.info("Response for Symbol : " + symbols + " : " + amtd);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return amtd;

	}

	static long lastlogintime;

	public static void login() throws IOException {
		long now = System.currentTimeMillis();
		if (lastlogintime != 0 && ((now - lastlogintime) < 1000 * 60 * 5)) {
			System.err.println("We tried to set this less than 5 minutes ago");
			System.exit(0);
		}

		lastlogintime = now;
		// http://ameritrade02.streamer.com/!U=870000189&W=b3cb690339ba220bde92c9f42e4e75bbaf5615db&A=userid=870000189&token=b3cb690339ba220bde92c9f42e4e75bbaf5615db&company=AMER&segment=UAMER&cddomain=A000000008835677&usergroup=ACCT&accesslevel=ACCT&authorized=Y&acl=ADAQC1DRESGKIPMAOLPNQSRFSPTETFTOTTUAWSQ2NS&timestamp=1143132528988&appid=testapp1|

		OrderedHashMap ohm = new OrderedHashMap();
		ohm.put("userid", Config.getProperty("AMTDUserName"));
		ohm.put("password", Config.getProperty("AMTDPassword"));
		ohm.put("source", Config.getProperty("AMTDsourceID")); // F3
		ohm.put("version", "1001");
		String url = "https://apis.tdameritrade.com/apps/300/LogIn?source=" + Config.getProperty("AMTDsourceID")
				+ "&version=1001";
		String res = Utils.sendURLPostRequest(url, ohm);
		XMLNode root = new XMLNodeBuilder(res).getRoot();

		SessionControl.setSessionid(
				root.getChildwithNameNonNull("xml-log-in").getChildwithNameNonNull("session-id").getValue());
		SessionControl.setSegment(root.getChildwithNameNonNull("xml-log-in").getChildwithNameNonNull("accounts")
				.getChildwithName("account").getChildwithNameNonNull("segment").getValue());
		SessionControl.setCompany(root.getChildwithNameNonNull("xml-log-in").getChildwithNameNonNull("accounts")
				.getChildwithName("account").getChildwithNameNonNull("company").getValue());
		//

		// Sending information through HTTPS: POST

	}
}
