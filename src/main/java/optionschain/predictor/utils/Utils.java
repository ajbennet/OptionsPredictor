package optionschain.predictor.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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


				CloseableHttpResponse response = httpclient.execute(request);
				try {
					EntityUtils.consume(response.getEntity());
				} finally {
					response.close();
				}
			}

			PoolStats stats1 = cm.getTotalStats();

			// Sleep 10 sec and let the connection evictor do its job
			Thread.sleep(10000);

			PoolStats stats2 = cm.getTotalStats();

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
			// EntityUtils.consume(response.getEntity());
			body = EntityUtils.toString(response.getEntity(), "UTF-8");
			// logger.info("Response: " + body);
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

	public static InputStream getStreamfromURL(String str) throws IOException {
		URL url = new URL(str);
		return url.openStream();
	}

	public static String sendURLPostRequest(String urlstr, OrderedHashMap paramOHM) throws IOException {

		URL url = new URL(urlstr);
		URLConnection urlConn = url.openConnection();
		urlConn.setDoInput(true); // Let the run-time system (RTS) know that we
									// want input.
		urlConn.setDoOutput(true); // Let the RTS know that we want to do
									// output.
		urlConn.setUseCaches(false); // No caching, we want the real thing.
		urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// Specify
																						// the
																						// content
																						// type.
		// Send POST output.
		DataOutputStream printout = new DataOutputStream(urlConn.getOutputStream());

		StringBuffer data = new StringBuffer();
		for (int i = 0; i < paramOHM.size(); i++) {
			String key = (String) paramOHM.getKey(i);
			String value = (String) paramOHM.getValue(i);

			data.append(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
			if (i < paramOHM.size() - 1) {
				data.append("&");
			}
		} // for

		printout.writeBytes(data.toString());
		printout.flush();
		printout.close();
		// Get response data.

		String resp = StringHelper.inputStreamtoString(urlConn.getInputStream());
		return resp;
	}

}
