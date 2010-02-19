package ntr.ttme;

import javax.microedition.lcdui.Graphics;

public class View
{
    public static final double WORLD_MIN_X = -32768;
    public static final double WORLD_MAX_X = 32767;
    public static final double WORLD_MIN_Y = -32768;
    public static final double WORLD_MAX_Y = 32767;

    public static final double NO_ZOOM = 1.0d; // 1:1
    public static final double MAX_ZOOM_IN = 8.0d; // zoom factor x8
    public static final double MAX_ZOOM_OUT = 1.0d/16.0d; // zoom factor x(1/16)

    public static final int COLOR_WHITE = 0x00ffffff;
    public static final int DEFAULT_BACKBUFFER_COLOR = COLOR_WHITE;

    // view
    private int viewWidth;
    private int viewHeight;

    // world
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private DoubleRectangle visibleWorld; // world area displayed on the view
    private double viewCenterWorldX; // world view-point X
    private double viewCenterWorldY; // world view-point Y

    // zooming
    private double zoomFactor; // [MAX_ZOOM_OUT, 1.0d) - zoom out; 1.0d - normal view; (1.0d, MAX_ZOOM_IN] - zoom in;

    // rendering to screen or maybe to printer...
    private boolean forcedResolution;
    private double WYSIWYGScaleX;
    private double WYSIWYGScaleY;

    // double-buffering
    private Graphics frontBuffer;
    private Graphics backBuffer;
    private int backBufferColor;

    public View( int w, int h,
                 double zoomFactor,
                 double viewWorldX, double viewWorldY )
    {
        forcedResolution = false; // resolution will be detected from the screen

        viewWidth = w;
        viewHeight = h;

        setZoomFactor( zoomFactor );

        configureWYSIWYG();

        minX = WORLD_MIN_X;
        maxX = WORLD_MAX_X;
        minY = WORLD_MIN_Y;
        maxY = WORLD_MAX_Y;

        viewCenterWorldX = viewWorldX;
        viewCenterWorldY = viewWorldY;
        visibleWorld = new DoubleRectangle(); // instantiate only, re-shaped in #setWorldViewPoint()

        centerViewPoint(); // calculate visibleWorld using view-point (x, y)
    }

    public View( int w, int h )
    {
        this( w, h,
              NO_ZOOM,
              0.0d, 0.0d );
    }

    public View( int w, int h,
                 double zoomFactor )
    {
        this( w, h,
              zoomFactor,
              0.0d, 0.0d );
    }

    public View( int w, int h,
                 double zoomFactor,
                 double viewWorldX, double viewWorldY,
                 int xRes, int yRes )
    {
        forcedResolution = true; // user defined resolution (printer?)

        viewWidth = w;
        viewHeight = h;

        setZoomFactor( zoomFactor );

        WYSIWYGScaleX = xRes;
        WYSIWYGScaleY = yRes;

        minX = WORLD_MIN_X;
        maxX = WORLD_MAX_X;
        minY = WORLD_MIN_Y;
        maxY = WORLD_MAX_Y;

        viewCenterWorldX = viewWorldX;
        viewCenterWorldY = viewWorldY;
        visibleWorld = new DoubleRectangle(); // instantiate only, re-shaped in #setWorldViewPoint()

        centerViewPoint(); // calculate visibleWorld using view-point (x, y)
    }

    public void setFrontBuffer( Graphics frontBuffer )
    {
        this.frontBuffer = frontBuffer; // frontBuffer.create( viewX, viewY, viewWidth, viewHeight );
    }

    public Graphics getFrontBuffer()
    {
        if (! hasFrontBuffer())
        {
            throw new IllegalStateException( "FrontBuffer NOT set!" );
        }
        return frontBuffer;
    }

    public void setBackBuffer( Graphics backBuffer )
    {
        setBackBuffer( backBuffer, DEFAULT_BACKBUFFER_COLOR );
    }

    public void setBackBuffer( Graphics backBuffer, int backBufferColor )
    {
        this.backBuffer = backBuffer;
        this.backBufferColor = backBufferColor;

        clearBackBuffer();
    }

