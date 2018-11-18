package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.szwisdom.common.net.http.MySSLProtocolSocketFactory;

/**
 * HTTP请求帮助类
 * @author Administrator
 */
public class HttpClientUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);

	private static final String APPLICATION_JSON = "application/json";
	
	private static final String APPLICATION_XML = "application/xml";

	@SuppressWarnings("unused")
	private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

	private static final String UTF_8 = "UTF-8";
	
	private static HttpClient getHttpClient() {
		HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = httpConnectionManager.getParams();
		params.setConnectionTimeout(5000); 
        params.setSoTimeout(50000);
		params.setDefaultMaxConnectionsPerHost(32);//important 
		params.setMaxTotalConnections(256);//important
		params.setBooleanParameter("http.protocol.expect-continue" , false);
		HttpClient httpClient = new HttpClient(httpConnectionManager);
		return httpClient;
	}

	/**
	 * 往指定的URL发送数据。（JSON）
	 * 
	 * @param data
	 * @param url
	 * @return
	 */
	public final static String sendData(String url) {		
		HttpClient httpClient = getHttpClient();//new HttpClient();
		PostMethod httpPost = new PostMethod(url);
		httpPost.setRequestHeader("content-type", APPLICATION_JSON);
		httpPost.addRequestHeader("Connection", "close");
		
		try {
			int responseCode = httpClient.executeMethod(httpPost);
			if (responseCode == 200) {
				byte[] resBody = httpPost.getResponseBody();				
				String responseString = "";
				if (null == resBody || 0 == resBody.length) {
					responseString = httpPost.getResponseBodyAsString();
				} else {
					responseString = new String(resBody, UTF_8);
				}
				return responseString;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Assert.fail(e.getLocalizedMessage());
		} finally {
			httpPost.releaseConnection();
		}
		return "";
	}
	
	/**
	 * 往指定的URL发送数据。（JSON）
	 * 
	 * @param data
	 * @param url
	 * @return
	 */
	public final static String sendData(String data, String url) {
		return sendData(data, url, null);
	}
	
	public final static String sendData(String data, String url, String charset) {
		return sendData(data, url, charset, APPLICATION_JSON);
	}
	
	public final static String sendDataXML(String data, String url, String charset) {
		return sendData(data, url, charset, APPLICATION_XML);
	}
	
	private static String sendData(String data, String url, String charset, String contentType) {
		HttpClient httpClient = getHttpClient();//new HttpClient();
		PostMethod httpPost = new PostMethod(url);
		httpPost.setRequestHeader("content-type", contentType);
		httpPost.addRequestHeader("Connection", "close");
		
		try {
			RequestEntity se = new StringRequestEntity(data,null,charset);
			httpPost.setRequestEntity(se);
			int responseCode = httpClient.executeMethod(httpPost);
			if (responseCode == 200) {
				byte[] resBody = httpPost.getResponseBody();				
				String responseString = "";
				if (null == resBody || 0 == resBody.length) {
					responseString = httpPost.getResponseBodyAsString();
				} else {
					responseString = new String(resBody, UTF_8);
				}
				return responseString;
			}

		} catch (Exception e) {
			e.printStackTrace();
			// Assert.fail(e.getLocalizedMessage());
		} finally {
			httpPost.releaseConnection();
		}
		return "";
	}
	
	/**
	 * 往指定的URL发送数据。（String）
	 * 
	 * @param data
	 * @param url
	 * @return
	 */
	public final static String sendStringData(String data, String url) {
		HttpClient httpClient = getHttpClient();//new HttpClient();
		PostMethod httpPost = new PostMethod(url);
		httpPost.setRequestHeader("content-type", "application/x-www-form-urlencoded");
		httpPost.addRequestHeader("Connection", "close");
		
		try {
			RequestEntity se = new StringRequestEntity(data,null,null);
			httpPost.setRequestEntity(se);
			int responseCode = httpClient.executeMethod(httpPost);
			if (responseCode == 200) {
				byte[] resBody = httpPost.getResponseBody();				
				String responseString = "";
				if (null == resBody || 0 == resBody.length) {
					responseString = httpPost.getResponseBodyAsString();
				} else {
					responseString = new String(resBody, UTF_8);
				}
				return responseString;
			}

		} catch (Exception e) {
			e.printStackTrace();
			// Assert.fail(e.getLocalizedMessage());
		} finally {
			httpPost.releaseConnection();
		}
		return "";
	}

	/**
	 * 往指定的URL发送数据。（JSON+HTTPS）
	 * 
	 * @param data
	 * @param url
	 * @return
	 */
	public final static String sendDataHttps(String data, String url) {
		HttpClient httpClient = getHttpClient();//new HttpClient();
		try {
			Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);

			Protocol.registerProtocol("https", myhttps);

			PostMethod httpPost = new PostMethod(url);
			httpPost.setRequestHeader("content-type", "application/json");
			httpPost.addRequestHeader("Connection", "close");
			RequestEntity se = new StringRequestEntity(data,null,null);
			httpPost.setRequestEntity(se);
			int responseCode = httpClient.executeMethod(httpPost);
			if (responseCode == 200) {
				byte[] resBody = httpPost.getResponseBody();
				String responseString = "";
				if (null == resBody || 0 == resBody.length) {
					responseString = httpPost.getResponseBodyAsString();
				} else {
					responseString = new String(resBody, UTF_8);
				}
				httpPost.releaseConnection();
				return responseString;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String postByText(String url, Map<String, String> params) throws HttpException, IOException {
		return post(url,params,"text/html;charset=utf-8");
	}
	
	public static String postByXml(String url, Map<String, String> params) throws HttpException, IOException {
		return post(url,params,"text/xml;charset=utf-8");
	}
	
	public static String post(String url, Map<String, String> params,String contentType) throws HttpException, IOException {
		PostMethod post = new PostMethod(url);  
		post.setRequestHeader("Content-Type",contentType);
		post.addRequestHeader("Connection", "close");
        for (String key : params.keySet()){
        	post.setParameter(key, params.get(key));
        }
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(post);
        String response = post.getResponseBodyAsString();
        post.releaseConnection();
		return response;
	}
	
	public static String postByBodyWithCert(String url, String body, String contentType, String certPath)
			throws HttpException, Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		File file = new File(certPath);
		String fileName = file.getName();
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		FileInputStream instream = new FileInputStream(file);
		try {
			keyStore.load(instream, fileName.toCharArray());
		} finally {
			instream.close();
		}
		// Trust own CA and all self-signed certs
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, fileName.toCharArray()).build();
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] {"TLSv1"}, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();//TODO Alyan:初始化HttpConnectionManager
		StringBuilder sb = new StringBuilder();
		try {

			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type", contentType);
			httpPost.addHeader("Connection", "close");
			
			HttpEntity se = new StringEntity(body,"UTF-8");
			httpPost.setEntity(se);

			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
					String text;
					while ((text = bufferedReader.readLine()) != null) {
						sb.append(text);
					}

				}
				EntityUtils.consume(entity);
			} finally {
				httpPost.releaseConnection();
				response.close();
			}
		} finally {
			httpclient.close();
		}
		return sb.toString();
	}
	
	@SuppressWarnings("deprecation")
	public static String postByBody(String url, String body,String contentType) throws HttpException, IOException {
		PostMethod post = new PostMethod(url);
        post.setRequestHeader("Content-Type",contentType);
        post.addRequestHeader("Connection", "close");
        post.setRequestBody(body);
        HttpClient httpClient = getHttpClient();//new HttpClient();
        httpClient.executeMethod(post);
        String response = post.getResponseBodyAsString();
        post.releaseConnection();
		return response;
	}
	
	public final static String sendDataHttpsViaGet(String url) {
		LOG.debug("进来httpS");
		HttpClient httpClient = getHttpClient();//new HttpClient();
		if(url.startsWith("https")){
			
		} else {
			return sendDataHttpViaGet(url);
		}
		try {
			Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);

			Protocol.registerProtocol("https", myhttps);

			GetMethod httpGet = new GetMethod(url);
			httpGet.setRequestHeader("content-type", "application/json");
			httpGet.setRequestHeader("X-FORWARDED-FOR","183.61.189.134");
			httpGet.addRequestHeader("Connection", "close");

			int responseCode = httpClient.executeMethod(httpGet);
			if (responseCode == 200) {
				byte[] resBody = httpGet.getResponseBody();
				String responseString = "";
				if (null == resBody || 0 == resBody.length) {
					responseString = httpGet.getResponseBodyAsString();
				} else {
					responseString = new String(resBody, "UTF-8");
				}
				LOG.debug("Http url="+url);
				LOG.debug("Http get ret="+responseString);
				httpGet.releaseConnection();
				return responseString;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
			// Assert.fail(e.getLocalizedMessage());
		}
		return "";
	}

	public final static String sendDataHttpViaGet(String url) {
		HttpClient httpClient = getHttpClient();//new HttpClient();
		GetMethod httpGet = new GetMethod(url);
		try {			
			httpGet.setRequestHeader("content-type", "application/json");
			httpGet.addRequestHeader("Connection", "close");

			int responseCode = httpClient.executeMethod(httpGet);
			if (responseCode == 200) {
				byte[] resBody = httpGet.getResponseBody();
				String responseString = "";
				if (null == resBody || 0 == resBody.length) {
					responseString = httpGet.getResponseBodyAsString();
				} else {
					responseString = new String(resBody, "GBK");
				}
				return responseString;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpGet.releaseConnection();
		}
		return "";
	}
	public final static String sendGetModelUrl(String url) {
		HttpClient httpClient = getHttpClient();//new HttpClient();
		GetMethod httpGet = new GetMethod(url);
		try {			
			httpGet.setRequestHeader("content-type", "application/json");
			httpGet.addRequestHeader("Connection", "close");

			int responseCode = httpClient.executeMethod(httpGet);
			if (responseCode == 200) {
				byte[] resBody = httpGet.getResponseBody();
				String responseString = "";
				if (null == resBody || 0 == resBody.length) {
					responseString = httpGet.getResponseBodyAsString();
				} else {
					responseString = new String(resBody, "utf-8");
				}
				return responseString;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpGet.releaseConnection();
		}
		return "";
	}
	@SuppressWarnings("deprecation")
	public final static String sendPOSTModelUrl(String url,String jb) {
		HttpClient httpClient = getHttpClient();//new HttpClient();
		PostMethod httpPost = new PostMethod(url);
		try {			
			httpPost.setRequestHeader("content-type", "application/json");
			httpPost.addRequestHeader("Connection", "close");
			httpPost.setRequestBody(jb);
			int responseCode = httpClient.executeMethod(httpPost);
			if (responseCode == 200) {
				byte[] resBody = httpPost.getResponseBody();
				String responseString = "";
				if (null == resBody || 0 == resBody.length) {
					responseString = httpPost.getResponseBodyAsString();
				} else {
					responseString = new String(resBody, "utf-8");
				}
				return responseString;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpPost.releaseConnection();
		}
		return "";
	}
}
