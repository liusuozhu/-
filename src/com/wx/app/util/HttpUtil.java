package com.wx.app.util;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;

public class HttpUtil {

	private HttpUtil(){}
	
	public static String httpsGet(String url){
		HttpClient client = new HttpClient();
		Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
		Protocol.registerProtocol("https", myhttps);
		GetMethod getMethod = new GetMethod(url);
		getMethod.setFollowRedirects(true);
		getMethod.addRequestHeader("Content-Type","text/html;charset=UTF-8"); 
		HttpMethodParams params = new HttpMethodParams();
		params.setContentCharset("UTF-8");
		getMethod.setParams(params);
		try {
			client.executeMethod(getMethod);
			return getMethod.getResponseBodyAsString();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
