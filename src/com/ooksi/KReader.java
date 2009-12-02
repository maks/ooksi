package com.ooksi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.kobjects.kui.KCommandListener;
import org.kobjects.kui.KDisplay;
import org.kobjects.kui.KForm;
import org.kobjects.kui.KStringItem;
import org.kobjects.kui.KStyle;

public class KReader extends MIDlet implements KCommandListener {

	private Display display;
	private Command exitCmd;
	private Command showImageCmd;
	private Command playCmd;
	
	private Vector itemList;

	private KForm mainForm;
	
	
	protected void startApp() throws MIDletStateChangeException {
		display = Display.getDisplay(this);
		mainForm = new KForm("Google Reader App");

		KDisplay.getDisplay(this).setCurrent(mainForm);
		
		showImageCmd = new Command("Show Img", Command.SCREEN, 0);
		mainForm.addCommand(showImageCmd);
		
		playCmd = new Command("Play", Command.SCREEN, 0);
		mainForm.addCommand(playCmd);
		
		exitCmd = new Command("Exit", Command.EXIT, 0);
		mainForm.addCommand(exitCmd);
		
		mainForm.setCommandListener(this);				
	}

	
	private void destroyImpl() {
		try {
			destroyApp(false);
			notifyDestroyed();
		} catch (MIDletStateChangeException  mste) {
			System.err.println("err destorying midlet");
		}
	}

	private void showImage() {
		
		Image latestIcon = null;
		Image img = null;
		try {
			//latestIcon = Image.createImage("/com/manichord/kala/latest.png");
			img = Image.createImage("/com/manichord/kala/dilbert1.jpg");
			img = Image.createImage(img, 190, 0, 190, 174, Sprite.TRANS_NONE);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		KStringItem imgItem = new KStringItem(null, "img");
		imgItem.setImage(img);
		KStringItem latestItem = new KStringItem(null,"Latest");
		KStringItem subsItem = new KStringItem(null,"Subscriptions");
		KStringItem tagsItem = new KStringItem(null,"Tags");
		
		int[] border = {KStyle.FRAME_ROUNDED, 1, 2, 0x00ff0000};
		
		subsItem.setContentStyle(0, 
				new KStyle(
						0x0000FF, 
						Font.SIZE_LARGE | Font.STYLE_BOLD,
						border, KStyle.BG_WHITE,
						0x0000FF, Graphics.LEFT
				)
		);
		
		mainForm.append(imgItem);
		mainForm.append(subsItem);
		mainForm.append(tagsItem);
		
	}
	
	private void play() {
		
		String[] list = Manager.getSupportedContentTypes("file");
		InputStream is = getClass().getResourceAsStream("/com/manichord/kala/test.wav");
	    
	    
		for(int i=0; i < list.length;i++) {
			System.out.println("types:"+list[i]);
		}
		try {
			Player p = Manager.createPlayer("file:////com/manichord/kala/test.wav");
			//Player p = Manager.createPlayer(is, "audio/X-wav");
			p.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MediaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void commandAction(Command cmd, Object source) {
		System.out.println("cmd:"+cmd);
		if (cmd == exitCmd) {
			this.destroyImpl();
		}
		if (cmd == showImageCmd) {
			this.showImage();
		}
		if (cmd == playCmd) {
			this.play();
		}
	}
	
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub
		
	}

}
