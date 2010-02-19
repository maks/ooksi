package ntr.ttme;

class DoubleMatrix3x3
{
    private static final DoubleMatrix3x3 ZERO =
        new DoubleMatrix3x3( 0, 0, 0,
                             0, 0, 0,
                             0, 0, 0 );

    private static final DoubleMatrix3x3 IDENTITY =
        new DoubleMatrix3x3( 1, 0, 0,
                             0, 1, 0,
                             0, 0, 1 );

    double[][] element;

    public DoubleMatrix3x3()
    {
        element = new double[3][3];
    }

    public DoubleMatrix3x3( double m11, double m12, double m13,
                            double m21, double m22, double m23,
                            double m31, double m32, double m33 )
    {
        element=new double[3][3];

        set( m11, m12, m13,
             m21, m22, m23,
             m31, m32, m33 );
    }

    public DoubleMatrix3x3( DoubleMatrix3x3 m )
    {
        element = new double[3][3];

        set( m );
    }

    public void set( double m11, double m12, double m13,
                     double m21, double m22, double m23,
                     double m31, double m32, double m33 )
    {
        element[0][0]=m11; element[0][1]=m12; element[0][2]=m13;
        element[1][0]=m21; element[1][1]=m22; element[1][2]=m23;
        element[2][0]=m31; element[2][1]=m32; element[2][2]=m33;
    }

    public void set( DoubleMatrix3x3 m )
    {
        element[0][0]=m.element[0][0]; element[0][1]=m.element[0][1]; element[0][2]=m.element[0][2];
        element[1][0]=m.element[1][0]; element[1][1]=m.element[1][1]; element[1][2]=m.element[1][2];
        element[2][0]=m.element[2][0]; element[2][1]=m.element[2][1]; element[2][2]=m.element[2][2];
    }

    public DoubleMatrix3x3 add( DoubleMatrix3x3 m )
    {
        element[0][0]+=m.element[0][0]; element[0][1]+=m.element[0][1]; element[0][2]+=m.element[0][2];
        element[1][0]+=m.element[1][0]; element[1][1]+=m.element[1][1]; element[1][2]+=m.element[1][2];
        element[2][0]+=m.element[2][0]; element[2][1]+=m.element[2][1]; element[2][2]+=m.element[2][2];

        return this; // for easier combining of matrix operations
    }

    public DoubleMatrix3x3 sub( DoubleMatrix3x3 m )
    {
        element[0][0]-=m.element[0][0]; element[0][1]-=m.element[0][1]; element[0][2]-=m.element[0][2];
        element[1][0]-=m.element[1][0]; element[1][1]-=m.element[1][1]; element[1][2]-=m.element[1][2];
        element[2][0]-=m.element[2][0]; element[2][1]-=m.element[2][1]; element[2][2]-=m.element[2][2];

        return this; // for easier combining of matrix operations
    }

    public DoubleMatrix3x3 rightMul( DoubleMatrix3x3 m )
    {
        double r11 = element[0][0]*m.element[0][0] + element[0][1]*m.element[1][0] + element[0][2]*m.element[2][0];
        double r12 = element[0][0]*m.element[0][1] + element[0][1]*m.element[1][1] + element[0][2]*m.element[2][1];
        double r13 = element[0][0]*m.element[0][2] + element[0][1]*m.element[1][2] + element[0][2]*m.element[2][2];

        double r21 = element[1][0]*m.element[0][0] + element[1][1]*m.element[1][0] + element[1][2]*m.element[2][0];
        double r22 = element[1][0]*m.element[0][1] + element[1][1]*m.element[1][1] + element[1][2]*m.element[2][1];
        double r23 = element[1][0]*m.element[0][2] + element[1][1]*m.element[1][2] + element[1][2]*m.element[2][2];

        double r31 = element[2][0]*m.element[0][0] + element[2][1]*m.element[1][0] + element[2][2]*m.element[2][0];
        double r32 = element[2][0]*m.element[0][1] + element[2][1]*m.element[1][1] + element[2][2]*m.element[2][1];
        double r33 = element[2][0]*m.element[0][2] + element[2][1]*m.element[1][2] + element[2][2]*m.element[2][2];

        set( r11, r12, r13,
             r21, r22, r23,
             r31, r32, r33 );

        return this; // for easier combining of matrix operations
    }

