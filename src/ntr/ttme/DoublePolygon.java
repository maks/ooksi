package ntr.ttme;

public final class DoublePolygon
{
    public static final int DEFAULT_INITIAL_CAPACITY = 4;

    int numberOfPoints;
    double xPoints[];
    double yPoints[];

    DoubleRectangle bounds = null;

    public DoublePolygon()
    {
        this( DEFAULT_INITIAL_CAPACITY );
    }

    public DoublePolygon( int initialCapacity )
    {
        xPoints = new double[initialCapacity];
        yPoints = new double[initialCapacity];
    }

    public DoublePolygon( double xPoints[], double yPoints[], int numberOfPoints )
    {
        this( numberOfPoints );

        System.arraycopy( xPoints, 0, this.xPoints, 0, numberOfPoints );
        System.arraycopy( yPoints, 0, this.yPoints, 0, numberOfPoints );

        this.numberOfPoints = numberOfPoints;
    }

    public int getNumberOfPoints()
    {
        return numberOfPoints;
    }

    public double getPointX( int index )
    {
        return xPoints[index];
    }
    public double getPointY( int index )
    {
        return yPoints[index];
    }

    public void clear()
    {
        numberOfPoints = 0;
    }

    public void setPoint( int i, double x, double y )
    {
        xPoints[i] = x;
        yPoints[i] = y;
    }

    public void ensureCapacity( int required )
    {
        double[] newArray;
        int newSize;

        if (xPoints.length < required ||
            yPoints.length < required)
        {
            newSize = determineNewSize( required );

            newArray = new double[newSize];
            if (numberOfPoints > 0)
            {
                System.arraycopy( xPoints, 0, newArray, 0, numberOfPoints );
            }
            xPoints = newArray;

            newArray = new double[newSize];
            if (numberOfPoints > 0)
            {
                System.arraycopy( yPoints, 0, newArray, 0, numberOfPoints );
            }
            yPoints = newArray;
        }
    }

    private int determineNewSize( int n )
    {
        int result = Math.max( numberOfPoints, 1 );

        while (result <= n)
        {
            result *= 2;
        }

        return result;
    }

    public void ensureFreeSpaceOnHead( int requiredFree )
    // note: this method makes space at start, then it is copying elements
    //      on positions after required free space and finally
    //      it sets "numberOfPoints" to requiredFree+numberOfPoints
    //      That means you will loose old "numberOfPoints" information!
    //      This method should be called before some transfering using System.arraycopy
    {
        int required = requiredFree + numberOfPoints;

        double[] newArray;

        int newSize;

        if (xPoints.length < required ||
            yPoints.length < required)
        {
            newSize = determineNewSize( required );

            newArray = new double[newSize];
            if (numberOfPoints > 0)
            {
                System.arraycopy( xPoints, 0, newArray, requiredFree, numberOfPoints );
            }
            xPoints = newArray;

            newArray = new double[newSize];
            if (numberOfPoints > 0)
            {
                System.arraycopy( yPoints, 0, newArray, requiredFree, numberOfPoints );
            }
            yPoints = newArray;
        }
        else
        {
            if (numberOfPoints > 0)
            {
                System.arraycopy( xPoints, 0, xPoints, requiredFree, numberOfPoints );
                System.arraycopy( yPoints, 0, yPoints, requiredFree, numberOfPoints );
            }
        }

        numberOfPoints = required;
    }

    public void ensureFreeSpaceOnTail( int requiredFree )
    // note: this method makes space at end and then
    //      it sets "numberOfPoints" to numberOfPoints+requiredFree
    //      That means you will loose old "numberOfPoints" information!
    //      This method should be called before some transfering using System.arraycopy
    {
        ensureCapacity( numberOfPoints + requiredFree );
        numberOfPoints += requiredFree;
    }

    public void translate( double deltaX, double deltaY )
    {
        int i;

        for (i=0; i<numberOfPoints; i++)
        {
            if (xPoints[i] != Double.MAX_VALUE)
            {
                xPoints[i]+=deltaX;
            }
            yPoints[i] += deltaY;
        }

        if (bounds != null)
        {
            bounds.translate( deltaX, deltaY );
        }
    }

