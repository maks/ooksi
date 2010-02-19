package ntr.ttme;

public class DoublePoint
{
    public double x;
    public double y;

    public DoublePoint()
    {
        this( 0, 0 );
    }

    public DoublePoint( DoublePoint p )
    {
        this( p.x, p.y );
    }

    public DoublePoint( double x, double y )
    {
        this.x = x;
        this.y = y;
    }

    public DoublePoint duplicate()
    {
        return new DoublePoint( x, y );
    }

    public void copyFrom( DoublePoint p )
    {
        setLocation( p.x, p.y );
    }

    public void setLocation( double x, double y )
    {
        this.x = x;
        this.y = y;
    }

    public void translate( double dx, double dy )
    {
        x += dx;
        y += dy;
    }

    public boolean equals( Object obj )
    {
        if (obj instanceof DoublePoint)
        {
            DoublePoint p = (DoublePoint)obj;

            return x == p.x &&
                   y == p.y;
        }
        else
        {
            return false;
        }
    }

} // END DoublePoint
