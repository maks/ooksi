package ntr.ttme;

import javax.microedition.lcdui.Graphics;

public class SplineCurveRenderer
{
    public static final int DEFAULT_NUMBER_OF_SPLINE_POINTS = 5;

    int numberOfSplinePoints;
    IntPolygon points;
    DoublePolygon worldPoints;

    public SplineCurveRenderer()
    {
        this( DEFAULT_NUMBER_OF_SPLINE_POINTS );
    }

    public SplineCurveRenderer( int p )
    {
        numberOfSplinePoints = p;
        points = new IntPolygon();
        worldPoints = new DoublePolygon( p );
    }

    public void RawDrawSpline( Graphics g,
                               int p0x, int p0y,
                               int p1x, int p1y,
                               int p2x, int p2y,
                               int c )
    {
        calculateWorldSpline( p0x, p0y,
                              p1x, p1y,
                              p2x, p2y );

        points.npoints = 0; // clear
        for (int i=0; i<worldPoints.getNumberOfPoints(); i++)
        {
            points.addPoint(
                (int)worldPoints.getPointX( i ),
                (int)worldPoints.getPointY( i ) );
        }

        g.setColor( c );
        Polyline.draw( g, points.xpoints, points.ypoints, points.npoints );
    }

    public void calculateWorldSpline( double p0x, double p0y,
                                      double p1x, double p1y,
                                      double p2x, double p2y )
    {
        double progress;
        double rest;
        double leftSqr;
        double rightSqr;
        double m;

        worldPoints.clear();
        worldPoints.addPoint( p0x, p0y );

        for (int i=0; i<=numberOfSplinePoints; i++)
        {
            progress = (double)i / numberOfSplinePoints;  // progress 0..1

            rest=1-progress;
            leftSqr = rest*rest;
            rightSqr = progress*progress;
            m = 2*progress*rest;

            worldPoints.addPoint( leftSqr*p0x + m*p1x + rightSqr*p2x,
                                  leftSqr*p0y + m*p1y + rightSqr*p2y );
        }
    }

} // END SplineCurveRenderer
