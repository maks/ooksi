package org.kobjects.kui;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Screen;
import javax.microedition.lcdui.TextBox;

import org.kobjects.utils4me.DateFormatException;
import org.kobjects.utils4me.Tools;

/**
 * The root class for all input fields.
 * 
 * @date 06.02.2005
 * @author Stefan Haustein
 */
public class KTextField extends KItem implements CommandListener {

	private static Hashtable KEYS;

	public static final int MODE_ABC = 4;
	public static final int MODE_Abc = 5;
	public static final int MODE_abc = 6;
	public static final int MODE_123 = 7;

	public static final Character CHAR_0 = new Character('0');
	public static final Character CHAR_1 = new Character('1');
	public static final Character CHAR_HASH = new Character('#');
	public static final Character CHAR_STAR = new Character('*');

	private static final String[] DEFAULT_KEYS = { "+0", ".,-?!'@:;/()1",
			"abc2ä", "def3", "ghi4", "jkl5", "mno6ö", "pqrs7ß", "tuv8ü",
			"wxyz9" };

	private static final String[] RIM_7100_KEYS = { " .", "er", "tz", "uiü",
			"df", "gh", "jk", "cv", "bn", "m" };

	public static final int ANY = 0;
	public static final int EMAILADDR = 1;
	public static final int NUMERIC = 2;
	public static final int PHONENUMBER = 3;
	public static final int URL = 4;
	public static final int DATE = 256;

	public static final int FULLSCREEN = 16384;

	public static final int PASSWORD = 0x010000; // 65536
	public static final int CONSTRAINT_MASK = 0x01ff; // 65535

	private long lastKeyTime;

	// text and lines are mutal exclusive!
	private String text;
	private Vector lines;

	int selected;
	public int cursorX;
	protected int line0;
	protected int cursorY;
	protected int maxLen;
	protected int lastKey;
	protected boolean mayReplace;
	protected boolean fullScreen;

	protected boolean password;
	public int constraints;
	public int mode = MODE_Abc;
	protected int keyIndex = 0;

	protected boolean cursorOn;
	protected boolean charEntered;
	protected Screen lcduiScreen;

	static void init() {

		KEYS = new Hashtable();

		if( KDisplay.PLATFORM.startsWith("rim") ) {

			if( KDisplay.CANVAS_HEIGHT > KDisplay.CANVAS_WIDTH ) {
				for( int i = 0; i < 10; i++ ) {
					KEYS.put(new Character((char) (48 + i)), RIM_7100_KEYS[i]);
				}
				KEYS.put(CHAR_HASH, "\u000f");
				KEYS.put(new Character('q'), "qw");
				KEYS.put(new Character('a'), "asäß");
				KEYS.put(new Character('y'), "yx");
				KEYS.put(new Character('o'), "opö");
			}
		}
		else {
			KEYS.put(CHAR_STAR, "\u000f");
			KEYS.put(CHAR_HASH, " ");

			for( int i = 0; i < 10; i++ ) {
				KEYS.put(new Character((char) (48 + i)), DEFAULT_KEYS[i]);
			}

			if( KDisplay.PLATFORM.startsWith("mot") ) {
				KEYS.put(CHAR_1, ".?!,@'-_:;()&\"~10%$+*/\\[]=><#§");
				KEYS.put(CHAR_STAR, " ");
				//
				// SKerkewitz: Don't give key "0" the ability to switch thru
				// modes
				// because else we stuck in numeric mode
				// KEYS.put(CHAR_0, "\u000f");
				KEYS.put(CHAR_HASH, "\u000f");
			}
			else if( KDisplay.siemensKeys
					|| KDisplay.PLATFORM.startsWith("sie")
					|| KDisplay.PLATFORM.startsWith("benq") ) {
				if( KDisplay.DELETE_SK != null ) {
					KDisplay.DELETE_SK = "<C";
				}
				KEYS.put(CHAR_1, "\u00201");
				KEYS.put(CHAR_STAR, "*+.,?!1@'-_():;&/%#<=>\"$§");
				KEYS.put(CHAR_0, ".,?!'\"0+-()@/:_");
				KEYS.put(CHAR_HASH, "\u000f");
			}
			else if( KDisplay.PLATFORM.startsWith("nokia")
					|| KDisplay.PLATFORM.startsWith("lg") ) {
				KEYS.put(CHAR_1, ".,?!'\"1-()@/:");
				KEYS.put(CHAR_STAR, ".,?!1@'-_():;&/%*#+<=>\"$§");
				KEYS.put(CHAR_0, "\u00200");
				KEYS.put(CHAR_HASH, "\u000f");

				// if(regPlatform.equals("nokias40")){

				// }
				// else if(jPlatform.equals("nokias60")){
				// TODO: Set Thread count!!!
				// }
			}
		}
	}

