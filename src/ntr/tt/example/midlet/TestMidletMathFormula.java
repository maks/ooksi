package ntr.tt.example.midlet;

import java.io.InputStream;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import ntr.ttme.DoubleRectangle;
import ntr.ttme.TrueTypeException;
import ntr.ttme.TrueTypeRegistry;
import ntr.ttme.TrueTypeText;
import ntr.ttme.View;

public class TestMidletMathFormula extends MIDlet implements CommandListener
{
    static final Integer COLOR_RED = new Integer( 0x00ff0000 );
    static final Integer COLOR_GREEN = new Integer( 0x0000ff00 );
    static final Integer COLOR_BLUE = new Integer( 0x000000ff );
    static final Integer COLOR_YELLOW = new Integer( 0x00f1d471 );
    static final Integer COLOR_GRAY = new Integer( 0x00777777 );

    static final String LMATH2_INTEGRAL = "\u0073";
    static final String LMATH2_ROOT = "\u0093";

    private Command cmd_exit;

    private Display display;

    public TestMidletMathFormula()
    {
    }

    protected void startApp() throws MIDletStateChangeException
    {
        display = Display.getDisplay( this );

        InputStream inputStream = null;
        try
        {
            inputStream = getClass().getResourceAsStream( "lmath2.ttf" );

            TrueTypeRegistry.instance.registerTrueType( "FONT1", inputStream );

            inputStream = getClass().getResourceAsStream( "Acoustic_Light.ttf" );

            TrueTypeRegistry.instance.registerTrueType( "FONT2", inputStream );
        }
        catch( TrueTypeException e )
        {
            System.out.println( "Unable to load font!" );
            e.printStackTrace();
        }
        catch( Throwable t )
        {
            System.out.println( "Error!" );
            t.printStackTrace();
        }
        finally
        {
            try
            {
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
            catch( Throwable ignore ) {}
        }

        MyCanvas myCanvas = new MyCanvas();
        myCanvas.setCommandListener(this);

        cmd_exit = new Command( "Exit", Command.EXIT, 0 );
        myCanvas.addCommand( cmd_exit );

        display.setCurrent( myCanvas );
    }

    protected void pauseApp()
    {
        TrueTypeRegistry.instance.deregisterAll();
    }

    protected void destroyApp( boolean arg0 ) throws MIDletStateChangeException
    {
        notifyDestroyed();
    }

    public void commandAction( Command c, Displayable d )
    {
        if ( c == cmd_exit )
        {
            try
            {
                destroyApp( true );
            }
            catch ( MIDletStateChangeException e )
            {
                showException( e );
            }
        }
    }

    public void showException( Exception e )
    {
        Alert alert = new Alert( "Error" );
        alert.setString( e.getMessage() );
        alert.setType( AlertType.ERROR );
        alert.setTimeout( Alert.FOREVER );

        display.setCurrent( alert );
    }

    private class MyCanvas extends Canvas
    {
        private Image hiddenImage;
        private View view;

        public MyCanvas()
        {
            super();
        }

        public void paint( Graphics g )
        {
            if (view == null)
            {
                int width = getWidth();
                int height = getHeight();

                view = new View( width, height );
                hiddenImage = Image.createImage( width, height );
                view.setBuffers( g, hiddenImage.getGraphics() );
                view.clearBackBuffer();

                try
                {
                    // Screen coordinates can be converted to world coordinates. (TTME uses "world coordintates" for rendering vector graphics)
                    // Btw, world coordinates can be converted back to screen coordinates ("view coordinates") 

                    double integralXPosition = view.convertXViewToWorld( 0 ); // start writing from the left side of the screen on the screen (x-coord=0)
                    double integralBaselineY = view.convertYViewToWorld( getHeight()/2 ); // put font "baseline" on the middle of the screen

                    TrueTypeText integralText = new TrueTypeText( "FONT1",
                                                                  integralXPosition,
                                                                  integralBaselineY,
                                                                  LMATH2_INTEGRAL,
                                                                  72,
                                                                  COLOR_RED,
                                                                  COLOR_RED );
                    integralText.drawToBackBuffer( view );
                    DoubleRectangle integralBounds = integralText.getBounds();

                    double integralRightSideX = integralBounds.getX()+integralBounds.getWidth();
                    double integralBottomY = integralBounds.getY()+integralBounds.getHeight();
                    double integralMiddleY = integralBounds.getY()+integralBounds.getHeight()/2;

                    TrueTypeText rootText = new TrueTypeText( "FONT1",
                                                              integralRightSideX,
                                                              integralBottomY,
                                                              LMATH2_ROOT,
                                                              20,
                                                              COLOR_BLUE,
                                                              COLOR_BLUE );
                    rootText.drawToBackBuffer( view );
                    DoubleRectangle rootBounds = rootText.getBounds();

                    double rootRightSideX = rootBounds.getX()+rootBounds.getWidth();

                    TrueTypeText xMinus2Text = new TrueTypeText( "FONT2",
                                                           rootRightSideX,
                                                           integralBottomY,
                                                           "x-2",
                                                           20,
                                                           COLOR_BLUE,
                                                           COLOR_BLUE );
                    xMinus2Text.drawToBackBuffer( view );
                    DoubleRectangle xMinus2Bounds = xMinus2Text.getBounds();

                    hiddenImage.getGraphics().drawLine( view.convertXWorldToView( rootRightSideX ),
                                                        view.convertYWorldToView( rootBounds.getY() ),
                                                        view.convertXWorldToView( rootRightSideX + xMinus2Bounds.getWidth() ),
                                                        view.convertYWorldToView( rootBounds.getY() ) );

                    hiddenImage.getGraphics().drawLine( view.convertXWorldToView( rootBounds.getX() ),
                                                        view.convertYWorldToView( integralMiddleY )+4, // 4 pixels spacing
                                                        view.convertXWorldToView( rootRightSideX + xMinus2Bounds.getWidth() ),
                                                        view.convertYWorldToView( integralMiddleY )+4 );

                    TrueTypeText text4 = new TrueTypeText( "FONT2",
                                                           rootRightSideX,
                                                           integralMiddleY,
                                                           "2x",
                                                           20,
                                                           COLOR_BLUE,
                                                           COLOR_BLUE );
                    text4.drawToBackBuffer( view );

                    double fourPixelsWorldSpacing = view.convertYDistanceViewToWorld( 4 ); // 4 view pixels converted to world dimensions 

                    TrueTypeText dxText = new TrueTypeText( "FONT2",
                                                           rootRightSideX + xMinus2Bounds.getWidth(),
                                                           integralMiddleY + xMinus2Bounds.getHeight()/2 + fourPixelsWorldSpacing,
                                                           "dx", // sorry for the uppercase, the font has only capital letters
                                                           20,
                                                           COLOR_BLUE,
                                                           COLOR_BLUE );
                    dxText.drawToBackBuffer( view );
                }
                catch ( TrueTypeException e )
                {
                    System.out.println( "Unable to vreate vector text!" );
                    e.printStackTrace();
                }

            }

            g.drawImage( hiddenImage, 0, 0, Graphics.TOP|Graphics.LEFT );
        }

    } // END MyCanvas

} // END TestMidletMathFormula
