package common;

import com.google.gson.Gson;

public class CallInterfaceUtil {

	//加载配置文件
	private static final SystemConfig sc = SystemConfig.getInstance();
		
	/**
	 * 调用接口
	 * @param data  JSON参数
	 * @param url   路径
	 * @return
	 */
	public static String useInterface(String data,String url,String labelName){
		//得到 路径
		String path = sc.getPath(labelName) + url;
		//调用接口
		String result = HttpClientUtil.sendData(data,path);
		return result;
	}
	
	public static String useCharsetInterface(String data, String url,String labelName, String charset){
		//得到 路径
		String path = sc.getPath(labelName) + url;
		//调用接口
		String result = HttpClientUtil.sendData(data,path,charset);
		return result;
	}
	
	public static String useInterfaceXML(String data, String url, String labelName, String charset){
		//得到 路径
		String path = sc.getPath(labelName) + url;
		//调用接口
		String result = HttpClientUtil.sendDataXML(data,path,charset);
		return result;
	}
	
	/**
	 * 调用接口
	 * @param data  String参数
	 * @param url   路径
	 * @return
	 */
	public static String useStringInterface(String data,String url,String labelName){
		//得到 路径
		String path = sc.getPath(labelName) + url;
		//调用接口
		String result = HttpClientUtil.sendStringData(data, path);
		return result;
	}
	
	/**
	 * 调用接口
	 * @param data  实体类
	 * @param url   路径
	 * @return
	 */
	public static String useInterface(Object data,String url,String labelName){
		Gson gson = new Gson();
		//转换JSON
		String dateJson = gson.toJson(data);
		return useInterface(dateJson,url,labelName);
	}
	
	/**
	 * 调用接口
	 * @param data  实体类
	 * @param url   路径
	 * @return
	 */
	public static String useInterface(String url,String labelName){
		//得到 路径
		String path = sc.getPath(labelName) + url;
		return HttpClientUtil.sendData(path);
	}
	
	/**
	 * 调用接口
	 * @param data  实体类
	 * @param url   路径
	 * @return
	 */
	public static String useInterfaceViaGet(String url,String labelName){
		//得到 路径
		String path = sc.getPath(labelName) + url;
		return HttpClientUtil.sendDataHttpViaGet(path);
	}

	public static String GetMothod(String url,String labelName){
		//得到 路径
		String path = sc.getPath(labelName) + url;
		return HttpClientUtil.sendGetModelUrl(path);
	}
}
