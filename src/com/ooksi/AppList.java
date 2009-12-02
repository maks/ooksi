package com.ooksi;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Image;

import org.kobjects.kui.KList;

import com.google.minijoe.sys.JsArray;
import com.ooksi.net.HTTPResultListener;
import com.ooksi.net.Http;

public class AppList extends Thread implements HTTPResultListener {
	
	static final String WEB_APP_URL = "http://localhost:8080/kala/";
	static final String APPS_LIST_URL = WEB_APP_URL + "list";

	final Vector apps = new Vector();

	KList applicationList;
	
	public AppList(KList listUI) {
		applicationList = listUI;
		this.start();
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
			
			try {
				Image icon = Image.createImage("/orca.png");
				this.applicationList.append(app.getName(), icon /*app.getIcon()*/);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}

	public void progressUpdate(int progress) {
		// TODO Auto-generated method stub
		
	}
	
	private void parseAppList(String list) {
		JsArray apps = MIDPUtils.split(list, "\n", list.length());
		for (int i = 0; i < apps.size(); i++) {
			JsArray toks = MIDPUtils.split(apps.getString(i), ",", 0);
			this.apps.addElement(new AppData(toks.getString(0), toks
					.getString(1), toks.getString(2)));
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
