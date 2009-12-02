package org.kobjects.kui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.kobjects.utils4me.KRectangle;
import org.kobjects.utils4me.Tools;

/**
 * A abstract superclass for components that can be added to a {@link KForm}.
 * All {@link KItem} objects have a label field, which is a string that is
 * attached to the item. The label is typically displayed near the component
 * when it is displayed within a screen. The label should be positioned on the
 * same horizontal row as the item or directly above the item. The
 * implementation should attempt to distinguish label strings from other
 * textual content, possibly by displaying the label in a different font,
 * aligning it to a different margin, or appending a colon to it if it is
 * placed on the same line as other string content. If the screen is scrolling
 * the implementation should try to keep the label visible at the same time as
 * the Item.
 * <p>
 * Because this class is abstract you cannot create an instance of this class.
 * 
 * @author Stefan Haustein
 */
public abstract class KItem {

	//
	// A list of all possible appearance modes
	public static final int TYPE_PLAIN = 0;
	public static final int TYPE_BUTTON = 3;
	public static final int TYPE_INDENT = 4;
	public static final int TYPE_COMPACT = 5;
	public static final int TYPE_FULLSCREEN = 6;
	public static final int TYPE_TAB = 7;
	public static final int TYPE_INPUT = 8;

	/** a simple horizontal line like in plain HTML */
	public static final int TYPE_HLINE = 9;

	public static final int CONTENT = 0;
	public static final int LABEL = 3;

	public static final int STATE_UNFOCUSSED = 0;
	public static final int STATE_FOCUSSED = 1;
	public static final int STATE_PRESSED = 2;

	/**
	 * Overrides style align if set
	 * 
	 * @deprecated
	 */

	public int align;

	/** Specifies the type of this item. See TYPE_xxx for more. */
	protected int itemType;

	/** The rectangle that defines the bgValues fill */
	protected final KRectangle rectBackgroundFill = new KRectangle();
	protected final KRectangle rectContentFill = new KRectangle();

	/** The object that String representation is the text of the label */
	protected Object labelTextObj;

	/** The layouted label */
	protected WordWrap formattedLabel;

	/** The Position of the layout */
	protected final KRectangle rectLabel = new KRectangle();

	protected int contentX;
	protected int contentY;
	protected int contentW, contentH;

	Command defaultCommand;

	// TODO: Merge with appearanceMode?

	/** The {@link KForm} that own this item */
	protected KForm ownerForm;

	int height = -1;

	public Image image;
	protected int img0;
	protected int img1;
	protected int img2;
	protected int imgCols;
	protected int imgRows;

	protected int imgX;
	protected int imgY;

	//
	// SKerkewitz: hack for the HR tag. We need to know the fullWidth in loadingProgress
	// everytime we layout the element.
	/** relative fullWidth in loadingProgress, 100 means full screen fullWidth */
	public int relativeWidth;

	private KStyle[] kStyles; // = KForm.DEFAULT_CONTENT_STYLE;
	final private int styleId;

	public int getState() {
		return ownerForm != null && ownerForm.getFocussedItem() == this ? STATE_FOCUSSED
				: STATE_UNFOCUSSED;
	}

	/**
	 * Does some basic initialization. Will be called by derived classes. You
	 * can give any object as label. The implementation will call the
	 * toString() method to get the actual string representation of the object
	 * which will be displayed as label.
	 * 
	 * @param objLabel
	 * @param type
	 */
	protected KItem(final Object objLabel, final int type) {

		//
		// Store the given appearance mode and the the styles.
		this.itemType = type;
		switch( type ) {
		case TYPE_INPUT:
		case TYPE_FULLSCREEN:
			styleId = KStyle.STYLE_EDIT;
			break;
		case TYPE_BUTTON:
			styleId = KStyle.STYLE_BUTTON;
			break;
		default:
			styleId = KStyle.STYLE_ITEM;
		}

		//
		// Set the label for this item.
		setLabel(objLabel);
	}

