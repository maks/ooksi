package com.ooksi.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class Http implements Runnable {

	private Hashtable headers = new Hashtable();
	private int responseCode = 0;
	byte[] responseBody = new byte[0];
	boolean done = false;
	private String url;
	private String httpMethod;
	private HTTPResultListener callback;
	
	//methods
	public static String GET = "GET";
	public static String POST = "POST";
	public static String HEAD = "HEAD";
		
	
	/**
	 * Async HTTP fetch given url
	 */
	public static Http fetchUrl(String url, String httpMethod, HTTPResultListener callback) {
		Http http = new Http(url, httpMethod);
		http.callback = callback;
		Thread worker = new Thread(http);
		worker.start();
		return http;
	}
	
	private Http(String url, String httpMethod) {
		this.url = url;
		this.httpMethod = (httpMethod != null) ? httpMethod: this.GET;
	}
	
	public void run() {
		
		HttpConnection c = null;
		DataInputStream dis = null;

		try {
			c = (HttpConnection) Connector.open(this.url);

			c.setRequestMethod(this.httpMethod);

			// Enumeration keys = this.headers.keys();
			// while(keys.hasMoreElements()) {
			// Object key = keys.nextElement();
			// c.setRequestProperty((String)key, (String)headers.get(key));
			// }

			int len = (int) c.getLength();
			this.responseCode = c.getResponseCode();
			dis = c.openDataInputStream();
			if (len > 0) {
				responseBody = new byte[len];
				dis.readFully(responseBody);
			} else {
				throw new RuntimeException("zero content length in HTTP response stream");
			}
		} catch (IOException e) {
			// swallow exception for now
			e.printStackTrace();
		} finally {
			this.done = true;
			if (dis != null)
				try {
					dis.close();
					if (c != null) {
						c.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		this.callback.httpResult(this);
	}
	
	public String getResponseText() {
		StringBuffer resp = new StringBuffer();
		for(int i=0; i < this.responseBody.length; i++) {
			resp.append((char)this.responseBody[i]);
		}
		return resp.toString();
	}
	
	public byte[] getResponseBody() {
		return this.responseBody;
	}
	
	public int getResponseCode() {
		return this.responseCode;
	}
}
