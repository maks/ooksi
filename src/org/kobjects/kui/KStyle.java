package org.kobjects.kui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.kobjects.utils4me.KRectangle;
import org.kobjects.utils4me.Tools;


/**
 * Simple data class to store style definitions. Keep in mind that most information
 * are shared by multiple object so be careful if you try to manipulate one instance
 * of {@link KStyle} directly.
 *
 * @author stefan.haustein
 */
public final class KStyle {

	public static final int STYLE_CONTENT = 0;
	public static final int STYLE_ITEM = 1;
	public static final int STYLE_EDIT = 7;
	public static final int STYLE_BUTTON = 13;
	public static final int STYLE_TITLE = 19;
	public static final int STYLE_COMMAND = 20;
	public static final int STYLE_POPUP = 23;

	public static final int[] FR_MEDIUM_BLUE = {5, 0,1,0xccccff};
	public static final int[] FR_DARK_BLUE = {5, 0,1,0xaaaaff};
	public static final int[] FR_BLACK = {1,1,1,0};

	public static final int[] BG_LIGHT_BLUE = {32, 0x0eeeeff};
	public static final int[] BG_MEDIUM_BLUE = {32, 0x0ccccff};
	public static final int[] BG_DARK_BLUE = {32, 0x0aaaaff};
	public static final int[] BG_WHITE = {32, 0x0ffffff};

	public static final KStyle ST_BG = new KStyle(0, 9, null, BG_LIGHT_BLUE, 0, 0);
	public static final KStyle ST_TITLE = new KStyle(0, 9, null, BG_MEDIUM_BLUE, 0, 0);
	public static final KStyle ST_LABEL = new KStyle(0, 8, null, BG_LIGHT_BLUE, 0x07d147d, 0);
	public static final KStyle ST_LABEL_FOCUS = new KStyle(0x3c143c, 8, null, BG_DARK_BLUE, 0x03c143c, 0);
	public static final KStyle ST_BUTTON_LABEL = new KStyle(0, 8, null, null, 0x07d147d, 0);

	public static final KStyle ST_PLAIN = new KStyle(0, 8, null, null, 0x07d147d, 0);
	public static final KStyle ST_PLAIN_FOCUS = new KStyle(0, 8, null, BG_DARK_BLUE, 0x03c143c, 0);

	public static final KStyle ST_INPUT = new KStyle(0, 8, null, null, 0x07d147d, 0);
	public static final KStyle ST_INPUT_FOCUS = new KStyle(0, 9, null, BG_WHITE, 0x07d147d, 0);

	public static final KStyle ST_BUTTON = new KStyle(0, 8, FR_MEDIUM_BLUE, BG_MEDIUM_BLUE, 0x07d147d, 0);
	public static final KStyle ST_BUTTON_FOCUS = new KStyle(0, 8, FR_DARK_BLUE, BG_DARK_BLUE, 0x07d147d, 0);

	public static final KStyle ST_POPUP = new KStyle(0, 8, FR_BLACK, BG_LIGHT_BLUE, 0x07d147d, 0);
	public static final KStyle ST_POPUP_FOCUS = new KStyle(0, 8, null, BG_MEDIUM_BLUE, 0x07d147d, 0);

	/** Constant for drawing a simple border */
	public static final int FRAME_SIMPLE = 1;
	public static final int FRAME_ROUNDED = 5;
	public static final int FRAME_RELIEF = 17;

	public static final int FILL_TRANSPARENT = 0;
	public static final int FILL_PLAIN = 32;
	public static final int FILL_GRADIENT = 128+64;

	/** Text Color */
	public int color;

	/** Text Font */
	public int font;

	/** Background array, see apply */
    public int[] bgValues;

	/**
	 * An integer array defining the frame. The first value defines the type of the
	 * frame (FRAME_SIMPLE, FRAME_ROUNDED, FRAME_RELIEF). The following two
	 * values define the the width of the frame in pixels (starting and end point
	 * relative to the filled area). Additional values define the frame color. If the
	 * array is omitted (null), no frame is drawn.
	 */
    public int[] frameValues;

	public int xc;

	public int align;

	/**
	 * Creates a style to be used with KUI elements.
	 *
	 * @param textColor Text color ARGB value.
	 * @param font MIDP font bit combination (Font.SIZE_SMALL, Font.STYLE_BOLD, ...)
	 * @param frame An integer array defining the frame. The first value defines the type of the frame
	 *       (FRAME_SIMPLE, FRAME_ROUNDED, FRAME_RELIEF). The following two values define the the
	 *       width of the frame in pixels (starting and end point relative to the filled area).
	 *       Additional values define the frame color. If the array is omitted (null), no frame is drawn
	 * @param bgValues An integer array defining the bgValues color. The first value defines the
	 *       fill style (FILL_PLAIN, FILL_GRADIENT). In the case of FILL_PLAIN, the next value defines the
	 *       fill color. In the case of FILL_GRADIENT, a sequence of RGB color values and loadingProgress valuese
	 *       (in the range from 0...255) defines the fill gradient.
	 * @param extraColor An extra color, used e.g for the sub-focus in choice groups
	 * @param align A combination of Graphics.TOP/BOTTOM/VCENTER and Graphics.LEFT/RIGHT/HCENTER.
	 */
    public KStyle(int textColor, int font, int[] frame, int[] background, int extraColor, int align){
    	this.color = textColor;
    	this.font = font;
    	this.frameValues = frame;
    	this.bgValues = background;

    	this.xc = extraColor;
    	this.align = align;
    }

