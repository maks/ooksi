package ntr.ttme;

public class TTGlyph
{
    short numContours;
    short xMin;
    short yMin;
    short xMax;
    short yMax;
    TTContour[] contour;
    short advanceWidth;
    short leftSideBearing;
    short rightSideBearing;

    TTGlyph()
    {
        super();
    }

    public short getAdvanceWidth()
    {
        return advanceWidth;
    }

    public short getLeftSideBearing()
    {
        return leftSideBearing;
    }

    public short getRightSideBearing()
    {
        return rightSideBearing;
    }

    public short getNumContours()
    {
        return numContours;
    }

    public short getXMin()
    {
        return xMin;
    }

    public short getYMin()
    {
        return yMin;
    }

    public short getXMax()
    {
        return xMax;
    }

    public short getYMax()
    {
        return yMax;
    }

    public TTContour getContour( short index )
    {
        if (numContours > 0 &&
            index < numContours &&
            contour != null)
        {
            return contour[index];
        }
        else
        {
            return null;
        }
    }

} // END TTGlyph
