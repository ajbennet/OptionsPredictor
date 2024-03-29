package optionschain.predictor;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
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
import optionschain.predictor.model.Amtd;
import optionschain.predictor.model.Expirations;
import optionschain.predictor.model.Expiry;
import optionschain.predictor.model.OptionsChain;
import optionschain.predictor.model.Puts;
import optionschain.predictor.utils.Config;
import optionschain.predictor.utils.Utils;

public class WorkerThread implements Runnable {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(WorkerThread.class);
	private String command;
	private static OptionsChain chain;
	private static ApplicationContext context;
	private static OptionsChainDao dao;
	private static int counter = 1;
	static {

		context = new ClassPathXmlApplicationContext("Spring-Module.xml");

		dao = (OptionsChainDao) context.getBean("optionschainDAO");
	}

	public WorkerThread(String s) throws JsonParseException, IOException {
		this.command = s;

	}

	public void run() {
		// = " + command);
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
		OptionsChain chain = getOptionsChain(this.command);
		logger.debug("Options Chain Expirations : " + chain.getExpirations());

		if (chain.getPuts() == null) {
			logger.info("Stocks:" + this.command + " : Does not have puts");
			return;
		}
		;
		addPuts(chain);
		counter++;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			logger.error(e.getLocalizedMessage(), e);
		}
		Expiry firstExpiry = chain.getExpiry();

		for (int i = 0; i < chain.getExpirations().length; i++) {
			Expirations expiry = chain.getExpirations()[i];
			if (firstExpiry.getD().equals(expiry.getD()) && firstExpiry.getM().equals(expiry.getM())) {
				logger.info("Skipping because expiry is the same - " + expiry + " : " + firstExpiry);
				continue;
			} else {
				OptionsChain chainExp = getOptionsChainWithExpiry(this.command, expiry);
				if (chainExp.getPuts() == null) {
					logger.info("Stocks:" + this.command + " : Does not have puts");
					return;
				}
				;
				addPuts(chainExp);
				counter++;
			}
			try {
				if (counter % 3000 == 0) {
					// sleep for 2 hrs after every 3000;
					Thread.sleep(2 * 60 * 100);
				}
				;
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				logger.error(e.getLocalizedMessage(), e);
			}

		}

	}

	private void addPuts(OptionsChain chain) throws ParseException {
		for (int i = 0; i < chain.getPuts().length; i++) {
			Puts puts = chain.getPuts()[i];
			double strike = Utils.convertToDouble(puts.getStrike());
			double bid = Utils.convertToDouble(puts.getB());
			double roc = bid / (strike - bid);
			long diff = Utils.convertToUtilDate(puts.getExpiry()).getTime() - new Date().getTime();
			double days = TimeUnit.MILLISECONDS.toDays(diff);
			if (days == 0) {
				days = 1;
			} else {
				days = days + 1;
			}
			double aroc = ((double) Math.pow((double) (1 + roc), (double) (365 / days))) - 1;
			dao.insert(puts, roc, aroc, Utils.convertToDouble(chain.getUnderlying_price()), this.command, days);

		}

	}

	@Override
	public String toString() {
		return this.command;
	}

	private OptionsChain getOptionsChain(String symbol) throws JsonParseException, IOException {
		final String uri = "https://www.google.com/finance/option_chain?q=" + symbol + "&output=json";
		logger.info("Counter: " + counter + " URL: " + new Date() + " : " + uri);
		RestTemplate restTemplate = new RestTemplate();
		// HttpClient httpClient =
		// HttpClientBuilder.create().setMaxConnTotal(100).setMaxConnPerRoute(100).build();
		// restTemplate.setRequestFactory(new
		// HttpComponentsClientHttpRequestFactory(httpClient));
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

		JsonFactory factory = new JsonFactory();
		factory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		JsonParser jp = factory.createParser(result.getBody());
		ObjectMapper mapper = new ObjectMapper();
		OptionsChain optionsChain = mapper.readValue(jp, OptionsChain.class);
		return optionsChain;
	}

	private OptionsChain getOptionsChainWithExpiry(String symbol, Expirations expiry)
			throws JsonParseException, IOException {
		final String uri = "https://www.google.com/finance/option_chain?q=" + symbol + "&expd=" + expiry.getD()
				+ "&expm=" + expiry.getM() + "&expy=" + expiry.getY() + "&output=json&";
		logger.info("Counter: " + counter + " URL: " + new Date() + " : " + uri);

		String body = Utils.httpPoolRequest(uri);

		JsonFactory factory = new JsonFactory();
		factory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		JsonParser jp = factory.createParser(body);
		ObjectMapper mapper = new ObjectMapper();
		OptionsChain optionsChain = mapper.readValue(jp, OptionsChain.class);

		return optionsChain;
	}

}
