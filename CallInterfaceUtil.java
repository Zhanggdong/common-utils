package common;

import com.google.gson.Gson;

public class CallInterfaceUtil {

	//���������ļ�
	private static final SystemConfig sc = SystemConfig.getInstance();
		
	/**
	 * ���ýӿ�
	 * @param data  JSON����
	 * @param url   ·��
	 * @return
	 */
	public static String useInterface(String data,String url,String labelName){
		//�õ� ·��
		String path = sc.getPath(labelName) + url;
		//���ýӿ�
		String result = HttpClientUtil.sendData(data,path);
		return result;
	}
	
	public static String useCharsetInterface(String data, String url,String labelName, String charset){
		//�õ� ·��
		String path = sc.getPath(labelName) + url;
		//���ýӿ�
		String result = HttpClientUtil.sendData(data,path,charset);
		return result;
	}
	
	public static String useInterfaceXML(String data, String url, String labelName, String charset){
		//�õ� ·��
		String path = sc.getPath(labelName) + url;
		//���ýӿ�
		String result = HttpClientUtil.sendDataXML(data,path,charset);
		return result;
	}
	
	/**
	 * ���ýӿ�
	 * @param data  String����
	 * @param url   ·��
	 * @return
	 */
	public static String useStringInterface(String data,String url,String labelName){
		//�õ� ·��
		String path = sc.getPath(labelName) + url;
		//���ýӿ�
		String result = HttpClientUtil.sendStringData(data, path);
		return result;
	}
	
	/**
	 * ���ýӿ�
	 * @param data  ʵ����
	 * @param url   ·��
	 * @return
	 */
	public static String useInterface(Object data,String url,String labelName){
		Gson gson = new Gson();
		//ת��JSON
		String dateJson = gson.toJson(data);
		return useInterface(dateJson,url,labelName);
	}
	
	/**
	 * ���ýӿ�
	 * @param data  ʵ����
	 * @param url   ·��
	 * @return
	 */
	public static String useInterface(String url,String labelName){
		//�õ� ·��
		String path = sc.getPath(labelName) + url;
		return HttpClientUtil.sendData(path);
	}
	
	/**
	 * ���ýӿ�
	 * @param data  ʵ����
	 * @param url   ·��
	 * @return
	 */
	public static String useInterfaceViaGet(String url,String labelName){
		//�õ� ·��
		String path = sc.getPath(labelName) + url;
		return HttpClientUtil.sendDataHttpViaGet(path);
	}

	public static String GetMothod(String url,String labelName){
		//�õ� ·��
		String path = sc.getPath(labelName) + url;
		return HttpClientUtil.sendGetModelUrl(path);
	}
}