	void doLayout() {

		// restrict area

		rectBackgroundFill.x = 0;
		rectBackgroundFill.y = 0;
		rectBackgroundFill.w = KDisplay.CONTENT_WIDTH;

		if( itemType == TYPE_BUTTON ) {

			final KStyle contentStyle = getContentStyle(getState());
			int borderWidth = 0;
			if( contentStyle != null && contentStyle.frameValues != null
					&& contentStyle.frameValues.length > 2 ) {
				borderWidth = getContentStyle(getState()).frameValues[2];
			}

			rectContentFill.x = KDisplay.BORDER + borderWidth;
			rectContentFill.y = KDisplay.BORDER / 2 + borderWidth;

			rectContentFill.w = KDisplay.CONTENT_WIDTH
					- (rectContentFill.x << 1);
			rectContentFill.h = doLayout(rectContentFill.x, rectContentFill.y
					+ KDisplay.BORDER / 2, rectContentFill.w, false)
					+ KDisplay.BORDER;

			height = (rectContentFill.y << 1) + rectContentFill.h;
			rectBackgroundFill.h = height;
		}
		else if( itemType == TYPE_FULLSCREEN ) {
			contentX = KDisplay.BORDER;
			contentY = KDisplay.BORDER;
			contentW = KDisplay.CONTENT_WIDTH - 2 * KDisplay.BORDER;
			contentH = getPrefContentHeight(KDisplay.CONTENT_WIDTH);

			height = contentH + 2 * KDisplay.BORDER;

			rectContentFill.x = 0;
			rectContentFill.y = 0;
			rectContentFill.w = KDisplay.CONTENT_WIDTH;
			rectContentFill.h = height;

			rectBackgroundFill.h = height;
		}
		else if( itemType == TYPE_COMPACT ) {
			height = doLayout(0, 0, KDisplay.CONTENT_WIDTH, false);

			rectContentFill.x = 0;
			rectContentFill.y = 0;
			rectContentFill.w = KDisplay.CONTENT_WIDTH;
			rectContentFill.h = height;

			rectBackgroundFill.h = height;

		}
		else if( itemType == TYPE_HLINE ) {
			height = doLayout(0, 0, KDisplay.CONTENT_WIDTH, false);
			height = 2; // pixel height for all HR lines
			rectContentFill.x = 0;
			rectContentFill.y = 0;
			rectContentFill.w = KDisplay.CONTENT_WIDTH;
			rectContentFill.h = height;

			rectBackgroundFill.h = height;

			//
			// Scale the HR line. This seems a little bit odd because we can not use
			// floats here. So we use a fix point math
			int newWidth = KDisplay.CONTENT_WIDTH * 1000;
			newWidth /= 100;
			newWidth *= relativeWidth;
			newWidth /= 1000;
			final int dif = KDisplay.CONTENT_WIDTH - newWidth;
			rectBackgroundFill.x = dif / 2;
			rectBackgroundFill.w = newWidth;
		}
		else if( (itemType == TYPE_INDENT || itemType == TYPE_INPUT)
				&& labelTextObj != null ) {

			// render a short label left to the text

			final boolean indent = itemType == TYPE_INDENT;

			height = doLayout(KDisplay.BORDER, KDisplay.BORDER / 2,
					KDisplay.CONTENT_WIDTH - KDisplay.BORDER * 2, true)
					+ KDisplay.BORDER;

			final String lbl = labelTextObj.toString();
			if( lbl.length() < 4 && image == null && indent ) {
				//nebeneinander
				final Font f = getLabelStyle(getState()).getFont();

				contentX = KDisplay.BORDER * 2
						+ Math.max(KDisplay.SYMBOL_WIDTH, f.stringWidth(lbl));
				contentW = KDisplay.CONTENT_WIDTH - KDisplay.BORDER
						- contentX;
				contentH = getPrefContentHeight(contentW);
				if( contentH != 0 ) {
					contentY = KDisplay.BORDER / 2;
					height = Math.max(height, contentH + KDisplay.BORDER);
					rectContentFill.x = contentX;
					rectContentFill.y = 0;
					rectContentFill.h = contentH + KDisplay.BORDER;
					rectContentFill.w = contentW;
				}
			}
			else {
				contentW = KDisplay.CONTENT_WIDTH
						- (indent ? KDisplay.BORDER * 3
								- KDisplay.SYMBOL_WIDTH : KDisplay.BORDER * 2);

				contentH = getPrefContentHeight(contentW);
				if( contentH != 0 ) {
					//	height += KForm.BORDER;
					contentY = height + KDisplay.BORDER / 2;
					contentX = indent ? KDisplay.BORDER * 2
							+ KDisplay.SYMBOL_WIDTH : KDisplay.BORDER;
					rectContentFill.x = contentX - KDisplay.BORDER;
					rectContentFill.y = contentY - KDisplay.BORDER / 2;
					rectContentFill.h = contentH + KDisplay.BORDER;
					rectContentFill.w = contentW + 2 * KDisplay.BORDER;

					height += rectContentFill.h;
				}
			}

			rectBackgroundFill.h = height;
		}
		else if( itemType == TYPE_TAB ) {
			height = doLayout(KDisplay.BORDER, KDisplay.BORDER / 2,
					KDisplay.CONTENT_WIDTH - -KDisplay.BORDER * 2, false)
					+ KDisplay.BORDER;

			contentW = KDisplay.CONTENT_WIDTH;
			contentH = getPrefContentHeight(KDisplay.CONTENT_WIDTH);

			rectContentFill.x = 0;
			rectContentFill.y = 0;
			rectContentFill.w = KDisplay.CONTENT_WIDTH;
			rectContentFill.h = getPrefContentHeight(KDisplay.CONTENT_WIDTH);

			//contentFillX += contentH;

			rectLabel.y += contentH;
			contentX = 0;
			contentY = 0;
			rectBackgroundFill.y = contentH;
			rectBackgroundFill.h = formattedLabel == null ? 0
					: (formattedLabel.getHeight() + KDisplay.BORDER);
		}
		else {
			height = doLayout(KDisplay.BORDER, KDisplay.BORDER / 2,
					KDisplay.CONTENT_WIDTH - KDisplay.BORDER * 2, false)
					+ KDisplay.BORDER;

			rectContentFill.x = 0;
			rectContentFill.w = KDisplay.CONTENT_WIDTH;
			if( image == null && labelTextObj != null ) {
				rectContentFill.y = contentY - KDisplay.BORDER / 2;
				rectContentFill.h = contentH + KDisplay.BORDER;
			}
			else {
				rectContentFill.y = 0;
				rectContentFill.h = height;
			}

			rectBackgroundFill.h = height;

			if( this instanceof KChoiceGroup ) {
				contentX = 0;
				contentW = KDisplay.CONTENT_WIDTH;
			}
		}
	}

