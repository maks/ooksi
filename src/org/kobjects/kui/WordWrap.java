/* Copyright (c) 2002,2003, Stefan Haustein, Oberhausen, Rhld., Germany */
package org.kobjects.kui;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * A class supporting word wrap for MIDP.
 */
public final class WordWrap {

	/** The kStyle of the text */
	private KStyle kStyle;

	/** the actual text */
    private String strText;

    /** the given horizontal space for the text */
    int fullWidth;

    /** the width of the cut away (for example a image) */
    int cutAwayWidth;

    /** the height of the cut away (for example a image) */
    int cutAwayHeight;

    private final StringBuffer positions = new StringBuffer();

    /**
     * Initializes the WordWrap object with the given Font, the text string to
     * be wrapped, and the target fullWidth.
     *
     * @param font:
     *            The Font to be used to calculate the character widths.
     * @param strText:
     *            The text string to be wrapped.
     * @param width:
     *            The available space for the text.
     */
    public WordWrap(KStyle style, String txt, int width, int cutWidth, int cutHeight ) {

        kStyle = style;
        strText = txt;
        fullWidth = width;
        cutAwayWidth = cutWidth;
        cutAwayHeight = cutHeight;

        wrapText();
    }

    /**
     * Initializes the WordWrap object with the given Font, the text string to
     * be wrapped, and the target fullWidth.
     *
     * @param font:
     *            The Font to be used to calculate the character widths.
     * @param strText:
     *            The text string to be wrapped.
     * @param width:
     *            The available space for the text.
     */
    public WordWrap(KStyle style, String txt, int width) {
    	this(style, txt, width, 0,0);
    }

    /**
     * Utility method that does the actual text wrapping.
     */
    private void wrapText()
    {
    	 int curPos = 0;
         final int strLen = strText.length();
         final Font font = kStyle.getFont();

         //
         // Process all character of given string.
         while(curPos < strLen) {

         	final int start = curPos;
         	int i = curPos;

         	while (true) {

         		//
         		// Search for the next non alpha numeric character
         		while( i < strLen && strText.charAt(i) > ' ' ) {
         			i++;
         		}

                //
                // Because of the cut away support we need to calculate the available
                // width per line.
                int availableWidth;
                if( getHeight() > cutAwayHeight ) {
                	availableWidth = fullWidth;
                }
                else {
                	availableWidth = fullWidth - cutAwayWidth;
                }

         		//
         		// Calculate how much space we would need from the current position
         		// to the found non alphanumeric character or text length.
         		final int w = font.stringWidth(strText.substring(start, i));

         		//
         		// If we would need more space for a single word then we have we need
         		// to find a maximal count of character we display in one row and split
         		// the word at this position.
         		if (curPos == start && w > availableWidth ) {

     				//
     				// Find the position where we have to split the word.
     				while (i > curPos
     						&& font.stringWidth(strText.substring(start, --i)) > availableWidth) {
     					// Empty block
     				}
     				curPos = i;
     				break;
     			}



         		//
         		// If we could display more then this just store that position so we can
         		// use this position if we did not found a better one.
         		if( w <= availableWidth ) {
         			curPos = i;
         		}

         		//
         		// If we need more space then we have or we found a newline character
         		// the use the last valid position we found
         		if( w > availableWidth || i >= strLen || strText.charAt(i) == '\n'
         			|| strText.charAt(i) == '\r' ) {
         			break;
         		}

         		i++;
         	}

         	//
         	// Save the position
         	positions.append((char) (curPos >= strLen ? strLen : ++curPos));
         }
    }

	public int getHeight() {
		final Font font = kStyle.getFont();
		return positions.length() * font.getHeight();
	}

	public void paint(final Graphics g, final int x, final int y, final int textAlign) {
		paint(g, x, y, textAlign, kStyle);
	}

	public void paint(final Graphics g, final int x_, final int y_, final int textAlign_, final KStyle kStyle) {

		int x = x_;
		int y = y_;
		int textAlign = textAlign_;

		final int c = g.getColor();
		y += KDisplay.applyStyle(kStyle, g);
		x ++;

		switch(textAlign){
		case Graphics.HCENTER: x += fullWidth / 2; break;
		case Graphics.RIGHT: x += fullWidth; break;
		default: textAlign = Graphics.LEFT; break;
		}

		int pos = 0;
		final Font font = kStyle.getFont();
		final int count = positions.length();
		for( int i = 0; i < count; i++ ) {

			final int cut = positions.charAt(i);

			int posX = x;
			if( y < cutAwayHeight ) {
				posX += cutAwayWidth;
			}

			g.drawSubstring(
					strText,
					pos,
					cut > 0 && strText.charAt(cut-1) <= ' ' ? cut-1-pos : cut-pos,
					posX,
					y,
					textAlign|Graphics.TOP);
              pos = cut;
              y += font.getHeight();
          }

		g.setColor(c);
	}


	public Vector extract() {

		final Vector result = new Vector();

		int pos = 0;
		final int count = positions.length();
		for(int i = 0; i < count; i++){
			final int cut = positions.charAt(i);
       	  	final String line = strText.substring(pos, /*cut > 0 /*&& strText.charAt(cut-1) <= ' '  ? cut-1 :*/ cut);
       	  	result.addElement(line);
       	  	pos = cut;
         }

         return result;
	}

	/**
	 * Overwrite this method so we get the plain String representation of the
	 * wordwrap.
	 */
	public String toString()
	{
		return strText;
	}
}