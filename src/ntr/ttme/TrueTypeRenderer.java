package ntr.ttme;

public class TrueTypeRenderer
{
    private static PolygonFiller POLYGON_FILLER = new NtrPolygonFiller();

    protected SplineCurveRenderer spline = new SplineCurveRenderer( 10 );
    protected BezierCurveRenderer bezier = new BezierCurveRenderer();

    protected int[] x = new int[4]; // only 3 points used when rendering fonts, 4th for GeneralCurve decoding
    protected int[] y = new int[4];

    protected double[] fx = new double[4];
    protected double[] fy = new double[4];

    protected short[] t = new short[4];

    public DoublePolygon curves = new DoublePolygon();
    public DoublePolygon transformedCurves = new DoublePolygon();

    public DoublePolygon worldPoints = new DoublePolygon();

    public TrueTypeRenderer()
    {
        super();
    }

    public void renderStringToPolygon( TrueTypeDefinition ttf,
                                       double xBase, double yBase,
                                       String textToDraw,
                                       double pointSize,
                                       double charSpacing )
        throws TrueTypeBusinessException, TrueTypeTechnicalException
    {
        worldPoints.numberOfPoints = 0;
        curves.numberOfPoints = 0;

        double xpos = xBase;

        double scale = pointSize/(72 * ttf.getUnitsPerMSqr());

        charSpacing *= (double)( ttf.getXMax() )*scale;

        for (int i=0; i<textToDraw.length(); i++)
        {
            char ch = textToDraw.charAt( i );

            double advance = renderCharToPolygon( ttf, xpos, yBase, ch, scale );
            xpos += Math.max( charSpacing, advance );
        } // for

        transformedCurves = curves.duplicate();
    } // renderStringToPolygon

