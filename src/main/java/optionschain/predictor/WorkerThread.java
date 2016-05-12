package optionschain.predictor;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import optionschain.predictor.db.OptionsChainDao;
import optionschain.predictor.model.OptionsChain;
import optionschain.predictor.model.Puts;

public class WorkerThread implements Runnable {

	private String command;
	private static OptionsChain chain;
	private static ApplicationContext context;
	private static OptionsChainDao dao;

	static {
		
		context = new ClassPathXmlApplicationContext("Spring-Module.xml");

		dao = (OptionsChainDao) context.getBean("optionschainDAO");
	}

	public WorkerThread(String s) throws JsonParseException, IOException {
		this.command = s;

	}

	public void run() {
		System.out.println(Thread.currentThread().getName() + "Start. Command = " + command);
		try {
			processCommand();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + "End.");
	}

	private void processCommand() throws JsonParseException, IOException, ParseException {
			System.out.println("Initializing options chain");
			OptionsChain chain = getOptionsChain(this.command);
		
		for (int i = 0; i < chain.getPuts().length; i++) {
			Puts puts = chain.getPuts()[i];
			double strike = Utils.convertToDouble(puts.getStrike());
			double bid = Utils.convertToDouble(puts.getB());
			double roc = bid / (strike - bid);

			long diff = Utils.convertToUtilDate(puts.getExpiry()).getTime() - new Date().getTime();
			double days = TimeUnit.MILLISECONDS.toDays(diff);
			double aroc = ((double) Math.pow((double) (1 + roc), (double) (365 / days))) - 1;
			dao.insert(puts, roc, aroc, Utils.convertToDouble(chain.getUnderlying_price()));

		}

	}

	@Override
	public String toString() {
		return this.command;
	}

	private static OptionsChain getOptionsChain(String symbol) throws JsonParseException, IOException {
		final String uri = "https://www.google.com/finance/option_chain?q="+symbol+"&output=json";

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

		// logger.info(result.getBody());

		JsonFactory factory = new JsonFactory();
		factory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		JsonParser jp = factory.createParser(result.getBody());
		ObjectMapper mapper = new ObjectMapper();
		OptionsChain optionsChain = mapper.readValue(jp, OptionsChain.class);

		System.out.println("OptionsChain : " + optionsChain);
		return optionsChain;
	}
}
