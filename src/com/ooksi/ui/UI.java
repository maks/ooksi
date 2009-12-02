package com.ooksi.ui;

import java.util.Hashtable;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import org.kobjects.kui.KCommandListener;
import org.kobjects.kui.KDisplay;
import org.kobjects.kui.KForm;
import org.kobjects.kui.KStringItem;

import com.google.minijoe.sys.JsArray;
import com.google.minijoe.sys.JsFunction;
import com.google.minijoe.sys.JsObject;

public class UI extends JsObject implements KCommandListener {

	private static UI singleton;
	
	private static final int ID_SHOW_ALERT = 2001;
	private static final int ID_ADD_ITEM = 2002;
	private static final int ID_NEW_PAGE = 2003;
	private static final int ID_ADD_IMAGE = 2004;
	private static final int ID_SET_TITLE = 2005;
	private static final int ID_ADD_COMMAND = 2006;
	
	static final JsObject MIDP_PROTOTYPE = 
		new JsObject(JsObject.OBJECT_PROTOTYPE);

	private static final int ID_POPUP_MESSAGE = 0;

		
	private MIDlet midlet;
	private KForm mainForm;
	
	private Hashtable cmdFunctions = new Hashtable();

	private JsObject callBackScope;
	
	public UI(MIDlet midlet, JsObject scope) {
		super(MIDP_PROTOTYPE);
		this.midlet = midlet;
		this.callBackScope = scope;
			
		addNative("alert", ID_SHOW_ALERT, 1);
		addNative("newPage", ID_NEW_PAGE, 1);
		addNative("addItem", ID_ADD_ITEM, 1);
		addNative("addImage", ID_ADD_IMAGE, 1);
		addNative("setTitle", ID_SET_TITLE, 1);
		addNative("addCmd", ID_ADD_COMMAND, 1);
		
	}

	public void evalNative(int index, JsArray stack, int sp, int parCount) {
		switch (index) {
		case ID_SHOW_ALERT:
			this.alert(stack.getString(sp + 2));
			break;
		case ID_NEW_PAGE:
			mainForm = new KForm(stack.getString(sp + 2));
			KDisplay.getDisplay(midlet).setCurrent(mainForm);
			mainForm.setCommandListener(this);
			stack.setObject(sp, this);
		break;	
		case ID_ADD_ITEM:		
			KStringItem item;
			if (stack.getObject(sp+3) != null) {
				System.out.println("labelled item");
				item = new KStringItem(stack.getString(sp+3), 
						stack.getString(sp+2));
			} else {
				item = new KStringItem(null, stack.getString(sp+2));
			}
			if (stack.getObject(sp+4) != null) {
				item.setImage(((ImageObject)stack.getObject(sp+4)).getMidpImage());			
			}
			
			mainForm.append(item);
		break;
		case ID_ADD_IMAGE:
			KStringItem imgItem = new KStringItem(null, null);
			imgItem.setImage(((ImageObject)stack.getObject(sp+2)).getMidpImage());
			mainForm.append(imgItem);
		break;
		case ID_SET_TITLE:
			mainForm.setTitle(stack.getString(sp + 2));
		break;
		case ID_ADD_COMMAND:
			JsObject obj = (JsObject)stack.getObject(sp + 2);
			
			String cmdLabel = obj.getString("label");
			JsFunction func = (JsFunction) obj.getObject("cmd");
			Command cmd = new Command(cmdLabel, Command.SCREEN, 0);
			cmdFunctions.put(cmd, func);
			mainForm.addCommand(cmd);
		break;
		case ID_POPUP_MESSAGE:
			this.kuiPopup(stack.getString(sp + 2));
		break;	
		
		default:
			super.evalNative(index, stack, sp, parCount);
		}
	}

	private void kuiPopup(String text) {
		KDisplay.getDisplay(midlet).popup(text, 3 * 1000);
		
	}

	public void alert(String text) {
		//this.midpAlert(text, AlertType.INFO);
		this.kuiAlert(text);
	}
	
	public void prompt(String text) {
		//todo
	}
	
	private void kuiAlert(String text) {
		KForm kalert = KDisplay.getDisplay(midlet).
			createAlert("alert title", text, null);
		KDisplay.getDisplay(midlet).setCurrent(kalert);
	}
	
	public static UI getUI(MIDlet midlet, JsObject scope) {
		if (singleton == null) {
			singleton = new UI(midlet, scope);
		}
		return singleton; 
	}

	public void commandAction(Command command, Object source) {
		JsFunction callback = (JsFunction) this.cmdFunctions.get(command);
		
		synchronized (this.midlet) {
			JsArray callBackStack = new JsArray();
			callBackStack.setObject(0, this.callBackScope);
			callBackStack.setObject(1, callback);

			callback.eval(callBackStack, 0, 1);
		}
		
	}
}
