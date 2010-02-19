package ntr.ttme;

public interface Transformable
{
    public void translate( double dx, double dy );

    public void rotate( double angle );

    public void rotate( double angle, double x, double y );

    public void scale( double xFactor, double yFactor );

    public void scale( double xFactor, double yFactor,
                       double originX, double originY,
                       double dx, double dy );

    public void skew( double angle );

    public void skew( double angle, double originX, double originY );

} // END Transformable