    protected double renderCharToPolygon( TrueTypeDefinition ttf,
                                          double xBase, double yBase,
                                          char ch,
                                          double scale )
        throws TrueTypeBusinessException, TrueTypeTechnicalException
    {
        // contour nesting coordinates (located in "infinity")
        double sx = Double.MAX_VALUE;
        double sy = 0;

        // handling OFF_CURVE start
        double offX = 0;
        double offY = 0;
        double firstX = 0;
        double firstY = 0;

        short glyphIdx = ttf.mapCharacterToGlyph( (short)ch );

        double advance = ((double)ttf.getGlyphAdvanceWidth( glyphIdx ))*scale;

        short numberOfContours=ttf.getNumContours( glyphIdx );

        for (short c=0; c<numberOfContours; c++)
        {
            short numberOfPoints = ttf.getNumPoints( glyphIdx, c );

            if (numberOfPoints > 1)
            {
                double bottom = yBase; //-(double)ttf.getDescender()*scale; // descender is negative

                boolean contourStarted = false;
                boolean startedWithOffCurve = false;

                int lastFilledPoint = -1;

                for (short p=0; p<=numberOfPoints; p++)
                {
                    short pnt = ((p < numberOfPoints) ? p : (short)( p-numberOfPoints ));

                    lastFilledPoint++;
                    if (lastFilledPoint > 2) // this can never happen for a valid TTF file
                    {
                        // lastFilledPoint = 0;
                        String msg = "Font contains bad point-sequence! ";
                        for (int i=0; i<lastFilledPoint; i++)
                        {
                            msg += (t[i] + ";");
                        }

                        throw new TrueTypeBusinessException( msg );
                    }

                    fx[lastFilledPoint] = xBase + (double)ttf.getFontPointX( glyphIdx, c, pnt )*scale;
                    fy[lastFilledPoint] = bottom - (double)ttf.getFontPointY( glyphIdx, c, pnt )*scale;
                    t[lastFilledPoint] = ttf.getFontPointType( glyphIdx, c, pnt );

                    if (lastFilledPoint == 1) // OFF_CURVE-start check for 2 points
                    {
                        if (t[0] == TTPoint.TYPE_OFF_CURVE)
                        {
                            startedWithOffCurve = true;

                            offX = fx[0];
                            offY = fy[0];

                            if (t[1] == TTPoint.TYPE_ON_CURVE)
                            {
                                fx[0] = fx[1];
                                fy[0] = fy[1];
                                t[0] = t[1];

                                lastFilledPoint = 0; // only 1 point left (OFF will be used at end)
                            }
                            else
                            {
                                fx[0] = (fx[0] + fx[1])/2;
                                fy[0] = (fy[0] + fy[1])/2;
                                t[0] = TTPoint.TYPE_ON_CURVE;
                            }

                            firstX = fx[0];
                            firstY = fy[0];
                        } // if
                    } // OFF_CURVE-start check

                    if (lastFilledPoint == 1) // 2 points
                    {
                        if (t[0] == TTPoint.TYPE_ON_CURVE)
                        {
                            if (t[1] == TTPoint.TYPE_ON_CURVE)
                            {
                                // ON + ON - draw line
                                if (! contourStarted)
                                {
                                    sy = fy[0];

                                    worldPoints.addPoint( sx, sy );

                                    worldPoints.addPoint( fx[0], fy[0] );

                                    curves.addPoint( fx[0], fy[0] );

                                    contourStarted = true;
                                }
                                worldPoints.addPoint( fx[1], fy[1] );
                                curves.addPoint( fx[1], fy[1] );
                                curves.addPoint( sx, CurveType.LINE );

                                fx[0] = fx[1];
                                fy[0] = fy[1];
                                t[0] = t[1];
                                lastFilledPoint = 0;
                            } // if
                        } // if
                    }
                    else if (lastFilledPoint == 2) // 3 points
                    {
                        if (t[0] == TTPoint.TYPE_ON_CURVE)
                        {
                            if (t[1] == TTPoint.TYPE_OFF_CURVE)
                            {
                                if (t[2] == TTPoint.TYPE_ON_CURVE) // ON + OFF + ON - draw spline
                                {
                                    spline.calculateWorldSpline( fx[0], fy[0],
                                                                 fx[1], fy[1],
                                                                 fx[2], fy[2] );
                                    if (! contourStarted)
                                    {
                                        sy = spline.worldPoints.yPoints[0];

                                        worldPoints.addPoint( sx, sy );

                                        worldPoints.addPoint( spline.worldPoints.xPoints[0],
                                                              spline.worldPoints.yPoints[0] );

                                        curves.addPoint( fx[0], fy[0] );

                                        contourStarted = true;
                                    }

                                    for (int i=1; i<spline.worldPoints.numberOfPoints; i++)
                                    {
                                        worldPoints.addPoint( spline.worldPoints.xPoints[i],
                                                              spline.worldPoints.yPoints[i] );
                                    }
                                    curves.addPoint( fx[1], fy[1] );
                                    curves.addPoint( fx[2], fy[2] );
                                    curves.addPoint( sx, CurveType.SPLINE );

                                    fx[0] = fx[2];
                                    fy[0] = fy[2];
                                    t[0] = t[2];

                                    lastFilledPoint = 0;
                                }
                                else // ON + OFF + OFF - create imaginary point and use it
                                {
                                    double tX = (fx[1]+fx[2])/2;
                                    double tY = (fy[1]+fy[2])/2;
                                    short tType = TTPoint.TYPE_ON_CURVE;

                                    // draw spline 0-1-T
                                    spline.calculateWorldSpline( fx[0], fy[0],
                                                                 fx[1], fy[1],
                                                                 tX, tY );
                                    if (! contourStarted)
                                    {
                                        sy = spline.worldPoints.yPoints[0];

                                        worldPoints.addPoint( sx, sy );

                                        worldPoints.addPoint( spline.worldPoints.xPoints[0],
                                                              spline.worldPoints.yPoints[0] );

                                        curves.addPoint( fx[0], fy[0] );

                                        contourStarted=true;
                                    }

                                    for (int i=1; i<spline.worldPoints.numberOfPoints; i++)
                                    {
                                        worldPoints.addPoint( spline.worldPoints.xPoints[i],
                                                              spline.worldPoints.yPoints[i] );
                                    }
                                    curves.addPoint( fx[1], fy[1] );
                                    curves.addPoint( tX, tY );
                                    curves.addPoint( sx, CurveType.SPLINE );

                                    fx[0] = tX;
                                    fy[0] = tY;
                                    t[0] = tType;

                                    fx[1] = fx[2];
                                    fy[1] = fy[2];
                                    t[1] = t[2];

                                    lastFilledPoint = 1;
                                } // else
                            } // if
                        } // if
                    } // else if

                } // for p

                // finish OFF_CURVE-started contour
                if (startedWithOffCurve) // ON + OFF + ON - draw spline
                {
                    if (t[lastFilledPoint] == TTPoint.TYPE_ON_CURVE)
                    {
                        double lastX = fx[lastFilledPoint];
                        double lastY = fy[lastFilledPoint];

                        // Last point is ON.
                        // Form SPLINE of last, OFF and FIRST
                        spline.calculateWorldSpline( lastX, lastY,
                                                     offX, offY,
                                                     firstX, firstY );
                        if (! contourStarted) // this should never happen with valid font-files
                        {
                            sy = spline.worldPoints.yPoints[0];
                            worldPoints.addPoint( sx, sy );

                            worldPoints.addPoint( spline.worldPoints.xPoints[0],
                                                  spline.worldPoints.yPoints[0] );

                            curves.addPoint( lastX, lastY );

                            contourStarted = true;
                        }

                        for (int i=1; i<spline.worldPoints.numberOfPoints; i++)
                        {
                            worldPoints.addPoint( spline.worldPoints.xPoints[i],
                                                  spline.worldPoints.yPoints[i] );
                        }
                        curves.addPoint( offX, offY );
                        curves.addPoint( firstX, firstY );
                        curves.addPoint( sx, CurveType.SPLINE );
                    }
                    else
                    {
                        // double lastX = (fx[lastFilledPoint] + offX)/2;
                        // double lastY = (fy[lastFilledPoint] + offY)/2;

                        // lastFilledPoint is OFF_CURVE, and that means that is the repeated OFF.
                        // OFF_CURVE *MUST* have predcessor.
                        // add SPLINE formed of predcessor (for last), last and first point.
                        spline.calculateWorldSpline( fx[lastFilledPoint-1], fy[lastFilledPoint-1],
                                                     fx[lastFilledPoint], fy[lastFilledPoint],
                                                     firstX, firstY );
                        if (! contourStarted) // this should never happen with valid font-files
                        {
                            sy = spline.worldPoints.yPoints[0];

                            worldPoints.addPoint( sx, sy );

                            worldPoints.addPoint( spline.worldPoints.xPoints[0],
                                                  spline.worldPoints.yPoints[0] );

                            curves.addPoint( fx[lastFilledPoint-1], fy[lastFilledPoint-1] );

                            contourStarted = true;
                        }

                        for (int i=1; i<spline.worldPoints.numberOfPoints; i++)
                        {
                            worldPoints.addPoint( spline.worldPoints.xPoints[i],
                                                  spline.worldPoints.yPoints[i] );
                        }
                        curves.addPoint( fx[lastFilledPoint], fy[lastFilledPoint] );
                        curves.addPoint( firstX, firstY );
                        curves.addPoint( sx, CurveType.SPLINE );
                    } // else
                } // OFF_CURVE-started-curves

                worldPoints.addPoint( sx, sy );
                curves.addPoint( sx, CurveType.CURVE_END );
            } // if
        } // for

        return advance;
    } // renderCharToPolygon

