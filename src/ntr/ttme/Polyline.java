package ntr.ttme;

import javax.microedition.lcdui.Graphics;

public class Polyline
{
    static void draw( Graphics g, int[] xpoints, int[] ypoints, int npoints )
    {
        for (int i=1; i<npoints; i++)
        {
            g.drawLine( xpoints[i-1], ypoints[i-1], xpoints[i], ypoints[i] );
        }
    }
}