	/**
	 * Helper method called by doLayout for all cases except INDENT.
	 */
	protected int doLayout(final int x_, final int y_, final int w_,
			final boolean ignoreContent) {

		final int hAlign = getHAlign();

		int x = x_;
		final int y = y_;
		int w = w_;

		if( hAlign == Graphics.HCENTER ) {
			int h = 0;
			if( image != null ) {
				imgX = x + (w - image.getWidth() / imgCols) / 2;
				imgY = y;
				h += image.getHeight() / imgRows;
			}

			if( labelTextObj != null ) {
				if( h != 0 ) {
					h += KDisplay.BORDER / 2;
				}

				rectLabel.x = x;
				rectLabel.y = y + h;
				rectLabel.w = w;
				formattedLabel = new WordWrap(getLabelStyle(getState()),
						labelTextObj.toString(), w);
				h += formattedLabel.getHeight();
			}

			if( !ignoreContent ) {
				contentH = getPrefContentHeight(w);
				if( contentH != 0 ) {
					contentX = x;

					if( h != 0 ) {
						h += KDisplay.BORDER;
					}

					contentY = y + h;
					h += contentH;
					contentW = w;
				}
			}

			return h;
		}

		int imgH = 0;
		int txtH = 0;

		if( image != null ) {
			imgX = x;
			imgY = y;

			//
			// Check for special case in tag <a>. If the image is an arrow then
			// the label is full fullWidth, next line is image and then the text.
			if( image == KDisplay.SYMBOLS ) {
				imgY += 1;
				if( labelTextObj != null ) {

					rectLabel.x = x;
					rectLabel.y = y;
					rectLabel.w = w;
					formattedLabel = new WordWrap(getLabelStyle(getState()),
							labelTextObj.toString(), w);
					imgY += formattedLabel.getHeight() + KDisplay.BORDER;
				}
			}
			else {
				//
				// Default is center the image horizontal
				final int delta = image.getWidth() / imgCols
						+ KDisplay.BORDER;
				x += delta;
				w -= delta;
			}
			imgH = image.getHeight() / imgRows;
		}

		if( labelTextObj != null ) {
			rectLabel.x = x;
			rectLabel.y = y;
			rectLabel.w = w;
			formattedLabel = new WordWrap(getLabelStyle(getState()),
					labelTextObj.toString(), w);
			txtH = formattedLabel.getHeight();
		}

		if( !ignoreContent ) {

			//
			// Check for special case in tag <a>. If the image is an arrow then
			// the label is full fullWidth, next line is image and then the text.
			if( image == KDisplay.SYMBOLS ) {
				final int delta = image.getWidth() / imgCols
						+ KDisplay.BORDER;
				x += delta;
				w -= delta;
			}

			contentH = getPrefContentHeight(w);
			if( contentH != 0 ) {
				contentX = x;
				if( txtH != 0 ) {
					txtH += KDisplay.BORDER;
				}
				contentY = y + txtH;
				txtH += contentH;
				contentW = w;
			}
		}

		final int h = Math.max(imgH, txtH);

		if( getVAlign() == Graphics.VCENTER ) {

			if( img0 != KDisplay.ICON_ARROW_OFF ) {
				imgY += (h - imgH) / 2;
			}
			rectLabel.y += (h - txtH) / 2;
			contentY += (h - txtH) / 2;
		}

		return h;
	}