    public void transformCurves( DoubleMatrix3x3 ctm )
    {
        transformedCurves.numberOfPoints = 0;
        for (int i=0; i<curves.numberOfPoints; i++)
        {
            if (curves.xPoints[i] != Double.MAX_VALUE)
            {
                transformedCurves.addPoint(
                    ctm.element[0][0] * curves.xPoints[i] +
                    ctm.element[0][1] * curves.yPoints[i] +
                    ctm.element[0][2],
                    ctm.element[1][0] * curves.xPoints[i] +
                    ctm.element[1][1] * curves.yPoints[i] +
                    ctm.element[1][2] );
            }
            else
            {
                transformedCurves.addPoint( curves.xPoints[i], curves.yPoints[i] );
            }
        }
    }

    public void decodeCurves()
    {
        worldPoints.numberOfPoints = 0;

        boolean contourStarted = false;
        int lastFilledPoint = -1;

        for (int i=0; i<transformedCurves.numberOfPoints; i++)
        {
            if (transformedCurves.xPoints[i] == Double.MAX_VALUE) // rendering command
            {
                if (transformedCurves.yPoints[i] == CurveType.LINE)
                {
                    if (lastFilledPoint >= 1)
                    {
                        if (! contourStarted)
                        {
                            worldPoints.addPoint( Double.MAX_VALUE, fy[lastFilledPoint-1] );
                            worldPoints.addPoint( fx[lastFilledPoint-1], fy[lastFilledPoint-1] );
                            contourStarted = true;
                        }
                        worldPoints.addPoint( fx[lastFilledPoint], fy[lastFilledPoint] );

                        fx[0] = fx[lastFilledPoint];
                        fy[0] = fy[lastFilledPoint];
                        lastFilledPoint = 0;
                    }
                }
                else if (transformedCurves.yPoints[i] == CurveType.SPLINE)
                {
                    if (lastFilledPoint == 2)
                    {
                        spline.calculateWorldSpline( fx[0], fy[0],
                                                     fx[1], fy[1],
                                                     fx[2], fy[2] );

                        if (! contourStarted)
                        {
                            worldPoints.addPoint( Double.MAX_VALUE, spline.worldPoints.yPoints[0] );
                            worldPoints.addPoint( spline.worldPoints.xPoints[0],
                                                  spline.worldPoints.yPoints[0] );
                            contourStarted = true;
                        }
                        for (int j=1; j<spline.worldPoints.numberOfPoints; j++)
                        {
                            worldPoints.addPoint( spline.worldPoints.xPoints[j],
                                                  spline.worldPoints.yPoints[j] );
                        }

                        fx[0] = fx[2];
                        fy[0] = fy[2];
                        lastFilledPoint = 0;
                    }
                }
                else if (transformedCurves.yPoints[i] == CurveType.BEZIER)
                {
                    if (lastFilledPoint == 3)
                    {
                        bezier.worldCalculateBezier( fx[0], fy[0],
                                                     fx[1], fy[1],
                                                     fx[2], fy[2],
                                                     fx[3], fy[3] );

                        if (! contourStarted)
                        {
                            worldPoints.addPoint( Double.MAX_VALUE, bezier.worldPoints.yPoints[0] );
                            worldPoints.addPoint( bezier.worldPoints.xPoints[0],
                                                  bezier.worldPoints.yPoints[0] );
                            contourStarted = true;
                        }
                        for (int j=1; j<bezier.worldPoints.numberOfPoints; j++)
                        {
                            worldPoints.addPoint( bezier.worldPoints.xPoints[j],
                                                  bezier.worldPoints.yPoints[j] );
                        }

                        fx[0] = fx[3];
                        fy[0] = fy[3];
                        lastFilledPoint = 0;
                    }
                }
                else if (transformedCurves.yPoints[i] == CurveType.CURVE_END)
                {
                    if (contourStarted)
                    {
                        // note: next line moves infinitely from last decoded point if it exists
                        if (worldPoints.numberOfPoints > 0)
                        {
                            worldPoints.addPoint( Double.MAX_VALUE,
                                                  worldPoints.yPoints[worldPoints.numberOfPoints-1] );
                        }
                        contourStarted = false;
                    }
                    lastFilledPoint = -1;
                }
            }
            else
            {
                if (lastFilledPoint < 3) // this should always be true! (or data is invalid)
                {
                    lastFilledPoint++;
                    fx[lastFilledPoint] = transformedCurves.xPoints[i];
                    fy[lastFilledPoint] = transformedCurves.yPoints[i];
                }
            }
        } // for
        worldPoints.calculateBounds();
    } // decodeCurves

