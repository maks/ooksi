package ntr.ttme;

public abstract class MatrixTransformationAdapter
    implements Transformable
{
    protected boolean modified;

    protected DoubleMatrix3x3 ctm;

    protected MatrixTransformationAdapter()
    {
        ctm = DoubleMatrix3x3.createIdentityMatrix();
    }

    // Transformable interface

    public void translate( double dx, double dy )
    {
        ctm.rightMul( DoubleMatrix3x3.translate( dx, dy ) );
        modified = true;
    }

    public void rotate( double angle )
    {
        ctm.rightMul( DoubleMatrix3x3.rotate( angle ) );
        modified = true;
    }

    public void rotate( double angle, double x, double y )
    {
        ctm.rightMul( DoubleMatrix3x3.rotate( angle, x, y ) );
        modified = true;
    }

    public void scale( double xFactor, double yFactor )
    {
        ctm.rightMul( DoubleMatrix3x3.scale( xFactor, yFactor ) );
        modified = true;
    }

    public void scale( double xFactor, double yFactor,
                       double originX, double originY,
                       double dx, double dy )
    {
        ctm.rightMul( DoubleMatrix3x3.scale( xFactor, yFactor,
                                             originX, originY,
                                             dx, dy ) );
        modified = true;
    }

    public void skew( double angle )
    {
        ctm.rightMul( DoubleMatrix3x3.skew( angle ) );
        modified = true;
    }

    public void skew( double angle, double originX, double originY )
    {
        ctm.rightMul( DoubleMatrix3x3.skew( angle, originX, originY ) );
        modified = true;
    }

} // END MatrixTransformationAdapter