	public KTextField(final Object label, final String text,
			final int maxLen, final int constraints) {

		super(label, (constraints & FULLSCREEN) == 0 ? KItem.TYPE_INPUT
				: KItem.TYPE_FULLSCREEN);

		// System.out.println("Constraints: "+constraints+" hex:
		// "+Integer.toHexString(constraints));

		fullScreen = (constraints & FULLSCREEN) != 0;

		// System.out.println("fullscreen: "+fullScreen);

		if( KEYS == null ) {
			init();
		}

		if( constraints == DATE ) {
			setDate(Tools.parseIsoDate(text));
		}
		else {
			this.text = text == null ? "" : text;
		}

		this.maxLen = maxLen;
		this.password = (constraints & PASSWORD) != 0;
		this.constraints = constraints & CONSTRAINT_MASK;

		switch( this.constraints ) {
		case NUMERIC:
		case DATE:
		case PHONENUMBER:
			mode = MODE_123;
			break;
		case URL:
		case EMAILADDR:
			mode = MODE_abc;
			break;
		default:
			mode = MODE_Abc;
		}
	}

	protected int getPrefContentHeight(final int contentW) {

		// System.out.println("doLayoutContent called!");

		// contentY = Math.max(contentY, KForm.ICONS_FLAT.getHeight());

		int contentH;

		if( fullScreen ) {
			contentH = ownerForm.y1 - ownerForm.y0 - KDisplay.BORDER * 2;
		}
		else {
			contentH = getContentStyle(getState()).getFont().getHeight();
		}

		text = getString();

		lines = new Vector();
		lines.addElement(text);

		text = null;

		return contentH;
	}

