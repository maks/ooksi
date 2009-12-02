package org.kobjects.kui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/**
 * A KChoiceGroup is a group of selectable elements intended to be placed within
 * a {@link KForm}. The group may be created with a mode that requires a single
 * choice to be made or that allows multiple choices. The implementation is
 * responsible for providing the graphical representation of these modes and
 * must provide visually different graphics for different modes. For example, it
 * might use "radio buttons" for the single choice mode and "check boxes" for the
 * multiple choice mode.
 * 
 * @author Stefan Haustein
 */
public class KChoiceGroup extends KItem {

	/**
	 * EXCLUSIVE is a choice having exactly one element selected at time. All
	 * elements of an EXCLUSIVE type Choice  should be displayed in-line. That is
	 * the user should not need to perform any extra action to traverse among and
	 * select from the elements.
	 */
	public static final int EXCLUSIVE = 1;
	
	/**
	 * MULTIPLE is a choice that can have arbitrary number of elements selected at
	 * a time.
	 */
	public static final int MULTIPLE = 2;
	
	/**
	 * IMPLICIT is a choice in which the currently focused element is selected when
	 * a Command is initiated.
	 */
	public static final int IMPLICIT = 3;
	
	public static final int POPUP = 4;
	
	public int type;
    public String[] options;
    
    protected int selectedIndex;
    public boolean[] selected;
    
    protected int lineHeight;
    protected int focusIndex = 0;
    
    /**
     * Create a new {@link KChoiceGroup} instance.
     * 
     * @param label a object that string representation is the label of that item.
     * @param type the type of the {@link KChoiceGroup}.
     * @param options
     */
    public KChoiceGroup(Object label, int type, String[] options) {
    	super(label, KItem.TYPE_INPUT);
    	this.type = type;
        this.options = options;
        selected = new boolean[options.length];
    }
    
	protected int getPrefContentHeight(int w) {
    
    	lineHeight = Math.max(getContentStyle(getState()).getFont().getHeight(), 
    			KDisplay.SYMBOL_HEIGHT);
    	
    	return type == POPUP ? lineHeight : options.length * lineHeight;
    }

    public String getSelectedOption() {
    	
    	if(type != KChoiceGroup.MULTIPLE){
			return options[getSelectedIndex()];
		}
		
   		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < selected.length; i++){
			if(selected[i]){
				if(buf.length() > 0){
					buf.append(',');
				}
				buf.append(options[i]);
			}
		}

