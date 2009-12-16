package com.ooksi.util;

import com.google.minijoe.sys.JsObject;
import com.google.minijoe.sys.JsObjectFactory;
import com.ooksi.MIDPEnvironment;
import com.ooksi.ui.ImageObject;
import com.ooksi.xhr.XMLHttpRequestObject;

public class OoksiJsFactory implements JsObjectFactory {
	
	private static OoksiJsFactory factory;
	
	
	//Kala Js Native Object types
	public static final int KALA_XHR_TYPE = 1;
	public static final int KALA_IMAGE_TYPE = 2;
	
	private MIDPEnvironment environment;
	
	/**
	 * Initialises the factory singleton, or nothing if it already exists.
	 * 
	 * @param callbackContext  JsObject with context to use for callbacks by any 
	 * objects created by the factory
 	 * @param eventLock	 Object used as lock for callback to synchronise on
	 */
	public static OoksiJsFactory getFactory(MIDPEnvironment env) {
		if (factory == null) {
			factory = new OoksiJsFactory();
			factory.environment = env;;
		}
		return factory;
	}

	public JsObject newInstance(int type) {
		switch(type){
	      case KALA_XHR_TYPE: 
	    	  return new XMLHttpRequestObject(environment.getCallbackScope(), 
	    			  this);
	      case KALA_IMAGE_TYPE:
	    	  return new ImageObject(environment.getCallbackScope(), 
	    			  this,
	    			  environment.getCanvas());
	      default:
	        throw new IllegalArgumentException();
	    }
	}

}