    public void translateCurves( double deltaX, double deltaY )
    {
        int i;

        for (i=0; i<numberOfPoints; i++)
        {
            // don't translate LINE, SPLINE and CURVEEND
            if (xPoints[i] != Double.MAX_VALUE)
            {
                xPoints[i] += deltaX;
                yPoints[i] += deltaY;
            }
        }

        if (bounds != null)
        {
            bounds.translate( deltaX, deltaY );
        }
    }

    void calculateBounds()
    {
        double boundsMinX = Double.POSITIVE_INFINITY;
        double boundsMinY = Double.POSITIVE_INFINITY;
        double boundsMaxX = Double.NEGATIVE_INFINITY;
        double boundsMaxY = Double.NEGATIVE_INFINITY;

        int i;
        double x;
        double y;

        for (i=0; i<numberOfPoints; i++)
        {
            x = xPoints[i];
            if (x != Double.MAX_VALUE)
            {
                boundsMinX = Math.min( boundsMinX, x );
                boundsMaxX = Math.max( boundsMaxX, x );

                y = yPoints[i];
                boundsMinY = Math.min( boundsMinY, y );
                boundsMaxY = Math.max( boundsMaxY, y );
            }
        }

        bounds = new DoubleRectangle( boundsMinX,
                                      boundsMinY,
                                      boundsMaxX - boundsMinX,
                                      boundsMaxY - boundsMinY );
    }

    void recalculateBounds()
    {
        double boundsMinX = Double.POSITIVE_INFINITY;
        double boundsMinY = Double.POSITIVE_INFINITY;
        double boundsMaxX = Double.NEGATIVE_INFINITY;
        double boundsMaxY = Double.NEGATIVE_INFINITY;

        int i;
        double x;
        double y;

        for (i=0; i<numberOfPoints; i++)
        {
            x = xPoints[i];
            if (x != Double.MAX_VALUE)
            {
                boundsMinX = Math.min( boundsMinX, x );
                boundsMaxX = Math.max( boundsMaxX, x );

                y = yPoints[i];
                boundsMinY = Math.min( boundsMinY, y );
                boundsMaxY = Math.max( boundsMaxY, y );
            }
        }

        if (bounds == null)
        {
            bounds = new DoubleRectangle( boundsMinX,
                                          boundsMinY,
                                          boundsMaxX-boundsMinX,
                                          boundsMaxY-boundsMinY );
        }
        else
        {
            bounds.setX( boundsMinX );
            bounds.setY( boundsMinY );
            bounds.setWidth( boundsMaxX - boundsMinX );
            bounds.setHeight( boundsMaxY - boundsMinY );
        }
    } // recalculateBounds

    void updateBounds( double x, double y )
    {
        if (x != Double.MAX_VALUE)
        {
            bounds.setX( Math.min( bounds.getX(), x ) );
            bounds.setWidth( Math.max( bounds.getWidth(), x-bounds.getX() ) );

            bounds.setY( Math.min( bounds.getY(), y ) );
            bounds.setHeight( Math.max( bounds.getHeight(), y-bounds.getY() ) );
        }
    }

    public void addPoint( double x, double y )
    {
        double tmp[];

        if (numberOfPoints == xPoints.length)
        {
            tmp = new double[numberOfPoints*2];
            System.arraycopy( xPoints, 0, tmp, 0, numberOfPoints );
            xPoints=tmp;

            tmp=new double[numberOfPoints*2];
            System.arraycopy( yPoints, 0, tmp, 0, numberOfPoints );
            yPoints = tmp;
        }

        xPoints[numberOfPoints] = x;
        yPoints[numberOfPoints] = y;
        numberOfPoints++;

        if (bounds != null)
        {
            updateBounds( x, y );
        }
    }

    public DoubleRectangle getBounds()
    {
        if (bounds==null)
        {
            calculateBounds();
        };

        return bounds;
    }

