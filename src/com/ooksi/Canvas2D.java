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

import com.google.minijoe.sys.JsFunction;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Simple canvas implementation for the MiniJoe runtime demo.
 * 
 * @author Stefan Haustein
 */
public class Canvas2D extends Canvas {

	Image canvas;
	MIDPEnvironment env;

	public Canvas2D(MIDPEnvironment env) {
		this.env = env;
		canvas = Image.createImage(getWidth(), getHeight());
	}

	/** Draws the canvas image. */
	public void paint(Graphics g) {
		synchronized (env.getEventLock()) {
			System.out.println("ooksi canvas2d doing paint");
			g.drawImage(canvas, 0, 0, Graphics.TOP | Graphics.LEFT);
		}
	}

	public Image getCanvas() {
		return canvas;
	}

	/**
	 * If the key is a "typical" softkey code, the application is terminated.
	 * Otherwise, the key is forwarded to the JS onkeydown handler, if set.
	 */
	public void keyPressed(int code) {
		if (code == -6 || code == -7) {
			// soft key returns to start screen
			env.exit(null);
		} else {
			Object o = env.getObject("onkeydown");
			if (o instanceof JsFunction) {
				env.keyEvent((JsFunction) o, code, getGameAction(code));
			}
		}
	}

	/**
	 * Forwards the key event to the JS onkeyup handler, if set.
	 */
	public void keyReleased(int code) {
		Object o = env.getObject("onkeyup");
		if (o instanceof JsFunction) {
			env.keyEvent((JsFunction) o, code, getGameAction(code));
		}
	}
}