    public double calculateTextWidth( TrueTypeDefinition ttf,
                                      String textToMeasure,
                                      double pointSize,
                                      double charSpacing )
        throws TrueTypeBusinessException, TrueTypeTechnicalException
    {
        double width = 0.0d;

        double scale = pointSize/(72 * ttf.getUnitsPerMSqr());

        charSpacing *= (double)( ttf.getXMax() )*scale;

        for (int i=0; i<textToMeasure.length(); i++)
        {
            char ch = textToMeasure.charAt( i );

            short glyphIdx = ttf.mapCharacterToGlyph( (short)ch );
            double advance = ((double)ttf.getGlyphAdvanceWidth( glyphIdx ))*scale;

            if (i != textToMeasure.length()-1)
            {
                width += Math.max( charSpacing, advance );
            }
        } // for

        return width;
    }

    public static void setPolygonFiller( PolygonFiller polygonFiller )
    {
        POLYGON_FILLER = polygonFiller;
    }

    public static PolygonFiller getPolygonFiller()
    {
        return POLYGON_FILLER;
    }

    public static double calculateWorldAscender( TrueTypeDefinition ttf, double pointSize )
    {
        double scale = pointSize/(72d * ttf.getUnitsPerMSqr());
        return (double)ttf.getAscender()*scale;
    }

    public static double calculateWorldDescender( TrueTypeDefinition ttf, double pointSize )
    {
        double scale = pointSize/(72d * ttf.getUnitsPerMSqr());
        return (double)ttf.getDescender()*scale;
    }

} // END TrueTypeRenderer