	/**
	 * Returns the label of this item as string.
	 */
	public String getLabel() {

		/* Make sure the label is a valid object. */
		if( labelTextObj != null ) {
			return labelTextObj.toString();
		}

		/* Return a default string so the system do no crash. */
		return "null";
	}

	protected abstract int getPrefContentHeight(int w);

	int getHAlign() {
		int a = align & (Graphics.HCENTER | Graphics.LEFT | Graphics.RIGHT);

		if( a == 0 ) {
			a = getContentStyle(getState()).align
					& (Graphics.HCENTER | Graphics.LEFT | Graphics.RIGHT);
		}

		return a == 0 ? (itemType == TYPE_BUTTON ? Graphics.HCENTER
				: Graphics.LEFT) : a;
	}

	int getVAlign() {
		int a = align & (Graphics.VCENTER | Graphics.TOP | Graphics.BOTTOM);

		if( a == 0 ) {
			a = getContentStyle(getState()).align
					& (Graphics.VCENTER | Graphics.TOP | Graphics.BOTTOM);
		}

		return a == 0 ? (itemType == TYPE_INDENT ? Graphics.TOP
				: Graphics.VCENTER) : a;
	}

	/**
	 * Get the content style of this item. If the item has no explicit content
	 * style attached then it will use the content style of the parent
	 * {@link KForm} instance.
	 * 
	 * @param state
	 *            the id of the content style that is requested.
	 * @return the {@link KStyle} object for the given index.
	 */
	public KStyle getContentStyle(final int state) {

		//
		// First try to get the style from this item
		KStyle s = null;
		if( kStyles != null ) {
			s = kStyles[state];
		}

		//
		// If this item has no style then use the style from the KForm parent.
		return s == null ? ownerForm.getStyle(styleId + state + CONTENT) : s;
	}

	/**
	 * Get the label style of this item. If the item has no explicit label
	 * style attached then it will use the label style of the parent
	 * {@link KForm} instance.
	 * 
	 * @param state
	 *            the id of the label style that is requested.
	 * @return the {@link KStyle} object for the given index.
	 */
	public KStyle getLabelStyle(final int state) {

		//
		// First try to get the style from this item
		KStyle s = null;
		if( kStyles != null ) {
			s = kStyles[state + LABEL];
		}
		//
		// If this item has no style then use the style from the KForm parent.
		return s == null ? ownerForm.getStyle(styleId + state + LABEL) : s;
	}

	protected void keyPressed(final int code) {
		if( code == Canvas.FIRE && defaultCommand != null ) {
			ownerForm.sendCommand(defaultCommand, this);
		}
	}