    public boolean contains( double x, double y )
    {
        int hits;
        double ySave;
        int i, j, n;
        double dx, dy;
        double rx, ry;
        double s;

        if (getBounds().contains( x, y ))
        {
            hits=0;
            ySave=0;

            // Find a vertex that's not on the halfline
            i=0;
            while (i < numberOfPoints &&
                   yPoints[i] == y)
            {
                i++;
            }

            // Walk the edges of the polygon
            for (n=0; n<numberOfPoints; n++)
            {
                j = (i+1) % numberOfPoints;

                dx = xPoints[j] - xPoints[i];
                dy = yPoints[j] - yPoints[i];

                // Ignore horizontal edges completely
                if (dy != 0)
                {
                    // Check to see if the edge intersects
                    // the horizontal halfline through (x, y)
                    rx = x - xPoints[i];
                    ry = y - yPoints[i];

                    // Deal with edges starting or ending on the halfline
                    if (yPoints[j] == y &&
                        xPoints[j] >= x)
                    {
                        ySave = yPoints[i];
                    }

                    if (yPoints[i] == y &&
                        xPoints[i] >= x)
                    {
                        if ((ySave>y) != (yPoints[j]>y))
                        {
                            hits--;
                        }
                    }

                    // Tally intersections with halfline
                    s = ry / dy;
                    if (s >= 0.0 &&
                        s <= 1.0 &&
                        (s * dx) >= rx)
                    {
                        hits++;
                    }
                } // if

                i = j;
            } // for

            // Inside if number of intersections odd
            return (hits % 2) != 0;
        }
        else
        {
            return false;
        }
    }

    public static final boolean lineHit( double x1, double y1,
                                         double x2, double y2,
                                         double hx, double hy,
                                         double epsilon )
    {
        boolean hit = false;
        double tmp;
        double dx;
        double dy;

        // Checking horizontal hit
        if (y1 > y2)
        {
            tmp=x1;
            x1=x2;
            x2=tmp;

            tmp=y1;
            y1=y2;
            y2=tmp;
        }

        if (hy >= y1 &&
            hy <= y2)
        {
            if (y1 == y2)
            {
                dx = hx - x1;
                hit = (Math.abs( dx ) <= epsilon);
            }
            else
            {
                dx = (x2-x1) * ((hy-y1)/(y2-y1));
                hit = (Math.abs( hx - (x1+dx) ) <= epsilon);
            }
        }

        if (! hit)
        {
            // Checking vertical hit
            if (x1 > x2)
            {
                tmp = x1;
                x1 = x2;
                x2 = tmp;

                tmp = y1;
                y1 = y2;
                y2 = tmp;
            }

            if (hx >= x1 &&
                hx <= x2)
            {
                if (x1 == x2)
                {
                    dy = y2 - hy;
                    hit = (Math.abs( dy ) <= epsilon);
                }
                else
                {
                    dy = (y2-y1) * ((x2-hx)/(x2-x1));
                    hit = (Math.abs( (y2-dy) - hy ) <= epsilon);
                }
            }
        }

        return hit;
    }

    public boolean polygonWireHit( double wx, double wy, double epsilon )
    {
        boolean hit = false;

        int prev = numberOfPoints - 1;
        int i = 0;

        while ((! hit) &&
               (i < numberOfPoints))
        {
            hit = lineHit( xPoints[prev], yPoints[prev],
                           xPoints[i], yPoints[i],
                           wx, wy,
                           epsilon );
            prev = i;
            i++;
        }

        return hit;
    }

    public boolean polylineWireHit( double wx, double wy, double epsilon )
    {
        boolean hit = false;

        int prev = 0;
        int i = 1;

        while ((! hit) &&
               (i < numberOfPoints))
        {
            hit = lineHit( xPoints[prev], yPoints[prev],
                           xPoints[i], yPoints[i],
                           wx, wy,
                           epsilon );
            prev = i;
            i++;
        };
        return hit;
    }

