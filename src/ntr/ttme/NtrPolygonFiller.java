package ntr.ttme;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;

public class NtrPolygonFiller implements PolygonFiller
{
    public void fillPolygon( Graphics g, int[] xPoints, int[] yPoints, int nPoints )
    {
        int height = g.getClipHeight();
        Vector[] lines = new Vector[height];
        
        for (int i=0; i<nPoints-1; i++)
        {
            calcLine( height,
                      lines,
                      xPoints[i], yPoints[i],
                      xPoints[i+1], yPoints[i+1] );
        }

        /*int basex = -40;
        int basey = 130;

        //calcLine( height, lines, basex+50,  basey+0,   basex+100, basey+0 );
        calcLine( height, lines, basex+50,  basey+0,   basex+50,  basey-20 );
        calcLine( height, lines, basex+50,  basey-20,  basex+30,  basey-20 );
        calcLine( height, lines, basex+30,  basey-20,  basex+30,  basey-40 );
        calcLine( height, lines, basex+30,  basey-40,  basex+120, basey-40 );
        calcLine( height, lines, basex+120, basey-40,  basex+120, basey-20 );
        calcLine( height, lines, basex+120, basey-20,  basex+100, basey-20 );
        calcLine( height, lines, basex+100, basey-20,  basex+100, basey+0 );
        //---

        calcLine( height, lines, basex+100, basey+0,   basex+150, basey+50 );

        //calcLine( height, lines, basex+150, basey+50,  basex+150, basey+100 );
        calcLine( height, lines, basex+150,  basey+100, basex+170, basey+100 );
        calcLine( height, lines, basex+170,  basey+100, basex+170, basey+120 );
        calcLine( height, lines, basex+170,  basey+120, basex+190, basey+120 );
        calcLine( height, lines, basex+190,  basey+120, basex+190, basey+30 );
        calcLine( height, lines, basex+190,  basey+30,  basex+170, basey+30 );
        calcLine( height, lines, basex+170,  basey+30,  basex+170, basey+50 );
        calcLine( height, lines, basex+170,  basey+50,  basex+150, basey+50 );
        //---

        calcLine( height, lines, basex+150, basey+100, basex+100, basey+150 );

        //calcLine( height, lines, basex+100, basey+150, basex+50,  basey+150 );
        calcLine( height, lines, basex+50,  basey+150,  basex+50,  basey+170 );
        calcLine( height, lines, basex+50,  basey+170,  basex+30,  basey+170 );
        calcLine( height, lines, basex+30,  basey+170,  basex+30,  basey+190 );
        calcLine( height, lines, basex+30,  basey+190,  basex+120, basey+190 );
        calcLine( height, lines, basex+120, basey+190,  basex+120, basey+170 );
        calcLine( height, lines, basex+120, basey+170,  basex+100, basey+170 );
        calcLine( height, lines, basex+100, basey+170,  basex+100, basey+150 );
        //---

        calcLine( height, lines, basex+50,  basey+150, basex+0,   basey+100 );

        //calcLine( height, lines, basex+0,   basey+100, basex+0,   basey+50 );
        calcLine( height, lines, basex+0,   basey+100, basex-20,  basey+100 );
        calcLine( height, lines, basex-20,  basey+100, basex-20,  basey+120 );
        calcLine( height, lines, basex-20,  basey+120, basex-40,  basey+120 );
        calcLine( height, lines, basex-40,  basey+120, basex-40,  basey+30 );
        calcLine( height, lines, basex-40,  basey+30,  basex-20,  basey+30 );
        calcLine( height, lines, basex-20,  basey+30,  basex-20,  basey+50 );
        calcLine( height, lines, basex-20,  basey+50,  basex+0,   basey+50 );
        //---

        calcLine( height, lines, basex+0,   basey+50,  basex+50,  basey+0 );
        
        calcLine( height, lines, basex+75,  basey+20,  basex+130, basey+75 );
        calcLine( height, lines, basex+130, basey+75,  basex+75,  basey+130 );
        calcLine( height, lines, basex+75,  basey+130, basex+20,  basey+75 );
        calcLine( height, lines, basex+20,  basey+75,  basex+75,  basey+20 );
        */

        for (int y=0; y<height; y++)
        {
            if (lines[y] != null)
            {
                sortLine( lines[y] );
                fillLineBinary( g, lines[y], y );
            }
        }
    }

