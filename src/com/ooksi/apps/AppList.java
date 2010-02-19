package com.ooksi.apps;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Image;

import com.google.minijoe.sys.JsArray;
import com.ooksi.MIDPUtils;
import com.ooksi.net.HTTPResultListener;
import com.ooksi.net.Http;

public class AppList extends Thread implements HTTPResultListener {
	
	static final String WEB_APP_URL = "http://localhost:8080/kala/";
	static final String APPS_LIST_URL = WEB_APP_URL + "list";
	
	final Hashtable installedApps = new Hashtable();
	Hashtable appStoreListCache = new Hashtable();
	
	public AppList() {
		this.start(); //fetch list on new thread
	}
	
	public void run() {
		System.out.println("starting app list fetch");
		Http.fetchUrl(APPS_LIST_URL, Http.GET, this);
	}

	public void httpResult(Http result) {
		System.out.println("got from http:" + result.getResponseText());
		this.parseAppList(result.getResponseText());
		
		Enumeration apps = this.apps.elements();
		while (apps.hasMoreElements()) {
			AppData app = (AppData) apps.nextElement();
			
			//try {
				//TODO
				//Image icon = Image.createImage("/orca.png");
				//TODO - need to have custom item or img+text instead on list
			//} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}			
		}
	}

	public void progressUpdate(int progress) {
		// TODO Auto-generated method stub
		
	}
	
	private void parseAppList(String list) {
		AppData currApp;
		JsArray apps = MIDPUtils.split(list, "\n", list.length());
		for (int i = 0; i < apps.size(); i++) {
			currApp = AppData.parseDataString(apps.getString(i));
			this.appStoreListCache.put(new Integer(currApp.getId()), currApp);
		}
	}

	private String[] names() {
		String[] result = new String[this.apps.size()];
		Enumeration apps = this.apps.elements();
		int i = 0;
		while (apps.hasMoreElements()) {
			result[i] = ((AppData) apps.nextElement()).getName();
			i++;
		}
		return result;
	}
}
