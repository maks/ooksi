// Copyright 2008 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.ooksi;

import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDletStateChangeException;

import org.kobjects.kui.KDisplay;
import org.kobjects.kui.KItem;
import org.kobjects.kui.KList;

import com.google.minijoe.compiler.CompilerException;
import com.google.minijoe.compiler.Eval;
import com.google.minijoe.sys.JsFunction;

/**
 * Simple test midlet to run js stored locally within midlet jar
 * 
 * @author Maksim Lin
 */
public class Runner extends OoksiMIDlet implements CommandListener {

	static final Command CMD_EXIT = new Command("Exit", Command.EXIT, 1);

	static final String[] SAMPLES = 
		{ "alert.js", "clock.mjc", "drawImg.js", "functionplot.mjc",
			"xhrtest.js"};

	KList applicationList = new KList("JS Apps", List.IMPLICIT, SAMPLES, null);

	public Runner() {
		applicationList.addCommand(CMD_EXIT);
		//applicationList.setCommandListener(this);
	}

	protected void destroyApp(boolean unconditional)
			throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}

	public void startApp() throws MIDletStateChangeException {
		KDisplay.getDisplay(this).setCurrent(applicationList);
	}

	public void commandAction(Command cmd, Displayable d) {
		if (cmd == CMD_EXIT) {
			notifyDestroyed();
		} else {
//			String name = "/"
//					+ applicationList.getText(applicationList
//							.getSelectedIndex());
			MIDPEnvironment env = new MIDPEnvironment(this);
			env.screen.setFullScreenMode(true);

			//KDisplay.getDisplay(this).setCurrent(env.screen);

			
			//System.out.println("filename:" + name);
////			try {
////				if (name.endsWith(".mjc")) {
////					JsFunction.exec(new DataInputStream(getClass()
////						.getResourceAsStream(name)), env);
////					new Thread(env).start();
////				} else if (name.endsWith(".js")) {
////					String jsSrcText = MIDPUtils.readFileAsText(name);
////					System.out.println("running js:" + jsSrcText);
////					Eval.eval(jsSrcText, env);
////					new Thread(env).start();
////				}
//
//			} catch (IOException e) {
//				throw new RuntimeException(e.toString());
//			} catch (CompilerException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	public Object initStartDisplay() {
		return applicationList;
	}
}