    public boolean multiContourWireHit( double wx, double wy, double epsilon )
    {
        boolean hit = false;

        int prev = 0;
        int i = 1;

        while ((! hit) &&
               (i < numberOfPoints))
        {
            if (xPoints[prev] != Double.MAX_VALUE &&
                xPoints[i] != Double.MAX_VALUE)
            {
                hit = lineHit( xPoints[prev], yPoints[prev],
                               xPoints[i], yPoints[i],
                               wx, wy,
                               epsilon );
            }

            prev = i;
            i++;
        }

        return hit;
    }

    public void rotateAround( double cx, double cy, double angle )
    {
        boolean foundInline;
        boolean lookBack = false;

        double cdx;
        double cdy;

        for (int i=0; i<numberOfPoints; i++)
        {
            if (xPoints[i]==Double.MAX_VALUE)
            {
                foundInline=false;
                // This point should remain horizontally-in-line with start/end of contour
                if (i>0)
                {
                    if (xPoints[i-1]!=Double.MAX_VALUE)
                    {
                        yPoints[i]=yPoints[i-1];
                        foundInline=true;
                    }
                }

                if (! foundInline)
                {
                    if (i<(numberOfPoints-1))
                    {
                        if (xPoints[i+1]!=Double.MAX_VALUE)
                        {
                            lookBack = true; // next iteration (after rotate) shall fill yPoints[i-1]
                            foundInline = true;
                        }
                    }
                }
                // At this point, foundInline should always be true, or poly is invalid!
                // TODO: maybe throw an exception? (assert)
            }
            else
            {
                // Rotate point around (cx, cy)
                cdx = xPoints[i] - cx;
                cdy = yPoints[i] - cy;

                xPoints[i] = (double)( cx + cdx*Math.cos( angle ) - cdy*Math.sin( angle ) );
                yPoints[i] = (double)( cy + cdx*Math.sin( angle ) + cdy*Math.cos( angle ) );

                // Complete infinitely distant neighbour (predcessor)
                if (lookBack)
                {
                    yPoints[i-1] = yPoints[i];
                    lookBack = false;
                }
            } // else
        } // for
    } // rotateAround

    public void rotateCurvesAround( double cx, double cy, double angle )
    {
        double cdx;
        double cdy;

        for (int i=0; i<numberOfPoints; i++)
        {
            if (xPoints[i] != Double.MAX_VALUE) // don't rotate LINE, SPLINE and CURVEEND commands
            {
                // Rotate point around (cx, cy)
                cdx = xPoints[i] - cx;
                cdy = yPoints[i] - cy;

                xPoints[i] = (double)( cx + cdx*Math.cos( angle ) - cdy*Math.sin( angle ) );
                yPoints[i] = (double)( cy + cdx*Math.sin( angle ) + cdy*Math.cos( angle ) );
            }
        }
    }

    public void resize( double origX, double origY,
                        double dx, double dy,
                        double xScale, double yScale )
    {
        recalculateBounds();

        double distX = bounds.getX() - origX;
        origX += (distX*xScale);

        double distY = bounds.getY() - origY;
        origY += (distY*yScale);

        for (int i=0; i<numberOfPoints; i++)
        {
            // scale point
            if (xPoints[i] != Double.MAX_VALUE) // don't scale infinite x coordinates
            {
                distX = xPoints[i] - bounds.getX();
                xPoints[i] = dx + (origX + distX*xScale);
            }

            distY = yPoints[i] - bounds.getY();
            yPoints[i] = dy + (origY + distY*yScale);
        }
    }

    public void resizeCurves( double origX, double origY,
                              double dx, double dy,
                              double xScale, double yScale )
    {
        recalculateBounds();

        double distX = bounds.getX() - origX;
        origX += (distX*xScale);

        double distY = bounds.getY() - origY;
        origY += (distY*yScale);

        for (int i=0; i<numberOfPoints; i++)
        {
            if (xPoints[i] != Double.MAX_VALUE) // don't scale LINE, SPLINE and CURVEEND
            {
                // scale point
                distX = xPoints[i] - bounds.getX();
                xPoints[i] = dx + (origX + distX*xScale);

                distY = yPoints[i] - bounds.getY();
                yPoints[i] = dy + (origY + distY*yScale);
            }
        }
    }