	protected void keyReleased(final int code) {
	}

	protected void keyRepeated(final int code) {
		keyPressed(code);
	}

	/**
	 * Mark the layout of this element as invalid. This forces the item to
	 * layout itself when KUI will paint this item.
	 */
	protected final void invalidate() {
		height = Tools.NOTHING;
	}

	/**
	 * Render this item into the given {@link Graphics} context.
	 * 
	 * @param g
	 */
	synchronized void paint(final Graphics g) {

		if( itemType != TYPE_FULLSCREEN
				&& labelTextObj != null
				&& (formattedLabel == null || !labelTextObj.toString().equals(
						formattedLabel.toString())) ) {
			invalidate();
		}

		//
		// If the control is not fully layout yet then do it first.
		if( height == Tools.NOTHING ) {
			doLayout();
		}

		//
		// Get the style of this control
		final int state = getState();
		final KStyle labelStyle = getLabelStyle(state);
		final KStyle contentSytle = getContentStyle(state);

		labelStyle.fill(g, rectBackgroundFill);

		if( itemType != TYPE_HLINE ) {
			contentSytle.fill(g, rectContentFill);
		}

		//
		// If this item has an image attached then render the image
		if( image != null ) {

			final int w = (image.getWidth() + imgCols / 2) / imgCols;
			final int h = (image.getHeight() + imgRows / 2) / imgRows;

			//
			// Store the current clip settings so we can restore them later.
			final int cX = g.getClipX();
			final int cY = g.getClipY();
			final int cW = g.getClipWidth();
			final int cH = g.getClipHeight();

			g.setClip(imgX, imgY, w, h);
			int nr;
			switch( state ) {
			case STATE_FOCUSSED:
				nr = img1;
				break;
			case STATE_PRESSED:
				nr = img2;
				break;
			default:
				nr = img0;
			}

			g.drawImage(image, imgX
					- ((nr % imgCols) * image.getWidth() + imgCols / 2)
					/ imgCols, imgY
					- ((nr / imgCols) * image.getHeight() + imgRows / 2)
					/ imgRows, Graphics.TOP | Graphics.LEFT);

			//
			// Restore the old clipping setting.
			g.setClip(cX, cY, cW, cH);
		}

		//
		// Check for a label and render the label. NOTE: there is a difference between
		// label and text! In case of a button the caption is text NOT label!
		if( formattedLabel != null ) {

			formattedLabel.paint(g, rectLabel.x, rectLabel.y, Graphics.LEFT,
					labelStyle);

			//
			// Check if the label has a underline flag
			if( (labelStyle.font & 512) != 0 ) {
				//
				// Store current color first, draw the line with xc color and then restore
				// the current color again
				final int curColor = g.getColor();
				g.setColor(contentSytle.xc);
				g.drawLine(rectLabel.x, rectLabel.y
						+ formattedLabel.getHeight() + KDisplay.BORDER / 2,
						rectLabel.x + rectLabel.w, rectLabel.y
								+ formattedLabel.getHeight()
								+ KDisplay.BORDER / 2);
				g.setColor(curColor);
			}
		}

		//
		// Render the content pane of this item.
		g.translate(contentX, contentY);
		paint(g, contentW, contentH);
		g.translate(-contentX, -contentY);

		labelStyle.frame(g, 0, 0, KDisplay.CONTENT_WIDTH, height);

		if( !(this instanceof KChoiceGroup) ) {
			contentSytle.frame(g, rectContentFill);
		}
	}

	/**
	 * Render the content area of this item into the given {@link Graphics}
	 * context.
	 * 
	 * @param g
	 * @param cw
	 * @param ch
	 */
	protected abstract void paint(Graphics g, int cw, int ch);

	public void pointerPressed(final int x, final int y) {
		keyPressed(Canvas.FIRE);
	}

	public void pointerReleased(final int x, final int y) {
	}

	public void pointerDragged(final int x, final int y) {
	}

