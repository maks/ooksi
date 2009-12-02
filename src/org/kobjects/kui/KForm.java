/*
 * Created on 31.01.2005 by Stefan Haustein
 */
package org.kobjects.kui;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.kobjects.utils4me.Registry;

/**
 * A KForm is the KUI replacement of the native {@link Form} object that
 * contains an arbitrary mixture of items: images, read-only text fields,
 * editable text fields, editable date fields, gauges, choice groups, and
 * custom items.
 * <p>
 * In general, any subclass of the {@link KItem} class may be contained within
 * a form. The implementation handles layout, traversal, and scrolling. The
 * entire contents of the {@link KForm} scrolls together.
 * 
 * 
 * 
 * @author Stefan Haustein
 * 
 * TODO: Setting h to -1 to force a relayout may make problems if set while
 * painting(??)
 */
public class KForm implements CommandListener {

	// clean public static members

	/** indicate that a KForm become the active displayable form */
	public static final int DISPLAY_STATE_ACTIVE = 1;

	/** indicate that a KForm is no longer the active displayable form */
	public static final int DISPLAY_STATE_DEACTIVE = 2;

	/** Dismiss command (for alerts, see KDisplay.createAlert) */

	public static final Command DISMISS_COMMAND = new Command("Ok",
			Command.OK, 0);

	/** Select command for Lists */

	public static final Command SELECT_COMMAND = new Command("Select",
			Command.ITEM, 0);

	/** Internal constant for pointer/key press events */
	static final int PRESSED = 1;

	/** Internal constant for key repeated events */
	static final int REPEATED = 2;

	/** Internal constant for pointer dragged events */
	static final int DRAGGED = 3;

	/** Internal constant for key/pointer released events */
	static final int RELEASED = 4;

	/** current focus index in the option menu popup. -1 means not active. */
	private int commandPopupIndex = -1;

	/** determines whether the current item supports internal traversal */
	private boolean internalTraversal;

	/** Form title */
	public String title;

	/** Additional title element (e.g. timeout counter) */
	private String topRight;

	/**
	 * Internal Vector of KItems contained in this KForm. Initial capacity is
	 * 15, double each time we need to resize.
	 */
	private final Vector items = new Vector(15, 0);

	/** Current focus index. -1 means no item focussed */
	private int focusIndex = -1;

	/** Internal vector of commands */
	public Vector commands = new Vector();

	/** Internal pointer to the registered command listener */
	protected KCommandListener commandListener;

	/**
	 * Command offset (1 if there is a delete LSK or the first command is not
	 * negative; 0 otherwise.
	 */
	private int cmdOffset;

	/** y-coordinate of the upper left corner of the item area of this KForm */
	int y0;

	/** y-coordinate of the lower right corner of the item area of this KForm */
	int y1;

	/** x-coordinate of the upper left corner of the item area of this KForm */
	private int x0;

	/** x-coordinate of the lower right corner of the item area of this KForm */
	private int x1;

	/** Content height; -1 indicates that a relayout is necessary. */
	protected int h = -1;

	/** Center content vertically */
	public boolean bCenterVertical = true;

	/** First visible line (item index) */
	private int line0;

	/** need to display an up arrow (line0 > 0)? */
	private boolean upArrow;

	/** display a down arrow? */
	private boolean downArrow;

	/** The pixel offset of the current item (the first visible pixel) */
	private int offset;

	/** The line that is the cursor position. */
	int cursorLine;

	/** Used internally in connection with center. */
	private int startY;

	/** KStyle array (if different from the KDisplay styles) */
	private KStyle[] styles;

	/** Background image */
	private Image bgImage;

	/** Icon, displayed in the top left corner */
	private Image icon;

	/**
	 * Creates an empty KForm with the given title. If null, no title bar is
	 * drawn.
	 * 
	 * @param title
	 *            Title of the form
	 */

	public KForm(final String stitle) {
		this.title = stitle;
	}

	/** Shortcut for append(new KStringItem(null, text)); */

	public void append(final String text) {
		append(new KStringItem(null, text));
	}

	/** Append the given item at the end of this KForm. */

	public void append(final KItem item) {
		insert(items.size(), item);
	}

	/**
	 * Performs the item layout if necessary
	 */
	private void doLayout() {

		//
		// Re-layout the display is necessary.
		final KDisplay kDisplay = getKDisplay();
		h = kDisplay.doLayout(h);

		if( kDisplay.isWidescreen ) {
			x0 = kDisplay.getWidth() / 4;
			x1 = x0 * 3;
			y0 = 0;
			y1 = h;
		}
		else {
			x0 = 0;
			x1 = kDisplay.getWidth();

			final KStyle tfs = getStyle(KStyle.STYLE_TITLE);
			Font f = kDisplay.getFont(tfs.font);
			final int fy = kDisplay.getFontTopOffset(tfs.font);

			y0 = title == null ? 0 : (f.getHeight() + fy);

			if( icon != null ) {
				//
				// Because there can be an Icon a text in a single row we need to check
				// which height is the max height and use this one.
				y0 = Math.max(y0, icon.getHeight());
			}

			y1 = h;

			if( kDisplay.isFullscreenMode && !kDisplay.useNativeSoftkeys ) {
				f = kDisplay.getFont(getStyle(KStyle.STYLE_COMMAND).font);
				KDisplay.COMMAND_HEIGHT = Math.max(16 + 1, f.getHeight() + fy);
				y1 -= KDisplay.COMMAND_HEIGHT;
			}
		}
	}

