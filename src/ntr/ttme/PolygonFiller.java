package ntr.ttme;

import javax.microedition.lcdui.Graphics;

public interface PolygonFiller
{
    void fillPolygon( Graphics g, int[] xPoints, int[] yPoints, int npoints );
}