    public Graphics getBackBuffer()
    {
        if (! hasBackBuffer())
        {
            throw new IllegalStateException( "BackBuffer NOT set!" );
        }
        return backBuffer;
    }

    public void setBuffers( Graphics frontBuffer, Graphics backBuffer )
    {
        setFrontBuffer( frontBuffer );
        setBackBuffer( backBuffer );
    }

    public void setBuffers( Graphics frontBuffer, Graphics backBuffer, int backBufferColor )
    {
        setFrontBuffer( frontBuffer );
        setBackBuffer( backBuffer, backBufferColor );
    }

    public boolean hasFrontBuffer()
    {
        return (frontBuffer != null);
    }

    public boolean hasBackBuffer()
    {
        return (backBuffer != null);
    }

    public boolean hasBuffers()
    {
        return (hasFrontBuffer() && hasBackBuffer());
    }

    public void clearBackBuffer()
    {
        clearBackBuffer( backBufferColor );
    }

    public void clearBackBuffer( int color )
    {
        if (! hasBackBuffer())
        {
            throw new IllegalStateException( "BackBuffer NOT set, unable to clear!" );
        }
        backBuffer.setColor( color );
        backBuffer.fillRect( 0, 0, viewWidth, viewHeight );
    }

    public int convertXWorldToView( double worldXCoord )
    {
        return (int)( (worldXCoord - visibleWorld.x)*zoomFactor*WYSIWYGScaleX );
    }

    public int convertYWorldToView( double worldYCoord )
    {
        return (int)( (worldYCoord - visibleWorld.y)*zoomFactor*WYSIWYGScaleY );
    }

    public int convertXDistanceWorldToView( double distance )
    {
        return (int)( distance*zoomFactor*WYSIWYGScaleX );
    }

    public int convertYDistanceWorldToView( double distance )
    {
        return (int)( distance*zoomFactor*WYSIWYGScaleY );
    }

    public int convertMinDistanceWorldToView( double distance )
    {
        if (WYSIWYGScaleX < WYSIWYGScaleX)
        {
            return convertXDistanceWorldToView( distance );
        }
        else
        {
            return convertYDistanceWorldToView( distance );
        }
    }

    public double convertXViewToWorld( int viewXCoord )
    {
        return visibleWorld.x + (double)viewXCoord/(zoomFactor*WYSIWYGScaleX);
    }

    public double convertYViewToWorld( int viewYCoord )
    {
        return visibleWorld.y + (double)viewYCoord/(zoomFactor*WYSIWYGScaleY);
    }

    public double convertXDistanceViewToWorld( int distance )
    {
        return (double)distance / (zoomFactor*WYSIWYGScaleX);
    }

    public double convertYDistanceViewToWorld( int distance )
    {
        return (double)distance / (zoomFactor*WYSIWYGScaleY);
    }

    public double convertMaxDistanceViewToWorld( int distance )
    {
        if (WYSIWYGScaleX < WYSIWYGScaleY)
        {
            return convertXDistanceViewToWorld( distance );
        }
        else
        {
            return convertYDistanceViewToWorld( distance );
        }
    }

    public void setWorldViewPoint( double worldViewPointX, double worldViewPointY )
    {
        visibleWorld.x = 0.0d;
        visibleWorld.y = 0.0d;
        visibleWorld.width = convertXDistanceViewToWorld( viewWidth );
        visibleWorld.height = convertYDistanceViewToWorld( viewHeight );

        // protect world boundaries

        if (minX <= worldViewPointX)
        {
            if (maxX >= worldViewPointX)
            {
                visibleWorld.x = worldViewPointX - visibleWorld.width/2.0d;
            }
            else
            {
                visibleWorld.x = maxX - visibleWorld.width/2.0d;
            }
        }
        else
        {
            visibleWorld.x = minX - visibleWorld.width/2.0d;
        }

        if (minY <= worldViewPointY)
        {
            if (maxY >= worldViewPointY)
            {
                visibleWorld.y = worldViewPointY - visibleWorld.height/2.0d;
            }
            else
            {
                visibleWorld.y = maxY - visibleWorld.height/2.0d;
            }
        }
        else
        {
            visibleWorld.y = minY - visibleWorld.height/2.0d;
        }

        // calculate view-point as the middle of visibleWorld rectangle
        viewCenterWorldX = visibleWorld.x + visibleWorld.width/2.0d;
        viewCenterWorldY = visibleWorld.y + visibleWorld.height/2.0d;
    }