	public void paint(final Graphics g, final int contentW, final int contentH) {

		final int state = getState();
		final KStyle s = getContentStyle(state);

		final int lh = s.getFont().getHeight();

		if( state == STATE_FOCUSSED ) {

			int icon;
			switch( mode ) {
			case MODE_ABC:
				icon = 9;
				break;
			case MODE_Abc:
				icon = 10;
				break;
			case MODE_abc:
				icon = 11;
				break;
			default:
				icon = 12;
			}

			KDisplay.drawIcon(g, KDisplay.SYMBOLS, 0, fullScreen ? 0
					: (contentH - KDisplay.SYMBOL_HEIGHT) / 2, icon, 1,
					KDisplay.SYMBOL_COUNT);

			// g.setColor(0);
			// g.drawRect(contentX, contentY, 12, 12);

		}

		KDisplay.applyStyle(s, g);

		// long dt = System.currentTimeMillis() - lastKeyTime;
		mayReplace &= System.currentTimeMillis() - lastKeyTime < 1000;

		final int availableW = contentW - KDisplay.SYMBOL_WIDTH
				- KDisplay.BORDER;

		int x0 = KDisplay.SYMBOL_WIDTH + KDisplay.BORDER;
		int screenCursorX = x0;

		final int cx = g.getClipX();
		final int cy = g.getClipY();
		final int cw = g.getClipWidth();
		final int ch = g.getClipHeight();

		int y = 0;

		if( fullScreen ) {
			if( cursorY < line0 ) {
				line0 = cursorY;
			}
			if( cursorY >= line0 + contentH / lh ) {
				line0 = cursorY - (contentH / lh) / 2;
			}
		}

		final Font font = s.getFont();

		for( int i = line0; i <= lines.size(); i++ ) {
			String line = getLine(i);
			final int len = line.length();

			if( fullScreen
					&& len > 0
					&& font.substringWidth(line, 0,
							line.charAt(len - 1) <= ' ' ? len - 1 : len) > availableW ) {

				// System.out.println("line too long:
				// "+font.stringWidth(line)+ " max: "+availableW);

				final StringBuffer buf = new StringBuffer();

				int cursorPos = -1;
				for( int j = i; j < lines.size(); j++ ) {
					if( j == cursorY ) {
						cursorPos = buf.length() + cursorX;
					}
					buf.append(getLine(j));
				}

				lines.setSize(i);
				final Vector repl = new WordWrap(s, buf.toString(),
						availableW).extract();

				for( int j = 0; j < repl.size(); j++ ) {
					final String r = (String) repl.elementAt(j);
					if( cursorPos >= 0 ) {
						if( cursorPos <= r.length() ) {
							cursorX = cursorPos;
							cursorY = i + j;
							cursorPos = -1;
						}
						else {
							cursorPos -= r.length();
						}
					}
					setLine(i + j, r);
				}

				line = getLine(i);
			}

			if( password ) {
				final StringBuffer buf = new StringBuffer(line.length());
				// System.out.println("repl: "+mayReplace+" dt: "+dt+ "
				// buflen:"+buf.length()+" cx:"+cursorX+" state:"+state);
				while( buf.length() < line.length() ) {
					buf.append(mayReplace && buf.length() == cursorX - 1
							&& state == STATE_FOCUSSED ? line.charAt(buf.length())
							: '*');
				}
				line = buf.toString();
			}

			if( state == STATE_FOCUSSED && i == cursorY ) {

				if( cursorX > getLine(cursorY).length() ) {
					cursorX = getLine(cursorY).length();
				}

				screenCursorX += s.getFont().substringWidth(line, 0, cursorX);

				if( !fullScreen ) {
					boolean scrolled = false;
					while( screenCursorX > availableW ) {
						screenCursorX -= availableW * 2 / 3;
						x0 -= availableW * 2 / 3;
						scrolled = true;
					}

					int ofs = KDisplay.SYMBOL_WIDTH + KDisplay.BORDER;
					if( scrolled ) {
						KDisplay.drawIcon(g, KDisplay.SYMBOLS, ofs,
								(contentH - KDisplay.SYMBOL_HEIGHT) / 2,
								KDisplay.ICON_LEFT_RIGHT_ON * 2, 2,
								KDisplay.SYMBOL_COUNT);

						ofs += KDisplay.SYMBOL_WIDTH / 2;
					}

					g.setClip(ofs, 0, contentW - ofs, contentH);
				}
				// if(x0 != contentX){
				//
				//
				// }

				if( mayReplace && cursorX > 0 ) {
					final int dx = s.getFont().charWidth(
							line.charAt(cursorX - 1));
					g.drawLine(screenCursorX - dx, y + lh, screenCursorX, y
							+ lh);
				}
				else if( cursorOn ) {
					g.drawLine(screenCursorX, y, screenCursorX, y + lh);
				}

				if( line.length() == maxLen && !fullScreen ) {
					KDisplay.drawIcon(g, KDisplay.SYMBOLS, x0
							+ s.getFont().stringWidth(line),
							(contentH - KDisplay.SYMBOL_HEIGHT) / 2,
							KDisplay.ICON_BULLET, 1, KDisplay.SYMBOL_COUNT);
				}
			}

			s.drawString(g, line, x0, y);
			y += lh;

			if( y > cy + ch ) {
				break;
			}
		}

		g.setClip(cx, cy, cw, ch);
	}

	public String getLine(final int index) {
		return lines == null ? text
				: (index < lines.size() ? (String) lines.elementAt(index)
						: "");
	}

	public String getString() {
		if( lines == null ) {
			return text;
		}

		final StringBuffer buf = new StringBuffer();
		for( int i = 0; i < lines.size(); i++ ) {
			buf.append(getLine(i));
		}
		// System.out.println("Value: '"+buf+"'");
		return buf.toString();
	}

	public final void setString(final String text) {
		this.text = text;
		lines = null;
		invalidate();
	}

	public void setLine(final int index, final String line) {
		while( index >= lines.size() ) {
			lines.addElement("");
		}
		lines.setElementAt(line, index);
	}