	public void setLabel(final Object l_) {

		Object l = l_;
		if( labelTextObj == null ? l == null : labelTextObj.equals(l) ) {
			return;
		}

		//
		// If a label begins with an ">" then a small arrow image should appear. The
		// ">" will be stripped from the text.
		final String labelText = l.toString();
		if( labelText != null && labelText.length() > 0
				&& labelText.charAt(0) == '>' ) {
			image = KDisplay.SYMBOLS;
			imgRows = KDisplay.SYMBOL_COUNT;
			imgCols = 1;
			img0 = KDisplay.ICON_ARROW_OFF;
			img1 = KDisplay.ICON_ARROW_ON;
			if( labelText.length() > 1 ) {
				l = labelText.substring(1);
			}
			else {
				l = null;
			}
		}
		else if( "*".equals(l) ) {
			image = KDisplay.SYMBOLS;
			imgRows = KDisplay.SYMBOL_COUNT;
			imgCols = 1;
			img0 = KDisplay.ICON_BULLET;
			img1 = KDisplay.ICON_BULLET;
			l = null;
		}

		synchronized( this ) {
			this.labelTextObj = l;
			invalidate();
		}

		if( ownerForm != null ) {
			KDisplay.requestRepaint();
		}
	}

	public void setDefaultCommand(final Command cmd) {
		defaultCommand = cmd;
	}

	/**
	 * Called by the system when traversal has entered the item or has
	 * occurred within the item. The direction of traversal and the item's
	 * visible rectangle are passed into the method. The method must do one of
	 * the following: it must either update its state information pertaining
	 * to its internal traversal location, set the return rectangle to
	 * indicate a region associated with this location, and return true; or,
	 * it must return false to indicate that this item does not support
	 * internal traversal, or that that internal traversal has reached the
	 * edge of the item and that traversal should proceed to the next item if
	 * possible.
	 * 
	 * @param dir
	 *            the direction of traversal, one of Canvas.UP, Canvas.DOWN,
	 *            Canvas.LEFT, Canvas.RIGHT, or NONE.
	 * @param viewportWidth
	 *            the width of the container's viewport.
	 * @param viewportHeight
	 *            the height of the container's viewport.
	 * @param visRect_inout
	 *            passes the visible rectangle into the method, and returns
	 *            the updated traversal rectangle from the method.
	 * @return if internal traversal had occurred, false if traversal should
	 *         proceed out.
	 */
	public boolean traverse(final int dir, final int viewportWidth,
			final int viewportHeight, final int[] visRect_inout) {
		return defaultCommand != null && getState() != STATE_FOCUSSED;
	}

	/**
	 * Called by the system when traversal has occurred out of the item. This
	 * may occur in response to the CustomItem having returned false to a
	 * previous call to traverse(), if the user has begun interacting with
	 * another item, or if Form containing this item is no longer current. If
	 * the CustomItem is using highlighting to indicate internal traversal,
	 * the CustomItem should set its state to be unhighlighted and request a
	 * repaint. (Note that painting will not occur if the item is no longer
	 * visible.)
	 */
	protected void traverseOut() {
		// does nothing if not overwritten.
	}

	public void setLayout(final int layout) {
		itemType = layout;
		invalidate();
	}

	public void setContentStyle(final int state, final KStyle style) {

		if( kStyles == null ) {
			kStyles = new KStyle[6];
		}

		kStyles[state + CONTENT] = style;
	}

	public void setImage(final Image img) {
		setImage(img, 1, 1, 0, 0, 0);
	}

	public void setImage(final Image image, final int imgCols,
			final int imgRows, final int plainImageIndex,
			final int focusImageIndex, final int pressedImageIndex) {
		this.image = image;
		this.imgCols = imgCols;
		this.imgRows = imgRows;
		this.img0 = plainImageIndex;
		this.img1 = focusImageIndex;
		this.img2 = pressedImageIndex;
		invalidate();
		if( ownerForm != null ) {
			KDisplay.requestRepaint();
		}
	}

	public void setLabelStyle(final int state, final KStyle style) {

		if( kStyles == null ) {
			kStyles = new KStyle[6];
		}

		kStyles[state + LABEL] = style;
	}

	/**
	 * Let KUI classes access the KDisplay singleton.
	 * 
	 * @return
	 */
	final protected KDisplay getKDisplay() {
		return KDisplay.kDisplay;
	}
}
