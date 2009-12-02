package com.ooksi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;

import com.ooksi.net.HTTPResultListener;
import com.ooksi.net.Http;

public class AppData implements HTTPResultListener {

	//todo change to be from app proprty
	private static final String WEB_APP_URL = "http://localhost:8080/kala/";
	
	private int id;
	private String name;
	private String description;
	private boolean compiled;
	private Image icon;
	private String code;

	public AppData(String id, String name, String compiled) {
		try {
			this.id = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			System.out.println("invalid app id number:" + id);
		}
		this.compiled = ("c".equals(compiled)) ? true : false;
		this.name = name;
	}

	private void fetchCode() {
		String url = WEB_APP_URL+"apps/"+this.id+"/code";
		Http res = Http.fetchUrl(url, Http.GET, this);
		while(true) {
			synchronized (this) {
				if (this.code != null) {
					break;
				}					
			}
			try {
				System.out.println("sleep");
				Thread.sleep(1000);						
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String codeAsString() {
		if (this.code == null) {
			this.fetchCode();
		}
		System.out.println("ret code:" + this.code);
		return this.code;
	}

	public InputStream codeAsStream() {
		if (this.code == null) {
			this.fetchCode();
		}
		return new ByteArrayInputStream(this.code.getBytes());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public void httpResult(Http result) {
		System.out.println("got from http:" + result.getResponseText());
		synchronized (this) {
			this.code = result.getResponseText();
		}
	}

	public void progressUpdate(int progress) {
		// TODO Auto-generated method stub
		
	}
}