	/**
	 * Returns the style with the given identification. If no form specific
	 * style is set, the corresponding KDisplay style is returned.
	 * 
	 * @see KDisplay.getStyle(int)
	 */
	public KStyle getStyle(final int index) {
		return (styles == null || styles[index] == null) ? getKDisplay().defaultStyles[index]
				: styles[index];
	}

	/**
	 * Sets the style with the given identification for this form.
	 * 
	 * @see KDisplay.setStyle(int,KStyle)
	 */
	public void setStyle(final int index, final KStyle style) {
		if( styles == null ) {
			styles = new KStyle[getKDisplay().defaultStyles.length];
		}

		styles[index] = style;
	}

	/**
	 * Remove the given command. If the command is not attached to this form,
	 * nothing happens.
	 */

	public void removeCommand(final Command cmd) {
		if( cmd != null && !getKDisplay().isFullscreenMode ) {
			throw new RuntimeException("Command Issue!");
			// super.removeCommand(cmd);
			// return;
		}

		commands.removeElement(cmd);
		KDisplay.requestRepaint();
	}

	/**
	 * The main internal render method. Get called by the paint method of
	 * {@link KDisplay}. Checks first if layout is needed and then render the
	 * form, the title and the softkeys.
	 */
	synchronized final void paintAll(final Graphics g) {

		//
		// Check if we need to call the layout method
		if( h != getKDisplay().getHeight() ) {
			doLayout();
		}

		//
		// Store the current clip settings
		final int clipX = g.getClipX();
		final int clipY = g.getClipY();
		final int clipW = g.getClipWidth();
		final int clipH = g.getClipHeight();

		//
		// Draw the content of the KForm object
		g.setClip(x0, y0, x1 - x0, y1 - y0);
		g.translate(x0, y0);
		paint(g);
		g.translate(-x0, -y0);
		g.setClip(clipX, clipY, clipW, clipH);

		//
		// Draw the title and the softkey area.
		drawTitle(g);
		drawSoftKeys(g);
	}

	/**
	 * Set the title icon, displayed to the left of the title.
	 */
	public void setTitleIcon(final Image imgIcon) {
		icon = imgIcon;
	}

	/** Draws the title area of this form */

	protected void drawTitle(final Graphics g) {

		final KStyle s = getStyle(KStyle.STYLE_TITLE);
		final KDisplay kDisplay = getKDisplay();
		final Font f = kDisplay.getFont(s.font);

		if( kDisplay.isWidescreen ) {
			s.fill(g, 0, 0, x0, y1);
			final int ofs = KDisplay.applyStyle(s, g);

			if( title != null ) {
				g.drawString(title, 0, ofs, Graphics.LEFT | Graphics.TOP);
			}

			if( topRight != null ) {
				g.drawString(topRight, 0, y1 + ofs, Graphics.LEFT
						| Graphics.BOTTOM);
			}
		}
		else if( title != null ) {
			s.fill(g, 0, 0, x1, y0);
			KDisplay.applyStyle(s, g);

			int tx = (x1 - f.stringWidth(title)) / 2;

			if( icon != null ) {
				g.drawImage(icon, 0, 0, Graphics.TOP | Graphics.LEFT);
				tx = icon.getWidth() + KDisplay.BORDER / 2;
			}
			else if( tx < 0 || topRight != null ) {
				tx = KDisplay.BORDER;
			}

			final int ty = (y0 - f.getHeight()) / 2;

			s.drawString(g, title, tx, ty);
			if( topRight != null ) {
				s.drawString(g, topRight,
						x1 - g.getFont().stringWidth(topRight)
								- KDisplay.BORDER, ty);
			}
		}
		else if( icon != null ) {
			g.drawImage(icon, 0, 0, Graphics.TOP | Graphics.LEFT);
		}
	}