	private boolean isLegalChar(final char c) {
		switch( constraints ) {
		case DATE:
			return c == '.' || (c >= '0' && c <= '9');
		case PHONENUMBER:
			if( "*+#".indexOf(c) != -1 )
				return true;
		case NUMERIC:
			return c >= '0' && c <= '9';
		default:
			return true;
		}
	}

	public void keyPressed(final int code) {
		keyEvent(code, KForm.PRESSED);
	}

	public void keyRepeated(final int code) {
		keyEvent(code, KForm.REPEATED);
	}

	void keyEvent(final int code_, final int event) {

		int code = code_;

		// System.out.println("Text field key press: "+code+"/"+event);

		if( code == Canvas.FIRE ) {
			// if(lcduiScreen == null){
			if( constraints != DATE ) {
				lcduiScreen = new TextBox("" + labelTextObj, getString(),
						maxLen, constraints | (password ? PASSWORD : 0));
			}
			else {
				final DateField df = new DateField(null, DateField.DATE);
				final Calendar c = getDate();
				if( c != null ) {
					df.setDate(c.getTime());
				}
				lcduiScreen = new Form("" + labelTextObj);
				((Form) lcduiScreen).append(df);
			}
			lcduiScreen.addCommand(new Command("OK", Command.OK, 0));
			lcduiScreen.setCommandListener(this);
			// }
			KDisplay.kDisplay.setCurrent(lcduiScreen);
		}

		int prevPos = cursorX;

		boolean digit = code >= '0' && code <= '9';

		final String line = getLine(cursorY);

		if( code >= 32 && (getLength() < maxLen || mayReplace || fullScreen) ) {
			if( (mode == MODE_123 && digit) || constraints == NUMERIC
					|| constraints == PHONENUMBER || constraints == DATE ) {

				mayReplace = false;

				if( event == KForm.REPEATED && constraints == PHONENUMBER
						&& cursorX == 0 && !line.startsWith("+") ) {
					code = '+';
				}
				else if( !digit && constraints == DATE ) {
					code = '.';
				}
				else if( !isLegalChar((char) code) ) {
					return;
				}
			}
			else {
				final long dt = System.currentTimeMillis() - lastKeyTime;

				final String keys = (String) KEYS.get(new Character(
						(char) code));

				if( keys != null ) {
					if( keys.length() == 1 ) {
						code = keys.charAt(0);
					}
					else if( event != KForm.REPEATED ) {

						mayReplace = true;

						if( dt < 1000 && code == lastKey ) {
							if( prevPos > 0 )
								prevPos--;
							keyIndex++;
							if( keyIndex >= keys.length() ) {
								keyIndex = 0;
							}
						}
						else {
							keyIndex = 0;
							if( getLength() >= maxLen ) {
								return;
							}
						}

						lastKey = code;

						code = keys.charAt(keyIndex);
					}

					if( code == 15 ) {
						switch( mode ) {
						case MODE_123:
							mode = MODE_abc;
							break;

						case MODE_abc:
							mode = MODE_Abc;
							break;

						case MODE_Abc:
							mode = MODE_ABC;
							charEntered = false;
							break;

						case MODE_ABC:
							mode = MODE_123;
							break;
						}
						KDisplay.requestRepaint();
						return;
					}

					if( mode == MODE_ABC
							|| (mode == MODE_Abc && (prevPos == 0 || "\"%/&'.!;-? ".indexOf(line.charAt(prevPos - 1)) != -1)) ) {
						code = Character.toUpperCase((char) code);
					}

					lastKeyTime += dt;
				}
			}

			setLine(cursorY, line.substring(0, prevPos) + ((char) code)
					+ line.substring(cursorX));
			// if(text.length() > maxLen){
			// text = text.substring(0, maxLen);
			// }

			cursorX = prevPos + 1;

			KDisplay.requestRepaint();
		}
		else {
			boolean replaceModeChanged = mayReplace;
			mayReplace = false;
			switch( code ) {
			case KDisplay.KEYCODE_CLEAR:
				if( cursorX > 0 ) {
					setLine(cursorY, line.substring(0, cursorX - 1)
							+ line.substring(cursorX));
					cursorX--;
					KDisplay.requestRepaint();
				}
				else if( cursorY > 0 ) {
					cursorX = getLine(cursorY - 1).length();
					setLine(cursorY - 1, getLine(cursorY - 1)
							+ getLine(cursorY));
					if( lines.size() > cursorY ) {
						lines.removeElementAt(cursorY);
					}
					cursorY--;
				}
				break;

			case Canvas.LEFT:
				if( cursorX > 0 ) {
					cursorX--;
					KDisplay.requestRepaint();
				}
				else if( cursorY > 0 ) {
					cursorY--;
					cursorX = getLine(cursorY).length();
					KDisplay.requestRepaint();
				}
				break;

			case Canvas.RIGHT:
				if( cursorX < getLine(cursorY).length() ) {
					cursorX++;
				}
				else if( fullScreen && cursorY < lines.size() - 1 ) {
					cursorY++;
					cursorX = 0;
				}
				else if( !replaceModeChanged && getLength() < maxLen ) {
					setLine(cursorY, getLine(cursorY) + " ");
					cursorX++;
				}
				KDisplay.requestRepaint();
				break;

			case Canvas.UP:
				if( fullScreen && cursorY > 0 ) {
					cursorY--;
					cursorX = Math.min(cursorX, getLine(cursorY).length());
					KDisplay.requestRepaint();
				}
				break;

			case Canvas.DOWN:
				if( fullScreen && cursorY < lines.size() ) {
					cursorY++;
					cursorX = Math.min(cursorX, getLine(cursorY).length());
					KDisplay.requestRepaint();
				}
				break;
			}
		}
	}

