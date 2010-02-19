package ntr.ttme;

public class TTPoint
{
    // font-definition point-types
    public static final byte TYPE_NONE  = 0;
    public static final byte TYPE_OFF_CURVE = 1; // bi2spline
    public static final byte TYPE_ON_CURVE = 2;

    short x;
    short y;
    byte type;

    TTPoint()
    {
        super();
    }

    public short getX()
    {
        return x;
    }

    public short getY()
    {
        return y;
    }

    public byte getType()
    {
        return type;
    }

} // END TTPoint