    private void calcLine( int height, Vector[] lines, int x1, int y1, int x2, int y2 )
    {
        if ((y1 < 0 && y2 < 0) || (y1 >= height && y2 >= height))
        {
            return;
        }

        if (y2 < y1)
        {
            int tmp = y1;
            y1 = y2;
            y2 = tmp;
            
            tmp = x1;
            x1 = x2;
            x2 = tmp;
        }

        int dx = x2-x1;
        int dy = y2-y1;
        int adx = Math.abs( dx );
        int ady = Math.abs( dy );

        if (y1 == y2)
        {
            if (y1>=0 && y1<height)
            {
                Vector yline = lines[y1];
                if (yline == null)
                {
                    yline = new Vector();
                    lines[y1] = yline;
                }
                yline.addElement( new Integer( x1 ) );
                yline.addElement( new Integer( x2 ) );
            }
            return;
        }
        else if (adx > ady)
        {
            int sgnx = dx/adx;
            int sgny = dy/ady;

            int x=x1;
            int y=y1;
            int ymeasure = adx;

            int levelx = x;

            do
            {
                x+=sgnx; // next pixel
                // use reminder to measure y advance
                if (ymeasure<ady)
                {
                    if (y>=0 && y<height)
                    {
                        Vector yline = lines[y];
                        if (yline == null)
                        {
                            yline = new Vector();
                            lines[y] = yline;
                        }
                        yline.addElement( new Integer( levelx ) );
                        levelx = x;
                    }
                    ymeasure += adx;
                    y+=sgny;
                }
                ymeasure-=ady;
            }
            while( y!=y2 );
        }
        else
        {
            if (dx==0)
            {
                int sgny = dy/ady;
                
                int x=x1;
                int y=y1;
                do
                {
                    if (y>=0 && y<height)
                    {
                        Vector yline = lines[y];
                        if (yline == null)
                        {
                            yline = new Vector();
                            lines[y] = yline;
                        }
                        yline.addElement( new Integer( x ) );
                    }
                    y+=sgny; // next scanline
                }
                while( y!=y2 );
            }
            else
            {
                int sgnx = dx/adx;
                int sgny = dy/ady;
    
                int x=x1;
                int y=y1;
                int xmeasure = ady;
                do
                {
                    if (y>=0 && y<height)
                    {
                        Vector yline = lines[y];
                        if (yline == null)
                        {
                            yline = new Vector();
                            lines[y] = yline;
                        }
                        yline.addElement( new Integer( x ) );
                    }
                    y+=sgny; // next scanline
                    // use reminder to measure x advance
                    if (xmeasure<adx)
                    {
                        xmeasure += ady;
                        x+=sgnx;
                    }
                    xmeasure-=adx;
                }
                while( y!=y2 );
            }
        }
    }

    private void sortLine( Vector vector )
    {
        new QuickSort( vector ).run();
    }

    private void fillLineBinary( Graphics g, Vector vector, int y )
    {
        int width = g.getClipWidth();

        int size = vector.size();
        boolean penDown = false;
        int penx = Integer.MIN_VALUE;

        boolean outside = false;

        for (int i=0; i<size; i++)
        {
            int x = ((Integer)vector.elementAt( i )).intValue();
            if (x>=width)
            {
                if (x==Integer.MAX_VALUE)
                {
                    continue;
                }
                if (outside)
                {
                    return;
                }
                if (penx != x)
                {
                    outside = true;
                }
            }
            if (x>=0)
            {
                if (penDown)
                {
                    if (penx < 0)
                    {
                        penx = 0;
                    }
                    if (x >= width)
                    {
                        g.drawLine( penx, y, width, y );
                        return;
                    }
                    else
                    {
                        g.drawLine( penx, y, x, y );
                    }
                }
            }
            penDown = !penDown;
            penx = x;
        }
    }

}