    /**
	 * Copy constructor.
	 *
	 * @param other the {@link KStyle} to clone.
	 */
    public KStyle( KStyle other ) {
    	this.color = other.color;
    	this.font = other.font;
    	this.frameValues = other.frameValues;
    	this.bgValues = other.bgValues;

    	this.xc = other.xc;
    	this.align = other.align;
    }


    public int fill(final Graphics gc, final KRectangle rect){
    	return fill(gc, rect.x, rect.y, rect.w, rect.h);
    }


    /**
     * Fill the given rectangle with this style.
     * @param gc a valid reference to a {@link Graphics} context.
     * @param x0 x coordinate of the upper left corner,
     * @param y0 y coordinate of the upper left corner.
     * @param w the width of the rectangle
     * @param h the height of the rectangle
     * @return the return the font top offset in pixel the font set in this style.
     */
    public int fill(final Graphics gc, final int x0, final int y0, final int w, final int h){

    	//
    	// If we don't have a valid array then draw no backround
    	final int bgType = (bgValues == null ? KStyle.FILL_TRANSPARENT : bgValues[0]);

    	//
    	// Check for possible background types.
    	if( (bgType & (KStyle.FILL_PLAIN)) != 0 ) {

    		//
    		// If background style is plain then just fill the rectangle
    		gc.setColor(bgValues[1]);
    		gc.fillRect(x0, y0, w, h);

    	}
    	else if( (bgType & KStyle.FILL_GRADIENT) != 0 ) {

    		int r = 0;
    		int g = 0;
    		int b = 0;

    		int deltaRed = 0;
    		int deltaGreen = 0;
    		int deltaBlue = 0;

    		//
    		// Start position of the gradient next segment.
    		int nextSegStartPos = y0;
	    	int cp = 1;

	    	//
	    	// We draw a single line for each vertical pixel
    		for( int y = y0; y < y0 + h; y++ ) {

    			//
    			// Check if a new gradient segment start at the current y position
    			if(y >= nextSegStartPos){

    				//
    				// Make sure that the array contains enough data. If not use
    				// backup values to prevent a exception.
    				if( cp > bgValues.length-2 ) {

    					//
    					// This will cause a simple fill with the current color
    					deltaRed=0;
    					deltaGreen=0;
    					deltaBlue=0;

    					// XXX: Lets hope we never find a display with more then
    					// 1000 pixel in y direction
    					nextSegStartPos = 9999;
    				}
    				else {

    					//
    					// Get the start color from the background array for the current
    					// segment.
    					final int startRGBColor = bgValues[cp++];

    					//
    					// Calculate the Y position where the next gradient segment will
    					// start. Make sure it's in valid range.
    					nextSegStartPos = y0 + h * (cp == bgValues.length-1 ? 256 : bgValues[cp++]) / 255;
    					if(nextSegStartPos >= y0+h) {
    						nextSegStartPos = y0+h-1;
    					}

    					//
    					// Get the destination color from the background array.
    					final int destRGBColor = bgValues[cp];

    					//System.out.println("from "+Integer.toString(rgb0, 16)+ " to "+ bg[cp-1]/255+" % c="+Integer.toString(rgb1, 16));

    					//
    					// Calculate how many single line we can draw in this gradient
    					// segment. Make sure we have at least one line, else we run
    					// into a division by zero
    					int steps = (nextSegStartPos - y);
    					if(steps == 0) {
    						steps = 1;
    					}

    					r =  startRGBColor        & 0x0ff0000;
    					g = (startRGBColor << 8)  & 0x0ff0000;
    					b = (startRGBColor << 16) & 0x0ff0000;

    					//
    					// Calculate the delta value for each RGB component. Each time we
    					// draw a line we increase the RGB value by these deltas to create
    					// the gradient effect
    					deltaRed =  ((destRGBColor & 0x0ff0000) - r) / steps;
    					deltaGreen = (((destRGBColor << 8)  & 0x0ff0000) - g) / steps;
    					deltaBlue = (((destRGBColor << 16) & 0x0ff0000) - b) / steps;

    					//System.out.println("dr: "+dr/65536.0+" dg:"+dg/65536.0+ " db:"+db/65536.0 + " step " + steps);
    				}
    			}

    			//
    			// Build 24bit RGB color value
    			final int c = (r & 0x0ff0000)  | ((g >> 8) & 0x0ff00) | ((b >> 16) & 0x0ff);

    			//System.out.println("Before: " + Integer.toHexString(c));
    			gc.setColor(c);
    			gc.drawLine(x0, y, x0+w, y);

    			//
    			// Add RGB delta values to the current color.
    			r += deltaRed;
    			g += deltaGreen;
    			b += deltaBlue;
    		}
    	}

    	return KDisplay.applyStyle(this, gc);
    }

