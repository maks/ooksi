package ntr.ttme;

public class DoubleRectangle
{
    double x;
    double y;
    double width;
    double height;

    public DoubleRectangle()
    {
        this( 0, 0, 0, 0 );
    }

    public DoubleRectangle( DoubleRectangle r )
    {
        this( r.x, r.y, r.width, r.height );
    }

    public DoubleRectangle( double x, double y, double width, double height )
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public DoubleRectangle( double width, double height )
    {
        this( 0, 0, width, height );
    }

    public double getX()
    {
        return x;
    }
    public void setX( double x )
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }
    public void setY( double y )
    {
        this.y = y;
    }

    public double getWidth()
    {
        return width;
    }
    public void setWidth( double width )
    {
        this.width = width;
    }

    public double getHeight()
    {
        return height;
    }
    public void setHeight( double height )
    {
        this.height = height;
    }

    public DoubleRectangle getBounds()
    {
        return new DoubleRectangle( x, y, width, height );
    }

    public void copyFrom( DoubleRectangle r )
    {
        reshape( r.x, r.y, r.width, r.height );
    }

    public void reshape( double x, double y, double width, double height )
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setLocation( double x, double y )
    {
        this.x = x;
        this.y = y;
    }

    public void setSize( double width, double height )
    {
        this.width = width;
        this.height = height;
    }

    public void translate( double dx, double dy )
    {
        x += dx;
        y += dy;
    }

    public boolean contains( DoublePoint p )
    {
        return contains( p.x, p.y );
    }

    public boolean contains( double x, double y )
    {
        return (x >= this.x &&
                y >= this.y &&
                (x-this.x) < this.width &&
                (y-this.y) < this.height);
    }

    public boolean intersects( DoubleRectangle r )
    {
        return !( (r.x + r.width) <= x ||
                  (r.y + r.height) <= y ||
                  r.x >= (x + width) ||
                  r.y >= (y + height) );
    }

    public DoubleRectangle intersection( DoubleRectangle r )
    {
        double x1 = Math.max( x, r.x );
        double x2 = Math.min( x+width, r.x+r.width );
        double y1 = Math.max( y, r.y );
        double y2 = Math.min( y+height, r.y+r.height );

        return new DoubleRectangle( x1, y1, x2-x1, y2-y1 );
    }

    public boolean containsRectangle( DoubleRectangle r )
    {
        return contains( r.x, r.y ) &&
               contains( r.x+r.width, r.y ) &&
               contains( r.x+r.width, r.y+r.height ) &&
               contains( r.x, r.y+r.height );
    }

    public DoubleRectangle union( DoubleRectangle r )
    {
        double x1 = Math.min( x, r.x );
        double x2 = Math.max( x+width, r.x+r.width );
        double y1 = Math.min( y, r.y );
        double y2 = Math.max( y+height, r.y+r.height );

        return new DoubleRectangle( x1, y1, x2-x1, y2-y1 );
    }

    public void add( DoubleRectangle r )
    {
        double x1 = Math.min( x, r.x );
        double x2 = Math.max( x+width, r.x+r.width );
        double y1 = Math.min( y, r.y );
        double y2 = Math.max( y+height, r.y+r.height );

        x = x1;
        y = y1;
        width = x2-x1;
        height = y2-y1;
    }

    public boolean isEmpty()
    {
        return width <= 0 ||
               height <= 0;
    }

    public boolean equals( Object obj )
    {
        if (obj instanceof DoubleRectangle)
        {
            DoubleRectangle r = (DoubleRectangle)obj;

            return x == r.x &&
                   y == r.y &&
                   width == r.width &&
                   height == r.height;
        }
        else
        {
            return false;
        }
    }

} // END DoubleRectangle
