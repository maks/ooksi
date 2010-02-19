package ntr.ttme;

import javax.microedition.lcdui.Graphics;

public final class BezierCurveRenderer
{
    public static final int DEFAULT_CASTELJAU_RECURSION_DEPTH = 5;

    int recursionDepth;
    IntPolygon points;
    DoublePolygon worldPoints;

    public BezierCurveRenderer()
    {
        recursionDepth = DEFAULT_CASTELJAU_RECURSION_DEPTH;
        points = new IntPolygon();
        worldPoints = new DoublePolygon();
    }

    public BezierCurveRenderer( int depth )
    {
        recursionDepth = depth;
        points = new IntPolygon();
        worldPoints = new DoublePolygon();
    }

    public int getRecursionDepth()
    {
        return recursionDepth;
    }

    public void renderBezier( Graphics g,
                              int p0x, int p0y,
                              int p1x, int p1y,
                              int p2x, int p2y,
                              int p3x, int p3y,
                              int curveColor )
    {
        calculateBezier( p0x, p0y,
                         p1x, p1y,
                         p2x, p2y,
                         p3x, p3y );
        g.setColor( curveColor );
        Polyline.draw( g, points.xpoints, points.ypoints, points.npoints );
    }

    public void calculateBezier( int p0x, int p0y,
                                 int p1x, int p1y,
                                 int p2x, int p2y,
                                 int p3x, int p3y )
    {
        points.npoints = 0;
        points.addPoint( p0x, p0y );
        casteljauBezierSubDivision( p0x, p0y,
                                    p1x, p1y,
                                    p2x, p2y,
                                    p3x, p3y,
                                    recursionDepth );
    }

    private void casteljauBezierSubDivision( int p0x, int p0y,
                                             int p1x, int p1y,
                                             int p2x, int p2y,
                                             int p3x, int p3y,
                                             int depth )
    {
        int q0x; // 1st set of Casteljau bezier curve construction points
        int q0y;
        int q1x;
        int q1y;
        int q2x;
        int q2y;
        int r0x; // 2nd set of points
        int r0y;
        int r1x;
        int r1y;
        int r2x;
        int r2y;

        if (depth==0)
        {
            points.addPoint( p3x, p3y );
        }
        else
        {
            // 1st-set-construction-points are at p0..p3 half distance: -|^|-
            q0x=(p0x+p1x)/2;
            q0y=(p0y+p1y)/2;

            q1x=(p1x+p2x)/2;
            q1y=(p1y+p2y)/2;

            q2x=(p2x+p3x)/2;
            q2y=(p2y+p3y)/2;

            // 2nd-set-construction-points are at q0..q2 half distance: -/\-
            r0x=(q0x+q1x)/2;
            r0y=(q0y+q1y)/2;

            r1x=(q1x+q2x)/2;
            r1y=(q1y+q2y)/2;

            // 3rd point is between r0 and r1 /-@-\
            r2x=(r0x+r1x)/2;
            r2y=(r0y+r1y)/2;

            // Bezier curve subdivision into left and right part
            casteljauBezierSubDivision( p0x, p0y,
                                        q0x, q0y,
                                        r0x, r0y,
                                        r2x, r2y,
                                        depth-1 );
            casteljauBezierSubDivision( r2x, r2y,
                                        r1x, r1y,
                                        q2x, q2y,
                                        p3x, p3y,
                                        depth-1 );
        } // else
    } // casteljauBezierSubDivision

    public void worldCalculateBezier( double p0x, double p0y,
                                      double p1x, double p1y,
                                      double p2x, double p2y,
                                      double p3x, double p3y )
    {
        worldPoints.clear();
        worldPoints.addPoint( p0x, p0y );
        worldCasteljauBezierSubDivision( p0x, p0y,
                                         p1x, p1y,
                                         p2x, p2y,
                                         p3x, p3y,
                                         recursionDepth );
    }

    private void worldCasteljauBezierSubDivision( double p0x, double p0y,
                                                  double p1x, double p1y,
                                                  double p2x, double p2y,
                                                  double p3x, double p3y,
                                                  int depth )
    {
        double q0x; // 1st set of Casteljau bezier curve construction points
        double q0y;
        double q1x;
        double q1y;
        double q2x;
        double q2y;
        double r0x; // 2nd set of points
        double r0y;
        double r1x;
        double r1y;
        double r2x;
        double r2y;

        if (depth==0)
        {
            worldPoints.addPoint( p3x, p3y );
        }
        else
        {
            // 1st-set-construction-points are at p0..p3 half distance: -|^|-
            q0x=(p0x+p1x)/2;
            q0y=(p0y+p1y)/2;

            q1x=(p1x+p2x)/2;
            q1y=(p1y+p2y)/2;

            q2x=(p2x+p3x)/2;
            q2y=(p2y+p3y)/2;

            // 2nd-set-construction-points are at q0..q2 half distance: -/\-
            r0x=(q0x+q1x)/2;
            r0y=(q0y+q1y)/2;

            r1x=(q1x+q2x)/2;
            r1y=(q1y+q2y)/2;

            // 3rd point is between r0 and r1 /-@-\
            r2x=(r0x+r1x)/2;
            r2y=(r0y+r1y)/2;

            // Bezier curve subdivision into left and right part
            worldCasteljauBezierSubDivision( p0x, p0y,
                                             q0x, q0y,
                                             r0x, r0y,
                                             r2x, r2y,
                                             depth-1 );
            worldCasteljauBezierSubDivision( r2x, r2y,
                                             r1x, r1y,
                                             q2x, q2y,
                                             p3x, p3y,
                                             depth-1 );
        } // else
    } // worldCasteljauBezierSubDivision

} // END BezierCurveRenderer
