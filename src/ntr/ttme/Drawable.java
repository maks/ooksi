package ntr.ttme;

import javax.microedition.lcdui.Graphics;

public interface Drawable
{
    public static final int BUFFER_BACK = 0;
    public static final int BUFFER_FRONT = 1;

    public void drawToBackBuffer( View view );

    public void drawToFrontBuffer( View view );

    public void draw( View view, int buffer );

    public void draw( View view, Graphics g );

} // END Drawable
