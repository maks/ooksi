package org.kobjects.kui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class KList extends KChoiceGroup {
	
	public Image itemIcons[] = new Image[1];
	
	public KList(Object title, int type) {
		super(title, type, new String[]{"EMPTY"});
	}

	public KList(Object label, int type, String[] options, Image[] icons)  {
		super(label, type, options);
		if (icons != null) {
			itemIcons = icons;
		}
	}
	
	public void addCommand(Command cmd) {
		
	}
	
	/**
	 * Appends an element to the KList.
	 * 
	 * @param stringPart
	 * @param imagePart
	 * @return the assigned index of the element
	 */
	public int append(String stringPart, Image imagePart) {
		//FIXME: need to change KChoiceGroupand hence KList to use a vector of a Class
		//that encapsulates a string, image and bool so we dont need to create 
		//new arrays and copies everytime we add/del a entry
		
		int pos = options.length;
		try {
			String[] nuOptions = new String[options.length+1];
			System.arraycopy(options, 0, nuOptions, 0, options.length);
			options = nuOptions;
			boolean[] nuSelected = new boolean[selected.length+1];
			System.arraycopy(selected, 0, nuSelected, 0, selected.length);
			selected = nuSelected;
			Image[] nuIcons = new Image[itemIcons.length+1];
			System.arraycopy(itemIcons, 0, nuIcons, 0, itemIcons.length);
			itemIcons = nuIcons;		
			
			options[pos] = stringPart;
			itemIcons[pos] = imagePart;
			System.out.println("added to klist:"+stringPart);
			return pos;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * Sets the String and Image parts of the element referenced by elementNum, 
	 * replacing the previous contents of the element.
	 * 
	 * @param elementNum
	 * @param stringPart
	 * @param imagePart
	 */
	public void set(int elementNum, String stringPart, Image imagePart) {
		options[elementNum] = stringPart;
		itemIcons[elementNum] = imagePart;
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
            			
            			
            			// SKerkewitz: new style is just a filled bgValues 
            			g.fillRect(0, currentY, contentW, lineHeight-1);
            			g.setColor(getContentStyle(STATE_FOCUSSED).color);
            		}
            		else
                    	g.setColor(getContentStyle(STATE_UNFOCUSSED).color);
            	}
        		    
            	boolean on = (type == EXCLUSIVE && i == selectedIndex)
					|| (type == IMPLICIT && i == focusIndex)
					|| (type == MULTIPLE && selected[i]);
            	
//            	KDisplay.drawIcon(g, 
//            			KDisplay.SYMBOLS, KDisplay.BORDER, 
//        				currentY + (lineHeight-KDisplay.SYMBOL_HEIGHT) / 2,
//        				(type - 1) * 2 + (on ? 1 : 0), 
//						1, KDisplay.SYMBOL_COUNT);
            	
            	int iconHeight = (itemIcons[i] != null) ? itemIcons[i].getHeight() : 0;
            	int iconWidth =  (itemIcons[i] != null) ? itemIcons[i].getWidth() : 0;
            	
            	if (itemIcons.length > 0 && itemIcons[i] != null) {
            		System.out.println("draw icon");
	         
	            	g.drawImage(itemIcons[i], KDisplay.BORDER, 
	            			currentY, 
	            			Graphics.TOP | Graphics.LEFT);
            	}
            	int imgBorder = Math.max(KDisplay.SYMBOL_WIDTH, iconWidth);
            	s.drawString(g, 
            			options[i], 
            			(KDisplay.BORDER*2) + imgBorder,
            			currentY + (lineHeight-textHeight) / 2);
            	
            	
        		currentY += Math.max(lineHeight, iconHeight);       		
            }
        }
    }
}