    public int frame(final Graphics gc, final KRectangle rect){
    	return frame(gc, rect.x, rect.y, rect.w, rect.h);
    }

    public int frame(final Graphics gc, final int x0, final int y0, final int w, final int h){

    	//System.out.println("grad("+x0+","+y0+","+w+","+h);

    	final int[] fr = this.frameValues;
    	final int frType = fr == null ? 0 : fr[0];


    	//System.out.println("OK");

    	if((frType & KStyle.FRAME_SIMPLE) != 0){

    		int pos = 3;

   			for(int i = fr[1]; i <= fr[2]; i++){

   				if((frType & KStyle.FRAME_RELIEF) == FRAME_RELIEF){
   					gc.setColor(fr[3]);
       				gc.drawLine(x0-i, y0-i, x0+w+i+i, y0-i);
       				gc.drawLine(x0-i, y0-i, x0-i, y0+h+i+i);

       				gc.setColor(fr[4]);
       				gc.drawLine(x0-i, y0+h+i+i, x0+w+i+i, y0+h+i+i);
       				gc.drawLine(x0+w+i+i, y0-i, x0+w+i+i, y0+h+i+i);
   				}
   				else {
   					gc.setColor(fr[pos]);

   					if((frType & KStyle.FRAME_ROUNDED) == FRAME_ROUNDED && i > 0){

   						final int j = i*i;

   						gc.drawLine(x0+j, y0-i, x0+w-j-j, y0-i);
   	       				gc.drawLine(x0+j, y0+h+i-1, x0+w-j-j, y0+h+i-1);

   	       				gc.drawLine(x0-i, y0+j, x0-i, y0+h-j-j);
   	       				gc.drawLine(x0+w+i-1, y0+j, x0+w+i-1, y0+h-j-j);
   					}
   					else {
   						gc.drawRect(x0-i,y0-i,w+i+i-1,h+i+i-1);
   					}

   					if(pos < fr.length-1){
   						pos++;
   					}
   				}
    		}
    	}

    	return KDisplay.applyStyle(this, gc);
    }


    /**
     * Creates a new {@link KStyle} instance where every field in this style is null
     * is replaced by the given style.
     *
     * @param other
     * @return
     */
    public KStyle merge(final KStyle other) {

    	final KStyle kStyle = new KStyle(this);

    	if( kStyle.color == Tools.NOTHING ) {
    		kStyle.color = other.color;
    	}

    	if( kStyle.font == Tools.NOTHING ) {
    		kStyle.font = other.font;
    	}

    	if( kStyle.frameValues == null ) {
    		kStyle.frameValues = other.frameValues;
    	}

    	if( kStyle.bgValues == null ) {
    		kStyle.bgValues = other.bgValues;
    	}

    	if( kStyle.xc == Tools.NOTHING ) {
    		kStyle.xc = other.xc;
    	}

    	if( kStyle.align == Tools.NOTHING ) {
    		kStyle.align = other.align;
    	}

    	return kStyle;
    }


    /**
     * Return the {@link Font} of this style.
     * @return a {@link Font} instance
     */
	public Font getFont() {
		return KDisplay.kDisplay.getFont(font);
	}

	/**
	 * Draw the given string at the given coordinates at the given {@link Graphics}
	 * context using the font settings of this style.
	 *
	 * @param g a valid {@link Graphics} instance.
	 * @param s a {@link String} with the text to draw.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 */
	public void drawString(final Graphics g, final String s, final int x, final int y_) {
		int y = y_;
		g.setFont(getFont());
		y += KDisplay.kDisplay.fontTopOffset[font & 31];
		g.drawString(s, x, y, Graphics.TOP|Graphics.LEFT);
	}

    // --- overwrite toString() method only in debug mode
//#ifdef debug_style_
//@	/**
//@	 *
//@	 */
//@	public String toString() {
//@
//@		String sBackground = "Background :";
//@
//@		if( bgValues != null ) {
//@			for( int i = 0; i < bgValues.length; i++ ) {
//@				sBackground += bgValues[i] + " ";
//@			}
//@		}
//@		else {
//@			sBackground += "NULL ";
//@		}
//@
//@		int[] bg = this.bgValues ;
//@		int bgType = bg == null ? 0 : bg[0];
//@
//@		if( (bgType & (KStyle.FILL_PLAIN)) != 0) {
//@			sBackground += "PLAIN, " + bg[1];
//@		}
//@		else if( (bgType & KStyle.FILL_GRADIENT) != 0) {
//@			sBackground += "GRADIENT, " + bg[2] + ", " + bg[3];
//@		}
//@		else {
//@			sBackground +="NONE";
//@		}
//@
//@		sBackground += " Color: " + color;
//@
//@
//@		return sBackground;
//@	}
//#endif

}