    public DoubleMatrix3x3 leftMul( DoubleMatrix3x3 m )
    {
        double r11 = m.element[0][0]*element[0][0] + m.element[0][1]*element[1][0] + m.element[0][2]*element[2][0];
        double r12 = m.element[0][0]*element[0][1] + m.element[0][1]*element[1][1] + m.element[0][2]*element[2][1];
        double r13 = m.element[0][0]*element[0][2] + m.element[0][1]*element[1][2] + m.element[0][2]*element[2][2];

        double r21 = m.element[1][0]*element[0][0] + m.element[1][1]*element[1][0] + m.element[1][2]*element[2][0];
        double r22 = m.element[1][0]*element[0][1] + m.element[1][1]*element[1][1] + m.element[1][2]*element[2][1];
        double r23 = m.element[1][0]*element[0][2] + m.element[1][1]*element[1][2] + m.element[1][2]*element[2][2];

        double r31 = m.element[2][0]*element[0][0] + m.element[2][1]*element[1][0] + m.element[2][2]*element[2][0];
        double r32 = m.element[2][0]*element[0][1] + m.element[2][1]*element[1][1] + m.element[2][2]*element[2][1];
        double r33 = m.element[2][0]*element[0][2] + m.element[2][1]*element[1][2] + m.element[2][2]*element[2][2];

        set( r11, r12, r13,
             r21, r22, r23,
             r31, r32, r33 );

        return this; // for easier combining of matrix operations
    }

    public static DoubleMatrix3x3 add( DoubleMatrix3x3 m1, DoubleMatrix3x3 m2 )
    {
        return new DoubleMatrix3x3( m1 ).add( m2 );
    }

    public static DoubleMatrix3x3 sub( DoubleMatrix3x3 m1, DoubleMatrix3x3 m2 )
    {
        return new DoubleMatrix3x3( m1 ).sub( m2 );
    }

    public static DoubleMatrix3x3 mul( DoubleMatrix3x3 m1, DoubleMatrix3x3 m2 )
    {
        return new DoubleMatrix3x3( m1 ).rightMul( m2 );
    }

    // 2D-transformation matrices

    public static DoubleMatrix3x3 createIdentityMatrix()
    {
        return new DoubleMatrix3x3( IDENTITY );
    }

    public static DoubleMatrix3x3 createZeroMatrix()
    {
        return new DoubleMatrix3x3( ZERO );
    }

    public static DoubleMatrix3x3 translate( double dx, double dy )
    {
        DoubleMatrix3x3 result = createIdentityMatrix();

        result.element[0][2] = dx;
        result.element[1][2] = dy;

        return result;
    }

    public static DoubleMatrix3x3 rotate( double angle )
    {
        DoubleMatrix3x3 result = createIdentityMatrix();

        double cosa = Math.cos( angle );
        double sina = Math.sin( angle );

        result.element[0][0]=cosa; result.element[0][1]=-sina;
        result.element[1][0]=sina; result.element[1][1]=cosa;

        return result;
    }

    public static DoubleMatrix3x3 rotate( double angle, double x, double y )
    {
        return translate( -x, -y ).leftMul(
               rotate( angle ) ).leftMul(
               translate( x, y ) );
    }

    public static DoubleMatrix3x3 scale( double xFactor, double yFactor )
    {
        DoubleMatrix3x3 result = createIdentityMatrix();

        result.element[0][0] = xFactor;
        result.element[1][1] = yFactor;

        return result;
    }

    public static DoubleMatrix3x3 scale( double xFactor, double yFactor,
                                         double originX, double originY,
                                         double dx, double dy )
    {
        return translate( -originX, -originY ).leftMul(
               scale( xFactor, yFactor ) ).leftMul(
               translate( originX+dx, originY+dy ) );
    }

    public static DoubleMatrix3x3 skew( double angle )
    {
        DoubleMatrix3x3 result = createIdentityMatrix();

        result.element[0][1] = (1.0d / Math.tan( angle )); // ctg( angle )

        return result;
    }

    public static DoubleMatrix3x3 skew( double angle, double originX, double originY )
    {
        return translate( -originX, -originY ).leftMul(
               skew( angle ) ).leftMul(
               translate( originX, originY ) );
    }

} // END DoubleMatrix3x3
