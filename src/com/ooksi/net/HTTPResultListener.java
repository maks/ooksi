package com.ooksi.net;

public interface HTTPResultListener {

	public void httpResult(Http http);
	
	public void progressUpdate(int progress);
}
