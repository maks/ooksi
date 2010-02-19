package ntr.ttme;

/**
    low-level bitwise operations
 */
public class Bitwise
{
    public static short toSHORT( byte b1, byte b2 )
    {
        int sb1 = (b1>=0 ? (int)b1 : 256+(int)b1);
        int sb2 = (b2>=0 ? (int)b2 : 256+(int)b2);

        return (short)( sb1*0x100 + sb2 );
    }

    public static short toUSHORT( byte b1, byte b2 )
    {
        int sb1 = (b1>=0 ? (int)b1 : 256+(int)b1);
        int sb2 = (b2>=0 ? (int)b2 : 256+(int)b2);

        short result = (short)( sb1*0x100 + sb2 );
        // return result;
        return result > 0 ? result : (short)(result & 0x7FFF);
    }

    public static short toUSHORTsigned( byte b1, byte b2 )
    {
        int sb1 = (b1>=0 ? (int)b1 : 256+(int)b1);
        int sb2 = (b2>=0 ? (int)b2 : 256+(int)b2);

        short result = (short)( sb1*0x100 + sb2 );
        return result;
    }

    public static int toULONG( byte b1, byte b2, byte b3, byte b4 )
    {
        int sb1 = (b1>=0 ? (int)b1 : 256+(int)b1);
        int sb2 = (b2>=0 ? (int)b2 : 256+(int)b2);
        int sb3 = (b3>=0 ? (int)b3 : 256+(int)b3);
        int sb4 = (b4>=0 ? (int)b4 : 256+(int)b4);

        return (sb1*0x1000000 + sb2*0x10000 + sb3*0x100 + sb4);
    }

    public static boolean isBitSet( byte b, int bitPosition )
    {
        return ((b & (1<<bitPosition)) != 0);
    }

} // END Bitwise