    public void pan( int viewXDistance, int viewYDistance )
    {
        setWorldViewPoint(
            viewCenterWorldX - convertXDistanceViewToWorld( viewXDistance ),
            viewCenterWorldY - convertYDistanceViewToWorld( viewYDistance ) );
    }

    public void setZoomFactor( double zoomFactor )
    {
        if (zoomFactor >= 1)
        {
            this.zoomFactor = Math.min( zoomFactor, MAX_ZOOM_IN );
        }
        else
        {
            this.zoomFactor = Math.max( zoomFactor, MAX_ZOOM_OUT );
        }
    }

    public boolean zoomIn( int x, int y )
    {
        if (zoomFactor < MAX_ZOOM_IN)
        {
            setZoomFactor( zoomFactor*2.0d );
            setWorldViewPoint( convertXViewToWorld( x ), convertYViewToWorld( y ) );
            return true;
        }
        else
        {
            return false;
        }
    }

    public void centerViewPoint()
    {
        setWorldViewPoint( viewCenterWorldX, viewCenterWorldY );
    }

    public boolean zoomIn()
    {
        if (zoomFactor < MAX_ZOOM_IN)
        {
            setZoomFactor( zoomFactor*2.0d );
            centerViewPoint();
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean zoomOut()
    {
        if (zoomFactor > MAX_ZOOM_OUT)
        {
            setZoomFactor( zoomFactor/2.0d );
            centerViewPoint();
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean noZoom()
    {
        if (zoomFactor != NO_ZOOM)
        {
            setZoomFactor( NO_ZOOM );
            centerViewPoint();
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean setXFitZoom( double xDistanceToFit )
    {
        double oldZoomFactor = zoomFactor;

        setZoomFactor(
            (double)viewWidth / (Math.abs( xDistanceToFit )*WYSIWYGScaleX) );

        return (zoomFactor != oldZoomFactor);
    }

    public boolean setYFitZoom( double yDistanceToFit )
    {
        double oldZoomFactor = zoomFactor;

        setZoomFactor(
            (double)viewHeight / (Math.abs( yDistanceToFit )*WYSIWYGScaleY) );

        return (zoomFactor != oldZoomFactor);
    }

    public boolean setXYFitZoom( double xDistanceToFit, double yDistanceToFit )
    {
        double oldZoomFactor = zoomFactor;

        setZoomFactor(
            Math.min(
                (double)viewWidth / (Math.abs( xDistanceToFit )*WYSIWYGScaleX),
                (double)viewHeight / (Math.abs( yDistanceToFit )*WYSIWYGScaleY) ) );

        return (zoomFactor != oldZoomFactor);
    }

    private void configureWYSIWYG()
    {
        if (! forcedResolution)
        { // otherwise these parameters are already set by constructor
            WYSIWYGScaleX = 72; // Toolkit.getDefaultToolkit().getScreenResolution();  /* /(4.0d/3.0d); */
            WYSIWYGScaleY = WYSIWYGScaleX;
        }
    }

    public boolean setRanges( double areaX, double areaY, double areaW, double areaH )
    {
        if (areaW > 0 &&
            areaH > 0)
        {
            double x = areaX - areaW;
            double y = areaY - areaH;
            double w = areaW * 3;
            double h = areaH * 3;

            minX = x;
            maxX = x + w;

            minY = y;
            maxY = y + h;

            return true;
        }
        else
        {
            return false;
        }
    }

    public double getViewCenterWorldX()
    {
        return viewCenterWorldX;
    }

    public double getViewCenterWorldY()
    {
        return viewCenterWorldY;
    }

    public double getMinX()
    {
        return minX;
    }

    public double getMaxX()
    {
        return maxX;
    }

    public double getMinY()
    {
        return minY;
    }

    public double getMaxY()
    {
        return maxY;
    }

} // END View