	/**
	 * Draws the content area of this form. Overwrite this method if you
	 * intend to use this form like a MIDP Canvas. XXX SKerkewitz: the
	 * top/bottom - bottom/top draw condition is not the best solution.
	 */
	public void paint(final Graphics g) {

		//
		// Get the fullWidth and height of KForm. Note that this is only the visible
		// space that the KForm can use on the display.
		final int w = x1 - x0;
		final int h = y1 - y0;

		//
		// Draw the background first
		getStyle(KStyle.STYLE_CONTENT).fill(g, 0, 0, w, h);
		if( bgImage != null ) {
			g.drawImage(bgImage, 0, 0, Graphics.TOP | Graphics.LEFT);
		}

		//
		// If this form has no child items then we are finished here.
		if( size() == 0 ) {
			return;
		}

		//
		// If the first visible item is below the cursor line then the the first item to the
		// same then the cursor line. This should make sure that the focus item is
		// always visible.
		if( line0 > cursorLine ) {
			line0 = cursorLine;
		}

		//
		// Because not every item has the same height and we do not want to around
		// jump or make the focused element always the top element we have to
		// recalculate how many item we can draw above the focused element but
		// make sure the focused element is still complete visible.
		int remaining = h - getHeight(cursorLine);
		int first = cursorLine;
		while( first > line0 ) {
			remaining -= getHeight(first - 1);
			if( remaining < 0 ) {
				break;
			}
			first--;
		}

		//
		// We can start with the item at index "line0" and be sure that the focused item
		// is completely visible
		line0 = first;

		//
		// We need to catch a special case: there are not enough elements below
		// the current element to fill the free space then render from button to top.
		boolean bDrawBottomToTop = false;
		if( line0 > 0 ) {

			//
			// Check for normal rendering. If we have to render more item then there
			// is space then the normal top to down rendering is fine. If not then we
			// should render bottom to top.
			int ty = 0;
			bDrawBottomToTop = true;
			for( int i = line0; i < size(); i++ ) {
				final int hi = getHeight(i);
				ty += hi;
				if( ty > h ) {
					bDrawBottomToTop = false;
					break;
				}
			}
		}

		//
		// Check if we need to draw from top to bottom or from bottom to top
		if( bDrawBottomToTop ) {

			//#mdebug debug
			//@			//System.out.println("Render bottom to top");
			//#enddebug

			final int oldX = g.getTranslateX();
			final int oldY = g.getTranslateY();

			//
			// Render the last item first and the run up until we come to the first
			// item that is full/partial visible
			g.translate(0, h);

			for( int i = size() - 1; i >= line0 - 1; i-- ) {
				final int hi = getHeight(i);
				g.translate(0, -hi);
				paint(i, g);
			}

			//
			// Restore the old translation
			g.translate(oldX - g.getTranslateX(), oldY - g.getTranslateY());

			//
			// Recalculate if there any items above or below the focused item.
			upArrow = true;
			downArrow = false;

		}
		else {

			//
			// The default top to bottom draw method
			//#mdebug debug
			//@			//System.out.println("Render top to bottom");
			//#enddebug

			//
			// If there are not enought controls on the form check and VALIGN of the
			// form is not set to TOP then try to bCenterVertical the content.
			startY = 0;
			if( offset == 0 && line0 == 0 && bCenterVertical ) {
				// try to bCenterVertical

				int usedH = 0;

				for( int i = line0; i < size(); i++ ) {
					usedH += getHeight(i);
					if( usedH > h ) {
						break;
					}
				}

				startY = (h - usedH) / 2;
				if( startY < 0 ) {
					startY = 0;
				}
			}

			//#mdebug debug
			//@			System.out.println("Render top to bottom, offset " + offset + " startY: " +startY);
			//#enddebug

			//
			// Render child item starting at index line0 until the display is full or we
			// running out of items.
			g.translate(0, -offset + startY);
			int ty = 0;
			for( int i = line0; i < size(); i++ ) {
				paint(i, g);
				final int hi = getHeight(i);
				ty += hi;
				g.translate(0, hi);
				if( ty > h ) {
					break;
				}
			}
			g.translate(0, -ty + offset - startY);

			//
			// Recalculate if there any items above or below the focused item.
			upArrow = line0 > 0;
			downArrow = ty > h;
		}
	}

	/** Returns y pos of item i relative to window start */

	int getY(final int index) {
		int y = startY;
		for( int i = line0; i < index; i++ ) {
			y += getHeight(i);
		}
		return y;
	}

	/** Returns the item index at the given screen location */