	private int getLength() {

		if( text != null ) {
			return text.length();
		}

		int len = 0;
		for( int i = 0; i < lines.size(); i++ ) {
			len += getLine(i).length();
		}
		return len;
	}

	public Calendar getDate() {
		final Calendar c = Calendar.getInstance();

		/*
		 * Get the current String from this control. If the String is null or
		 * empty then we just return null.
		 */
		text = getString();
		if( text == null || Tools.isEmptyString(text) ) {
			return null;
		}

		int year = -1;
		int month = -1;
		int day = -1;

		/* Try to create and setup up a calendar object for the date. */
		int pos = 0;
		for( int i = 0; i < 3; i++ ) {
			final int cut = i == 2 ? text.length() : text.indexOf('.', pos);
			try {
				final int val = Integer.parseInt(text.substring(pos, cut).trim());
				switch( i ) {
				case 0:
					//c.set(Calendar.DAY_OF_MONTH, val);
					day = val;
					break;
				case 1:
					//c.set(Calendar.MONTH, val - 1);
					month = val;
					break;
				case 2:
					//c.set(Calendar.YEAR, val < 100 ? 2000 + val : val);
					year = val < 100 ? 2000 + val : val;
					break;
				}
			} catch (final Exception e) {
				//#mdebug fatal
				e.printStackTrace();
				//#enddebug

				/* Catch that exception and throw a RuntimeException instead. */
				throw new DateFormatException(getLabel()
						+ " contains not a valid date. (" + text + ")");
			}
			pos = cut + 1;
		}

		/* Make sure the date is valid. */
		Tools.checkDate(day, month, year);

		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.YEAR, year);

		return c;
	}

	/**
	 * @param calendar
	 */
	public final void setDate(final Calendar calendar) {

		setString(calendar.get(Calendar.DAY_OF_MONTH) + "."
				+ (calendar.get(Calendar.MONTH) + 1) + '.'
				+ calendar.get(Calendar.YEAR));
	}

	public boolean traverse(final int dir, final int w, final int h,
			final int[] inout) {

		if( getState() == STATE_UNFOCUSSED ) {
			return true;
		}

		if( itemType != TYPE_FULLSCREEN ) {
			return false;
		}

		keyPressed(dir);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
	 *      javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(final Command cmd, final Displayable d) {
		if( lcduiScreen instanceof TextBox ) {
			setString(((TextBox) lcduiScreen).getString());
		}
		else {
			final DateField df = (DateField) ((Form) lcduiScreen).get(0);
			final Calendar c = Calendar.getInstance();
			c.setTime(df.getDate());
			setDate(c);
		}
		KDisplay.kDisplay.setCurrent(ownerForm);
	}

}
