package ntr.ttme;

public class TTContour
{
    short numPoints;
    TTPoint[] point;

    TTContour()
    {
        numPoints = 0;
        point    = null;
    }

    public short getNumPoints()
    {
        return numPoints;
    }

    public TTPoint getPoint( short index )
    {
        if (index < numPoints &&
            point!=null)
        {
            return point[index];
        }
        else
        {
            return null;
        }
    }

} // END TTContour