	private int getIndexAt(final int py) {
		final int size = size();
		int ly = y0 + startY;
		for( int i = line0; i < size; i++ ) {
			ly += getHeight(i);
			if( py < ly ) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Draws the soft keys and the option menu
	 */
	protected void drawSoftKeys(final Graphics g) {

		//
		// If we are forced to used the native implementation then do nothing here
		final KDisplay kDisplay = getKDisplay();
		if( kDisplay.useNativeSoftkeys == true )
			return;

		int cmd1x;
		int cmd1y;
		int cmd1a;
		int cmd2x;
		int cmd2y;
		int cmd2a;

		final KStyle s = getStyle(KStyle.STYLE_COMMAND);
		int ofs;

		if( kDisplay.isWidescreen ) {
			s.fill(g, x1, 0, KDisplay.CANVAS_WIDTH - x1,
					KDisplay.CANVAS_HEIGHT);
			ofs = KDisplay.applyStyle(s, g);

			cmd1x = KDisplay.CANVAS_WIDTH + KDisplay.BORDER;
			cmd1y = ofs;
			cmd1a = Graphics.TOP | Graphics.RIGHT;
			cmd2x = cmd1x;
			cmd2y = KDisplay.CANVAS_HEIGHT + ofs;
			cmd2a = Graphics.BOTTOM | Graphics.RIGHT;
		}
		else {
			s.fill(g, 0, y1, x1, KDisplay.CANVAS_HEIGHT - y1);
			ofs = KDisplay.applyStyle(s, g);

			final Font f = g.getFont();

			cmd1x = KDisplay.BORDER;
			cmd1y = y1 + ofs + (KDisplay.CANVAS_HEIGHT - y1 - f.getHeight())
					/ 2;
			cmd1a = Graphics.TOP | Graphics.LEFT;
			cmd2x = KDisplay.CANVAS_WIDTH - KDisplay.BORDER;
			cmd2y = cmd1y;
			cmd2a = Graphics.TOP | Graphics.RIGHT;
		}

		if( commandPopupIndex != -1 ) {

			g.drawString(Registry.get("kui.cmd.cancel", "Cancel"), cmd1x,
					cmd1y, cmd1a);
			g.drawString(Registry.get("kui.cmd.select", "Select"), cmd2x,
					cmd2y, cmd2a);

			// determine size

			final KStyle ps = getStyle(KStyle.STYLE_POPUP);
			final KStyle psf = getStyle(KStyle.STYLE_POPUP
					+ KItem.STATE_FOCUSSED);
			//        	KStyle fs = styles[STYLE_POPUP];

			final Font f = ps.getFont();
			final Font ff = psf.getFont();
			int w = 0;
			final int h = (commands.size() - 1 + cmdOffset)
					* (f.getHeight() + KDisplay.BORDER);

			for( int i = 1 - cmdOffset; i < commands.size(); i++ ) {
				final Command cmd = (Command) commands.elementAt(i);
				final int len = Math.max(f.stringWidth(cmd.getLabel()),
						ff.stringWidth(cmd.getLabel()));
				if( len > w ) {
					w = len;
				}
			}

			w += 2 * KDisplay.BORDER;
			//h+=BORDER;

			final int wx = x1 - w;
			int wy = y1 - h;

			ps.fill(g, wx, wy, w, h);
			ps.frame(g, wx, wy, w, h);

			for( int i = commands.size() - 1; i >= 1 - cmdOffset; i-- ) {
				final Command cmd = (Command) commands.elementAt(i);

				if( i == commandPopupIndex ) {
					psf.fill(g, wx, wy + KDisplay.BORDER / 2, w,
							f.getHeight());
				}

				ps.drawString(g, getCommandLabel(cmd), wx + KDisplay.BORDER,
						wy + KDisplay.BORDER / 2);
				wy += f.getHeight() + KDisplay.BORDER;
			}

		}
		else {

			final KItem fi = getFocussedItem();
			if( KDisplay.DELETE_SK != null && KDisplay.DELETE_SK.length() > 0
					&& fi instanceof KTextField ) {
				g.drawString(KDisplay.DELETE_SK, cmd1x, cmd1y, cmd1a);
				cmdOffset = 1;
			}
			else if( commands.size() == 1
					&& KDisplay.isPositiveCommand((Command) commands.elementAt(0)) ) {
				cmdOffset = 1;
			}
			else {
				cmdOffset = 0;

				if( commands.size() > 0 ) {
					g.drawString(
							getCommandLabel((Command) commands.elementAt(0)),
							cmd1x, cmd1y, cmd1a);
				}
			}

			String rsk = null;
			if( commands.size() == 2 - cmdOffset ) {
				rsk = getCommandLabel((Command) commands.elementAt(1 - cmdOffset));
			}
			else if( commands.size() > 2 - cmdOffset ) {
				rsk = Registry.get("kui.cmd.options", "Options");
			}
			else if( fi != null && fi.defaultCommand != null ) {
				rsk = getCommandLabel(fi.defaultCommand);
			}

			if( rsk != null ) {
				g.drawString(rsk, cmd2x, cmd2y, cmd2a);
			}

			if( size() > 1 && kDisplay.isFullscreenMode
					&& !kDisplay.isWidescreen ) {

				final int indicatorY = y1
						+ (KDisplay.CANVAS_HEIGHT - y1 - KDisplay.SYMBOLS.getHeight()
								/ KDisplay.SYMBOL_COUNT) / 2;
				final int indicatorX = (x1 - x0) / 2 - 8;

				KDisplay.drawIcon(g, KDisplay.SYMBOLS, indicatorX,
						indicatorY, upArrow ? KDisplay.ICON_UP_ON
								: KDisplay.ICON_UP_OFF, 1,
						KDisplay.SYMBOL_COUNT * 2);
				KDisplay.drawIcon(g, KDisplay.SYMBOLS, indicatorX, indicatorY
						+ KDisplay.SYMBOL_HEIGHT / 2 + 1,
						downArrow ? KDisplay.ICON_DOWN_ON
								: KDisplay.ICON_DOWN_OFF, 1,
						KDisplay.SYMBOL_COUNT * 2);
			}
		}
	}

	/**
	 * Set the focus to the given item index.
	 * 
	 * @param dir
	 *            the direction from which we come to the new item.
	 * @return true if the focus have change, else false.
	 */
	public boolean setFocusIndex(final int i, final int dir) {

		//		if (focusIndex == i)
		//			return true;
		//

		//#mdebug info
		//@		System.out.println("KForm::setFocusIndex(): i:" + i);
		//#enddebug

		final KItem target = get(i);

		final int[] inout = new int[] { 0, -target.contentY, getWidth(),
				Math.min(getHeight(), target.height) - target.contentY };

		if( !target.traverse(dir, getWidth(), getHeight() - target.contentY,
				inout) ) {

			//#mdebug info
			//@			System.out.println("KForm::setFocusIndex(): return false");
			//#enddebug

			return false;
		}

		offset = inout[1] + target.contentY;
		internalTraversal = true;

		final KItem prev = getFocussedItem();
		if( prev != null ) {
			prev.traverseOut();
		}

		focusIndex = i;
		cursorLine = i;
		KDisplay.requestRepaint();

		//#mdebug info
		//@		System.out.println("KForm::setFocusIndex(): return true");
		//#enddebug

		return true;
	}

	/**
	 * Set the focus to the given index. SKerkewitz: this is mainly a bloody
	 * hack to set the focus to an element properly without any layout/paint
	 * calls.
	 * 
	 * @param i
	 *            the index of the element you want to set the focus.
	 * @return not used yet.
	 */
	public boolean setFocusIndexForced(final int i) {
		//#mdebug info
		//@		System.out.println("KForm::setFocusIndexForced(): i:" + i);
		//#enddebug
		focusIndex = i;
		cursorLine = i;
		offset = 0;

		//
		// Set internalTravesel so the travese get called on next keyevent
		internalTraversal = true;
		return true;
	}

	/** Returns the item with the given index */
	public KItem get(final int i) {
		return (KItem) items.elementAt(i);
	}

	/** Returns the index of the focused item */
	public int getFocusIndex() {
		return focusIndex;
	}

	/**
	 * Indicates that the pointer was pressed at the given location. Only
	 * called if the pointer is not located on an item.
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 */

	public void pointerPressed(final int x, final int y) {
		// nothing yet
	}

	/**
	 * Indicates that the pointer was released at the given location. Only
	 * called if the pointer is not located on an item.
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 */

	public void pointerReleased(final int x, final int y) {
		// nothing yet
	}

	/**
	 * Indicates that the pointer was dragged to the given location. Only
	 * called if the pointer is not located on an item.
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 */

	public void pointerDragged(final int x, final int y) {
		// nothing yet
	}

	/**
	 * Called internally by display in order to distribute pointer events
	 * 
	 * @param x
	 *            X-Coordinate of the pointer event
	 * @param y
	 *            Y-Coordinate of the pointer event
	 * @param type
	 *            Type of the event; one of PRESSED, RELEASED or DRAGGED
	 */
	void pointerEvent(final int x, final int y_, final int type) {

		int y = y_;

		//
		// Check for softkey area first
		final KDisplay kDisplay = getKDisplay();
		if( y >= kDisplay.getHeight() - 16 ) {
			final int w = kDisplay.getWidth();
			if( x < w / 2 - 8 ) {
				keyEvent(KDisplay.KEYCODE_LSK, PRESSED);
			}
			else if( x > w / 2 + 8 ) {
				keyEvent(KDisplay.KEYCODE_RSK, PRESSED);
			}
			else if( y > KDisplay.kDisplay.getHeight() - 8 ) {
				keyEvent(kDisplay.getKeyCode(KDisplay.DOWN), PRESSED);
			}
			else {
				keyEvent(kDisplay.getKeyCode(KDisplay.UP), PRESSED);
			}
		}
		else {
			//
			// Try to determine what item is at the given position
			final int i = getIndexAt(y);
			if( i == -1 ) {
				//
				// If there is no item at that position then redirect the call to the
				// generic pointer methods.
				switch( type ) {
				case PRESSED:
					pointerPressed(x, y);
					break;
				case RELEASED:
					pointerReleased(x, y);
					break;
				case DRAGGED:
					pointerDragged(x, y);
					break;
				}
			}
			else if( i != focusIndex ) {
				//
				// If the item at that position does not have the focus then focus
				// it first.
				setFocusIndex(i, 0);
			}
			else {
				//
				// If the item at that position has the focus then trigger the pointer
				// method.
				final KItem item = getFocussedItem();
				y = y - y0 - getY(i);

				switch( type ) {
				case PRESSED:
					item.pointerPressed(x, y);
					break;
				case RELEASED:
					item.pointerReleased(x, y);
					break;
				case DRAGGED:
					item.pointerDragged(x, y);
					break;
				}
			}
		}
	}

	/**
	 * The key event dispatcher method. Called internally by KDisplay in order
	 * to distribute key events.
	 */
	void keyEvent(final int keyCode, final int event) {

		// Handled by Command Popup?

		if( commandPopupIndex != -1 ) {
			if( event != KForm.RELEASED ) {

				switch( keyCode ) {
				case KDisplay.KEYCODE_LSK:
					commandPopupIndex = -1;
					repaint();
					break;

				case Canvas.FIRE:
				case KDisplay.KEYCODE_RSK:
					sendCommand(
							(Command) commands.elementAt(commandPopupIndex),
							this);
					commandPopupIndex = -1;
					repaint();
					break;

				case Canvas.DOWN:
					commandPopupIndex--;
					if( commandPopupIndex < 1 - cmdOffset ) {
						commandPopupIndex = commands.size() - 1;
					}
					repaint();
					break;

				case Canvas.UP:
					commandPopupIndex++;
					if( commandPopupIndex >= commands.size() ) {
						commandPopupIndex = 1 - cmdOffset;
					}
					repaint();
					break;
				}
			}
			return;
		}

		// Soft key?

		//
		// Get the currently focused item
		final KItem focusedItem = getFocussedItem();

		//
		// Handle soft key pressed events first
		if( event == KForm.PRESSED ) {
			final int count = commands.size();

			switch( keyCode ) {
			case KDisplay.KEYCODE_LSK:

				if( KDisplay.DELETE_SK != null
						&& focusedItem instanceof KTextField ) {
					focusedItem.keyPressed(KDisplay.KEYCODE_CLEAR);
					return;
				}

				if( count > 0 ) {
					sendCommand((Command) commands.elementAt(0), this);
					return;
				}
				break;
			case KDisplay.KEYCODE_RSK:

				if( count == 2 - cmdOffset ) {
					sendCommand((Command) commands.elementAt(1 - cmdOffset),
							this);
					return;
				}
				else if( count > 2 - cmdOffset ) {
					commandPopupIndex = commands.size() - 1;

					repaint();
					return;
				}
				else {
					final KItem item = getFocussedItem();
					if( item != null && item.defaultCommand != null ) {
						sendCommand(item.defaultCommand, item);
						return;
					}
				}
				break;
			}
		}

		//
		// Check if we have to delegate the event to child items
		if( size() > 0 ) {

			//
			// If there is already a focused item and we have a UP or DOWN event
			// we need to go to the next item.
			if( focusedItem != null && internalTraversal && event != RELEASED
					&& (keyCode == Canvas.UP || keyCode == Canvas.DOWN) ) {

				final int[] visRect_inout = new int[] {
						0,
						offset - focusedItem.contentY,
						getWidth(),
						Math.min(getHeight(), focusedItem.height)
								- focusedItem.contentY };

				//
				// The the item try to handle the key event internaly
				internalTraversal = focusedItem.traverse(keyCode, getWidth(),
						getHeight() - focusedItem.contentY, visRect_inout);

				//
				// If the item could handle the event internaly then update the off
				// and we are done here.
				if( internalTraversal ) {
					offset = visRect_inout[1] + focusedItem.contentY;
					return;
				}

				//
				// SKerkewitz: bloody hack to make sure that traverse get called next
				// time if we are at the first item
				if( cursorLine == 0 )
					internalTraversal = true;
			}

			//
			// Check if we can cycle thru child elements
			if( size() > 1 || focusedItem == null
					|| focusedItem.itemType != KItem.TYPE_FULLSCREEN ) {
				if( listKeyEvent(keyCode, event) ) {
					KDisplay.requestRepaint();
					return;
				}
			}

			//
			// Send event to the focused item if we have one
			if( focusedItem != null ) {
				switch( event ) {
				case PRESSED:
					focusedItem.keyPressed(keyCode);
					break;
				case RELEASED:
					focusedItem.keyReleased(keyCode);
					break;
				case REPEATED:
					focusedItem.keyRepeated(keyCode);
					break;
				}
			}

			//
			// SKerkewitz: let the form form get the key in any case!
			//return;
		}

		//
		// In case we have no child we handle the event ourself
		switch( event ) {
		case PRESSED:
			keyPressed(keyCode);
			break;
		case RELEASED:
			keyReleased(keyCode);
			break;
		case REPEATED:
			keyRepeated(keyCode);
			break;
		}

		//
		// Repaint the screen in any case.
		KDisplay.requestRepaint();
	}

	/**
	 * Called when a key was pressed, but there is no focussed item to consume
	 * the event.
	 * 
	 * @param code
	 *            normalized keycode
	 */
	public void keyPressed(final int code) {
		// overwrite if neccessary
	}

	/**
	 * Called when a key was repeated, but there is no focussed item to
	 * consume the event.
	 * 
	 * @param code
	 *            normalized keycode
	 */

	public void keyRepeated(final int code) {
		keyPressed(code);
	}

	/**
	 * Called when a key was released, but there is no focussed item to
	 * consume the event.
	 * 
	 * @param code
	 *            normalized keycode
	 */

	public void keyReleased(final int code) {
		// overwrite if neccessary
	}

	/**
	 * Internal method; sends the command to the command listener if set and
	 * the command is not null
	 */

	void sendCommand(final Command cmd, final Object src) {
		if( commandListener != null && cmd != null ) {
			commandListener.commandAction(cmd, src);
		}
	}

	/**
	 * This is the LCDUI command listener interface. We need this only because
	 * some mobile devices will not handle the softkeys if no command is
	 * attached to them. Just forward the event into the KUI event pipe.
	 */
	public void commandAction(final Command cmd, final Displayable display) {
		//#mdebug debug
		//@		System.out.println("Command" + cmd);
		//#enddebug

		sendCommand(cmd, this);
	}

	/**
	 * Add the given command to this form.
	 */
	public void addCommand(final Command cmd) {

		//
		// Don't allow null command and don't allow add the same command multiple
		// times
		if( cmd == null || commands.contains(cmd) )
			return;

		if( KDisplay.isPositiveCommand(cmd) ) {
			commands.addElement(cmd);
		}
		else {
			commands.insertElementAt(cmd, 0);
		}

		if( !getKDisplay().isFullscreenMode ) {
			throw new RuntimeException("super.addCommand(cmd);return;");
		}
		else {
			repaint();
		}
	}

	/**
	 * Set the {@link KCommandListener} for this form. Be aware that there can
	 * be only one single listener. If there already a listener attached then
	 * the old listener will be overridden,
	 */
	public void setCommandListener(final KCommandListener listener) {
		if( !getKDisplay().isFullscreenMode ) {
			throw new RuntimeException("super.setCommandListener(listener);");
		}
		commandListener = listener;
	}

	/**
	 * Paint the item with given index on the given {@link Graphics} context
	 */
	private void paint(final int i, final Graphics g) {
		try {
			((KItem) items.elementAt(i)).paint(g);
		} catch (final Exception e) {
			//#mdebug fatal
			e.printStackTrace();
			//#enddebug
		}
	}

	/**
	 * Internal method; Returns the height of the item with the given index.
	 * Performs doLayout() if necessary.
	 */

	private int getHeight(final int i) {
		try {
			final KItem item = get(i);
			if( item.height == -1 ) {
				item.doLayout();
			}
			return item.height;
		} catch (final Exception e) {
			e.printStackTrace();
			return 16;
		}
	}

	/** Returns the number of items in this form */

	public int size() {
		return items.size();
	}

	/** insert the given item at the given index. */
	public void insert(final int index, final KItem item) {
		item.ownerForm = this;
		items.insertElementAt(item, index);
	}

	/** Sets the form title and requests a relayout */

	public void setTitle(final String title) {

		if( (title == null) != (this.title == null) ) {
			this.h = -1;
		}

		this.title = title;
		repaint();
	}

	/**
	 * Returns the title of this form, as set in the constructor or by
	 * setTitle().
	 */

	public String getTitle() {
		return title;
	}

	/**
	 * Returns the focused item, or null if none is focused.
	 * 
	 * @return the focused item, or null if none is focused.
	 */
	public KItem getFocussedItem() {
		//
		// If the index is in valid range give back the item, else null
		return focusIndex >= 0 && focusIndex < size() ? (KItem) items.elementAt(focusIndex)
				: null;
	}

	/**
	 * Request a repaint of the form.
	 */
	public void repaint() {
		KDisplay.requestRepaint();
	}

	/**
	 * Internal method, returns the label of the given command, including
	 * suitable labels for DISMISS_COMMAND and SELECT_COMMAND.
	 */

	static String getCommandLabel(final Command cmd) {
		if( cmd == DISMISS_COMMAND ) {
			return Registry.get("kui.cmd.ok", "Ok");
		}

		if( cmd == SELECT_COMMAND ) {
			return Registry.get("kui.cmd.select", "Select");
		}

		return cmd.getLabel();
	}

	/**
	 * Called by the display every 100 ms if the form is visible. If you
	 * override this method, please call super to make sure it will take care
	 * of cursor blinking.
	 */

	protected void tick() {
		if( commandPopupIndex == -1
				&& getFocussedItem() instanceof KTextField ) {
			final KTextField tf = (KTextField) getFocussedItem();

			if( System.currentTimeMillis() % 1000 >= 500 != tf.cursorOn ) {

				//
				// Invert the flag
				tf.cursorOn ^= true;
				repaint();
			}

		}
	}

	/**
	 * Calculates the fullWidth of the content area (excluding title,
	 * softkeys)
	 * 
	 * @return fullWidth in pixels
	 */

	public int getWidth() {
		return x1 - x0;
	}

	/**
	 * Calculates the height of the content area (excluding title, softkeys)
	 * 
	 * @return height in pixels
	 */

	public int getHeight() {

		if( h == -1 ) {
			doLayout();
		}
		return y1 - y0;
	}

	/**
	 * 
	 * @param keyCode
	 * @param event
	 *            the type of the key event.
	 * @return true if the event was handled, else false.
	 */
	private boolean listKeyEvent(final int keyCode, final int event) {

		//
		// We handle only pressed or repeated events here
		if( event != KForm.PRESSED && event != KForm.REPEATED )
			return false;

		//
		// Some debug output
		//#mdebug info
		//@		String sKeyCode = "<unknown> code: " + keyCode;
		//@		switch( keyCode ) {
		//@		case Canvas.UP:
		//@			sKeyCode = "UP";
		//@			break;
		//@		case Canvas.DOWN:
		//@			sKeyCode = "DOWN";
		//@			break;
		//@		case Canvas.LEFT:
		//@			sKeyCode = "LEFT";
		//@			break;
		//@		case Canvas.RIGHT:
		//@			sKeyCode = "RIGHT";
		//@			break;
		//@		}
		//@
		//@		String sEvent = "<unknown>";
		//@		switch( event ) {
		//@		case PRESSED:
		//@			sEvent = "PRESSED";
		//@			break;
		//@		case REPEATED:
		//@			sEvent = "REPEATED";
		//@			break;
		//@		case DRAGGED:
		//@			sEvent = "DRAGGED";
		//@			break;
		//@		case RELEASED:
		//@			sEvent = "RELEASED";
		//@			break;
		//@		}
		//@
		//@		System.out.println("listKeyEvent(): keyCode: " + sKeyCode
		//@				+ ", event: " + sEvent);
		//@		System.out.println("listKeyEvent(): offset: " + offset
		//@				+ ", cursorLine: " + cursorLine + ", focusIndex: "
		//@				+ focusIndex);
		//#enddebug

		switch( keyCode ) {
		case Canvas.UP:

			//
			// If we are scrolling in a text for example the just go up some lines
			if( offset > 0 ) {

				//
				// Make sure that we only scroll back in plain text elements
				final KItem item = get(cursorLine);
				if( item != null && item.itemType == KItem.TYPE_PLAIN ) {
					offset -= 16;
					offset = (offset < 0) ? 0 : offset;
				}
				else {
					offset = 0; // in all other KUI elements we "jump to head"
				}
				return true;
			}

			//
			// If we are at the top element then try to focus that last element on that
			// page.
			if( cursorLine == 0 ) {
				//				setFocusIndexForced(size()-1);
				//				KDisplay.requestRepaint();
				//				return true;
				return false; // SKerkewitz: Joca don't like wrap around
			}

			if( focusIndex > -1 && focusIndex < cursorLine ) {
				cursorLine = focusIndex;
			}

			while( cursorLine > 0 ) {
				cursorLine--;
				if( setFocusIndex(cursorLine, Canvas.UP)
						|| cursorLine < line0 )
					break;
			}
			return true;

		case Canvas.DOWN:

			if( getHeight(cursorLine) + getY(cursorLine) > (y1 - y0) + offset
					+ getY(line0) ) {
				offset += 16;
				return true;
			}

			//
			// Try to find the next focus able item
			//#mdebug info
			//@			System.out
			//@					.println("KForm::listKeyEvent(): set focus to next item");
			//#enddebug
			while( cursorLine < size() - 1 ) {
				offset = 0;
				cursorLine++;
				// SKerkewitz: the check (cursorLine > focusIndex) make absolute no
				// sense to me
				//if (cursorLine > focusIndex && setFocusIndex(cursorLine, Canvas.DOWN))
				if( setFocusIndex(cursorLine, Canvas.DOWN) )
					return true;
				final int botidx = getIndexAt(y0 + h + 1);
				if( botidx != -1 && botidx <= cursorLine )
					return true;
			}

			//			//
			//			// It seem that there are no more item below the current item. So just set
			//			// the first item the focus one.
			////#mdebug
			//			System.out.println("KForm::listKeyEvent(): force focus to 0");
			////#enddebug
			//			offset = 0;
			//			cursorLine = 0;
			//			setFocusIndexForced(cursorLine);
			//			KDisplay.requestRepaint();
			//			return true;

			//
			// SKerkewitz: Joca don't like wrap around
			return false;
		}

		return false;
	}

	public void setBackgroundImage(final Image img) {
		this.bgImage = img;
	}

	/**
	 * Sets a short text in the right corner of the title bar. The title is
	 * automatically aligned to the right in this case.
	 * 
	 * @param text
	 *            Text to be displayed in the top right area
	 */

	public void setInfo(final String text) {

		if( text != topRight && (text == null || !text.equals(topRight)) ) {
			topRight = text;
			repaint();
		}
	}

	/**
	 * Let KUI classes access the KDisplay singleton.
	 * 
	 * @return
	 */
	final protected KDisplay getKDisplay() {
		return KDisplay.kDisplay;
	}

	/**
	 * This callback method will be called if a {@link KForm} will become the
	 * active or a currently active {@link KForm} will be become deactive.
	 * 
	 * @param stateChange
	 *            a int indicating the new state.
	 */
	public void displayStateChange(final int stateChange,
			final Object prevDisplayable) {
		// Default is nothing, overwrite it if you need to do some special stuff
		// if a KForm will become active or deactive.
	}

	public boolean isInternalTraversal() {
		return internalTraversal;
	}
}
