/*
 * Created on 13.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code KStyle - Code Templates
 */
package org.kobjects.kui;

import javax.microedition.lcdui.Graphics;

/**
 * @author haustein
 *
 */
public class KGauge extends KItem {

    int value;
    int maxValue;


    public KGauge(Object label, int value, int maxValue) {
        super(label, KItem.TYPE_PLAIN);
        this.value = value;
        this.maxValue = maxValue;
    }

    public int getPrefContentHeight(int contentW){
        return Math.min(10, getContentStyle(getState()).getFont().getHeight());
    }



    public void setValue(int newValue){
        if(newValue != value){
            value = newValue;
            if(ownerForm != null) KDisplay.requestRepaint();
        }
    }


    public void paint(Graphics g, int contentW, int contentH){

        //int avail = KDisplay.CONTENT_WIDTH - KDisplay.BORDER - 4;
        int avail = contentW - KDisplay.BORDER - 4;
        int fill = avail * value / maxValue;

       // g.setColor(style[state|CONTENT].bg);
       // g.fillRect(KForm.INDENT, labelHeight, avail+6, height - labelHeight);

        g.setColor(getContentStyle(getState()).color);
        g.drawRect(KDisplay.BORDER, 1, avail+1, contentH - 4);
        g.fillRect(KDisplay.BORDER+1, 2, fill, contentH - 5);

      /*  if(fill != avail){
        	g.setColor(style[state].bg);
        	g.fillRect(KForm.INDENT+fill+1, labelHeight+2, avail-fill, height - labelHeight - 5);
        }*/
    }


}
