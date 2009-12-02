package org.kobjects.kui;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

import org.kobjects.utils4me.Registry;
import org.kobjects.utils4me.Tools;

/**
 * KDisplay is a singleton class.
 * 
 * @author Stefan Haustein
 */
public final class KDisplay extends Canvas implements Runnable,
		KCommandListener {

	/** Array with debug strings that should be displayed on the device */
	private static String[] debugStrings;

	/** Normalized key code for the left soft key */
	public static final int KEYCODE_LSK = -6;

	/** Normalized key code for the right soft key */
	public static final int KEYCODE_RSK = -7;

	/** Normalized key code for the clear key */
	public static final int KEYCODE_CLEAR = -8;

	public static final int ICON_DOWN_ON = 14;
	public static final int ICON_UP_ON = 15;
	public static final int ICON_DOWN_OFF = 16;
	public static final int ICON_UP_OFF = 17;
	public static final int ICON_LEFT_RIGHT_ON = 13;
	public static final int ICON_LEFT_RIGHT_OFF = 14;
	public static final int ICON_ARROW_OFF = 4;
	public static final int ICON_ARROW_ON = 5;
	public static final int ICON_BULLET = 6;

	public static final int SYMBOL_COUNT = 15;
	public static final Image SYMBOLS = KDisplay.createImage("kui/symbols.png");
	public static final int SYMBOL_WIDTH = SYMBOLS.getWidth();
	public static final int SYMBOL_HEIGHT = SYMBOLS.getHeight()
			/ SYMBOL_COUNT;

	/** Array with all possible fonts for the KUI system */
	private final Font[] fonts = new Font[32];

	/** Array with pixel offset of all possible font in the KUI system */
	final int[] fontTopOffset = new int[32];

	/*
	 * public static final String[] DEFAULT_STYLES = {
	 * "bg:32,eeeeff;xc:7d147d;lf:8;tf:8", // 0
	 * "bg:32,eeeeff;xc:7d147d;lf:8;tf:8",
	 * "bg:32,aaaaff;cbg:32,aaaaff;tc:000000;lc:3c143c;lf:8;tf:8", "", // 1 2
	 * 3 "bg:32,eeeeff;xc:7d147d;lf:8;tf:8",
	 * "bg:32,aaaaff;cbg:32,ffffff;tc:000000;lc:3c143c;tf:9;lf:8", "", // 4 5
	 * 6 "lf:9;cbg:32,ccccff;cfr:5,0,1,ccccff;tc:000000;tf:8",
	 * "lf:9;cbg:32,aaaaff;cfr:5,0,1,aaaaff;tc:000000;tf:9", "", // 7 8 9
	 * "bg:32,ccccff;tc:000000;tf:9", "bg:32,ccccff;tc:000000;tf:9",
	 * "bg:32,ccccff;fr:1,1,1,7d147d",
	 * "bg:32,aaaaff;cbg:32,ffffff;tc:000000;lc:3c143c;tf:9;lf:8"// 10 11 12 };
	 */

	/**
	 * Array with all default styles.
	 */
	KStyle[] defaultStyles = {
			KStyle.ST_BG, // 0

			KStyle.ST_PLAIN, KStyle.ST_PLAIN_FOCUS,
			KStyle.ST_PLAIN_FOCUS, // 1, 2, 3
			KStyle.ST_LABEL, KStyle.ST_LABEL_FOCUS,
			KStyle.ST_LABEL_FOCUS, // 4, 5, 6

			KStyle.ST_INPUT, KStyle.ST_INPUT_FOCUS,
			KStyle.ST_INPUT_FOCUS, // 7, 8, 9
			KStyle.ST_LABEL, KStyle.ST_LABEL_FOCUS,
			KStyle.ST_LABEL_FOCUS, // 10, 11, 12

			KStyle.ST_BUTTON, KStyle.ST_BUTTON_FOCUS,
			KStyle.ST_BUTTON_FOCUS, // 13, 14, 15
			KStyle.ST_BUTTON_LABEL, KStyle.ST_BUTTON_LABEL,
			KStyle.ST_BUTTON_LABEL, // 16, 17, 18

			KStyle.ST_TITLE, // 19 TITLE
			KStyle.ST_TITLE, KStyle.ST_TITLE, KStyle.ST_TITLE, // 20, 21, 22 Softkeys

			KStyle.ST_POPUP, KStyle.ST_POPUP_FOCUS, KStyle.ST_POPUP_FOCUS, // 23, 24, 25 Popup
	};

	public static String PLATFORM = "j2me";
	public static String DELETE_SK = "Löschen";

	static int deviceLSK = -6;
	static int deviceRSK = -7;
	static int deviceClear = -8;

	private int stall = -1;

	/**
	 * Use native soft key implementation or not. SKerkewitz: the T-Mobile MDA
	 * and SDA work not in fullscreen for some reason and there soft key
	 * button send only events if there is a command attached to them. So in
	 * some very rare cases like the MDA/SDA we need to use the native JavaME
	 * softkeys implementation to make them work properly.
	 */
	boolean useNativeSoftkeys = false;

	/**
	 * The one and only instance of KDisplay. Do not access this variable
	 * directly from outside the KUI project. Use getDisplay() instead.
	 */
	static KDisplay kDisplay;

	/**
	 * The instance of the {@link Display}.
	 */
	public static Display midpDisplay;

	/**
	 * The current instance of KForm or any derived class that is on top of
	 * the display stack.
	 */
	private KForm currentForm;

	/** Time stamp of the last completed call of requestRepaint */
	private static long repaintRequest;

	/** Time stamp of the last repaint */
	private long lastRepaint;

	/**
	 * Set the relative progress of the current loading progress. The value is
	 * in range between 1..100. A value of -1 means that nothing is loaded
	 * right now.
	 */
	public int loadingProgress = -1;

	/** Image with the log of the main application. XXX: Move to Midlet?! */
	public Image imgAppLogo;

	// --- Variables for the popup waitscreenMessage subsystem ---

	/** The current popupMessage text or null if there is no popupMessage. */
	private WordWrap popupMessage;

	/** The y coordinate of the popup */
	private int popupY;

	private long popupTime;
	private long popupReleaseTime;

	/** Was a key pressed while a popup is visible? */
	private boolean isKeyPressedWhilePopup;

	static boolean siemensKeys;

	public static Form repaintTimeoutForm;

	private KForm alertNext;

	// --- 8 ball circle waitscreen animation */

	/** Number of circle/dots for the waiting screen animation */
	private final static int ANI_WAITSCREEN_NUM_CIRCLES = 8;

	/** The time in milliseconds between each animation step */
	private final static int ANI_WAITSCREEN_TIME_THRESHOLD = 90;

	/** X coordinates for all 8 circles */
	private final int[] aniWaitScreenPosX = //new int[ANI_WAITSCREEN_NUM_CIRCLES];
	{ 0, 707, 1000, 707, 0, -707, -1000, -707 };
	/** Y coordinates for all 8 circles */
	private final int[] aniWaitScreenPosY = //new int[ANI_WAITSCREEN_NUM_CIRCLES];
	{ 1000, 707, 0, -707, -1000, -707, 0, 707 };

	/**
	 * Array for the 8 RGB values in hex (without leading "0x" in the
	 * Registry.ini under the key "waitscreen.animation.colortable"
	 */
	private int[] aniWaitScreenRGBTable; //new int[ANI_WAITSCREEN_NUM_CIRCLES];

	/** Time stamp to calculate the difference between two animation steps */
	private long aniWaitScreenLastTime = Tools.NOTHING;

	/** Simple counter to keep the animation running */
	private int aniWaitScreenCurAnimationStep = 0;

	/** Amount of maximal bubbles to display. Depends on loading Progress */
	private int aniNumBubbles;

	// --- Image based waitscreen animation */

	/** The image for the image based wait screen animation */
	private Image imgWaitscreenAnimation;
	private int animationX;
	private int animationY;
	private int animationCount;

	/**
	 * Message displayed while waitscreen or null if no message should be
	 * displayed
	 */
	private WordWrap waitscreenMessage;

	/**
	 * Assume fullscreen mode although CanvasSwitch is not loadable (for SE P
	 * 900, 910)
	 */
	public boolean fakeFullscreen = false;

	/** True if the application run in full screen, else false */
	public boolean isFullscreenMode = true;

	public static int CANVAS_HEIGHT;

	public static int CANVAS_WIDTH;

	public static int CONTENT_WIDTH;

	/** 0 if commands are handled by the system, command font height otherwise */
	public static int COMMAND_HEIGHT;

	public static int BORDER = 4; // divable by 2

	/** True is the display is isWidescreen, else false */
	public boolean isWidescreen = false;

	/** Indicate if we run into a stall timeout or not */
	public static boolean bStallTimeout = false;

	/**
	 * Returns the display instance. The MIDlet parameter is included for
	 * LCDUI-Compatibility and possible future extensions, but currently
	 * ignored.
	 */
	public static KDisplay getDisplay(final MIDlet midlet) {

		//
		// If we already have an KDisplay instance then just return that instance
		if( kDisplay != null ) {
			return kDisplay;
		}

		//
		// Create a new KDisplay instance and do some init and setup stuff
		kDisplay = new KDisplay();
		midpDisplay = Display.getDisplay(midlet);
		kDisplay.setFullScreenMode(true);

		//
		// Get the platform from the system properties
		PLATFORM = Registry.get("Joca-J2ME-Platform",
				System.getProperty("microedition.platform"));
		PLATFORM = PLATFORM == null ? "j2me" : PLATFORM.trim().toLowerCase();

		//
		// Use native softkeys only on Intent JTE environments for now
		if( (PLATFORM.toLowerCase().compareTo("intent jte") == 0)
				|| PLATFORM.toLowerCase().startsWith("rim") ) {
			kDisplay.useNativeSoftkeys = true;
		}

		//
		// Start the internal service loop which keep most of the KDisplay stuff
		// alive
		new Thread(kDisplay).start();

		//
		// Replace all char of '_', '/', '&' and '?' by a simple '-'
		final StringBuffer buf = new StringBuffer();
		for( int i = 0; i < PLATFORM.length(); i++ ) {
			final char c = PLATFORM.charAt(i);
			buf.append("_/&? ".indexOf(c) == -1 ? c : '-');
		}
		PLATFORM = buf.toString();

		if( PLATFORM.startsWith("sonyericsson") ) {
			if( KDisplay.PLATFORM.indexOf("p9") != -1 ) {
				kDisplay.fakeFullscreen = true;
			}
		}
		else if( KDisplay.PLATFORM.equals("symbian-os") ) {
			kDisplay.fakeFullscreen = true;
		}

		DELETE_SK = Registry.get("delete-sk", DELETE_SK);
		if( DELETE_SK != null && DELETE_SK.length() == 0 ) {
			DELETE_SK = null;
		}

		kDisplay.setupWaitscreen();

		//
		// Check for the image of images based waiting screen
		if( kDisplay.imgWaitscreenAnimation == null ) {
			final String anim = Registry.get("animation", null);
			if( anim != null && !Tools.isEmptyString(anim) ) {
				final String[] animArr = Tools.split(anim, ',');
				kDisplay.imgWaitscreenAnimation = KDisplay.createImage("animation.png");
				kDisplay.animationX = Integer.parseInt(animArr[0]);
				kDisplay.animationY = Integer.parseInt(animArr[1]);
				kDisplay.animationCount = Integer.parseInt(animArr[2]);
			}
		}

		return kDisplay;
	}

	public void setupWaitscreen() {
		//
		// There are two default wait screen animations. A simple loadingProgress
		// based circle and a 8 circle animation. There have to be a key/value pair
		// in the registry.ini with 8 RGB (hex, without leading "0x") values to use
		// the 8 circle animation. So first check if the color table is already read
		// in else check if we can read it in. If we can't find it in there registry fail
		// back to the old simple circle animation.
		final String colorTable = Registry.get(
				"waitscreen.animation.colortable", null);
		if( colorTable != null ) {
			final String[] values = Tools.split(colorTable, ',');
			if( values.length == ANI_WAITSCREEN_NUM_CIRCLES ) {
				aniWaitScreenRGBTable = new int[ANI_WAITSCREEN_NUM_CIRCLES];
				for( int i = 0; i < ANI_WAITSCREEN_NUM_CIRCLES; i++ ) {

					try {
						aniWaitScreenRGBTable[i] = Integer.parseInt(
								values[i], 16);
					} catch (final NumberFormatException e) {
						// Assign a default value in cases we screw up
						aniWaitScreenRGBTable[i] = 0xa0a0a0;
					}
				}
			}
		}
		else {
			aniWaitScreenRGBTable = null;
		}
	}

	/**
	 * Creates an alert form
	 * 
	 * @param title
	 *            Title of the alert
	 * @param text
	 *            Alert text
	 * @param image
	 *            Image
	 * @return
	 */
	public static KForm createAlert(final String title, final String text,
			final Image image) {

		final KForm form = new KForm(title);

		if( text != null ) {
			form.append(text);
		}

		form.addCommand(KForm.DISMISS_COMMAND);
		form.setCommandListener(kDisplay);

		return form;
	}

	/**
	 * Sets the current form or a other class derived from {@link Displayable}.
	 * Returns the previous active {@link Displayable} object or null if there
	 * was no previous object.
	 * 
	 * Because of LCDUI implementation bugs, KUI tries aggressively to keep
	 * control over the screen. Please use this call also for native screens
	 * (instead of Display.setCurrent) in order to make KUI aware that it
	 * shall not try to take the screen back.
	 */
	public Object setCurrent(final Object scr) {

		alertNext = currentForm;
		loadingProgress = -1;

		//
		// Get a reference of the current displayable object.
		final Object prevItem = getCurrent();

		//
		// Remove the command listener and all old native commands if we should
		// use native softkeys implementation.
		if( useNativeSoftkeys && currentForm != null ) {

			final Vector cmds = currentForm.commands;
			setCommandListener(null);
			for( int i = 0; i < cmds.size(); i++ ) {
				final Command cmd = (Command) cmds.elementAt(i);
				removeCommand(cmd);
			}
		}

		//
		// If the new Displaybale object not the current one then notify the current one that
		// it will be no longer active
		if( currentForm != null && currentForm != scr ) {
			currentForm.displayStateChange(KForm.DISPLAY_STATE_DEACTIVE, scr);
		}

		//
		// We need to handle KForm or derived classes differently then native
		// displayable objects. KForm need some extra measures.
		if( scr instanceof KForm ) {

			//
			// Notify the Displayable that it will become active
			currentForm = (KForm) scr;
			currentForm.displayStateChange(KForm.DISPLAY_STATE_ACTIVE,
					prevItem);

			//
			// Set all new native commands and the command listener if we should
			// use native softkeys implementation.
			if( useNativeSoftkeys ) {

				final Vector cmds = currentForm.commands;
				for( int i = 0; i < cmds.size(); i++ ) {
					final Command cmd = (Command) cmds.elementAt(i);
					addCommand(cmd);
				}

				setCommandListener(currentForm);
			}

			//
			// If the is currently no item that has the focus then search for the first
			// element that is focus able but set the cursor to the first element on that
			// page.
			final KItem focusItem = currentForm.getFocussedItem();
			if( focusItem == null ) {

				final int h = currentForm.getHeight();
				for( int i = 0; i < currentForm.size(); i++ ) {
					if( currentForm.getY(i) > h
							|| currentForm.setFocusIndex(i, 0) ) {
						break;
					}
				}

				//
				// Reset the cursor line to the first element
				currentForm.cursorLine = 0;
			}

			requestRepaint();
		}
		else {
			//
			// null covered here!
			currentForm = null;
			midpDisplay.setCurrent((Displayable) scr);
			requestRepaint();
		}

		return prevItem;
	}

	/**
	 * Requests a repaint; internal method, called by KForm.repaint()
	 */
	static public void requestRepaint() {

		//
		// If there is currently no KForm attached we do nothing here.
		if( kDisplay == null || kDisplay.currentForm == null ) {
			return;
		}

		repaintRequest = 0;
		//		final long time = System.currentTimeMillis();
		//		if(repaintRequest == 0 || time - repaintRequest > 1000) {
		//			repaintRequest = time;
		//
		//			//
		//			// If KDisplay is not already the current displayable object then make
		//			// active
		//			if(midpDisplay.getCurrent() != kDisplay){
		//				midpDisplay.setCurrent(kDisplay);
		//			}
		//
		//			kDisplay.repaint();
		//		}
	}

	/**
	 * Grays out the current screen contents and displays a loadingProgress
	 * bar and the waitscreenMessage (optional) on top. Useful to signal
	 * internal processing to the user. Set the value to -1 in order to remove
	 * the loadingProgress display.
	 * 
	 * @param amount
	 *            loadingProgress value
	 * @param waitscreenMessage
	 *            short optional waitscreenMessage string
	 */
	public void setLoadingProgress(final int amount, final String strMsg_) {

		String strMsg = strMsg_;

		if( loadingProgress == -1 ) {

			if( strMsg == null ) {
				strMsg = Registry.get("progress.text", null);
			}

			if( strMsg != null ) {
				waitscreenMessage = new WordWrap(defaultStyles[0], strMsg,
						KDisplay.CONTENT_WIDTH * 4 / 5);
			}
			else {
				waitscreenMessage = null;
			}
		}
		else if( amount == -1 ) {
			waitscreenMessage = null;
		}

		loadingProgress = amount;
		requestRepaint();
	}

	/**
	 * Internal draw method. First render the current KForm object (if any),
	 * then the loading screen animation (if we load anything), then the
	 * popupMessage (if any) and finally the debug messages (if any).
	 */
	protected synchronized void paint(final Graphics graphics) {

		//#mdebug debug
		//@		//System.out.println("KDisplay::paint() enter method...");
		//#enddebug

		//
		// Reset the repaint flags and store the current time.
		repaintRequest = System.currentTimeMillis(); // 0;
		lastRepaint = System.currentTimeMillis();

		//
		// If there is currently a KForm instance then let it render into the graphics
		// context.
		if( currentForm != null ) {
			currentForm.paintAll(graphics);
		}

		//
		// If we currently loading something then render the waiting animation over
		// all other elements.
		if( loadingProgress != Tools.NOTHING ) {

			final int w = getWidth();
			final int h = getHeight();

			//
			// Calculate the delta time since the last draw screen rendering and
			// increment the animation step counter if necessary.
			long aniWaitScreenDeltaTime = 0;
			if( aniWaitScreenLastTime == Tools.NOTHING ) {
				aniWaitScreenLastTime = System.currentTimeMillis();
				aniNumBubbles = 1;
			}
			else {
				aniWaitScreenDeltaTime = System.currentTimeMillis()
						- aniWaitScreenLastTime;
				if( aniWaitScreenDeltaTime >= ANI_WAITSCREEN_TIME_THRESHOLD ) {
					//
					// Just got one step ahead in the animation cycle and adjust/reset the
					// animation timer
					aniWaitScreenCurAnimationStep++;

					//
					// Only add more bubble if the animation step is increased
					// by one also
					if( aniNumBubbles < ((loadingProgress
							* ANI_WAITSCREEN_NUM_CIRCLES / 100) % ANI_WAITSCREEN_NUM_CIRCLES) ) {
						aniNumBubbles++;
					}

					aniWaitScreenLastTime = System.currentTimeMillis()
							+ (aniWaitScreenDeltaTime - ANI_WAITSCREEN_TIME_THRESHOLD);
				}
			}

			graphics.setColor(0);

			for( int i = 2 * Math.max(w, h) /* + (stall & 3) */; i > 0; i -= 3 ) {
				graphics.drawLine(0, i, i, 0);
			}

			final int bc = defaultStyles[0].xc;
			int r = (bc >> 16) & 255;
			int g = (bc >> 8) & 255;
			int b = (bc) & 255;

			if( stall != -1 && imgWaitscreenAnimation == null ) {

				final int p = (stall >= 10 ? 20 - stall : stall);
				r += (255 - r) * p / 10;
				g += (255 - g) * p / 10;
				b += (255 - b) * p / 10;
			}

			graphics.setColor((r << 16) | (g << 8) | b);

			final int x0 = w / 2;
			final int y0 = h * 3 / 5;

			//			graphics.setColor(stall >= 360 ? 0x0ffffff : 0);
			//			graphics.fillArc(w/2-w/8-4, h/2-w/8-4, w/4+8, w/4+8, 0, 360);
			//			graphics.setColor(stall >= 360 ? 0 : 0x0ffffff);
			//			graphics.fillArc(w/2-w/8-4, h/2-w/8-4, w/4+8, w/4+8, 90, 90 + stall % 360);

			//
			// If there is no waiting screen waitscreenMessage and no image was found for the
			// image based animation waiting screen then do either the 8 circle or
			// the one circle loadingProgress waiting screen
			if( waitscreenMessage == null && imgWaitscreenAnimation == null ) {

				//
				// Depending on either we have a color table or not do one waiting
				// screen or the other
				if( aniWaitScreenRGBTable == null ) {
					//
					// Draw standard waiting animation without any messages. A single
					// circle depending on the loadingProgress state.
					graphics.drawArc(x0 - w / 8 + 1, y0 - w / 8 + 1,
							w / 4 - 2, w / 4 - 2, 0, 360);
					graphics.drawArc(x0 - w / 8 + 2, y0 - w / 8 + 2,
							w / 4 - 4, w / 4 - 4, 0, 360);
					graphics.fillArc(x0 - w / 8, y0 - w / 8, w / 4, w / 4,
							90, (loadingProgress * 360 / 100) % 360);
				}
				else {
					//
					// Calculate constant values first
					final int w2 = w / 2;
					final int h2 = h / 2;
					final int scale = Math.min(w2, h2) / 4;
					final int circleSize = Math.min(w, h) / 16;

					//
					// Draw all circle with the current animation color
					final int aniStep = aniWaitScreenCurAnimationStep;

					//
					// Calculate new offset in the RGB value array to keep depending
					// on the current animation step and the circle to draw
					final int start = (aniStep % ANI_WAITSCREEN_NUM_CIRCLES);
					for( int i = 0; i < ANI_WAITSCREEN_NUM_CIRCLES; i++ ) {

						//
						// Exit the loop if we reached the current number of bubbles
						if( i > aniNumBubbles ) {
							break;
						}

						//
						// Calculate new offset in the RGB value array to keep depending
						// on the current animation step and the circle to draw
						final int offset = (7 - start + i)
								% (ANI_WAITSCREEN_NUM_CIRCLES);

						final int posx = (((scale) * aniWaitScreenPosX[offset]) / 1000)
								+ w2 - (circleSize / 2);
						final int posy = (((scale) * aniWaitScreenPosY[offset]) / 1000)
								+ h2 - (circleSize / 2);

						graphics.setColor(aniWaitScreenRGBTable[i]);
						graphics.fillArc(posx, posy, circleSize, circleSize,
								0, 360);
					}
				}
			}
			else {
				graphics.fillRect(
						(KDisplay.CANVAS_WIDTH - KDisplay.CONTENT_WIDTH) / 2,
						KDisplay.CANVAS_HEIGHT - KDisplay.COMMAND_HEIGHT,
						loadingProgress * KDisplay.CONTENT_WIDTH / 100,
						KDisplay.COMMAND_HEIGHT);

				graphics.setColor(defaultStyles[0].xc);

				if( stall != -1 || waitscreenMessage != null ) {

					final WordWrap msg = waitscreenMessage;

					int areaW = imgAppLogo.getWidth() + 16;
					int areaH = imgAppLogo.getHeight() + 16;

					if( msg != null ) {
						areaH += msg.getHeight() + 8;
						areaW = Math.max(areaW, msg.fullWidth + 16);
					}

					final int areaX = (KDisplay.CANVAS_WIDTH - areaW) / 2;
					final int areaY = (KDisplay.CANVAS_HEIGHT - areaH) / 3;

					final int imgX = (KDisplay.CANVAS_WIDTH - imgAppLogo.getWidth()) / 2;
					final int imgY = areaY + 8;

					graphics.setColor(defaultStyles[0].color);
					graphics.drawRect(areaX, areaY, areaW, areaH); // Shadow-appearance
					graphics.setColor(0x0ffffff);
					graphics.fillRect(areaX, areaY, areaW, areaH);

					graphics.drawImage(imgAppLogo, imgX, imgY, Graphics.TOP
							| Graphics.LEFT);

					if( msg != null ) {
						final KStyle s = defaultStyles[0];
						KDisplay.applyStyle(s, graphics);
						msg.paint(graphics, areaX + 8, areaY + 16
								+ imgAppLogo.getHeight(), Graphics.HCENTER);
					}

					if( stall != -1 ) {
						final int count = stall * animationCount / 20;

						if( imgWaitscreenAnimation != null
								&& count % animationCount < animationCount - 1 ) {
							KDisplay.drawIcon(graphics,
									imgWaitscreenAnimation,
									imgX + animationX, imgY + animationY,
									count % animationCount,
									animationCount - 1, 1);
							//					g.drawChar("-\\|/".charAt((count++) % 4), getWidth()/2, y0, Graphics.HCENTER|Graphics.TOP);
						}
					}
				}
			}
		}
		else {
			//
			// If loadingProgress != -1 then there is no waiting screen animation running. In
			// this case we need to stop the animation timer. Just set it to -1 will
			// do the job. Also reset the animation step counter
			aniWaitScreenLastTime = Tools.NOTHING;
			aniWaitScreenCurAnimationStep = 0;
		}

		// --- End of waiting animation ---

		//
		// Check for a popupMessage. Use a local variable for thread safety
		final WordWrap popup = this.popupMessage;
		if( popup != null ) {

			//
			// If the form has any special style for the popupMessage then use them,
			// else fall back to the default styles.
			KStyle kStyle = null;
			if( currentForm != null ) {
				kStyle = currentForm.getStyle(12 * 2);
			}
			else {
				kStyle = defaultStyles[12 * 2];
			}

			//
			// Calculate how fullWidth a popup should be depending on the displays
			// content fullWidth
			final int popupWidth = KDisplay.CONTENT_WIDTH / 3 * 2;

			int px;
			final int ah = kStyle.align
					& (Graphics.LEFT | Graphics.RIGHT | Graphics.HCENTER);
			switch( ah ) {

			case Graphics.LEFT:
				px = 0;
				break;

			case Graphics.RIGHT:
				px = KDisplay.CONTENT_WIDTH - popupWidth;
				break;

			default:
				px = (KDisplay.CONTENT_WIDTH - popupWidth) / 2;
			}

			kStyle.fill(graphics, px, popupY, popupWidth, popup.getHeight()
					+ KDisplay.BORDER);
			kStyle.frame(graphics, px, popupY, popupWidth, popup.getHeight()
					+ KDisplay.BORDER);
			KDisplay.applyStyle(kStyle, graphics);

			popup.paint(graphics, px + KDisplay.BORDER, popupY, Graphics.TOP
					| ah);
		}

		//
		// If there are any debug messages then render the waitscreenMessage on top
		// of everything
		if( debugStrings != null ) {
			graphics.setColor(0x0ffffff);
			final Font font = Font.getFont(Font.FACE_SYSTEM,
					Font.STYLE_PLAIN, Font.SIZE_SMALL);
			graphics.setFont(font);

			int w = 0;
			for( int i = 0; i < debugStrings.length; i++ ) {
				if( debugStrings[i] != null ) {
					w = Math.max(w, font.stringWidth(debugStrings[i]));
				}
			}
			graphics.fillRect(getWidth() - w, 0, w, debugStrings.length
					* font.getHeight());
			graphics.setColor(0x0880000);
			graphics.setFont(font);
			for( int i = 0; i < debugStrings.length; i++ ) {
				if( debugStrings[i] != null ) {
					graphics.drawString(debugStrings[i], getWidth(), i
							* font.getHeight(), Graphics.TOP | Graphics.RIGHT);
				}
			}
		}

		//#mdebug debug
		//@		//System.out.println("KDisplay::paint() leave method...");
		//#enddebug

	}

	/**
	 * Normalizes device dependent key codes to the corresponding WTK values.
	 */
	private int normalizeKey(final int keyCode) {
		// first, normalize keycode
		if( keyCode >= 32 && keyCode <= 64 ) {
			return keyCode;
		}

		try {
			final int ga = getGameAction(keyCode);

			switch( ga ) {
			case Canvas.UP:
			case Canvas.DOWN:
			case Canvas.LEFT:
			case Canvas.RIGHT:
			case Canvas.FIRE:
				if( getKeyCode(ga) == keyCode || getKeyCode(ga) >= 48 ) {
					return ga;
				}
			}
		} catch (final Exception e) {
			// Ignore SE getGameAction exception...
		}

		// workaround for BlackBerry not using pressed state
		if( PLATFORM.toLowerCase().startsWith("rim") ) {
			switch( keyCode ) {
			case -8: // cursor pressed
				keyPressed(KEYCODE_RSK);
				return keyCode;

				//            case 27:
				//                keyPressed(KEYCODE_LSK);        // back
				//                return keyCode;

			}
		}

		if( keyCode == deviceLSK ) {
			return KEYCODE_LSK;
		}
		if( keyCode == deviceRSK ) {
			return KEYCODE_RSK;
		}
		if( keyCode == deviceClear ) {
			if( DELETE_SK != null ) {
				DELETE_SK = null;
				Registry.set("delete-sk", "", true);
				Registry.flush();
			}

			return KEYCODE_CLEAR;
		}

		// if the phone is newly recognized as motorola phone, re-initialize key table

		if( PLATFORM.equals("j2me") ) {
			switch( keyCode ) {
			case -21:
			case 21:
			case 22:
			case -22:
				Registry.set("Joca-J2ME-Platform", "motorola", true);
				// PLATFORM will be set below
			}
		}
		else if( PLATFORM.equals("jbed") ) {
			switch( keyCode ) {
			case -202:
			case -203:
			case -204:
				Registry.set("Joca-J2ME-Platform", "lg", true);
				// PLATFORM will be set below
			}
		}

		if( !PLATFORM.equals(Registry.get("J2ME-Platform", PLATFORM)) ) {
			PLATFORM = Registry.get("J2ME-Platform", null);
			KTextField.init();
		}

		switch( keyCode ) {

		case -1: // Siemens
			if( !siemensKeys ) {
				siemensKeys = true;
				KTextField.init();
			}

		case -21: // MOT
		case -6: // Nokia, SE, Samsung, WTK,...
		case 21: // MOT
		case -202: // JBed LG
		case -1101: // ME4SE
		case 57345: // SDA II
		case -11: // SE Back Key mapped to LSK...
			return KEYCODE_LSK;

		case -4: // siemens
			if( !siemensKeys ) {
				siemensKeys = true;
				KTextField.init();
			}
		case -2: // SE P 900
		case -7: // Nokia, SE, ...
		case -22: // MOT
		case 22: // MOT
		case -203: // JBed LG
		case -1103: // ME4SE
		case 57346: // SDA II
			return KEYCODE_RSK;

		case -5: // SE P 900, SF65
		case 10:
		case -1102: // FIX THIS IN ME4SE!!!
			return Canvas.FIRE;

		case 8:
		case 127:
		case -16:
		case -204: // JBed LG
		case -1008: // ME4SE
			if( DELETE_SK != null ) {
				DELETE_SK = null;
				Registry.set("delete-sk", "", true);
			}
			return KEYCODE_CLEAR;

		case 131:
			return Canvas.LEFT;

		case 132:
			return Canvas.RIGHT;

		default:
			return keyCode;
		}
	}

	protected void keyPressed(final int keyCode) {
		//    	System.out.println("Key code: "+keyCode+ " normalized: "+normalizeKey(keyCode));

		if( !isKeyPressedWhilePopup && popupReleaseTime > 0 ) {
			popupTime = popupReleaseTime + System.currentTimeMillis();
		}
		isKeyPressedWhilePopup = true;

		if( currentForm != null && loadingProgress == -1 ) {
			currentForm.keyEvent(normalizeKey(keyCode), KForm.PRESSED);
		}
	}

	protected void keyReleased(final int keyCode) {
		if( currentForm != null && loadingProgress == -1 ) {
			currentForm.keyEvent(normalizeKey(keyCode), KForm.RELEASED);
		}
	}

	protected void keyRepeated(final int keyCode) {
		if( currentForm != null && loadingProgress == -1 ) {
			currentForm.keyEvent(normalizeKey(keyCode), KForm.REPEATED);
		}
	}

	/**
	 * Returns the current form or {@link Displayable}.
	 * 
	 * @return the current {@link KForm} instance or {@link Displayable}
	 *         instance that is currently visible.
	 */
	public Object getCurrent() {
		return currentForm == null ? (Object) midpDisplay.getCurrent()
				: currentForm;
	}

	/**
	 * Displays a popupMessage for the given time
	 * 
	 * @param text
	 *            Popup text
	 * @param time
	 *            Display time in millisectonds
	 */

	public void popup(final String text, final int time) {
		popupMessage = new WordWrap(defaultStyles[12 * 2], text,
				KDisplay.CONTENT_WIDTH * 2 / 3 - 2 * KDisplay.BORDER);

		if( time >= 0 ) {
			popupTime = time + System.currentTimeMillis();
			popupReleaseTime = 0;
		}
		else {
			popupTime = -time + System.currentTimeMillis();
			popupReleaseTime = -time;
		}

		popupY = -popupMessage.getHeight();

		isKeyPressedWhilePopup = false;

		requestRepaint();
	}

	/**
	 * Internal implementation for timer handling and for screen updates,
	 * please do not use.
	 */
	public void run() {

		int lastPercent = -1;

		long changeTime = 0;
		long currentTime = 0;

		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		while( true ) {

			try {
				Thread.sleep(Math.max(20,
						40 - (System.currentTimeMillis() - currentTime)));
			} catch (final InterruptedException e) {
				//#mdebug fatal
				e.printStackTrace();
				//#enddebug
			}

			boolean bNeedRepaint = false;

			currentTime = System.currentTimeMillis();

			//
			// Repaint only if we have either a pending repaint request or if the
			// refresh time has come.
			if( repaintRequest == 0 || currentTime - repaintRequest > 4000
					&& currentTime - lastRepaint > 4000 ) {
				//
				//			if( repaintRequest != 0
				//					&& time- repaintRequest > 4000 && time - lastRepaint > 4000 ) {
				//				if(repaintTimeoutForm == null){
				//					repaintTimeoutForm = new Form("UI Refresh");
				//				}
				//				midpDisplay.setCurrent(repaintTimeoutForm);
				//System.out.println("Repaint Timeout: "+(time-lastRepaint));
				//				try {
				//					Thread.sleep(100);
				//				}
				//				catch (InterruptedException e) {
				////#mdebug fatal
				//					e.printStackTrace();
				////#enddebug
				//				}

				//
				// SKerkewitz: Only set it current if it's not current right now because
				// on some devices like Nokia 65 this force the Midlet in the foreground.
				// Also only do it if a KUI form is active, else all native form dont work
				// anymore.
				if( midpDisplay.getCurrent() != kDisplay
						&& currentForm != null ) {
					midpDisplay.setCurrent(kDisplay);
				}

				bNeedRepaint = true;
				//requestRepaint();
			}

			if( popupMessage != null ) {
				if( currentTime > popupTime && isKeyPressedWhilePopup ) {
					if( popupY > -popupMessage.getHeight() ) {
						popupY -= 10;
						requestRepaint();
					}
					else {
						popupMessage = null;
						requestRepaint();
					}
				}
				else if( popupY < 0 ) {
					popupY = Math.min(0, popupY + 10);
					requestRepaint();
				}
			}

			if( loadingProgress == -1 || loadingProgress != lastPercent ) {
				//
				// Either we are not in loading screen or at last there is some loading
				// progress. So just store the current state.
				changeTime = currentTime;
				lastPercent = loadingProgress;

				//
				// If we are not in loading mode and there is currently a KForm object
				// as current then just called the tick method so it can update it's
				// state.
				if( loadingProgress == -1 ) {
					final KForm cf = currentForm;
					if( cf != null ) {
						cf.tick();
					}
				}
			}
			else if( stall == -1 || stall == 19 ) {
				//
				// In this case we are in loading mode but there was no progress since
				// the last time.
				if( currentTime - changeTime > 4000 ) {
					stall = 0;
					requestRepaint();
				}
				else {
					stall = -1;
					requestRepaint();
				}
			}
			else {
				//#mdebug info
				//@				System.out.println("Stall : " + (currentTime - changeTime));
				//#enddebug

				//
				// If we stall for 30 seconds then it seems that we run into a problem.
				// Set the connection timeout flag so other subsystems can check
				// for this kind of problem
				if( (currentTime - changeTime) > 30000 ) {
					bStallTimeout = true;
				}

				stall++;
				requestRepaint();
			}

			if( bNeedRepaint ) {
				kDisplay.repaint();
				kDisplay.serviceRepaints();
			}
		}
	}

	/**
	 * Internal implementation for alert handling, please do not use.
	 */
	public void commandAction(final Command command, final Object source) {
		setCurrent(alertNext);
	}

	/**
	 * Returns the underlying LCUDI display instance.
	 */
	public Display getDisplay() {
		return midpDisplay;
	}

	/**
	 * Creates a new image for the given String from the local file space.
	 * 
	 * @param fileName
	 *            s String with the filename of the image resource.
	 * @return a {@link Image} instance or null if the image could not be
	 *         loaded.
	 */
	public static Image createImage(final String fileName) {
		Image res = null;
		try {
			res = Image.createImage(fileName.charAt(0) == '/' ? fileName
					: ("/" + fileName));
		} catch (final Exception ex) {
			//#mdebug fatal
			ex.printStackTrace();
			//#enddebug
		}
		return res;
	}

	public int getFontTopOffset(final int code) {
		getFont(code & 31);
		return fontTopOffset[code & 31];
	}

	public Font getFont(final int code_) {
		final int code = code_ & 31;

		final int style = code & 7;
		final int size = code & 24;

		if( KDisplay.CANVAS_HEIGHT == 0 ) {
			return Font.getFont(Font.FACE_SYSTEM, style, size);
		}

		if( fonts[code] == null ) {
			Font small = Font.getFont(Font.FACE_SYSTEM, style,
					Font.SIZE_SMALL);
			Font medium = Font.getFont(Font.FACE_SYSTEM, style,
					Font.SIZE_MEDIUM);
			Font large = Font.getFont(Font.FACE_SYSTEM, style,
					Font.SIZE_LARGE);

			if( small.getHeight() > medium.getHeight() ) {
				final Font swap = medium;
				medium = small;
				small = swap;
			}

			if( small.getHeight() > large.getHeight() ) {
				final Font swap = large;
				large = small;
				small = swap;
			}

			if( medium.getHeight() > large.getHeight() ) {
				final Font swap = large;
				large = medium;
				medium = swap;
			}

			if( small.getHeight() < KDisplay.CANVAS_HEIGHT / 20 ) {
				small = medium.getHeight() < KDisplay.CANVAS_HEIGHT / 20 ? medium
						: large;
			}

			if( medium.getHeight() < KDisplay.CANVAS_HEIGHT / 15 ) {
				medium = large;
			}

			fonts[style | Font.SIZE_SMALL] = small;
			fonts[style | Font.SIZE_MEDIUM] = medium;
			fonts[style | Font.SIZE_LARGE] = large;

			fontTopOffset[style | Font.SIZE_SMALL] = calcFontTopOffset(small);
			fontTopOffset[style | Font.SIZE_MEDIUM] = calcFontTopOffset(medium);
			fontTopOffset[style | Font.SIZE_LARGE] = calcFontTopOffset(large);

		}

		return fonts[code];
	}

	private int calcFontTopOffset(final Font font) {

		final int w = font.charWidth('H');
		final int h = font.getHeight();

		final Image img = Image.createImage(w, h);
		final Graphics g = img.getGraphics();
		g.setColor(0x0ffffff);
		g.fillRect(0, 0, w, h);
		g.setFont(font);
		g.setColor(0);

		g.drawChar('H', 0, 0, Graphics.LEFT | Graphics.TOP);

		final int[] rgb = new int[w];

		int y0 = 0;

		l1: while( y0 < h ) {
			img.getRGB(rgb, 0, w, 0, y0, w, 1);

			for( int x = 0; x < w; x++ ) {
				// System.out.print(Integer.toString(rgb[x], 16)+"/");
				if( (rgb[x] & 0x0f0f0f0) != 0x0f0f0f0 ) {
					break l1;
				}
			}
			// System.out.println();
			y0++;
		}

		int y1 = h - 1;
		l2: while( y1 > y0 ) {
			img.getRGB(rgb, 0, w, 0, y1, w, 1);

			for( int x = 0; x < w; x++ ) {
				// System.out.print(Integer.toString(rgb[x], 16)+"/");
				if( (rgb[x] & 0x0f0f0f0) != 0x0f0f0f0 ) {
					break l2;
				}
			}
			y1--;
			// System.out.println();
		}

		// System.out.println("font "+font+ " h:"+h+" y0:"+y0+" y1:"+y1+ "
		// delta: "+(h - (y0+y1))/2);
		return (h - (y0 + y1) - 1) / 2;

	}

	/**
	 * Determines whether the given Command is a "positive" (OK, ITEM, SCREEN)
	 * or "negative" (CANCEL, BACK, STOP, EXIT) command.
	 * 
	 * @param cmd
	 *            The command to be examined
	 * @return true if the command is a "positive" command.
	 */

	public static boolean isPositiveCommand(final Command cmd) {
		switch( cmd.getCommandType() ) {
		case Command.CANCEL:
		case Command.BACK:
		case Command.STOP:
		case Command.EXIT:
			return false;
		default:
			return true;
		}
	}

	public void setStyle(final int index, final KStyle style) {
		defaultStyles[index] = style;
	}

	public KStyle getStyle(final int index) {
		return defaultStyles[index];
	}

	public static void debug(final int i, final String text) {
		if( debugStrings == null || debugStrings.length <= i ) {
			final String[] cpy = new String[i + 1];
			if( debugStrings != null ) {
				System.arraycopy(debugStrings, 0, cpy, 0, debugStrings.length);
			}
			debugStrings = cpy;
		}
		debugStrings[i] = text;
		requestRepaint();
	}

	/**
	 * Calculate the canvas size and the screen size. Setup the CanvasHeight,
	 * CanvasWidth, ContentWidth and the Border field.
	 * 
	 * @param h
	 *            the height of the current form. Use -1 to force a re-layout.
	 * @return an integer with the new/current height of the canvas.
	 */
	public int doLayout(final int height) {
		int h = height;
		if( h != getHeight() ) {
			h = getHeight();

			if( h > KDisplay.CANVAS_HEIGHT || h < KDisplay.CANVAS_HEIGHT - 50 ) {
				KDisplay.CANVAS_HEIGHT = h;
			}

			KDisplay.CANVAS_WIDTH = getWidth();
			isWidescreen = getHeight() / 2 < getWidth() / 3;

			KDisplay.CONTENT_WIDTH = isWidescreen ? KDisplay.CANVAS_WIDTH / 2
					: KDisplay.CANVAS_WIDTH;

			KDisplay.BORDER = (KDisplay.CONTENT_WIDTH / 56) << 1;
		}

		return h;
	}

	/**
	 * Apply the font and foreground color settings of this style to the given
	 * {@link Graphics} context.
	 * 
	 * @param g
	 *            a valid instance of {@link Graphics}
	 * @return the font top offset.
	 */
	public static int applyStyle(final KStyle kStyle, final Graphics g) {
		g.setFont(kDisplay.getFont(kStyle.font));
		g.setColor(kStyle.color);
		return kDisplay.getFontTopOffset(kStyle.font);
	}

	public static void drawIcon(final Graphics g, final Image img,
			final int x, final int y, final int index, final int countX,
			final int countY) {
		final int w = img.getWidth() / countX;
		final int h = img.getHeight() / countY;
		final int cX = g.getClipX();
		final int cY = g.getClipY();
		final int cW = g.getClipWidth();
		final int cH = g.getClipHeight();

		final int ix = index % countX;
		final int iy = index / countX;

		g.clipRect(x, y, w, h);
		g.drawImage(img, x - ix * w, y - iy * h, Graphics.TOP | Graphics.LEFT);

		g.setClip(cX, cY, cW, cH);
	}

	protected void pointerDragged(final int x, final int y) {
		if( kDisplay != null && currentForm != null ) {
			currentForm.pointerEvent(x, y, KForm.DRAGGED);
		}
	}

	protected void pointerPressed(final int x, final int y) {
		if( kDisplay != null && currentForm != null ) {
			currentForm.pointerEvent(x, y, KForm.PRESSED);
		}
	}

	protected void pointerReleased(final int x, final int y) {
		if( kDisplay != null && currentForm != null ) {
			currentForm.pointerEvent(x, y, KForm.RELEASED);
		}
	}
}
