package com.ooksi;

import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.kobjects.kui.KDisplay;
import org.kobjects.kui.KForm;
import org.kobjects.kui.KStringItem;

public class KuiTester extends MIDlet {

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		KForm mainForm = new KForm("Test KUI");
		KStringItem label = new KStringItem(null, null);
		
		HttpConnection c = null;
		DataInputStream dis = null;
		byte[] data = null;

		try {
			c = (HttpConnection) Connector.open("http://localhost:8080/pics/latest.png");
			int len = (int) c.getLength();
			dis = c.openDataInputStream();
			if (len > 0) {
				data = new byte[len];
				dis.readFully(data);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		Image img = null;
		try {
			//img = Image.createImage("/com/manichord/kala/latest.png");
			img = Image.createImage(data, 0, data.length);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		label.setImage(img);
		mainForm.append(label);
		KDisplay.getDisplay(this).setCurrent(mainForm);
	}
}