		return buf.toString();
    }
    
    public int getSelectedIndex() {
        return selectedIndex;
    }
    
    public void paint(Graphics g, int contentW, int contentH) {
    	
    	int state = getState();
        KStyle s = getContentStyle(state);
        KDisplay.applyStyle(s, g);
        int textHeight = s.getFont().getHeight();
        
        if(type == POPUP){       
            KDisplay.drawIcon(g, 
            		KDisplay.SYMBOLS, 
            		0, 
    				(lineHeight-KDisplay.SYMBOL_HEIGHT) / 2,
            		state == STATE_FOCUSSED 
            			? KDisplay.ICON_LEFT_RIGHT_ON 
            					: KDisplay.ICON_LEFT_RIGHT_OFF, 
       				1, 
       				KDisplay.SYMBOL_COUNT);
           
            s.drawString(g,  options[selectedIndex], 
            		 KDisplay.BORDER + KDisplay.SYMBOL_WIDTH,
        			(lineHeight-textHeight) / 2); 	    
        }
        else{
        	int currentY = 0;
            for(int i = 0; i < options.length; i++){
            	if(state == STATE_FOCUSSED){
            		if(i == focusIndex && options.length > 0){
            			g.setColor(getLabelStyle((STATE_FOCUSSED)).bgValues[1]);
            			
            			
            			//
            			// SKerkewitz: old selected style was a single line above and
            			// below the item
//            			g.drawLine(
//            					KDisplay.SYMBOL_WIDTH + KDisplay.BORDER,
//            					currentY+lineHeight-1,
//            					contentW,
//            					currentY+lineHeight-1);
//            			
//            			g.drawLine(
//            					KDisplay.SYMBOL_WIDTH + KDisplay.BORDER,
//            					currentY,
//            					contentW,
//            					currentY);

            			//
            			// SKerkewitz: new style is just a filled bgValues 
            			g.fillRect(0, currentY, contentW, lineHeight-1);
            			
//            			g.setStrokeStyle(Graphics.DOTTED);
//            			g.drawRect(contentX-1, currentY-1, contentW, lineHeight);
//            			g.setStrokeStyle(Graphics.SOLID);
            			g.setColor(getContentStyle(STATE_FOCUSSED).color);
            		}
            		else
                    	g.setColor(getContentStyle(STATE_UNFOCUSSED).color);
            	}
        		    
            	boolean on = (type == EXCLUSIVE && i == selectedIndex)
					|| (type == IMPLICIT && i == focusIndex)
					|| (type == MULTIPLE && selected[i]);
            	
            	KDisplay.drawIcon(g, 
            			KDisplay.SYMBOLS, KDisplay.BORDER, 
        				currentY + (lineHeight-KDisplay.SYMBOL_HEIGHT) / 2,
        				(type - 1) * 2 + (on ? 1 : 0), 
						1, KDisplay.SYMBOL_COUNT);
            	

        		
            	s.drawString(g, 
            			options[i], 
            			(KDisplay.BORDER*2) + KDisplay.SYMBOL_WIDTH,
            			currentY + (lineHeight-textHeight) / 2);
            	
        		currentY += lineHeight;       		
            }
        }
    }
    
    public boolean traverse(int dir, int w, int h, int[]inout) {
   
    	//
    	// Some debug line
//#mdebug debug
//@    	System.out.println("KChoiceGroup::traverse()");
//#enddebug
    	
    	//
    	// Traverse handling depends if the item has the focus or not
    	if(getState() == STATE_UNFOCUSSED) {
    		
    		//
    		// If the control has not the focus then slide in from top on down key or
    		// from below on up key.
    		if(type != POPUP){
    			if( dir == Canvas.UP ) {
    				focusIndex = options.length-1;
    			}
    			else if( dir == Canvas.DOWN ) {
    				focusIndex = 0;
    			}
    		}
//#mdebug debug
//@    		System.out.println("New focus index: "+focusIndex);
//#enddebug
    	}
    	else if(type == POPUP 
    			|| (dir == Canvas.UP && focusIndex == 0)
    			|| dir == Canvas.DOWN && focusIndex == options.length -1){
    		return false;
    	}
    	else {
    		//
    		// If the are currently focused then the standard keyPressed() method
    		// can handle it but only if the last traversal was internal.
    		if( ownerForm.isInternalTraversal() )
    			keyPressed(dir);
    	}
    	
    	if(type != POPUP){

    		int y0 = inout[1];
    		int vh = inout[3];
    		int fy = focusIndex*lineHeight;
    		
//			System.out.println("fy: "+fy+" y0: "+y0+ " vh: "+inout[3]);
			
    		if(fy >= inout[1] + inout[3] - lineHeight){
    			y0 = fy - vh + lineHeight;
//    			System.out.println("A) Changed y0 to "+y0);
    			inout[1] = y0;
    		}
    		if(fy < y0){
    			y0 = fy;
//    			System.out.println("B) Changed y0 to "+y0);
    			inout[1] = y0;
    		}
    	}
    	
    	return true;
    }

    public void keyPressed(int code) {
        
    	System.out.println("KChoiceGroup::keyPressed() : code: " + code);
    	System.out.println("... current focuse index: " + focusIndex);
    	
        if(type == POPUP){ 	       
            switch (code) {
            case Canvas.LEFT:
                if (--selectedIndex < 0) {
                    selectedIndex = options.length - 1;
                }
                KDisplay.requestRepaint();
                break;
            case Canvas.FIRE:
            case Canvas.RIGHT:
                selectedIndex = (selectedIndex + 1) % options.length;
                KDisplay.requestRepaint();
                break;
            }
        }
        else {
        	switch(code){
        	case Canvas.UP:
        		if(focusIndex > 0){
        			focusIndex--;
                    KDisplay.requestRepaint();
        			break;
        		}
        		break;
        	case Canvas.DOWN:
        		if(focusIndex < options.length-1){
        			focusIndex++;
                    KDisplay.requestRepaint();
        			break;
        		}
        		break;
        	case Canvas.FIRE:
        		setSelectedIndex(focusIndex, type == MULTIPLE ? !selected[focusIndex] : true);
                KDisplay.requestRepaint();
        		break;
        	}
        }
    }

    
    
    public void pointerPressed(int x, int y) {
    	if(type == POPUP){
    		int len = getKDisplay().getFont(getContentStyle(getState()).font).stringWidth(options[selectedIndex]);
   			keyPressed(x > Math.min(len+KDisplay.BORDER*2, getKDisplay().getWidth()/2) ? Canvas.RIGHT : Canvas.LEFT);
    	}
    	else{
    		int selFocusIndex = Math.max(0, Math.min(y / lineHeight, options.length-1));

    		if(selFocusIndex != focusIndex){
    			focusIndex = selFocusIndex;
    			KDisplay.requestRepaint();
    		}
    	}
    }


	public void setSelectedIndex(int idx, boolean selected) {
		this.selected[idx] = selected;
		selectedIndex = idx;
	}
}
