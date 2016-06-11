package optionschain.predictor;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Utils {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Utils.class);

	public static double convertToDouble(String value) {
		if (value == null || value.equals("-") || value.isEmpty()) {
			return 0.0d;
		} else {
			return Double.parseDouble(value.replace(",", ""));
		}
	}

	public static Date convertToDate(String dateStr) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
		Date startDate = (Date) new java.sql.Date(formatter.parse(dateStr).getTime());
		return startDate;
	}

	public static java.util.Date convertToUtilDate(String dateStr) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
		java.util.Date startDate = (java.util.Date) (formatter.parse(dateStr));
		return startDate;
	}

	static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

	static {
		cm.setMaxTotal(20);
	}

	static CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).evictExpiredConnections()
			.evictIdleConnections(5L, TimeUnit.SECONDS).build();

	public static void main(String[] args) throws Exception {

		try {
			// create an array of URIs to perform GETs on
			String[] urisToGet = { "http://hc.apache.org/", "http://hc.apache.org/httpcomponents-core-ga/",
					"http://hc.apache.org/httpcomponents-client-ga/", };

			for (int i = 0; i < urisToGet.length; i++) {
				String requestURI = urisToGet[i];
				HttpGet request = new HttpGet(requestURI);

				System.out.println("Executing request " + requestURI);

				CloseableHttpResponse response = httpclient.execute(request);
				try {
					System.out.println("----------------------------------------");
					System.out.println(response.getStatusLine());
					EntityUtils.consume(response.getEntity());
				} finally {
					response.close();
				}
			}

			PoolStats stats1 = cm.getTotalStats();
			System.out.println("Connections kept alive: " + stats1.getAvailable());

			// Sleep 10 sec and let the connection evictor do its job
			Thread.sleep(10000);

			PoolStats stats2 = cm.getTotalStats();
			System.out.println("Connections kept alive: " + stats2.getAvailable());

		} finally {
			httpclient.close();
		}
	}

	public static String restTemplatehttpRequest(String uri) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
		return result.getBody();
	}

	public static String httpPoolRequest(String uri) {
		HttpGet request = new HttpGet(uri);
		logger.info("Executing request " + uri);
		CloseableHttpResponse response = null;
		String body = null;
		try {
			response = httpclient.execute(request);
			logger.info(response.getStatusLine().toString());
			//EntityUtils.consume(response.getEntity());
			body = EntityUtils.toString(response.getEntity(), "UTF-8");
			//logger.info("Response: " + body);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PoolStats stats = cm.getTotalStats();
		logger.info("Connections kept alive: " + stats.getAvailable());
		return body;
	}
}
