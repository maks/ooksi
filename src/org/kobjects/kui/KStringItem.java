package org.kobjects.kui;

import javax.microedition.lcdui.Graphics;

/**
 * Merged functionality of KString, KText, KTextItem, and KOfferTextItem
 */
public class KStringItem extends KItem {

    public Object text = null;

    // --- variables set in doLayout

    /** Word wrap split positions, set in doLayout() */
    private WordWrap formattedText;

    public KStringItem(Object label, Object text){
    	this(label, text, TYPE_PLAIN);
    }

    public KStringItem(Object label, Object text, int appearance) {
    	super(label, appearance);

    	//System.out.println("layout = "+layout+" for "+label+"/"+text);

        this.text = text;

    }

    public void paint(Graphics g, int contentW, int contentH) {

//	    if(image != null){
//	    	int x = imgX;
//	    	int w = image.getWidth();
//	     	int h = (image.getHeight()+imgCount/2) / imgCount;
//
//	    	int cX = g.getClipX();
//	    	int cY = g.getClipY();
//	    	int cW = g.getClipWidth();
//	    	int cH = g.getClipHeight();
//
//	    	g.setClip(x, imgY, w, h);
//	    	int nr;
//	    	switch(state){
//	    	case STATE_FOCUSSED:
//	    		nr = img1; break;
//	    	case STATE_PRESSED:
//	    		nr = img2; break;
//	    	default:
//	    		nr = img0;
//	    	}
//	    	g.drawImage(image, x, imgY - (nr * image.getHeight() + imgCount/2) / imgCount, Graphics.TOP|Graphics.LEFT);
//
//	    	g.setClip(cX, cY, cW, cH);
//	    }

    	//
    	// Draw the content text
    	if(formattedText != null){

   		    //
    		// SKerkewitz: this call is senseless because the paint() method of
    		// WordWrap apply color/font setting by it's own. So we give WordWrap
    		// the correct style and everything is fine.
    		//getContentStyle(getState()).apply(g);
            formattedText.paint(g, 0, 0, getHAlign(), getContentStyle(getState()));
   		}
    }


    /**
     * SKerkewitz: We need to override the method in KItem here because we need
     * the full control.
     */
    protected int doLayout(int x_, int y, int w_, boolean ignoreContent) {
    	int x = x_;
    	int w = w_;
    	int hAlign = getHAlign();

    	if(hAlign == Graphics.HCENTER){
    		int h = 0;
    		if(image != null){
    			imgX = x + (w - image.getWidth()/imgCols) / 2;
    			imgY = y;
    			h += image.getHeight() / imgRows;
    		}

    		if(labelTextObj != null){
    			if(h != 0) {
    				h += KDisplay.BORDER / 2;
    			}

    			rectLabel.x = x;
    			rectLabel.y = y + h;
    			rectLabel.w = w;
    			formattedLabel = new WordWrap(getLabelStyle(getState()), labelTextObj.toString(), w);
    			h += formattedLabel.getHeight();
    		}

    		if(!ignoreContent){
    			contentH = getPrefContentHeight(w);
    			if(contentH != 0){
    				contentX = x;

    				if(h != 0) {
    					h += KDisplay.BORDER;
    				}

    				contentY = y+h;
    				h += contentH;
    				contentW = w;
    			}
    		}

    		return h;
    	}

		int imgH = 0;
		int txtH = 0;

		int fullWidth = w;
		int cutAwayWidth = 0;
		int cutAwayHeight = 0;

		int imgDelta = 0;

		if( image != null ) {
			imgX = x;
			imgY = y;

			//
			// Check for special case in tag <a>. If the image is an arrow then
			// the label is full fullWidth, next line is image and then the text.
			if( image == KDisplay.SYMBOLS || itemType == KItem.TYPE_INDENT) {
				imgY += 1;
				if( labelTextObj != null ) {

        			rectLabel.x = x;
        			rectLabel.y = y;
        			rectLabel.w = w;
        			formattedLabel = new WordWrap(getLabelStyle(getState()), labelTextObj.toString(), w);
        			imgY += formattedLabel.getHeight() + KDisplay.BORDER;
				}
			}
			else {
				//
				// Default is center the image horizontal
    			imgDelta = image.getWidth()/imgCols + KDisplay.BORDER;
//    			x += delta;
//    			w -= delta;
    			cutAwayWidth = imgDelta;
			}
			imgH = image.getHeight() / imgRows;
			cutAwayHeight = imgH;
		}

		if(labelTextObj != null){
			rectLabel.x = x + imgDelta;
			rectLabel.y = y;
			rectLabel.w = w;
			formattedLabel = new WordWrap(getLabelStyle(getState()), labelTextObj.toString(), w - imgDelta);
			txtH = formattedLabel.getHeight();
		}

		if(!ignoreContent){

			//
			// Check for special case in tag <a>. If the image is an arrow then
			// the label is full fullWidth, next line is image and then the text.
			if( image == KDisplay.SYMBOLS  || itemType == KItem.TYPE_INDENT) {
    			int delta = image.getWidth()/imgCols + KDisplay.BORDER;
    			x += delta;
    			w -= delta;
    			fullWidth -= delta;
			}

			contentH = getPrefContentHeight(fullWidth, cutAwayWidth, cutAwayHeight);
			if(contentH != 0){
				contentX = x;
				if(txtH != 0){
					txtH += KDisplay.BORDER;
				}
				contentY = y + txtH;
				txtH += contentH;
				contentW = w;
			}
		}

		int h = Math.max(imgH, txtH);

//		if(getVAlign() == Graphics.VCENTER){
//
//			if( img0 != KDisplay.ICON_ARROW_OFF ) {
//				imgY += (h - imgH) / 2;
//			}
//			labelY += (h - txtH) / 2;
//			contentY += (h - txtH) / 2;
//		}

		return h;
    }


    public int getPrefContentHeight(int fullWidth, int cutWidth, int cutHeight) {

//#mdebug info
//@    	System.out.println("KStringItem::getPrefContnetHeight(int,int,int): fullW:" + fullWidth + ", " + cutWidth + ", " + cutHeight);
//@    	System.out.println("... this: " + this);
//#enddebug

        if( text != null ) {
        	formattedText = new WordWrap(
        			getContentStyle(STATE_UNFOCUSSED), text.toString(),
        			fullWidth, cutWidth, cutHeight);
            return formattedText.getHeight();
        }

        return 0;
    }

    public int getPrefContentHeight(int fullWidth) {
//#mdebug warn
//@//    	System.out.println("WARNING: KStringItem::getPreContentHeight(int): DONT CALL THIS METHOD!!");
//@//    	new RuntimeException("DONT CALL ME!").printStackTrace();
//#enddebug
    	return getPrefContentHeight(fullWidth, 0, 0);
    }

	public void setText(Object string) {
		text = string;
		invalidate();
		if(ownerForm != null){
			 KDisplay.requestRepaint();
		}
	}

    public String getText() {
		return text == null ? null : text.toString();
	}
}
