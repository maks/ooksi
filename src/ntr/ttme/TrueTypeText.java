package ntr.ttme;

import javax.microedition.lcdui.Graphics;

public class TrueTypeText
    extends VectorGraphicsAdapter
{
    private TrueTypeDefinition ttf;

    private Integer color;
    private Integer fillColor;

    private double worldAscender;
    private double worldDescender;

    private TrueTypeRenderer renderer;
    
    private double textWorldWidth;

    private IntPolygon viewPoints;

    public TrueTypeText( String token,
                         double x,
                         double y,
                         String text,
                         double sizeInPoints,
                         Integer color )
        throws TrueTypeBusinessException, TrueTypeTechnicalException
    {
        this( token,
              x,
              y,
              text,
              sizeInPoints,
              color,
              null,
              0.02d );
    }

    public TrueTypeText( String token,
                         double x,
                         double y,
                         String text,
                         double sizeInPoints,
                         Integer color,
                         Integer fillColor )
        throws TrueTypeBusinessException, TrueTypeTechnicalException
    {
        this( token,
              x,
              y,
              text,
              sizeInPoints,
              color,
              fillColor,
              0.02d );
    }

    public TrueTypeText( String token,
                         double x,
                         double y,
                         String text,
                         double sizeInPoints,
                         Integer color,
                         Integer fillColor,
                         double characterSpacing )
        throws TrueTypeBusinessException, TrueTypeTechnicalException
    {
        super();

        ttf = TrueTypeRegistry.instance.get( token );
        if (ttf == null)
        {
            throw new TrueTypeBusinessException( "TrueType token='"+token+"' is NOT registered!" );
        }

        translate( 0, 0 );
        this.color = color;
        this.fillColor = fillColor;

        renderer = new TrueTypeRenderer();

        renderer.renderStringToPolygon(
            ttf,
            x, y,
            text,
            sizeInPoints,
            characterSpacing );

        textWorldWidth =
            renderer.calculateTextWidth(
                ttf,
                text,
                sizeInPoints,
                characterSpacing );

        this.worldAscender = TrueTypeRenderer.calculateWorldAscender( ttf, sizeInPoints );
        this.worldDescender = TrueTypeRenderer.calculateWorldDescender( ttf, sizeInPoints );
    }

    public void draw( View view, Graphics g )
    {
        if (viewPoints == null ||
            modified)
        {
            if (modified)
            {
                renderer.transformCurves( ctm );

                renderer.decodeCurves();
            }

            int nPoints = renderer.worldPoints.numberOfPoints;
            int[] xPoints = new int[nPoints];
            int[] yPoints = new int[nPoints];

            for (int i=0; i<nPoints; i++)
            {
                xPoints[i] = view.convertXWorldToView( renderer.worldPoints.xPoints[i] );
                yPoints[i] = view.convertYWorldToView( renderer.worldPoints.yPoints[i] );
            }

            viewPoints = new IntPolygon( xPoints, yPoints, nPoints );

            modified = false;
        }

        if (color != null)
        {
            if (fillColor != null)
            {
                g.setColor( fillColor.intValue() );
                PolygonFiller filler = TrueTypeRenderer.getPolygonFiller();
                if (filler != null)
                {
                    filler.fillPolygon( g, viewPoints.xpoints, viewPoints.ypoints, viewPoints.npoints );
                }
            }

            if (color != null)
            {
                g.setColor( color.intValue() );
            }
            else if (fillColor != null)
            {
                g.setColor( fillColor.intValue() );
            }

            for (int i=0; i<viewPoints.npoints-1; i++)
            {
                if (renderer.worldPoints.xPoints[i] != Double.MAX_VALUE)
                {
                    if (renderer.worldPoints.xPoints[i+1] != Double.MAX_VALUE)
                    {
                        g.drawLine( viewPoints.xpoints[i], viewPoints.ypoints[i],
                                    viewPoints.xpoints[i+1], viewPoints.ypoints[i+1] );
                    }
                }
            }
        }
    }

    public static String intArrayToString( int[] a )
    {
        if (a.length == 0)
        {
            return "";
        }
        String result = ""+a[0];
        for (int i=1; i<a.length; i++)
        {
            result += ","+a[i];
        }
        return result;
    }
    public double getTextWorldWidth()
    {
        return textWorldWidth;
    }

    public DoubleRectangle getBounds()
    {
        if (renderer.worldPoints == null)
        {
            return null;
        }
        else
        {
            return renderer.worldPoints.bounds;
        }
    }

    public double getWorldAscender()
    {
        return this.worldAscender;
    }

    public double getWorldDescender()
    {
        return this.worldDescender;
    }

    public int getViewAscender( View view )
    {
        return view.convertYDistanceWorldToView( this.worldAscender );
    }

    public int getWorldDescender( View view )
    {
        return view.convertYDistanceWorldToView( this.worldDescender );
    }

} // END TrueTypeText
