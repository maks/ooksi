package ntr.ttme;

import javax.microedition.lcdui.Graphics;

public class LineRenderer
{
    public static final void renderHorizontalDashedLine( Graphics g,
                                                         int x1, int x2, int y,
                                                         int dashLength,
                                                         int c )
    {
        int lineLength = x2-x1;

        int direction =
            Math.min( 1, Math.max( -1, lineLength ) ); // -1 or 1 when (x2 != x1)

        lineLength += direction; // correct the lineLength but in the correct direction!

        g.setColor( c );

        boolean penDown = true; // the value will alternate
        int currentX = x1;
        int endX = x1+lineLength;
        int currentStep;

        while (currentX != endX)
        {
            currentStep =
                Math.min( dashLength, Math.abs( endX-currentX ) );

            if (penDown)
            {
                g.drawLine( currentX, y,
                            currentX + direction*(currentStep - 1), y );
            }

            penDown = (! penDown);

            currentX += (direction*currentStep);
        }
    } // END drawHorDashedLine

    public static final void renderVerticalDashedLine( Graphics g,
                                                       int x, int y1, int y2,
                                                       int dashLength,
                                                       int c )
    {
        int lineLength = y2-y1;

        int direction =
            Math.min( 1, Math.max( -1, lineLength ) ); // -1 or 1 when (y2 != y1)

        lineLength += direction; // correct the lineLength but in the correct direction!

        g.setColor( c );

        boolean penDown = true; // the value will alternate
        int currentY = y1;
        int endY = y1+lineLength;
        int currentStep;

        while (currentY != endY)
        {
            currentStep =
                Math.min( dashLength, Math.abs( endY-currentY ) );

            if (penDown)
            {
                g.drawLine( x, currentY,
                            x, currentY + direction*(currentStep - 1) );
            }

            penDown = (! penDown);

            currentY += (direction*currentStep);
        }
    } // drawVerticalDashedLine

    public static final void renderDottedLine( Graphics g,
                                               int x1, int y1,
                                               int x2, int y2,
                                               int c )
    {
        if (y1 == y2)
        {
            renderHorizontalDashedLine( g, x1, x2, y1, 1, c );
        }
        else if (x1 == x2)
        {
            renderVerticalDashedLine( g, x1, y1, y2, 1, c );
        }
        else // Bresenham
        {
            int xd = Math.abs( x2-x1 );
            int yd = Math.abs( y2-y1 );

            boolean penDown = true;

            g.setColor( c );

            if (xd>yd) //Horizontal slope
            {
                if (x1 > x2) // swap
                {
                    int tmp;

                    tmp=x1;
                    x1=x2;
                    x2=tmp;

                    tmp=y1;
                    y1=y2;
                    y2=tmp;
                }

                int direction = (y2 - y1)/yd;
                xd++;
                yd++;
                int measure = xd;

                int currentY = y1;

                for (int i=x1; i<=x2; i++)
                {
                    if (penDown)
                    {
                        g.drawLine( i, currentY, i, currentY );
                    }

                    penDown = (! penDown);

                    if (measure >= yd) // keep measuring
                    {
                        measure -= yd;
                    }
                    else // break into the next line (keep the remaining 'measure' value!)
                    {
                        measure += xd;
                        measure -= yd;
                        currentY += direction;
                    }
                } // for
            }
            else // Vertical slope
            {
                if (y1 > y2) // swap
                {
                    int tmp;

                    tmp=y1;
                    y1=y2;
                    y2=tmp;

                    tmp=x1;
                    x1=x2;
                    x2=tmp;
                }

                int direction = (x2 - x1)/xd;
                xd++;
                yd++;
                int measure=yd;

                int currentX = x1;

                for (int i=y1; i<=y2; i++)
                {
                    if (penDown)
                    {
                        g.drawLine( currentX, i, currentX, i );
                    }

                    penDown = (! penDown);

                    if (measure >= xd) // keep measuring
                    {
                        measure -= xd;
                    }
                    else // break into the next line (keep the remaining 'measure' value!)
                    {
                        measure += yd;
                        measure -= xd;
                        currentX += direction;
                    }
                } // for
            } // else (Vertical slope)
        } // else (Bresenham)
    } // renderDottedLine

} // END LineRenderer