    public void horizontalPush( double baseY, double angle )
    {
        double distY;
        double sinA = Math.sin( -angle );

        for (int i=0; i<numberOfPoints; i++)
        {
            // horizontal push point
            if (xPoints[i] != Double.MAX_VALUE) // don't scale infinite x coordinates
            {
                distY = yPoints[i] - baseY;
                xPoints[i] += (distY*sinA);
            }
        }
    }

    public void horizontalPushCurves( double baseY, double angle )
    {
        double distY;
        double sinA = Math.sin( -angle );

        for (int i=0; i<numberOfPoints; i++)
        {
            // horizontal push point
            if (xPoints[i]!=Double.MAX_VALUE) // don't scale LINE, SPLINE, BEZIER, CURVEEND
            {
                distY = yPoints[i] - baseY;
                xPoints[i] += (distY*sinA);
            }
        }
    }

    public void verticalPush( double baseX, double angle )
    {
        boolean foundInline;
        boolean lookBack = false;

        double distX;
        double sinA = Math.sin( angle );

        for (int i=0; i<numberOfPoints; i++)
        {
            if (xPoints[i] == Double.MAX_VALUE)
            {
                foundInline=false;
                // This point should remain horizontally-in-line with start/end of contour
                if (i > 0)
                {
                    if (xPoints[i-1] != Double.MAX_VALUE)
                    {
                        yPoints[i] = yPoints[i-1];
                        foundInline = true;
                    }
                }

                if (! foundInline)
                {
                    if (i < (numberOfPoints-1))
                    {
                        if (xPoints[i+1] != Double.MAX_VALUE)
                        {
                            lookBack = true; // next iteration (after rotate) shall fill yPoints[i-1]
                            foundInline = true;
                        }
                    }
                }
                // At this point, foundInline should always be true, or poly is invalid!
                // TODO: maybe throw an exception? (assert)
            }
            else
            {
                // vertical push point
                distX = xPoints[i] - baseX;
                yPoints[i] += (sinA*distX);

                // Complete infinitely distant neighbour (predcessor)
                if (lookBack)
                {
                    yPoints[i-1] = yPoints[i];
                    lookBack = false;
                }
            }
        }
    }

    public void verticalPushCurves( double baseX, double angle )
    {
        double distX;
        double sinA = Math.sin( angle );

        for (int i=0; i<numberOfPoints; i++)
        {
            // vertical push point
            if (xPoints[i] != Double.MAX_VALUE) // don't scale LINE, SPLINE, BEZIER, CURVEEND
            {
                distX = xPoints[i] - baseX;
                yPoints[i] += (distX*sinA);
            }
        }
    }

    public void reverse() // note: should be used on plain data without commands!
    {
        if (numberOfPoints>1)
        {
            int j;
            double tmp;

            for (int i=0; i<(numberOfPoints/2); i++)
            {
                j = numberOfPoints - 1 - i;

                tmp = xPoints[i];
                xPoints[i] = xPoints[j];
                xPoints[j] = tmp;

                tmp = yPoints[i];
                yPoints[i] = yPoints[j];
                yPoints[j] = tmp;
            }
        }
    }

    public boolean containsAllPointsOf( DoublePolygon p )
    {
        int i;

        for (i=0; i<p.numberOfPoints; i++)
        {
            if (p.xPoints[i] != Double.MAX_VALUE)
            {
                if (! contains( p.xPoints[i], p.yPoints[i] ))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public DoublePolygon duplicate()
    {
        DoublePolygon result =
            new DoublePolygon( xPoints, yPoints, numberOfPoints );

        if (bounds != null)
        {
            result.bounds = new DoubleRectangle( bounds );
        }

        return result;
    }

} // END DoublePolygon
