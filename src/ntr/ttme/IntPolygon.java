package ntr.ttme;

public class IntPolygon
{
    int[] xpoints;
    int[] ypoints;
    int npoints;

    public IntPolygon() 
    {
        xpoints = new int[32];
        ypoints = new int[32];
        npoints = 0;
    }

    public IntPolygon( int xpoints[], int ypoints[], int npoints )
    {
        this.xpoints = new int[npoints];
        this.ypoints = new int[npoints];
        this.npoints = npoints;

        System.arraycopy( xpoints, 0, this.xpoints, 0, npoints );
        System.arraycopy( ypoints, 0, this.ypoints, 0, npoints ); 
    }

    public void addPoint( int x, int y )
    {
        if (npoints == xpoints.length)
        {
            int clone[];

            clone = new int[npoints*2];
            System.arraycopy( xpoints, 0, clone, 0, npoints );
            xpoints = clone;

            clone = new int[npoints*2];
            System.arraycopy( ypoints, 0, clone, 0, npoints );
            ypoints = clone;
        }

        xpoints[npoints] = x;
        ypoints[npoints] = y;
        npoints++;
    }

} // END IntPolygon
