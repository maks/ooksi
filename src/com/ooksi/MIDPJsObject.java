package com.ooksi;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import com.google.minijoe.sys.JsArray;
import com.google.minijoe.sys.JsObject;

public class MIDPJsObject extends JsObject implements CommandListener {

	private static final int ID_SHOW_ALERT = 1001;
	private static final int ID_GET_RESOURCE = 1002;
	private static final int ID_GET_PROPERTY = 1003;
	private static final int ID_GET_SYS_PROPERTY = 1004;
	
	
	private static MIDlet midlet;
	
	static final JsObject MIDP_PROTOTYPE = new JsObject(
			JsObject.OBJECT_PROTOTYPE).addNative("alert", ID_SHOW_ALERT, 1)
			.addNative("getResource", ID_GET_RESOURCE, 0)
			.addNative("getSystemProperty", ID_GET_SYS_PROPERTY, 1);
			//.addVar("ui", new UI(midlet));
	
	public MIDPJsObject(MIDlet midlet) {
		super(MIDP_PROTOTYPE);
		MIDPJsObject.midlet = midlet;
	}

	public void evalNative(int id, JsArray stack, int sp, int parCount) {
		switch (id) {		

		case ID_GET_RESOURCE:
			InputStream is = getClass().getResourceAsStream((String)stack.getObject(sp+2));
			StringBuffer buffer = new StringBuffer();
			int ch;
			try {
				while ((ch = is.read()) != -1) {
					buffer.append((char)ch);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stack.setObject(sp, buffer);
			break;

		case ID_GET_PROPERTY:
			//load res via midlet app property
			String val = midlet.getAppProperty((String)stack.getObject(sp+2));
			stack.setObject(sp, val);
			break;
					
		case ID_GET_SYS_PROPERTY:
			//load res via midlet system property
			String prop = System.getProperty((String)stack.getObject(sp+2));
			stack.setObject(sp, prop);
			System.out.println("got sys prop:"+prop);
			break;			
			
		default:
			super.evalNative(id, stack, sp, parCount);
		}
	}

	public void commandAction(Command c, Displayable d) {
		// TODO Auto-generated method stub

	}

	
	
}